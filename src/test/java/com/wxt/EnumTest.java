package com.wxt;

import com.wxt.async.EventType;
import com.wxt.model.EntityType;

import java.util.Arrays;
import java.util.List;

public class EnumTest {

    public static void main(String[] args) {
//        int t = EventType.LIKE.getValue();
        int t = EventType.COMMENT.getValue();
        System.out.println(t);

        System.out.println(EventType.LIKE);

        List<Integer> a = Arrays.asList(t);
        List<EventType>e = Arrays.asList(EventType.COMMENT);
        System.out.println(e);
    }
}
