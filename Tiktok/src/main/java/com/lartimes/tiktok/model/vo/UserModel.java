package com.lartimes.tiktok.model.vo;

import com.lartimes.tiktok.holder.UserHolder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/5 23:20
 */
@Data
public class UserModel {
    private List<Model> models;
    private Long userId;

    public static UserModel buildUserModel(List<String> labels, Long videoId, Double score) {
        UserModel userModel = new UserModel();
        ArrayList<Model> models = new ArrayList<>();
        userModel.setUserId(UserHolder.get());
        for (String label : labels) {
            final Model model = new Model();
            model.setLabel(label);
            model.setScore(score);
            model.setVideoId(videoId);
            models.add(model);
        }
        userModel.setModels(models);
        return userModel;
    }
}
