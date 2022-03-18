package com.example.fireengineserver.fireEngineServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FireEngineDecoder extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        // 处理UDP数据报
        System.out.println("\n\n收到UDP数据报：\t" + msg.toString());
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        String binaryCodeString = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("提取到数据部分：\t" + binaryCodeString);

        // 将表示二进制的字符串转为原始二进制数字
        BigInteger integer = new BigInteger(binaryCodeString, 2);
        String hexStr = integer.toString(16).toUpperCase(Locale.ROOT);
        System.out.println("十六进制表示：\t\t" + hexStr + "H");

        // 将接收到的消防主机数据存储下来(待处理)
        FireEngineServer.receivedStatus.add(new String[]{
                msg.sender().getHostString() + ":" + msg.sender().getPort(),
                hexStr + "H",
                new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date())
        });
    }
}
