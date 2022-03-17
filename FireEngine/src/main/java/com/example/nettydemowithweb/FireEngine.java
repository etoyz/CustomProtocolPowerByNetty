package com.example.nettydemowithweb;

import java.math.BigInteger;
import java.util.Random;

public class FireEngine {
    String getHexStatusCodeString() {
        if (!isReadSuccess.equals("on")) { // 如果读取失败
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

    BigInteger toBinary(String hexStr) {
        return new BigInteger(hexStr, 16);
    }

    /**
     * 功能码
     */
    public String functionCode;
    /**
     * 标识符
     */
    public String isReadSuccess;
    /**
     * 数据格式
     */
    public String dataFormat;
    /**
     * 个数
     */
    public String count;
    /**
     * 总报警个数
     */
    public String alarmCount;
    /**
     * 总故障个数
     */
    public String faultCount;
    /**
     * 总探测器个数
     */
    public String detectorCount;

    /**
     * 各类功能码
     */
    public static final String FUN_SYSTEM_INFO = "60";
    public static final String FUN_FAULT_INFO = "70";
    public static final String FUN_PARTITION_INFO = "80";
    public static final String FUN_DETECTOR_INFO = "90";
    public static final String FUN_FIRE_INFO = "A0";
}