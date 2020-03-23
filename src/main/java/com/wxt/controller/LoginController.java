package com.wxt.controller;

import com.wxt.dao.UserDao;
import com.wxt.model.LoginTicket;
import com.wxt.service.UserService;
import com.wxt.utils.TouTiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/reg/",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse response)

    {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                return TouTiaoUtils.getJsonString(0, "注册成功");
            } else {
                return TouTiaoUtils.getJsonString(1, map);
//                return map.toString();
            }
        }catch (Exception e)
        {
            logger.error("注册异常",e.getMessage());
            System.out.println("注册这里出现问题");
            e.printStackTrace();
            return TouTiaoUtils.getJsonString(1,"注册异常");
        }


    }

    @RequestMapping(path = "/login/",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember",defaultValue = "0")int rember,
                      HttpServletResponse response)
    {
        try {
            Map<String, Object> map = userService.login(username,password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
//                System.out.println(rember);
//                if (rember>0)
//                {
                    cookie.setMaxAge(3600*24*5);
//                }
                response.addCookie(cookie);
                return TouTiaoUtils.getJsonString(0, "登录成功");
            } else {
                return TouTiaoUtils.getJsonString(1, map);
//                return map.toString();
            }
        }catch (Exception e)
        {
            logger.error("登录异常",e.getMessage());
            System.out.println("登录这里出现问题");
            e.printStackTrace();
            return TouTiaoUtils.getJsonString(1,"登录异常");
        }

    }


    @RequestMapping(path = "/logou5",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public  RedirectView logout(HttpServletRequest httpServletRequest)
    {
        String ticket = null;
        if (httpServletRequest.getCookies()!=null)
        {
            for (Cookie cookie: httpServletRequest.getCookies())
            {

                if ("ticket".equals(cookie.getName()))
                {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
//        public RedirectView logout(@CookieValue("ticket") String ticket)
//        {

        if (ticket!=null)
        {
            userService.logout(ticket);
        }

        RedirectView red = new RedirectView("/",true);
        red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return red;
    }

//    @RequestMapping(path = "/logou2",method = {RequestMethod.GET,RequestMethod.POST})
//    public  String logout2(HttpServletRequest httpServletRequest)
//    {
//        String ticket = null;
//        if (httpServletRequest.getCookies()!=null)
//        {
//            for (Cookie cookie: httpServletRequest.getCookies())
//            {
//
//                if ("ticket".equals(cookie.getName()))
//                {
//                    ticket = cookie.getValue();
//                    break;
//                }
//            }
//        }
////        public RedirectView logout(@CookieValue("ticket") String ticket)
////        {
//
//        if (ticket!=null)
//        {
//            userService.logout(ticket);
//        }
//
//        return "redirct:/";
//    }

}
