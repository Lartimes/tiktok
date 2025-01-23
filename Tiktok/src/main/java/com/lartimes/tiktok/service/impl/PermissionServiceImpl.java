package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.PermissionMapper;
import com.lartimes.tiktok.model.user.Permission;
import com.lartimes.tiktok.service.PermissionService;
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
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
