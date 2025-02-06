package com.lartimes.tiktok.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.TypeMapper;
import com.lartimes.tiktok.mapper.UserMapper;
import com.lartimes.tiktok.mapper.UserSubscribeMapper;
import com.lartimes.tiktok.model.user.Favorites;
import com.lartimes.tiktok.model.user.Follow;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.user.UserSubscribe;
import com.lartimes.tiktok.model.video.Type;
import com.lartimes.tiktok.model.vo.*;
import com.lartimes.tiktok.service.FavoritesService;
import com.lartimes.tiktok.service.FollowService;
import com.lartimes.tiktok.service.InterestPushService;
import com.lartimes.tiktok.service.user.UserService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class);
    private final FavoritesService favoritesService;
    private final FollowService followService;
    private final InterestPushService interestPushService;

    private final RedisCacheUtil redisCacheUtil;
    private final UserSubscribeMapper userSubscribeMapper;
    private final TypeMapper typeMapper;

    public UserServiceImpl(FavoritesService favoritesService, FollowService followService, InterestPushService interestPushService, RedisCacheUtil redisCacheUtil, UserSubscribeMapper userSubscribeMapper, TypeMapper typeMapper) {
        this.favoritesService = favoritesService;
        this.followService = followService;
        this.interestPushService = interestPushService;
        this.redisCacheUtil = redisCacheUtil;
        this.userSubscribeMapper = userSubscribeMapper;
        this.typeMapper = typeMapper;
    }

    @Transactional
    @Override
    public boolean registerUser(RegisterVO registerVO) {
        User user = new User();
        BeanUtils.copyProperties(registerVO, user);
        final String email = user.getEmail();
        User existUser = this.getOne(new LambdaQueryWrapper<User>().eq(StringUtils.hasText(email), User::getEmail, email));
        if (existUser != null) {
            LOG.info("该邮箱已经被注册 : {}", email);
            throw new BaseException("该邮箱已经被注册");
        }

        final String md5Password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(md5Password);
        user.setGmtCreated(LocalDateTime.now());
        this.save(user);
        LOG.info("注册账户 user : {}", user);
        final Long userId = user.getId();
        Favorites favorites = new Favorites();
        favorites.setUserId(userId);
        favorites.setName("默认收藏夹");
        favorites.setGmtCreated(LocalDateTime.now());
        favoritesService.save(favorites);
        LOG.info("为user绑定默认收藏夹 : {}", favorites);
        user.setDefaultFavoritesId(favorites.getId());
        this.updateById(user);
        LOG.info("为user重新绑定默认收藏夹 : {}", user);
        return true;
    }

    @Override
    public User login(User user) {
        final String password = user.getPassword();
        LambdaQueryWrapper<User> email = new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail());
        final User destUser = this.getOne(email);
        if (destUser != null) {
            if (destUser.getPassword().equals(password)) {
                LOG.info("用户 : {} 登录成功", destUser);
                return destUser;
            }
        }
        if (ObjectUtils.isEmpty(destUser)) {
            LOG.info("邮箱错误/不存在: {}", user);
            throw new BaseException("邮箱错误");
        }
        LOG.info("密码错误 :{}", user);
        throw new BaseException("密码错误");
    }

    @Override
    public UserVO getInfo(Long userId) {
        final User user = this.getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            throw new BaseException("该账户似乎不存在/不可用");
        }
        final UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        long countFollowers = followService.count(new LambdaQueryWrapper<Follow>().eq(Objects.nonNull(user.getId()), Follow::getFollowId, user.getId()));
        long countFans = followService.count(new LambdaQueryWrapper<Follow>().eq(Objects.nonNull(user.getId()), Follow::getUserId, user.getId()));
        userVO.setFollow(countFollowers);
        userVO.setFans(countFans);
        return userVO;
    }

    @Transactional
    @Override
    public boolean updateUserVo(UserVO userVO) {
        final Long sourceId = userVO.getId();
        final Long userId = UserHolder.get();
        if (!Objects.equals(sourceId, userId)) {
            return Boolean.FALSE;
        }
        User user = new User();
        User selected = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, userVO.getId()));
        BeanUtils.copyProperties(selected, user);
        LOG.info("用户old 信息: {}", selected);
        BeanUtils.copyProperties(userVO, user);
        user.setGmtCreated(LocalDateTime.now());
        if (this.baseMapper.updateById(user) > 0) {
            return Boolean.TRUE;
        }
        LOG.info("进行用户更新 , {}", user);
        return Boolean.FALSE;
    }

    @Override
    public Page<User> getFansByPage(PageVo pageVo, Long userId) {
        Page<User> userPage = new Page<User>();
        Collection<Long> fansIds = followService.getFansCollection(userId, pageVo);
        LOG.info("获取粉丝collection : {}", fansIds);
        if (fansIds.isEmpty()) {
            return userPage;
        }
        HashSet<Long> followIds = new HashSet<>(followService.getFollowsCollection(userId, null));
        LOG.info("获取关注collection : {}", followIds);
        Map<Long, Boolean> map = new HashMap<>();
        // 遍历粉丝，查看关注列表中是否有
        for (Long fansId : fansIds) {
            map.put(fansId, followIds.contains(fansId));
        }

        Map<Long, User> userMap = getBaseInfoUserToMap(map.keySet());
        ArrayList<User> users = new ArrayList<>();
        // 遍历粉丝列表,保证有序性
        for (Long fansId : fansIds) {
            User user = userMap.get(fansId);
            user.setEach(map.get(user.getId()));
            users.add(user);
        }
        userPage.setRecords(users);
        userPage.setTotal(users.size());
        LOG.info("查看是否互关，准备返回: {}", userPage);
        return userPage;
    }

    @Override
    public Page<User> getFollowersByPage(PageVo pageVo, Long userId) {
        Page<User> userPage = new Page<>();
        Collection<Long> followIds = followService.getFollowsCollection(userId, pageVo);
        if (followIds.isEmpty()) {
            return userPage;
        }
        HashSet<Long> fansSet = new HashSet<>(followService.getFansCollection(userId, null));
        HashMap<Long, Boolean> map = new HashMap<>();
        for (Long followId : followIds) {
            map.put(followId, fansSet.contains(followId));
        }
        Map<Long, User> userMap = getBaseInfoUserToMap(map.keySet());
        ArrayList<User> users = new ArrayList<>();
        // 遍历粉丝列表,保证有序性
        for (Long followId : followIds) {
            User user = userMap.get(followId);
            user.setEach(map.get(user.getId()));
            users.add(user);
        }
        userPage.setRecords(users);
        userPage.setTotal(users.size());
        LOG.info("查看是否互关，准备返回: {}", userPage);
        return userPage;
    }

    private Map<Long, User> getBaseInfoUserToMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return this.getBaseMapper().selectList(new LambdaQueryWrapper<User>().in(User::getId, userIds)
                        .select(User::getSex, User::getDescription, User::getNickName, User::getAvatar, User::getId))
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Transactional
    @Override
    public Boolean followUser(Long followUserId) {
        Long userId = UserHolder.get();
        LOG.info("进行取关/关注操作");
        return followService.follow(userId, followUserId);
    }

    @Override
    public void addSearchHistory(Long userId, String searchName) {
        if (userId != null) {
            String key = RedisConstant.USER_SEARCH_HISTORY + userId;
            redisCacheUtil.addZSetWithScores(key, searchName, null);
            redisCacheUtil.expireBySeconds(key, RedisConstant.USER_SEARCH_HISTORY_TIME);
        }
    }

    @Override
    public Collection<Type> listSubscribeType(Long userId) {
        Set<Long> types = userSubscribeMapper.selectList(new LambdaQueryWrapper<UserSubscribe>()
                .eq(UserSubscribe::getUserId, userId)).stream().map(UserSubscribe::getTypeId).collect(Collectors.toSet());
        if (types.isEmpty()) {
            return Collections.emptyList();
        }
        List<Type> result = typeMapper.selectList(new LambdaQueryWrapper<Type>()
                .in(Type::getId, types));
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public void initModel(ModelVO modelVO) {
        interestPushService.initUserModel(modelVO.getUserId(), modelVO.getLabels());
    }

    @Override
    public void updateUserModel(UserModel userModel) {
        interestPushService.updateUserModel(userModel);
    }


}
