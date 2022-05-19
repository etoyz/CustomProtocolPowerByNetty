package com.example.fireengineserver.fireEngineServer;

import com.example.fireengineserver.LogUtils;
import com.example.fireengineserver.webService.WebSocketChannelSupervise;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.fireengineserver.fireEngineServer.FireEngineEncoder.responseToClient;


public class FireEngineDecoder extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws IOException {
        StringBuilder logBuilder = new StringBuilder();
        // 处理UDP数据报
        logBuilder.append("收到UDP数据报：").append(msg.toString()).append("\n");
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        String hexStr;
        RequestMsg requestMsg;
        try {
            hexStr = new ObjectMapper().readerFor(String.class).readValue(bytes); //decode
            if (!verifyCRC(hexStr)) { // 若校验失败
                responseToClient("否定回复：CRC校验失败".getBytes(), msg.sender().getHostString());
                return;
            }

            // 将表示二进制的字符串转为原始二进制数字
            BigInteger dataCarriedByInt = new BigInteger(hexStr, 16);
            logBuilder.append("二进制表示：\t\t").append(dataCarriedByInt.toString(2)).append("B").append("\n");
            logBuilder.append("十六进制表示：\t\t").append(hexStr).append("H").append("\n");

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
            requestMsg = new RequestMsg(); // aggregation
            requestMsg.setIp(msg.sender().getHostString() + ":" + msg.sender().getPort());
            requestMsg.setRawMsg(hexStr + "H");
            requestMsg.setConvertedMsg(convertedMessage);
            requestMsg.setRequestDate(new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()));
            requestMsg.setConvertedData(convertedData);
            FireEngineServer.receivedData.add(requestMsg);
        } catch (MismatchedInputException e) {  //如果不是系统消息，则偷懒（￣︶￣）↗　
            Map<String, String> fakeData = new ObjectMapper().readerFor(Map.class).readValue(bytes); //decode
            String convertedMsg = null;
            String functionCode = fakeData.get("functionCode");
            if (functionCode.equals("70")) {
                String protectzone = fakeData.get("protectZone");
                String devicetype = fakeData.get("deviceType");
                String devicecode = fakeData.get("deviceCode");
                String faultcode = fakeData.get("faultCode");
                convertedMsg = "故障信息：防护区" + protectzone + "设备类型" + devicetype + "设备编号" + devicecode + "故障码" + faultcode;
            } else if (functionCode.equals("80")) {
                convertedMsg = "分区信息:";
            } else if (functionCode.equals("90")) {
                String alarmcount = fakeData.get("protectZone1");
                String equipmtype = fakeData.get("equipmType");
                String identity = fakeData.get("Identity");
                String alarmlevel = fakeData.get("alarmLevel");
                String temperature = fakeData.get("Temperature");
                String co = fakeData.get("CO");
                String voc = fakeData.get("VOC");
                String smog = fakeData.get("Smog");
                convertedMsg = "探测器信息:防护区" + alarmcount + "  " + "设备类型" + equipmtype + "  " + "ID" + identity + "  " + "报警等级" + alarmlevel + "  " + "温度" + temperature + "  " + "CO" + co + "  " + "VOC" + voc + "  " + "烟雾" + smog;
            } else if (functionCode.equals("A0")) {
                String alarmcount = fakeData.get("protectZone2");
                String number = fakeData.get("Number");
                String status = fakeData.get("Status");
                convertedMsg = "灭火器信息:防护区" + alarmcount + "  " + "编号" + number + "  " + "状态" + status;
            }
            hexStr = "00000000000"; // TODO
            requestMsg = new RequestMsg();
            requestMsg.setIp(msg.sender().getHostString() + ":" + msg.sender().getPort());
            requestMsg.setRawMsg(hexStr);
            requestMsg.setConvertedMsg(convertedMsg);
            requestMsg.setRequestDate(new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()));

            FireEngineServer.receivedData.add(requestMsg);
        }
        logBuilder.append(requestMsg.getConvertedMsg()).append("\n");
        // 写入日志
        LogUtils.info(logBuilder.toString());

        // 使用WebSocket 动态更新服务器的dashboard(图表)
        TextWebSocketFrame tws;
        List<RequestMsg> responseData = new ArrayList<>();
        for (int i = 0; i < FireEngineServer.receivedData.size(); i++) {
            if (FireEngineServer.receivedData.get(i).getConvertedData() != null)
                responseData.add(FireEngineServer.receivedData.get(i));
        }
        try {
            String responseSerializeStr = new ObjectMapper().writeValueAsString(
                    responseData
            );
            tws = new TextWebSocketFrame(responseSerializeStr);
            WebSocketChannelSupervise.send2All(tws);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 传到下一个Handler
        Map<String, String> decoderResolveResult = new HashMap<>();
        decoderResolveResult.put("CLIENT_IP", msg.sender().getHostString());
        decoderResolveResult.put("CLIENT_PORT", String.valueOf(msg.sender().getPort()));
        decoderResolveResult.put("DATA", hexStr);
        ctx.write(decoderResolveResult);
    }

    private boolean verifyCRC(String datacrc) {
        String hexstr = datacrc.substring(0, datacrc.length() - 2);
        String mult = "101";//多项式
        BigInteger datadec = new BigInteger(hexstr, 16);//CRC16转10
        BigInteger multdec = new BigInteger(mult, 16);//多项式16转10
        BigInteger remainder = datadec.remainder(multdec);
        int multint = remainder.intValue();
        String multhex = Integer.toHexString(multint);
        return multhex.equals("0");
    }
}
