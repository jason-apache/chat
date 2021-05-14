package com.jason.chat.msg;


import com.jason.chat.dto.User;

import java.io.Serializable;

/**
 * @author: mahao
 * @date: 2021/5/11 17:33
 */
public class Head implements Serializable {

    private static final long serialVersionUID = -3213792362772304996L;
    private User user;
    private String time;
    private String action;
    private int state;

    public static final String JOIN = "J";
    public static final String LEAVE = "L";
    public static final String NORMAL = "N";

    protected Head() {
    }

    protected Head(User user, String time, String action) {
        this.user = user;
        this.time = time;
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public Head setUser(User user) {
        this.user = user;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Head setTime(String time) {
        this.time = time;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Head setAction(String action) {
        this.action = action;
        return this;
    }


    public int getState() {
        return state;
    }

    public Head setState(int state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return "{" + "\"user\":" +
                user +
                ",\"time\":\"" +
                time + '\"' +
                ",\"action\":\"" +
                action + '\"' +
                ",\"state\":" +
                state +
                '}';
    }
}
