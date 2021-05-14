package com.jason.chat.msg;

import java.io.Serializable;
import java.util.List;

/**
 * @author: mahao
 * @date: 2021/5/11 17:33
 */
public class Body implements Serializable {

    private static final long serialVersionUID = 1886584720219005220L;
    private String area;
    private Object data;

    public static final String AREA_MAIN = "m";
    public static final String AREA_ONLINE_LIST = "ol";

    protected Body() {
    }

    public static Body buildMain(Object data) {
        return new Body().setData(data).setArea(AREA_MAIN);
    }

    public static Body buildOnlineList(Object data) {
        return new Body().setData(data).setArea(AREA_ONLINE_LIST);

    }

    public Object getData() {
        return data;
    }

    public Body setData(Object data) {
        this.data = data;
        return this;
    }

    public String getArea() {
        return area;
    }

    public Body setArea(String area) {
        this.area = area;
        return this;
    }

    @Override
    public String toString() {
        String dataStr = data instanceof List ? data.toString() : '\"' + data.toString() + '\"';
        return "{" + "\"area\":\"" +
                area + '\"' +
                ",\"data\":" +
                dataStr +
                '}';
    }
}
