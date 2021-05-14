package com.jason.chat.msg;

import com.jason.chat.State;
import com.jason.chat.dto.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author: mahao
 * @date: 2021/5/11 17:32
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -12948864912696715L;
    private Head head;
    private Body body;

    private Message() {}

    public static Message join(User user, String password) {
        return new Message().setHead(new Head(user, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Head.JOIN))
                .setBody(new Body().setData(password));
    }

    public static Message leave(User user) {
        return new Message().setHead(new Head(user, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Head.LEAVE));
    }

    public static Message build(User user, Body body) {
        return new Message().setHead(new Head(user, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Head.NORMAL)
                .setState(State.NORMAL.getState())).setBody(body);
    }

    public static Message buildError(String error) {
        return new Message().setHead(new Head(null, null, null).setState(State.ERROR.getState()))
                .setBody(new Body().setData(error));
    }

    public boolean isJoin() {
        return Head.JOIN.equals(this.head.getAction());
    }

    public boolean isLeave() {
        return Head.LEAVE.equals(this.head.getAction());
    }

    public boolean match(State state) {
        return Objects.equals(state.getState(), this.head.getState());
    }

    public boolean invalid() {
        return null == this.getUser();
    }

    public Head getHead() {
        return head;
    }

    public Message setHead(Head head) {
        this.head = head;
        return this;
    }

    public Body getBody() {
        return body;
    }

    public Object getData() {
        return body == null ? null : body.getData();
    }

    public Message setBody(Body body) {
        this.body = body;
        return this;
    }

    public User getUser() {
        return this.head.getUser();
    }

    @Override
    public String toString() {
        return "{" + "\"head\":" +
                head +
                ",\"body\":" +
                body +
                '}';
    }
}
