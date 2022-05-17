package com.example.fireengineserver.fireEngineServer;

import com.example.fireengineserver.webService.WebSocketChannelSupervise;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FireEngineDecoder extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws IOException {
        // 处理UDP数据报
        System.out.println("\n\n收到UDP数据报：\t" + msg.toString());
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        String hexStr = new ObjectMapper().readerFor(String.class).readValue(bytes); //decode

        // 将表示二进制的字符串转为原始二进制数字
        BigInteger dataCarriedByInt = new BigInteger(hexStr, 16);
        System.out.println("收到二进制数据：\t\t" + dataCarriedByInt.toString(2) + "B");
        System.out.println("十六进制表示：\t\t" + hexStr + "H");

        // 处理接收到的消防主机数据
        String convertedMessage = "待处理"; // convertedMessage
        int intnum1 = 0;
        int intnum2 = 0;
        int intnum3 = 0;
        if (hexStr.substring(10, 12).equals("60")) {
            if (hexStr.substring(12, 14).equals("00")) {
                String strnum1 = hexStr.substring(18, 22);
                intnum1 = Integer.parseInt(strnum1, 16);
                String strnum2 = hexStr.substring(22, 26);
                intnum2 = Integer.parseInt(strnum2, 16);
                String strnum3 = hexStr.substring(26, 30);
                intnum3 = Integer.parseInt(strnum3, 16);
                convertedMessage = "系统信息：总报警个数" + intnum1 + "  " + "总故障个数" + intnum2 + "  " + "总探测器个数" + intnum3;
            } else {
                convertedMessage = "读取失败！";
            }
        } else {
            convertedMessage = "暂不支持";
        }
        Map<String, Integer> convertedData = new HashMap<>(); // convertedData
        convertedData.put("alarmCount", intnum1);
        convertedData.put("faultCount", intnum2);
        convertedData.put("detectorCount", intnum3);
        RequestMsg requestMsg = new RequestMsg(); // aggregation
        requestMsg.setIp(msg.sender().getHostString() + ":" + msg.sender().getPort());
        requestMsg.setRawMsg(hexStr + "H");
        requestMsg.setConvertedMsg(convertedMessage);
        requestMsg.setRequestDate(new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()));
        requestMsg.setConvertedData(convertedData);
        FireEngineServer.receivedStatus.add(requestMsg);
        // 使用WebSocket 更新服务器的dashboard
        TextWebSocketFrame tws;
        Map<Integer, RequestMsg> responseData = new HashMap<>();
        for (int i = 0; i < FireEngineServer.receivedStatus.size(); i++) {
            responseData.put(i, FireEngineServer.receivedStatus.get(i));
        }
        try {
            String responseSerializeStr = new ObjectMapper().writeValueAsString(
                    FireEngineServer.receivedStatus
            );
            tws = new TextWebSocketFrame(responseSerializeStr);
            WebSocketChannelSupervise.send2All(tws);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        Map<String, String> decoderResolveResult = new HashMap<>();
        decoderResolveResult.put("CLIENT_IP", msg.sender().getHostString());
        decoderResolveResult.put("CLIENT_PORT", String.valueOf(msg.sender().getPort()));
        decoderResolveResult.put("DATA", hexStr);
        ctx.write(decoderResolveResult);
    }
}
