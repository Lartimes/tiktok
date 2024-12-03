package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.po.User;
import com.lartimes.tiktok.model.vo.RegisterVO;
import com.lartimes.tiktok.model.vo.UserVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface UserService extends IService<User> {

    /**
     *
     * @param registerVO
     */
    boolean registerUser(RegisterVO registerVO);

    User login(User user);

    UserVO getInfo(Long userId);



}
