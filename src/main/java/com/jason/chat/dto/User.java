package com.jason.chat.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: mahao
 * @date: 2021/5/11 17:32
 */
public class User implements Serializable {

    private static final long serialVersionUID = 6473890450140189825L;
    private String name;
    private int sex;

    public User() {
    }

    public User(String name, int sex) {
        this.name = name;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public int getSex() {
        return sex;
    }

    public User setSex(int sex) {
        this.sex = sex;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (sex != user.sex) {
            return false;
        }
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + sex;
        return result;
    }

    @Override
    public String toString() {
        return "{" + "\"name\":\"" +
                name + '\"' +
                ",\"sex\":" +
                sex +
                '}';
    }
}
