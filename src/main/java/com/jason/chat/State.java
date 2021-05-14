package com.jason.chat;

/**
 * @author: mahao
 * @date: 2021/5/13 10:57
 */
public enum State {

    INVALID(-1, "无效请求"),
    NORMAL(0, "正常"),
    WRONG(1, "用户不存在或密码错误"),
    ALREADY_ONLINE(2, "该用户已经在线！无法登录"),
    ERROR(-2, "异常"),
    ;

    private final int state;
    private final String msg;

    State(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }
}
