package com.wxt;

import com.wxt.dao.CommentDAO;
import com.wxt.dao.LoginTicketDAO;
import com.wxt.dao.NewsDAO;
import com.wxt.dao.UserDao;
import com.wxt.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TouTiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDataTest {

    @Autowired
    UserDao userDao;

    @Autowired
    NewsDAO newsDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Test
    public void samll()
    {
        Random random = new Random();

        for (int i = 0;i<10;i++)
        {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(100)));
            user.setName(String.format("USER%d",i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*5*i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLikeCount(i+1);
            news.setUserId(i+1);
            news.setTitle(String.format("TITLE{%d}", i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);

            user.setPassword("testpass");
            userDao.updatPassword(user);

            for (int j = 0; j < 3; ++j) {
                Comment comment = new Comment();
                comment.setUserId(i+1);
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setStatus(0);
                comment.setCreatedDate(new Date());
                comment.setContent("Comment " + String.valueOf(j));
                commentDAO.addComment(comment);
            }

            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i+1));
            loginTicketDAO.addTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(), 2);


        }

        List<News>list = new ArrayList<>();

        list = newsDAO.selectByUserIdAndOffset(0,0,5);

        for (News n: list)
        {
            System.out.println(n);
        }

        System.out.println("=============");

        System.out.println(list.size());

        Assert.assertEquals("testpass",userDao.selectById(1).getPassword());
        userDao.deleteById(1);
        Assert.assertNull(userDao.selectById(1));

        Assert.assertEquals(1,loginTicketDAO.selectByTicket("TICKET1").getUserId());
        Assert.assertEquals(2,loginTicketDAO.selectByTicket("TICKET1").getStatus());
        Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_NEWS).get(0));
    }


}
