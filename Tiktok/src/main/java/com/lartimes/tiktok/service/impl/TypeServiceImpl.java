package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.TypeMapper;
import com.lartimes.tiktok.model.video.Type;
import com.lartimes.tiktok.model.user.UserSubscribe;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.UserSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {
    private static final Logger LOG = LogManager.getLogger(TypeServiceImpl.class);
    @Autowired
    private UserSubscribeService userSubscribeService;

    @Transactional
    @Override
    public boolean subscribeTypes(List<Long> typeIds, Long userId) {
        List<UserSubscribe> list = list(new LambdaQueryWrapper<Type>()
                .in(Type::getId, typeIds)
                .select(Type::getId)).stream().map(
                type -> {
                    return new UserSubscribe(null, type.getId(), userId);
                }
        ).toList();
        if (typeIds == null || typeIds.isEmpty()) {
            LOG.info("关注分类为空 userId : {}", userId);
            return Boolean.TRUE;
        }
        List<UserSubscribe> collections = list(new LambdaQueryWrapper<Type>()
                .in(Type::getId, typeIds)
                .select(Type::getId))
                .stream()
                .map(type -> new UserSubscribe(null, type.getId(), userId))
                .toList();
        return userSubscribeService.saveBatch(collections);
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
        return list(new LambdaQueryWrapper<Type>()
                .notIn(Type::getId, list)
                .select(Type::getId, Type::getName));
    }
}