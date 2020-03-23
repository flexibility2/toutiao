package com.wxt.controller;

import com.wxt.dao.NewsDAO;
import com.wxt.model.*;
import com.wxt.service.CommentService;
import com.wxt.service.NewsService;
import com.wxt.service.UserService;
import com.wxt.utils.TouTiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsContorller {


    private Logger logger = LoggerFactory.getLogger(NewsContorller.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/uploadImage/",method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile multipartFile)
    {
        try {
            String fileUrl = newsService.saveImage(multipartFile);
            if (fileUrl==null)
            {
                return TouTiaoUtils.getJsonString(1,"上传图片失败");
            }
            return TouTiaoUtils.getJsonString(0,fileUrl);
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("uploadImage 出错",e.getMessage());
            return TouTiaoUtils.getJsonString(1,"上传失败");
        }
    }

    @RequestMapping(path = "/image",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void image(@RequestParam("name") String name,
                        HttpServletResponse response)
    {
        response.setContentType("image/jpeg");

        try {
            StreamUtils.copy(new FileInputStream(new File(TouTiaoUtils.IMG_DIR + name)),response.getOutputStream());
        } catch (Exception e) {
            logger.error("显示图片出粗",e.getMessage());
        }
    }

    @RequestMapping(path = "/user/addNews",method = {RequestMethod.POST})
    @ResponseBody
    public String  addNews(@RequestParam("image") String imgge,
                         @RequestParam("title") String title,
                         @RequestParam("link") String link)
    {

        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setLink(link);
            news.setTitle(title);
            news.setImage(imgge);
            if (hostHolder.get() != null) {
                news.setUserId(hostHolder.get().getId());
            } else {
                news.setUserId(99);
            }
            newsService.addNews(news);
            return  TouTiaoUtils.getJsonString(0);
        }catch (Exception e)
        {
            logger.error("addnews出错", e.getMessage());
            return TouTiaoUtils.getJsonString(1,"addNess失败");
        }
    }

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model)
    {
        News news = newsService.selectById(newsId);
        try {
            if (news!=null)
            {
                // 评论
                List<Comment>comments = commentService.getCommentsByEntity(newsId, EntityType.ENTITY_NEWS);
                List<ViewObject>commentVos = new ArrayList<>();
                for (Comment comment:comments)  // 每一条评论
                {
                    ViewObject ov = new ViewObject();
                    ov.set("comment", comment);
                    ov.set("user",userService.getUser(comment.getUserId()));
                    commentVos.add(ov);
                }
                model.addAttribute("comments", commentVos);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }catch (Exception e)
        {
            logger.error("获取资讯明细错误" + e.getMessage());
        }

        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            content = HtmlUtils.htmlEscape(content);
            // 过滤content
            Comment comment = new Comment();
            comment.setUserId(hostHolder.get().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            // 更新news里的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
            // 怎么异步化
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }




}
