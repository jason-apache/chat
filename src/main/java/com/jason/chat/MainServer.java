package com.jason.chat;


import com.jason.chat.msg.Body;
import com.jason.chat.msg.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * @author: mahao
 * @date: 2021/5/11 17:31
 */
public class MainServer implements Runnable {

    private Selector selector;
    private SelectionKey serverKey;
    private final UserManager userManager = UserManager.getInstance();

    public MainServer() {
        this.init();
    }

    private void init() {
        try {
            selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
            server.socket().bind(new InetSocketAddress(Integer.parseInt(resourceBundle.getString("port"))));
            server.configureBlocking(false);
            serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            Printer.print("服务器已启动...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 处理完事件及时移除该事件注册，防止重复操作
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel socket = channel.accept();
                        socket.configureBlocking(false);
                        socket.register(selector, SelectionKey.OP_READ);
                    } else if (key.isValid() && key.isReadable()) {
                        this.read(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取客户端发送的数据，转发至其他所有在线的客户端
     * @param key 客户端读事件监听
     */
    private void read(SelectionKey key) {
        try {
            Message msg = IOUtil.read((SocketChannel) key.channel());
            if (msg.invalid()) {
                // 信息无效则下线处理
                this.offline((SocketChannel) key.channel(), msg);
                return;
            }
            if (msg.isJoin()) {
                // 新连接进入聊天室
                State loginState = userManager.online(msg);
                if (loginState == State.NORMAL) {
                    // 放入当前登陆人信息
                    key.attach(msg);
                    Printer.print(msg.getUser().getName() + " 加入了聊天");
                    this.publish(Message.build(msg.getUser(), Body.buildOnlineList(userManager.getAllUser())));
                } else {
                    // 登录失败
                    IOUtil.write((SocketChannel) key.channel(), Message.buildError(loginState.getMsg()));
                }
            } else if (msg.isLeave()) {
                // 退出了聊天室
                this.offline((SocketChannel) key.channel(), msg);
                this.publish(Message.build(msg.getUser(), Body.buildOnlineList(userManager.getAllUser())));
            } else if (userManager.isOnline(key)) {
                // 正常发送的群聊消息
                Printer.print(msg.getUser().getName() + " 说：" + msg.getData());
                this.publish(Message.build(msg.getUser(), Body.buildMain(msg.getData())));
            }
        } catch (IOException e) {
            // 可能是窗口被关闭，进行下线处理
            key.cancel();
            this.offline((SocketChannel) key.channel(), key.attachment());
            e.printStackTrace();
        }
    }

    /**
     * 发布至所有在线客户端信息
     * @param msg 信息
     */
    private void publish(Message msg) {
        for (SelectionKey key : selector.keys()) {
            if (key == serverKey || !userManager.isOnline(key)) {
                continue;
            }
            int state = IOUtil.write((SocketChannel) key.channel(), msg);
            if (state == State.INVALID.getState()) {
                this.offline((SocketChannel) key.channel(), msg);
            }
        }
    }

    /**
     * 断开与客户端连接
     * @param channel 数据通道
     * @param attachment 持有的登陆人信息
     */
    private void offline(SocketChannel channel, Object attachment) {
        try {
            if (attachment instanceof Message) {
                // 从在线者名单中移除
                if (null != ((Message) attachment).getUser()) {
                    userManager.offline(((Message) attachment).getUser());
                    Printer.print(((Message) attachment).getUser().getName() + "  离开了");
                }
            }
            try {
                channel.close();
            } catch (IOException e) {
                Printer.print("关闭通道失败：" + attachment);
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new MainServer()).start();
    }
}
