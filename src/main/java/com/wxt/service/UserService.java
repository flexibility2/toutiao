package com.wxt.service;

import com.wxt.dao.LoginTicketDAO;
import com.wxt.dao.UserDao;
import com.wxt.model.LoginTicket;
import com.wxt.model.User;
import com.wxt.utils.TouTiaoUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id)
    {
        return userDao.selectById(id);
    }


    public Map<String,Object> register(String name,String password)
    {
        Map<String, Object>map = new HashMap<>();

        if (StringUtils.isBlank(name))
        {
            map.put("msgname","用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password))
        {
            map.put("msgpwd","密码不能为空");
            return  map;
        }

        User user = userDao.selectByName(name);
        if (user!=null)
        {
            map.put("msgname","用户名已经注册");
            return map;
        }

        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(TouTiaoUtils.MD5(password + user.getSalt()));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDao.addUser(user);

//        map.put("userId",user.getId());

        String ticket = addLoginTicket(user.getId());

        map.put("ticket",ticket);

        return map;

    }


    public Map<String,Object>login(String name, String password )
    {
        Map<String,Object>map = new HashMap<>();

        if (StringUtils.isBlank(name))
        {
            map.put("msgname","用户名不能为空");
            return  map;
        }

        if (StringUtils.isBlank(password))
        {
            map.put("msgpwd","密码不能为空");
            return  map;
        }

        User user = userDao.selectByName(name);

        if (user==null)
        {
            map.put("msgname","用户名不存在");
            return map;
        }

        if (!TouTiaoUtils.MD5(password+ user.getSalt()).equals(user.getPassword()))
        {
            map.put("msgpwd","密码错误");
            return map;
        }


        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }


    public void logout(String ticket)
    {
       loginTicketDAO.updateStatus(ticket,1);
    }

    private  String addLoginTicket(int userId)
    {
        LoginTicket ticket =  new LoginTicket();

        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));

        loginTicketDAO.addTicket(ticket);

        return ticket.getTicket();


    }

}
