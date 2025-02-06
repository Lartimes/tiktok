package com.lartimes.tiktok.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.PermissionMapper;
import com.lartimes.tiktok.model.user.Permission;
import com.lartimes.tiktok.service.user.PermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lartimes
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
