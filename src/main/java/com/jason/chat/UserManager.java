package com.jason.chat;

import com.jason.chat.dto.User;
import com.jason.chat.msg.Message;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: mahao
 * @date: 2021/5/13 11:13
 */
public final class UserManager {

    private UserManager() {}
    private static final UserManager SELF = new UserManager();
    /**
     * 维护所有在线人员
     */
    private final List<User> onlineList = new CopyOnWriteArrayList<>();
    /**
     * 登录验证器
     */
    private final Validator validator = Validator.getInstance();


    public static UserManager getInstance() {
        return SELF;
    }

    public State online(Message msg) {
        if (!onlineList.contains(msg.getUser())) {
            if (validator.validate(msg.getUser().getName(), msg.getData().toString())) {
                this.onlineList.add(msg.getUser());
                return State.NORMAL;
            } else {
                return State.WRONG;
            }
        }
        return State.ALREADY_ONLINE;
    }

    public void offline(User user) {
        onlineList.remove(user);
    }

    public boolean isOnline(SelectionKey key) {
        return this.isOnline((Message) key.attachment());
    }

    public boolean isOnline(Message msg) {
        return null != msg &&  this.isOnline(msg.getUser());
    }

    public boolean isOnline(User user) {
        return onlineList.contains(user);
    }

    public List<User> getAllUser() {
        return onlineList;
    }
}
