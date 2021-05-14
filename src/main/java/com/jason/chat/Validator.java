package com.jason.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: mahao
 * @date: 2021/5/13 13:02
 */
public final class Validator {

    private Validator() {
        this.init();
    }
    private Map<String, String> usernamePasswordMap;
    private static final Validator SELF = new Validator();
    private static final String FILE_NAME = "user";
    private static final String IGNORE = "#";

    public static Validator getInstance() {
        return SELF;
    }

    private void init() {
        usernamePasswordMap = new HashMap<>(100);
        InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        if (null != is) {
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                List<String> info = reader.lines().collect(Collectors.toList());
                for (int i = 0; i < info.size(); i += 2) {
                    String str = info.get(i);
                    if (null == str || str.startsWith(IGNORE)) {
                        continue;
                    }
                    usernamePasswordMap.put(str, info.get(i + 1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validate(String username, String password) {
        if (null == username || null == password || username.trim().length() == 0 || password.trim().length() == 0) {
            return false;
        }
        return Objects.equals(password, usernamePasswordMap.get(username));
    }
}
