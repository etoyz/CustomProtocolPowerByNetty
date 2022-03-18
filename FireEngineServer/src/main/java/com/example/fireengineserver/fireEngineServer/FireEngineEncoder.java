package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;

public class FireEngineEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        String responseString;
        // CRC校验
        String crc = msg.substring(msg.length() - 4, msg.length() - 2);
        if (!verifyCRC(msg, crc)) { // 若校验失败
            String ID = msg.substring(8, 10);
            int len = 10;
            String resCrc = "00";
            responseString = "514E" + fixHexStr(Integer.toHexString(len), 4) + ID + "7F00E0" + resCrc + "45";
            responseString = responseString.toUpperCase(Locale.ROOT);
//            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseString.getBytes(StandardCharsets.UTF_8)), InetSocketAddress.createUnresolved("127.0.0.1", 788)));
            // 传统方式发送数据报
            Thread.sleep(1000);
            new DatagramSocket().send(new java.net.DatagramPacket(responseString.getBytes(), responseString.getBytes().length, InetAddress.getByName("127.0.0.1"), 788));
            return;
        }
        //提取数据有效部分 ID--数据内容
        msg = msg.substring(8); // 去头
        msg = msg.substring(0, msg.length() - 4); // 去尾

        //

        System.out.println(msg);
    }

    // TODO
    private boolean verifyCRC(String msg, String crc) {
        return false;
//        return new Random().nextBoolean();
    }

    private String fixHexStr(String hexStr, int targetLength) {
        if (hexStr == null) {
            return "0".repeat(Math.max(0, targetLength));
        } else if (hexStr.length() < targetLength) {
            return "0".repeat(Math.max(0, targetLength - hexStr.length())) + hexStr;
        }
        return hexStr.substring(0, targetLength);
    }
}
