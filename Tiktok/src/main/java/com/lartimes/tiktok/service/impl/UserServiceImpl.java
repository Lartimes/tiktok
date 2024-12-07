package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.UserMapper;
import com.lartimes.tiktok.model.po.Favorites;
import com.lartimes.tiktok.model.po.Follow;
import com.lartimes.tiktok.model.po.User;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.model.vo.RegisterVO;
import com.lartimes.tiktok.model.vo.UserVO;
import com.lartimes.tiktok.service.FavoritesService;
import com.lartimes.tiktok.service.FollowService;
import com.lartimes.tiktok.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private FavoritesService favoritesService;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserRoleServiceImpl userRoleServiceImpl;


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


}
