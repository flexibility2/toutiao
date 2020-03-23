package com.wxt.async;
import com.alibaba.fastjson.JSON;
import com.wxt.utils.JedisAdapter;
import com.wxt.utils.RedisKeysUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// applicationContext 应用上下文启动后，延迟加载 初始化bean实例

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    Map<EventType,List<EventHandler> >config = new HashMap<>();
    ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler >beans  = applicationContext.getBeansOfType(EventHandler.class);

        if (beans!=null)
        {
            for (Map.Entry<String,EventHandler> entry: beans.entrySet())
            {
                for (EventType eventType: entry.getValue().getSupportTypes())
                {
                    if (config.get(eventType)==null)
                    {
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    String key = RedisKeysUtils.getEventQueueKey();
                    List<String>event = jedisAdapter.brpop(0,key);
                    for (String name:event)
                    {
                        if (name.equals(key))continue;

                        EventModel model = JSON.parseObject(name,EventModel.class);

                        if (!config.containsKey(model.getType()))
                        {
                           logger.error("conifg里面没有model的这个type");
                        }

                        for (EventHandler handler:config.get(model.getType()))
                        {
                            handler.doHandler(model);
                        }

                    }
                }
            }
        });

        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
