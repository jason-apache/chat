package com.jason.chat;

/**
 * @author: mahao
 * @date: 2021/5/11 17:59
 */
public final class Printer {

    private Printer() {
    }

    public static void print(Object data) {
        System.out.println(data.toString());
    }
}
