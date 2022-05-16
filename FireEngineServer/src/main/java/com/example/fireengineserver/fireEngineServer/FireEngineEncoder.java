package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class FireEngineEncoder extends MessageToByteEncoder<Map<String, String>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Map<String, String> map, ByteBuf out) throws Exception {
        String hexStr = map.get("DATA"); // 请求的数据
        String ip = map.get("CLIENT_IP");
        String responseString; //上位机回复给消防主机的数据
        // CRC校验
        String crc = hexStr.substring(hexStr.length() - 4, hexStr.length() - 2);
        if (!verifyCRC(hexStr, crc)) { // 若校验失败
//            String ID = hexStr.substring(8, 10);
//            int len = 10;
//            String resCrc = "00";
//            responseString = "514E" + fixHexStr(Integer.toHexString(len), 4) + ID + "7F00E0" + resCrc + "45";
//            responseString = responseString.toUpperCase(Locale.ROOT);
            responseString = "CRC校验失败";
//            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(responseString.getBytes(StandardCharsets.UTF_8)), InetSocketAddress.createUnresolved("127.0.0.1", 788)));
            // 响应客户端
            responseToClient(responseString.getBytes(), ip);
            return;
        } else {
            responseString = "服务不支持"; // TODO
            responseToClient(responseString.getBytes(), ip);
        }

        //提取数据有效部分 ID--数据内容
        hexStr = hexStr.substring(8); // 去头
        hexStr = hexStr.substring(0, hexStr.length() - 4); // 去尾

        //
        System.out.println(hexStr);
    }

    // TODO
    private boolean verifyCRC(String msg, String crc) {
        return crc.equals("00");
    }

    private String fixHexStr(String hexStr, int targetLength) {
        if (hexStr == null) {
            return "0".repeat(Math.max(0, targetLength));
        } else if (hexStr.length() < targetLength) {
            return "0".repeat(Math.max(0, targetLength - hexStr.length())) + hexStr;
        }
        return hexStr.substring(0, targetLength);
    }

    /**
     * 响应给客户端
     *
     * @param data  响应的数据部分
     * @param dstIP 目的IP（消防主机IP）
     */
    private void responseToClient(byte[] data, String dstIP) throws Exception {
        Thread.sleep(500);
        new DatagramSocket().send(new java.net.DatagramPacket(data, data.length, InetAddress.getByName(dstIP), 788));
    }
}
