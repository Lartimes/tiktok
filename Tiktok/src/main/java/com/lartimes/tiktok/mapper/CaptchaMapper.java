package com.lartimes.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lartimes.tiktok.model.user.Captcha;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统验证码 Mapper 接口
 * </p>
 *
 * @author lartimes
 */
@Mapper
public interface CaptchaMapper extends BaseMapper<Captcha> {


}
