package com.jason.chat.frame;


import com.jason.chat.Client;
import com.jason.chat.dto.User;
import com.jason.chat.msg.Body;
import com.jason.chat.msg.Message;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author: mahao
 * @date: 2021/5/11 16:19
 */
public class ChatFrame {

    /**
     * 显示消息文本框
     */
    private final JTextArea readContext = new JTextArea(18, 30);
    /**
     * 发送消息文本框
     */
    private final JTextArea writeContext = new JTextArea(6, 30);
    /**
     * 用户列表模型
     */
    private final DefaultListModel<String> model = new DefaultListModel<>();
    /**
     * 用户列表
     */
    private final JList<String> list = new JList<>(model);
    /**
     * 发送消息按钮
     */
    private final JButton btnSend = new JButton("发送");
    /**
     * 关闭聊天窗口按钮
     */
    private final JButton btnClose = new JButton("关闭");
    /**
     * 窗体界面
     */
    private final JFrame frame = new JFrame("ChatFrame");
    /**
     * 用户姓名
     */
    private final User currUser;
    /**
     * 用于与服务器交互
     */
    private final Client service;

    public ChatFrame(Client service, User currUser) {
        this.currUser = currUser;
        this.service = service;
    }

    /**
     * 初始化界面控件及事件
     */
    private void init() {
        frame.setLayout(null);
        frame.setTitle(currUser.getName() + " 聊天窗口");
        frame.setSize(500, 500);
        frame.setLocation(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane readScroll = new JScrollPane(readContext);
        readScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(readScroll);
        JScrollPane writeScroll = new JScrollPane(writeContext);
        writeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.add(writeScroll);
        frame.add(list);
        frame.add(btnSend);
        frame.add(btnClose);
        readScroll.setBounds(10, 10, 320, 300);
        readContext.setBounds(0, 0, 320, 300);
        readContext.setEditable(false);
        // 自动换行
        readContext.setLineWrap(true);
        writeScroll.setBounds(10, 315, 320, 100);
        writeContext.setBounds(0, 0, 320, 100);
        // 自动换行
        writeContext.setLineWrap(true);
        list.setBounds(340, 10, 140, 445);
        btnSend.setBounds(150, 420, 80, 30);
        btnClose.setBounds(250, 420, 80, 30);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                service.logout();
                System.exit(0);
            }
        });

        btnSend.addActionListener(e -> {
            String msg = writeContext.getText().trim();
            if(msg.length() > 0){
                service.send(Message.build(currUser, Body.buildMain(writeContext.getText())));
            }
            writeContext.setText(null);
            writeContext.requestFocus();
        });

        btnClose.addActionListener(e -> {
            service.logout();
            System.exit(0);
        });

        list.addListSelectionListener(e -> {
        });
        // 添加按键监听
        writeContext.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSend.doClick();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        this.frame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    public void renderMsg(Message message) {
        if (null == message || message.invalid() || null == message.getData()) {
            return;
        }
        if (Body.AREA_ONLINE_LIST.equals(message.getBody().getArea())) {
            java.util.List<User> onlineUser = (java.util.List<User>) message.getData();
            this.renderOnlineUser(onlineUser);
        } else if (Body.AREA_MAIN.equals(message.getBody().getArea())) {
            this.renderMessage(message);
        }
    }

    /**
     * 更新在线用户列表
     * @param onlineUser 当前所有在线人员名单
     */
    private void renderOnlineUser(java.util.List<User> onlineUser) {
        model.removeAllElements();
        onlineUser.forEach(o -> model.addElement(o.getName()));
    }

    /**
     * 更新聊天内容显示区域
     * @param msg 服务器信息
     */
    private void renderMessage(Message msg) {
        String str = readContext.getText();
        if (!str.isEmpty()) {
            str += "\n";
        }
        String time = msg.getHead().getTime();
        String name = msg.getUser().getName();
        String detail = name + "\t" + time + "\r\n" + msg.getData().toString();
        readContext.setText(str + detail);
        readContext.selectAll();
    }

    // 显示界面
    public void show() {
        this.init();
    }
}
