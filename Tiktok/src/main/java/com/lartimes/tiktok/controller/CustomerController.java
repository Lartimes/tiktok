package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.vo.*;
import com.lartimes.tiktok.service.FavoritesService;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.user.UserService;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 12:09
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final UserService userService;
    private final QiNiuFileService qiNiuFileService;
    private final FavoritesService favoritesService;
    private final TypeService typeService;

    public CustomerController(UserService userService, QiNiuFileService qiNiuFileService, FavoritesService favoritesService, TypeService typeService) {
        this.userService = userService;
        this.qiNiuFileService = qiNiuFileService;
        this.favoritesService = favoritesService;
        this.typeService = typeService;
    }

    /**
     * 更新用户信息
     *
     * @param userVO 用户信息vo
     * @return
     */
    @PutMapping
    public R updateCustomer(@RequestBody @Validated
                            UserVO userVO) {
        if (userService.updateUserVo(userVO)) {
            return R.ok().message("更新成功");
        }
        return R.error().message("请重试");
    }

    /**
     * 获取个人信息
     *
     * @param userId 用户ID
     * @return
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
     * 获取关注Page<User>
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
     *
     * @param followUserId
     * @return
     */
    @PostMapping("/follow")
    public R follows(@RequestParam Long followUserId) {
        return userService.followUser(followUserId) ? R.ok().message("关注成功") : R.ok().message("取关成功");
    }

    /**
     * 图片token
     *
     * @return
     */
    @GetMapping("/avatar/token")
    public R getAvatarToken() {
        return R.ok().data(qiNiuFileService.getAvatarToken());
    }


    /**
     * 获取所有收藏夹
     *
     * @return
     */
    @GetMapping("/favorites")
    public R getFavorites() {
        Long userId = UserHolder.get();
        if (userId != null) {
            return R.ok().data(favoritesService.getFavoritesByUserId(userId));
        }
        return R.error();
    }

    /**
     * 获取收藏夹详细信息
     *
     * @param id
     * @return
     */
    @GetMapping("/favorites/{id}")
    public R getFavoritesById(@PathVariable Long id) {
        Long userId = UserHolder.get();
        if (userId != null) {
            return R.ok().data(favoritesService.getFavoriteById(userId, id));
        }
        return R.error();
    }

    /**
     * 删除某些收藏夹
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/favorites/{ids}")
    public R deleteFavorites(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        if (favoritesService.deleteFavorites(idList)) {
            return R.ok().message("删除成功");
        }
        return R.error().message("删除失败");
    }

    /**
     * 进行收藏夹的更新/添加
     *
     * @param favoritesVo
     * @return
     */
    @PostMapping("/favorites")
    public R changeFavorites(@RequestBody @Validated FavoritesVo favoritesVo) {
        Long holder = UserHolder.get();
        if (favoritesService.changgeFavorites(favoritesVo, holder)) {
            return R.ok().message("success");
        }
        return R.error().message("fail , 请重试");
    }


    /**
     * 进行多个分类的订阅
     *
     * @param typeIds
     * @return
     */
    @PostMapping("/subscribe")
    public R subscribeTypes(@RequestParam("types") String typeIds) {
        List<Long> idList = Arrays.stream(typeIds.split(","))
                .map(Long::parseLong)
                .toList();
        Long userId = UserHolder.get();
        if (userId != null) {
            return typeService.subscribeTypes(idList, userId) ? R.ok().message("订阅成功")
                    : R.error().message("订阅失败");
        }
        return R.error();
    }


    /**
     * 获取用户关注分类
     *
     * @return
     */
    @GetMapping("/subscribe")
    public R getSubscribe() {
        Long userId = UserHolder.get();
        if (userId != null) {
            return R.ok().data(typeService.getSubscribes(userId));
        }
        return R.error().message("请登录后重试");
    }


    /**
     * 获取未关注的分类
     *
     * @return
     */
    @GetMapping("/noSubscribe")
    public R getNoSubscribe() {
        Long userId = UserHolder.get();
        if (userId != null) {
            return R.ok().data(typeService.getNoSubscribes(userId));
        }
        return R.error().message("请登录后重试");
    }

    /**
     * 用户停留时长修改模型
     *
     * @param model
     * @return
     */
    @PostMapping("/updateUserModel")
    public R updateUserModel(@RequestBody Model model) {
        final Double score = model.getScore();
        if (score == -0.5 || score == 1.0) {
            final UserModel userModel = new UserModel();
            userModel.setUserId(UserHolder.get());
            userModel.setModels(Collections.singletonList(model));
            userService.updateUserModel(userModel);
        }
        return R.ok();
    }


}
