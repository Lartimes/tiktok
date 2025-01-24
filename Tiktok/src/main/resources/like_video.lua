local videoBitmap   =  'video:star:num:' .. ARGV[1]
local videoLikeZSet = 'video:star:ids:' .. ARGV[1]
local userId =  ARGV[2]
if videoBitmap and videoLikeZSet and userId then
    local isLiked = redis.call('ZSCORE', videoLikeZSet, userId)
    if isLiked then
        redis.call('SETBIT', videoBitmap, tostring(userId), 0)
        redis.call('ZREM', videoLikeZSet, userId)
    else
        redis.call('SETBIT', videoBitmap,tostring(userId), 1)
        redis.call('ZADD', videoLikeZSet,redis.call('TIME')[1], userId)
    end
    return 0
else
    return -1
end

