package com.wxt.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {

    private Map<String,Object>map = new HashMap<>();

    public void set(String key,Object val)
    {
        map.put(key,val);
    }

    public Object get(String key)
    {
        return map.get(key);
    }

}
