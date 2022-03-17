package com.example.nettydemowithweb;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class FireEngine {
    String getHexStatusCodeString() {
        if (!"on".equals(isReadSuccess)) { // 如果读取失败
            return functionCode + "010100";
        } else { // 如果读取成功
            if (functionCode.equals(FUN_SYSTEM_INFO)) { // 系统信息报文
                return "60000103" + alarmCount + faultCount + detectorCount;
            } else if (functionCode.equals(FUN_FAULT_INFO)) { // 故障信息报文
                return "700001";
            } else if (functionCode.equals(FUN_PARTITION_INFO)) { // 分区信息报文
                return "800001";
            } else if (functionCode.equals(FUN_DETECTOR_INFO)) { // 探测器信息报文
                return "900001";
            } else if (functionCode.equals(FUN_FIRE_INFO)) { // 灭火器信息报文
                return "A00001";
            }
        }
        return "";
    }

    String addHeadAndTail(String statusCode) {
        int length = 2 + 2 + 1 + statusCode.length() + 1 + 1;
        int id = (new Random()).nextInt((int) Math.pow(2, 8));
        String crc = "00"; // TODO
        return "514E" + Integer.toHexString(length) + Integer.toHexString(id) + statusCode + crc + "45";
    }

    public void sendToServer(BigInteger binaryCode) {
        try {
            // Step 1:Create the socket object for
            // carrying the data.
            DatagramSocket ds = new DatagramSocket();
            InetAddress ip = InetAddress.getByName("127.0.0.1");

            // convert the BigInteger into the byte array.
//            byte[] buf = binaryCode.toByteArray();
//            if (buf[0] == 0) {
//                byte[] tmp = new byte[buf.length - 1];
//                System.arraycopy(buf, 1, tmp, 0, tmp.length);
//                buf = tmp;
//            }
            byte[] buf = binaryCode.toString(2).getBytes(StandardCharsets.UTF_8);

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket DpSend =
                    new DatagramPacket(buf, buf.length, ip, 688);

            // Step 3 : invoke the send call to actually send
            // the data.
            ds.send(DpSend);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能码
     */
    private String functionCode;
    /**
     * 标识符
     */
    private String isReadSuccess;
    /**
     * 数据格式
     */
    private String dataFormat;
    /**
     * 个数
     */
    private String count;
    /**
     * 总报警个数
     */
    private String alarmCount;
    /**
     * 总故障个数
     */
    private String faultCount;
    /**
     * 总探测器个数
     */
    private String detectorCount;

    /**
     * 各类功能码
     */
    public static final String FUN_SYSTEM_INFO = "60";
    public static final String FUN_FAULT_INFO = "70";
    public static final String FUN_PARTITION_INFO = "80";
    public static final String FUN_DETECTOR_INFO = "90";
    public static final String FUN_FIRE_INFO = "A0";

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public void setIsReadSuccess(String isReadSuccess) {
        this.isReadSuccess = isReadSuccess;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setAlarmCount(String alarmCount) {
        this.alarmCount = alarmCount;
    }

    public void setFaultCount(String faultCount) {
        this.faultCount = faultCount;
    }

    public void setDetectorCount(String detectorCount) {
        this.detectorCount = detectorCount;
    }
}
