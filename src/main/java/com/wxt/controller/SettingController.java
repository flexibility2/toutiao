package com.wxt.controller;

import com.wxt.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingController {


    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/setting")
    @ResponseBody
    public String settring()
    {
        String name= null;
        if (hostHolder.get()!=null)
        {
            name = hostHolder.get().getName();
        }
        return String.format("now in setting %s",name);
    }

}
