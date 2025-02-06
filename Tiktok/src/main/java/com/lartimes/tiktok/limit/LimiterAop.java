package com.lartimes.tiktok.limit;

import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.LimiterException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.ObjectUtils;

//@Aspect
public class LimiterAop {


//    @Autowired
    private RedisCacheUtil redisCacheUtil;


    /**
     * 拦截
     *
     * @param joinPoint
     * @param limiter
     * @return
     * @throws Throwable
     */
    @Before("@annotation(limiter)")
    public Object restriction(ProceedingJoinPoint joinPoint, Limit limiter) throws Throwable {
        final Long userId = UserHolder.get();
        final int limitCount = limiter.limit();
        final String msg = limiter.msg();
        final long time = limiter.time();
        // 缓存是否存在
        String key = RedisConstant.VIDEO_LIMIT + userId;
        final Object o1 = redisCacheUtil.getKey(key);
        if (ObjectUtils.isEmpty(o1)) {
            redisCacheUtil.set(key, 1, time);
        } else {
            if (Integer.parseInt(o1.toString()) > limitCount) {
                throw new LimiterException(msg);
            }
            redisCacheUtil.getRedisTemplate().opsForValue().increment(key, 1);
        }
        Object o = joinPoint.proceed();
        return o;
    }


}