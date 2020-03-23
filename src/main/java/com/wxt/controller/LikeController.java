package com.wxt.controller;

import com.wxt.async.EventModel;
import com.wxt.async.EventProducer;
import com.wxt.async.EventType;
import com.wxt.model.EntityType;
import com.wxt.model.HostHolder;
import com.wxt.model.News;
import com.wxt.service.LikeService;
import com.wxt.service.NewsService;
import com.wxt.utils.JedisAdapter;
import com.wxt.utils.TouTiaoUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    private  Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        // 做了两件事，
        // 1, 将useId 加入newId的like集合中
        // 2. 得到改newsId like结合的 最新 用户数量
        if (hostHolder.get()==null)
        {
            logger.error("用户未登陆");
            return TouTiaoUtils.getJsonString(0,"0");
        }
        long likeCount = likeService.like(hostHolder.get().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.selectById(newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.get().getId()).setEntityId(newsId).
                setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return TouTiaoUtils.getJsonString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        // 做了两件事，
        // 1, 将useId 加入newId的like集合中
        // 2. 得到改newsId like结合的 最新 用户数量
        long dislikeCount = likeService.disLike(hostHolder.get().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.selectById(newsId);
        newsService.updateLikeCount(newsId,(int)dislikeCount);

        return TouTiaoUtils.getJsonString(0, String.valueOf(dislikeCount));
    }

}
