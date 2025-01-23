package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.UserSubscribeMapper;
import com.lartimes.tiktok.model.user.UserSubscribe;
import com.lartimes.tiktok.service.UserSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class UserSubscribeServiceImpl extends ServiceImpl<UserSubscribeMapper, UserSubscribe> implements UserSubscribeService {

}
