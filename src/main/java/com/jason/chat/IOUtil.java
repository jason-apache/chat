package com.jason.chat;

import com.jason.chat.msg.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author: mahao
 * @date: 2021/5/12 09:52
 */
public final class IOUtil {

    private static final int SIZE = 4;

    private IOUtil() {
    }

    /**
     * 将一个int转换为4位的byte
     * @param num 一个整型
     * @return 整形对应的4位byte
     */
    private static byte[] int2bytes(int num){
        byte[] result = new byte[4];
        result[0] = (byte)((num >>> 24) & 0xff);
        result[1] = (byte)((num >>> 16)& 0xff );
        result[2] = (byte)((num >>> 8) & 0xff );
        result[3] = (byte)(num & 0xff );
        return result;
    }

    /**
     * 将byte数组4个字节转换为int
     * @param bytes 数据流
     * @return 数据体大小
     */
    private static int bytes2int(byte[] bytes){
        if (bytes.length == 0) {
            return 0;
        }
        int a = (bytes[0] & 0xff) << 24;
        int b = (bytes[1] & 0xff) << 16;
        int c = (bytes[2] & 0xff) << 8;
        int d = (bytes[3] & 0xff);
        return a | b | c | d;
    }

    /**
     * 从通道中读取信息
     * @param channel 数据通道
     * @return 信息
     * @throws IOException 读取数据时通道被关闭、或无效的通道都可能引发此异常
     */
    public static Message read(SocketChannel channel) throws IOException {
        // 计算数据体大小，构建buffer
        int size = getSize(channel);
        if (size == State.INVALID.getState()) {
            return Message.build(null, null);
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        int len = channel.read(buffer);
        if (len != size) {
            // 未读完，此处重复读
            while ((len = channel.read(buffer)) > 0) {
            }
        }
        buffer.flip();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
            // 反序列化为java对象
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object msg = ois.readObject();
            if (msg instanceof Message) {
                return (Message) msg;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Message.build(null, null);
    }

    /**
     * 从通道中读取4个字节，转换为int，代表数据体大小
     * @param channel 数据通道
     * @return 数据体大小
     */
    private static int getSize(SocketChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(SIZE);
            channel.read(buffer);
            return bytes2int(buffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return State.INVALID.getState();
    }

    /**
     * 发送数据到数据通道
     * 将数据体大小添加到数据流的头部，方便接收方解析构建数组
     * @param channel 数据通道
     * @param msg 信息
     * @return 状态
     */
    public static int write(SocketChannel channel, Message msg) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(msg);
            oos.flush();
            oos.close();
            byte[] data = bos.toByteArray();
            // 计算数据体的具体大小
            byte[] length = int2bytes(data.length);
            // 将数据体大小加入到头部，组合发送
            channel.write(ByteBuffer.wrap(concat(length, data)));
            return State.NORMAL.getState();
        } catch (IOException e) {
            e.printStackTrace();
            return State.INVALID.getState();
        }
    }

    private static byte[] concat(byte[] arr1, byte[] arr2) {
        return concat(arr1, arr2, arr2.length);
    }

    private static byte[] concat(byte[] arr1, byte[] arr2, int len) {
        byte[] concat = new byte[arr1.length + len];
        System.arraycopy(arr1, 0, concat, 0, arr1.length);
        System.arraycopy(arr2, 0, concat, arr1.length, len);
        return concat;
    }
}
