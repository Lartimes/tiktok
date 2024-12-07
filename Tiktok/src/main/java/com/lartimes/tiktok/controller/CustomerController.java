package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.model.vo.UserVO;
import com.lartimes.tiktok.service.UserService;
import com.lartimes.tiktok.util.R;
import com.lartimes.tiktok.util.RedisCacheUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 12:09
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /**
     * 更新用户信息
     *
     * @param userVO
     * @return
     */
    @PutMapping
    public R updateCustomer(@RequestBody @Validated
                            UserVO userVO) {

        if (userService.updateUserVo(userVO)) {
            return R.ok();
        }
        return R.error().message("请重试");
    }

    /**
     * 获取个人信息
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @GetMapping("/getInfo/{userId}")
    public R getInfo(@PathVariable Long userId) {
        return R.ok().data(userService.getInfo(userId));
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getInfo/")
    public R getDefaultInfo(HttpServletRequest request) {
        return R.ok().data(userService.getInfo(UserHolder.get()));
    }

    /**
     * 获取粉丝Page<User>
     *
     * @param pageVo
     * @param userId
     * @return
     */
    @GetMapping("/fans")
    public R getFansByPage(PageVo pageVo, Long userId) {
        return R.ok().data(userService.getFansByPage(pageVo, userId));
    }
    /**
     * 获取粉丝Page<User>
     *
     * @param pageVo
     * @param userId
     * @return
     */
    @GetMapping("/follows")
    public R getFollowerSByPage(PageVo pageVo, Long userId) {
        return R.ok().data(userService.getFollowersByPage(pageVo, userId));
    }


    /**
     * 进行关注
     * @param followUserId
     * @return
     */
    @PostMapping("/follow")
    public R follows(@RequestParam Long followUserId) {
        System.out.println(followUserId);
        if (Boolean.TRUE.equals(userService.followUser(followUserId))) {
            return R.ok();
        }
        return R.error();
    }
}
