package com.wxt.async.Handler;

import com.wxt.async.EventHandler;
import com.wxt.async.EventModel;
import com.wxt.async.EventType;
import com.wxt.model.Message;
import com.wxt.model.User;
import com.wxt.service.MessageService;
import com.wxt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeNewsHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel eventModel) {
        Message msg = new Message();
        msg.setContent("用户" + userService.getUser(eventModel.getActorId()).getName()
        + "给你的咨询赞，http://127.0.0.1:8080/news/" + eventModel.getEntityId());
        msg.setFromId(eventModel.getActorId());
        msg.setToId(eventModel.getActorId());
        msg.setCreatedDate(new Date());
        msg.setConversationId("点赞会话");
        messageService.addMessage(msg);
    }

    @Override
    public List<EventType> getSupportTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
