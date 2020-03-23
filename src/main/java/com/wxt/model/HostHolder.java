package com.wxt.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private static ThreadLocal<User>userThreadLocal = new ThreadLocal<>();

    public void set(User user)
    {
        userThreadLocal.set(user);
    }

    public User get()
    {
        return userThreadLocal.get();
    }

    public void clear()
    {
        userThreadLocal.remove();
    }



}
