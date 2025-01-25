package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.model.vo.RegisterVO;
import com.lartimes.tiktok.model.vo.UserVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface UserService extends IService<User> {

    /**
     * @param registerVO
     */
    boolean registerUser(RegisterVO registerVO);


    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 根据UserId 获取信息
     *
     * @param userId
     * @return
     */
    UserVO getInfo(Long userId);

    /**
     * 修改个人信息
     *
     * @param userVO
     * @return
     */
    boolean updateUserVo(UserVO userVO);


    /**
     * 获取粉丝/关注 pageResult
     * @param pageVo
     * @param userId
     * @return
     */
    Page<User> getFansByPage(PageVo pageVo, Long userId);

    Page<User> getFollowersByPage(PageVo pageVo, Long userId);

    /**
     * 进行取关 或者  关注
     *
     * @param followUserId
     */
    Boolean followUser(Long followUserId);

    /**
     * 添加搜索记录
     * @param userId
     * @param searchName
     */
    void addSearchHistory(Long userId, String searchName);
}
