package com.example.fireengineserver.fireEngineServer;

import com.example.fireengineserver.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class FireEngineEncoder extends MessageToByteEncoder<Map<String, String>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Map<String, String> map, ByteBuf out) {
        String hexStr = map.get("DATA"); // 请求的数据
        String ip = map.get("CLIENT_IP");
        String responseString; //上位机回复给消防主机的数据
        responseString = "肯定回复：CRC校验成功";
        responseToClient(responseString.getBytes(), ip);
        LogUtils.info(responseString);
    }

    /**
     * 响应给客户端
     *
     * @param data  响应的数据部分
     * @param dstIP 目的IP（消防主机IP）
     */
    static void responseToClient(byte[] data, String dstIP) {
        try {
            Thread.sleep(500);
            new DatagramSocket().send(new java.net.DatagramPacket(data, data.length, InetAddress.getByName(dstIP), 788));
        } catch (Exception e) {
            LogUtils.warning(e.getMessage());
        }
    }
}
