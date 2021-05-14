package com.jason.chat;

import com.jason.chat.dto.User;
import com.jason.chat.frame.ChatFrame;
import com.jason.chat.frame.LoginFrame;
import com.jason.chat.msg.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author: mahao
 * @date: 2021/5/11 17:32
 */
public class Client implements Runnable {

    Selector selector;
    SocketChannel channel;
    ChatFrame chatFrame;
    LoginFrame loginFrame;
    private static boolean initialized;
    private boolean start;
    private static final Object LOCK = new Object();
    private static boolean login;
    private User currUser;

    public Client() {
        loginFrame = new LoginFrame(this);
    }

    /**
     * 初始化连接通道
     * @param host 地址
     * @param port 端口
     * @throws IOException 服务器未启动监听则引发此异常
     */
    public void initSocket(String host, int port) throws IOException {
        selector = Selector.open();
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        initialized = true;
        this.start();
    }

    public void login(User user, String password) {
        if (!initialized) {
            return;
        }
        this.send(Message.join(user, password));
    }

    private void login(Message msg) {
        if (msg.match(State.NORMAL)) {
            login = true;
            currUser = msg.getUser();
            // 隐藏掉登录窗口，并启动聊天面板
            loginFrame.hiding();
            this.initChatFrame();
        } else {
            loginFrame.errorText(msg.getData().toString());
        }
        // 得到登录响应，并唤醒
        synchronized (LOCK) {
            LOCK.notify();
        }
    }

    public void logout() {
        this.send(Message.leave(currUser));
        this.shutdown();
    }

    public void initChatFrame() {
        if (!initialized) {
            return;
        }
        if (login) {
            chatFrame = new ChatFrame(this, currUser);
            chatFrame.show();
        }
    }

    public void send(Message msg) {
        try {
            IOUtil.write(channel, msg);
            channel.register(selector, SelectionKey.OP_READ);
            if (!login) {
                // 同步等待服务器响应
                synchronized (LOCK) {
                    LOCK.wait();
                }
            }
        } catch (IOException | InterruptedException e) {
            Printer.print("发送信息失败：" + msg);
            e.printStackTrace();
        }
    }

    /**
     * 启动轮询监听
     */
    private void start() {
        this.start = true;
        new Thread(this).start();
    }

    private void shutdown() {
        this.start = false;
        try {
            this.channel.close();
            this.selector.close();
        } catch (Exception ignore){}
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                if (!start) {
                    break;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid() && key.isReadable()) {
                        Message msg = IOUtil.read((SocketChannel) key.channel());
                        // 未登录则处理登录逻辑
                        if (!login) {
                            this.login(msg);
                        }
                        chatFrame.renderMsg(msg);
                        Printer.print("服务器返回：" + msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
