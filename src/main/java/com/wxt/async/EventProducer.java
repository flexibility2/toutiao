package com.wxt.async;

import com.alibaba.fastjson.JSON;
import com.wxt.utils.JedisAdapter;
import com.wxt.utils.RedisKeysUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel)
    {
        try {
            String json = JSON.toJSONString(eventModel);
            String key = RedisKeysUtils.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
