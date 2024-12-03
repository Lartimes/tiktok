package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.vo.UserVO;
import com.lartimes.tiktok.service.UserService;
import com.lartimes.tiktok.util.R;
import com.lartimes.tiktok.util.RedisCacheUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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

    @PutMapping
    public void updateCustomer(@RequestBody @Validated
                               UserVO userVO) {
        final Long sourceId = userVO.getId();
        final Long userId = UserHolder.get();
        if (!Objects.equals(sourceId, userId)) {
            R.error().message("只可以修改自己的信息");
        }
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
}
