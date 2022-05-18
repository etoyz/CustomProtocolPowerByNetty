package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;
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
        if (!verifyCRC(hexStr)) { // 若校验失败
            responseString = "CRC校验失败";
            responseToClient(responseString.getBytes(), ip);
            return;
        } else {
            responseString = "CRC校验成功"; // TODO
            responseToClient(responseString.getBytes(), ip);
        }

        //提取数据有效部分 ID--数据内容
        hexStr = hexStr.substring(8); // 去头
        hexStr = hexStr.substring(0, hexStr.length() - 4); // 去尾

        System.out.println(hexStr);
    }

    //判断CRC是否校验成功
    private boolean verifyCRC(String datacrc){
        String hexstr = datacrc.substring(0,datacrc.length()-2);
        String mult = "101";//多项式
        BigInteger datadec = new BigInteger(hexstr,16);//CRC16转10
        BigInteger multdec = new BigInteger(mult,16);//多项式16转10
        BigInteger remainder = datadec.remainder(multdec);
        int multint = remainder.intValue();
        String multhex = Integer.toHexString(multint);
        return multhex.equals("0");
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
