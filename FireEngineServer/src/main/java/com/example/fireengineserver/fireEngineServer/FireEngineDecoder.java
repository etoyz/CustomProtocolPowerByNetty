package com.example.fireengineserver.fireEngineServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FireEngineDecoder extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        // 处理UDP数据报
        System.out.println("\n\n收到UDP数据报：\t" + msg.toString());
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        String binaryCodeString = new String(bytes, StandardCharsets.UTF_8); // 表示二进制的字符串
        System.out.println("提取到数据部分：\t" + binaryCodeString);

        // 将表示二进制的字符串转为原始二进制数字
        BigInteger binaryCode = new BigInteger(binaryCodeString, 2);
        String hexStr = binaryCode.toString(16).toUpperCase(Locale.ROOT);
        System.out.println("十六进制表示：\t\t" + hexStr + "H");

        // 将接收到的消防主机数据存储下来(待处理)
        String convertedMessage = "待处理";
        // TODO
        FireEngineServer.receivedStatus.add(new String[]{
                msg.sender().getHostString() + ":" + msg.sender().getPort(), // 请求者的IP和端口
                hexStr + "H", // 接收到的数据的16进制表示
                convertedMessage,
                new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()) // 接收到数据的时间
        });


        Map<String, String> decoderResolveResult = new HashMap<>();
        decoderResolveResult.put("CLIENT_IP", msg.sender().getHostString());
        decoderResolveResult.put("CLIENT_PORT", String.valueOf(msg.sender().getPort()));
        decoderResolveResult.put("DATA", hexStr);
        ctx.write(decoderResolveResult);
    }
}
