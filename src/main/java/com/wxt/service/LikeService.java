package com.wxt.service;

import com.wxt.utils.JedisAdapter;
import com.wxt.utils.RedisKeysUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    public int getLikeStatus(int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeysUtils.getLikeKey(entityId,entityType);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId)))
        {
            return 1;
        }
        else {
            String disLikeKey = RedisKeysUtils.getDisLikeKey(entityId,entityType);
            if (jedisAdapter.sismember(disLikeKey,String.valueOf(disLikeKey)))
            {
                return -1;
            }
            else return  0;
        }
    }

    public long like(int userId, int entityType, int entityId)
    {
        String likeKey = RedisKeysUtils.getLikeKey(entityId,entityType);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));

        String disLikeKey = RedisKeysUtils.getDisLikeKey(entityId,entityType);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId)
    {
        String disLikeKey = RedisKeysUtils.getDisLikeKey(entityId,entityType);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));

        String likeKey = RedisKeysUtils.getLikeKey(entityId,entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

}
