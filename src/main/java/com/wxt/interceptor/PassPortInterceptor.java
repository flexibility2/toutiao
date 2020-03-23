package com.wxt.interceptor;

import com.wxt.dao.LoginTicketDAO;
import com.wxt.dao.UserDao;
import com.wxt.model.HostHolder;
import com.wxt.model.LoginTicket;
import com.wxt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassPortInterceptor implements HandlerInterceptor {

    @Autowired
    private  LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String ticket = null;
        if (httpServletRequest.getCookies()!=null)
        {
            for (Cookie cookie: httpServletRequest.getCookies())
            {
                if (cookie.getName().equals("ticket"))
                {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket!=null )
        {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus()!=0)
            {
                return true;
            }

            User user = userDao.selectById(loginTicket.getUserId());
            hostHolder.set(user);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView!=null && hostHolder.get()!=null)
        {
            modelAndView.addObject("user",hostHolder.get());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();

        System.out.println(String.format("passinceptor clear.. %s",httpServletRequest.getRequestURL()));
    }
}
