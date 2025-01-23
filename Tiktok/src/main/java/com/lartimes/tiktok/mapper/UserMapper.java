package com.lartimes.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lartimes.tiktok.model.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lartimes
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {



}
