package com.wxt.controller;

import com.wxt.model.EntityType;
import com.wxt.model.HostHolder;
import com.wxt.model.News;
import com.wxt.model.ViewObject;
import com.wxt.service.LikeService;
import com.wxt.service.NewsService;
import com.wxt.service.UserService;
import org.omg.CORBA.portable.ValueInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller

public class HomeController {

    @Autowired
    private  NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    List<ViewObject> getNews(int userId, int offset,int limit)
    {
        List<News>news = newsService.getLatestNews(userId,offset,limit);
        List<ViewObject>ovs = new ArrayList<>();
        int localUserId = hostHolder.get() != null ? hostHolder.get().getId() : 0;
        for (News news1:news)
        {
            ViewObject ov = new ViewObject();
            ov.set("news",news1);
            ov.set("user",userService.getUser(news1.getUserId()));
            if (localUserId != 0) {
                ov.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news1.getId()));
            } else {
                ov.set("like", 0);
            }
            ovs.add(ov);
        }
        return ovs;
    }

    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @RequestParam(value = "pop" ,defaultValue = "0") int pop )
    {

        if (hostHolder.get()==null)
        {
            pop = 0;
        }
        model.addAttribute("pop",pop);
        model.addAttribute("vos", getNews(0, 0, 10));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String user(Model model, @PathVariable(value = "userId") int userId)
    {
        model.addAttribute("vos",getNews(userId,0,10));
        return "home";
    }

}
