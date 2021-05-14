package com.jason.chat.frame;

import com.jason.chat.Client;
import com.jason.chat.dto.User;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * @author: mahao
 * @date: 2021/5/11 16:19
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static JTextField textHost;
    private static JTextField textPort;
    private static JTextField textUsername;
    private static JPasswordField textPassword;
    private static JButton btnOK;
    private static JLabel label;
    /**
     * 客户端对象，通过此对象与服务器交互
     */
    private final Client service;

    private static final int MAIN_WIDTH = 500;
    private static final int MAIN_HEIGHT = 300;

    public LoginFrame(Client service) {
        this.service = service;
        // 5行1列 水平间距20 垂直间距10
        JPanel panel = new JPanel(new GridLayout(5, 1,20,10));
        this.add(panel);
        this.setLayout(new GridBagLayout());
        this.setTitle("登录");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(true);
        textHost = new JTextField(4);
        textHost.setText("localhost");
        this.render(panel, textHost, "服务器地址");
        textPort = new JTextField(4);
        textPort.setText("1234");
        // 限制只能输入数字
        textPort.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
                } else {
                    //屏蔽掉非法输入
                    e.consume();
                }
            }
        });
        this.render(panel, textPort, "端口");
        textUsername = new JTextField(4);
        this.render(panel, textUsername, "用户名");
        textPassword = new JPasswordField(4);
        this.render(panel, textPassword, "密码");
        label = new JLabel("服务器地址和端口一旦连接则无法更改");
        panel.add(label);
        btnOK = new JButton("OK");
        panel.add(btnOK);
        btnOK.setBounds(120, 10, 80, 25);
        this.renderLoginEvent();
        this.setPreferredSize(new Dimension(MAIN_WIDTH, MAIN_HEIGHT));
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    private void render(JPanel panel, JTextComponent text, String label) {
        text.setMinimumSize(new Dimension(300, 25));
        text.setMaximumSize(new Dimension(300, 25));
        text.setPreferredSize(new Dimension(300, 25));
        text.setBounds(10, 10, 300, 25);
        panel.add(new JLabel(label));
        panel.add(text);
        this.renderEnterKeyEvent(text);
    }

    private void renderLoginEvent() {
        btnOK.addActionListener(e -> {
            String host = textHost.getText();
            String portStr = textPort.getText();
            String username = textUsername.getText();
            String password = new String(textPassword.getPassword());
            if (isEmpty(host) || isEmpty(portStr) || isEmpty(username) || isEmpty(password)) {
                this.error("信息为空！");
                return;
            }
            int port = Integer.parseInt(portStr);
            if (port > 65535 || port < 1) {
                this.error("非法端口号！");
            }
            try {
                service.initSocket(host, Integer.parseInt(portStr));
            } catch (IOException ex) {
                ex.printStackTrace();
                this.error("连接服务器失败！");
                return;
            }
            service.login(new User().setName(username), password);
        });
    }

    private void renderEnterKeyEvent(JTextComponent text) {
        text.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnOK.doClick();
                }
            }
        });
    }

    private boolean isEmpty(String str) {
        return null == str || str.trim().length() == 0;
    }

    public void hiding() {
        setVisible(false);
    }

    private void error(String msg) {
        JOptionPane.showConfirmDialog(this, msg, "警告", JOptionPane.DEFAULT_OPTION);
    }

    public void errorText(String msg) {
        label.setText(msg);
    }

}