package com.wxt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Controller
public class IndexController {


    @RequestMapping("/")
    @ResponseBody
    public String index(HttpSession session)
    {
        return "hello " + session.getAttribute("code");
    }

    @RequestMapping("/profile/{groupId}/{userId}")
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") String userId,
                          @RequestParam(value = "type", defaultValue = "1") int type)
    {
        return String.format("gid: {%s}, uid: {%s}, type: {%d}",groupId,userId,type);
    }

    @RequestMapping("/vm")
    public String vm()
    {
        return "home";
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value = "wxt",defaultValue = "a") String wxt,
                           @RequestParam(value = "key",defaultValue = "key") String key
                           , HttpServletResponse rs)
    {
        rs.addCookie(new Cookie(key,"cookieval"));
        rs.addHeader(key,"headerval");
        return"respnse: " + wxt;
    }

    @RequestMapping(value = "/redirct/{code}")
    @ResponseBody
    public RedirectView redirct(@PathVariable("code") String code,
                                HttpSession session)
    {
//        session.setAttribute("code",code);
//        return "redirct:/";

        RedirectView red =new RedirectView("/",true);
        session.setAttribute("code",code);
        red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return red;

    }

    @RequestMapping(value = "/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key") String key)
    {


       if ("wxt".equals(key))
       {
           return "hell0" + key;
       }
        System.out.println(key);
       throw new IllegalArgumentException("key xx");
    }


    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e)
    {
        return "error " + e.getMessage();
    }






}
