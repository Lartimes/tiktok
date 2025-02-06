package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.TypeMapper;
import com.lartimes.tiktok.model.user.UserSubscribe;
import com.lartimes.tiktok.model.video.Type;
import com.lartimes.tiktok.model.vo.ModelVO;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.user.UserService;
import com.lartimes.tiktok.service.user.UserSubscribeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {
    private static final Logger LOG = LogManager.getLogger(TypeServiceImpl.class);
    @Autowired
    private UserSubscribeService userSubscribeService;
    @Autowired
    private UserService userService;

    @Transactional
    @Override
    public boolean subscribeTypes(List<Long> typeIds, Long userId) {
        if (typeIds == null || typeIds.isEmpty()) {
            LOG.info("关注分类为空 userId : {}", userId);
            return Boolean.TRUE;
        }
        final Collection<Type> types = listByIds(typeIds);
        if (typeIds.size() != types.size()) {
            throw new BaseException("不存在的分类");
        }
        List<UserSubscribe> collections = list(new LambdaQueryWrapper<Type>()
                .in(Type::getId, typeIds)
                .select(Type::getId))
                .stream()
                .map(type -> new UserSubscribe(null, type.getId(), userId))
                .toList();

        userSubscribeService.remove(new LambdaQueryWrapper<UserSubscribe>().eq(UserSubscribe::getUserId, userId));
        userSubscribeService.saveBatch(collections);

        // 初始化模型
        final ModelVO modelVO = new ModelVO();
        modelVO.setUserId(UserHolder.get());
        // 获取分类下的标签
        List<String> labels = new ArrayList<>();
        for (Type type : types) {
            labels.addAll(type.buildLabel());
        }
        modelVO.setLabels(labels);
        userService.initModel(modelVO);

        return Boolean.TRUE;
    }

    @Override
    public List<Type> getSubscribes(Long userId) {
        List<Long> list = userSubscribeService.list(new LambdaQueryWrapper<UserSubscribe>()
                .eq(UserSubscribe::getUserId, userId)
                .select(UserSubscribe::getId)).stream().distinct().map(UserSubscribe::getTypeId).toList();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<Type>()
                .in(Type::getId, list)
                .select(Type::getId, Type::getName));

    }

    @Override
    public List<Type> getNoSubscribes(Long userId) {
        List<Long> list = userSubscribeService.list(new LambdaQueryWrapper<UserSubscribe>()
                .eq(UserSubscribe::getUserId, userId)
                .select(UserSubscribe::getId)).stream().distinct().map(UserSubscribe::getTypeId).toList();
        if (list.isEmpty()) {
            return list(new LambdaQueryWrapper<Type>()
                    .select(Type::getId, Type::getName));
        }
        return list(new LambdaQueryWrapper<Type>()
                .notIn(Type::getId, list)
                .select(Type::getId, Type::getName));
    }

    @Override
    public List<String> random10Labels() {
        List<Type> types = list(new LambdaQueryWrapper<Type>());
        Collections.shuffle(types);
        ArrayList<String> result = new ArrayList<>();
        for (Type type : types) {
            for (String label : type.buildLabel()) {
                if (result.size() == 10) {
                    return result;
                }
                result.add(label);
            }
        }
        return result;
    }
}