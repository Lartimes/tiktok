package com.lartimes.tiktok.service.impl;

import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.vo.Model;
import com.lartimes.tiktok.model.vo.UserModel;
import com.lartimes.tiktok.service.InterestPushService;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/3 21:37
 */
@Service
public class InterestPushServiceImpl implements InterestPushService {
    private static final Logger LOG = LogManager.getLogger(InterestPushServiceImpl.class);
    private final RedisCacheUtil redisCacheUtil;
    @Autowired
    private TypeService typeService;

    public InterestPushServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public Collection<Long> listVideoIdByLabels(List<String> labelNames) {
        if (labelNames == null || labelNames.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> arr = new ArrayList<>();
        for (String labelName : labelNames) {
            arr.add(RedisConstant.SYSTEM_STOCK + labelName);
        }
        Set<Long> videoIds = new HashSet<>();
        final Collection<Object> result = redisCacheUtil.sRandom(arr);
        result.stream().filter(Objects::nonNull).distinct().map(a -> Long.parseLong(a.toString()))
                .forEach(videoIds::add);
        return videoIds;
    }

    @Override
    public void initUserModel(Long userId, List<String> labels) {
        final String key = RedisConstant.USER_MODEL + userId;
        Map<Object, Object> modelMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(labels)) {
            final int size = labels.size();
            // 将标签分为等分概率,不可能超过100个分类
            double probabilityValue = 100.0 / size;
            for (String labelName : labels) {
                modelMap.put(labelName, probabilityValue);
            }
        }
        redisCacheUtil.deleteKey(key);
        redisCacheUtil.hmset(key, modelMap);
        // 为用户模型设置ttl
        redisCacheUtil.expireBySeconds(key, RedisConstant.USER_MODEL_TIME); //5d
    }

    @Override
    public void updateUserModel(UserModel userModel) {
        Long userId = UserHolder.get();
        if (userId != null) {
            List<Model> models = userModel.getModels();
            Map<Object, Object> map = redisCacheUtil.hmget(RedisConstant.USER_MODEL + userId);
            if (map == null) {
                map = new HashMap<>();
            }
            for (Model model : models) {
                String label = model.getLabel();
                if (map.containsKey(label)) {
                    Double newScore = Double.parseDouble(map.get(label).toString()) + model.getScore();
                    map.put(label, newScore);
                } else {
                    map.put(label, model.getScore());
                }
            }
//            防止概率膨胀
            final int labelSize = map.keySet().size();
            map.replaceAll((o, v) -> (Double.parseDouble(v.toString()) + labelSize) / labelSize);
            redisCacheUtil.hmset(RedisConstant.USER_MODEL + userId, map);
        }

    }

    @Override
    public Collection<Long> listVideoIdByUserModel(User user) {
        // 创建结果集
        Set<Long> videoIds = new HashSet<>(10);
        if (user != null) {
            Long id = user.getId();
            Boolean sex = user.getSex();
            Map<Object, Object> map = redisCacheUtil.hmget(RedisConstant.USER_MODEL + id);
            if (!ObjectUtils.isEmpty(map)) {
                final String[] probabilityArray = initProbabilityArray(map); //概率模型生成
                int length = probabilityArray.length;
                Random random = new Random();
                ArrayList<String> arr = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    arr.add(probabilityArray[random.nextInt(length)]);
                }
                List<Long> result = new ArrayList<Long>(redisCacheUtil.pipeline(connection -> {
                    for (String label : arr) {
                        connection.zSetCommands().zRandMember((RedisConstant.SYSTEM_STOCK + label).getBytes());
                    }
                    return null;
                }).stream().distinct().map(a -> Long.parseLong(a.toString())).toList());
                List<Long> historyIds = redisCacheUtil.getRedisTemplate().executePipelined((RedisCallback<?>) connection -> {
                    for (Long history : result) {
                        connection.stringCommands().get((RedisConstant.HISTORY_VIDEO + history + ":" + id).getBytes());
                    }
                    return null;
                }).stream().filter(o -> !ObjectUtils.isEmpty(o)).map((history -> Long.parseLong(history.toString()))).toList();
                if (!ObjectUtils.isEmpty(historyIds)) {
                    for (Long simpId : historyIds) {
                        result.remove(simpId);
                    }
                }
                videoIds.addAll(result);

                // 随机挑选一个视频,根据性别: 男：美女 女：宠物
                final Long aLong = randomVideoId(sex);
                videoIds.add(aLong);
                return videoIds;
            }
        }
//        游客随机10个标签， 抽取视频
        List<String> random10Labels = typeService.random10Labels();
        final ArrayList<String> labelNames = new ArrayList<>();
        Random random = new Random();
        int size = random10Labels.size();
        for (int i = 0; i < 10; i++) {
            labelNames.add(RedisConstant.SYSTEM_STOCK + random10Labels.get(random.nextInt(size)));
        }
        Collection<Object> list = redisCacheUtil.sRandom(labelNames);
        if (!ObjectUtils.isEmpty(list)) {
            videoIds = list.stream().filter(id -> !ObjectUtils.isEmpty(id)).
                    map(id -> Long.valueOf(id.toString())).collect(Collectors.toSet());
        }
        return videoIds;
    }

    @Override
    public Collection<Long> listVideoIdByTypeId(Long typeId) {
        // 随机推送10个
        final List<Object> list = redisCacheUtil.getRedisTemplate().opsForSet().randomMembers(RedisConstant.SYSTEM_TYPE_STOCK + typeId, 12);
        // 可能会有null
        final HashSet<Long> result = new HashSet<>();
        for (Object aLong : Objects.requireNonNull(list)) {
            if (aLong != null) {
                result.add(Long.parseLong(aLong.toString()));
            }
        }
        return result;
    }

    @Override
    public void deleteSystemStockIn(Video destVideo) {
        final List<String> labels = destVideo.buildLabel();
        final Long videoId = destVideo.getId();
        redisCacheUtil.getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            for (String label : labels) {
                connection.sRem((RedisConstant.SYSTEM_STOCK + label).getBytes(), String.valueOf(videoId).getBytes());
            }
            return null;
        });
    }

    @Override
    public void deleteSystemTypeStockIn(Video destVideo) {
        final Long typeId = destVideo.getTypeId();
        redisCacheUtil.removeZSetValue(RedisConstant.SYSTEM_TYPE_STOCK + typeId, destVideo.getId());
    }

    @Override
    public void pushSystemTypeStockIn(Video video) {
        Long typeId = video.getTypeId();
        redisCacheUtil.addZSetWithScores(RedisConstant.SYSTEM_TYPE_STOCK + typeId, video.getId(), null);
    }

    @Override
    public void pushSystemStockIn(Video video) {
        List<String> labels = video.buildLabel();
        Long id = video.getId();
        redisCacheUtil.getRedisTemplate().executePipelined((RedisCallback<?>) connection -> {
            for (String label : labels) {
                connection.setCommands().sAdd((RedisConstant.SYSTEM_STOCK + label).getBytes(), id.toString().getBytes());
            }
            return null;
        });
    }

    private Long randomVideoId(Boolean sex) {
        String label = sex ? "美女" : "宠物";
        String key = RedisConstant.SYSTEM_STOCK + label;
        String intern = key.intern();
        Object o = redisCacheUtil.getRedisTemplate().opsForZSet().randomMember(intern);
        return Long.parseLong(o.toString());
    }

    private String[] initProbabilityArray(Map<Object, Object> map) {
        Map<String, Integer> probabilityMap = new HashMap<>();
        int size = map.size();
        final AtomicInteger n = new AtomicInteger(0);
        map.forEach((k, v) -> {
            // 防止结果为0,每个同等加上标签数
            int probability = (((Double) v).intValue() + size) / size;
            n.getAndAdd(probability);
            probabilityMap.put(k.toString(), probability);
        });

        final String[] probabilityArray = new String[n.get()];

        final AtomicInteger index = new AtomicInteger(0);
        // 初始化数组
        probabilityMap.forEach((labelsId, p) -> {
            int i = index.get();
            int limit = i + p;
            while (i < limit) {
                probabilityArray[i++] = labelsId;
            }
            index.set(limit);
        });
        return probabilityArray;

    }
}
