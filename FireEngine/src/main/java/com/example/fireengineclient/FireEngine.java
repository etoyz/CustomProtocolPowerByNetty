package com.example.fireengineclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FireEngine {
    public static String currentStatus = "暂未初始化！";

    // 自定义构造方法，初始化消防主机
    public FireEngine(Map<String, String> map) {
        setFunctionCode(map.get("functionCode"));
        if (map.get("functionCode").equals(FUN_SYSTEM_INFO)) { // 如果是系统消息，走原来的逻辑
            setIsReadSuccess(map.get("isReadSuccess"));
            setCount(getHexStrFromMap(map, "count"));
            setAlarmCount(getHexStrFromMap(map, "alarmCount"));
            setFaultCount(getHexStrFromMap(map, "faultCount"));
            setDetectorCount(getHexStrFromMap(map, "detectorCount"));
            setIsValid(map.get("isValid"));
        } else // 如果不是系统消息，则偷懒（￣︶￣）↗　
            setOtherData(map);
        id = (new Random()).nextInt((int) Math.pow(2, 8));
    }

    // 向控制台输出信息
    public void printLog() {
        if (functionCode.equals(FUN_SYSTEM_INFO)) {
            String statusCodeString = getHexStatusCodeString().toUpperCase(Locale.ROOT);
            String statusCodeStringAll = addHeadAndTail(statusCodeString).toUpperCase(Locale.ROOT);
            BigInteger statusCodeBinaryAll = new BigInteger(statusCodeStringAll, 16);
            System.out.println("\n\n模拟消费主机...");
            System.out.println("生成部分报文内容：\t\t" + statusCodeString + "H");
            System.out.println("生成全部报文数据：\t\t" + statusCodeStringAll + "H");
            System.out.println("十进制表示：\t\t\t" + statusCodeBinaryAll.toString(10));
            System.out.println("上传二进制串到工控主机：\t" + statusCodeBinaryAll.toString(2));
        } else
            System.out.println("暂时只支持”系统消息”日志输出“");
    }

    // 上传数据到特定主机
    public void sendToServer(String ipStr) throws Exception {
        byte[] buf;
        if (functionCode.equals(FUN_SYSTEM_INFO)) // 如果是系统消息，走原来的逻辑
            buf = new ObjectMapper().writeValueAsBytes(addHeadAndTail(getHexStatusCodeString()));
        else { // 如果不是系统消息，则偷懒（￣︶￣）↗　
            byte[] fakeData = new ObjectMapper().writeValueAsBytes(otherData);
            buf = fakeData;
        }
        new DatagramSocket().send(new DatagramPacket(buf, buf.length, InetAddress.getByName(ipStr), 688));
    }


    /* Instantiate client socket.
      No need to bind to a specific port */
    private static DatagramSocket clientSocket;

    // 阻塞监听服务器响应
    public void listenToServer(String ipStr) throws Exception {
        int SERVICE_PORT = 688;
        clientSocket = new DatagramSocket(788);

        InetAddress IPAddress = InetAddress.getByName(ipStr);

        byte[] receivingDataBuffer = new byte[1024];

        // Get the server response
        DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        updateStatus("等待服务器响应...");
        clientSocket.receive(receivingPacket);

        // Printing the received data
        String receivedData = new String(receivingPacket.getData()).trim();
        updateStatus("收到服务器响应信息: 【" + receivedData + "】");

        clientSocket.close();
    }

    // 停止监听服务器响应，防止一直阻塞
    public static void stopListen() {
        clientSocket.close();
    }

    private String getHexStatusCodeString() {
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

    private String addHeadAndTail(String data) {
        int length = 2 + 2 + 1 + data.length() / 2 + 1 + 1; // 长度单位是字节
        String resultStr = "514E" + fixHexStr(Integer.toHexString(length), 4)
                + fixHexStr(Integer.toHexString(id), 2)
                + data;
        resultStr += getCRC(resultStr);
        resultStr += "45";
        if (!"on".equals(isValid)) { // 模拟数据受损(演示CRC校验功能)
            resultStr = resultStr.substring(1);
        }
        return resultStr;
    }

    private String getCRC(String hexStr) {
        String mult = "101";//多项式
        BigInteger datadec = new BigInteger(hexStr, 16);//CRC16转10
        BigInteger multdec = new BigInteger(mult, 16);//多项式16转10
        BigInteger remainder = datadec.remainder(multdec);
        int multint = remainder.intValue();
        String multhex = Integer.toHexString(multint);
        String datastr = hexStr + multhex + "45H";
        System.out.println("CRC校验位为:\t" + multhex);
        return multhex;
    }

    private String getHexStrFromMap(Map<String, String> map, String key) {
        if (map.get(key) == null || map.get(key).equals(""))
            return "0";
        else
            return Integer.toHexString(Integer.parseInt(map.get(key)));
    }

    private void updateStatus(String status) {
        System.out.println(status);
        FireEngine.currentStatus = status;
    }

    /**
     * ID
     */
    private int id;
    /**
     * 是否有效的数据
     */
    private String isValid;
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
     * 其它数据
     */
    private Map<String, String> otherData;

    /**
     * 各类功能码
     */
    public static final String FUN_SYSTEM_INFO = "60";
    public static final String FUN_FAULT_INFO = "70";
    public static final String FUN_PARTITION_INFO = "80";
    public static final String FUN_DETECTOR_INFO = "90";
    public static final String FUN_FIRE_INFO = "A0";

    public void setFunctionCode(String functionCode) {
        this.functionCode = fixHexStr(functionCode, 2);
    }

    public void setIsReadSuccess(String isReadSuccess) {
        this.isReadSuccess = isReadSuccess;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = fixHexStr(dataFormat, 2);
    }

    public void setCount(String count) {
        this.count = fixHexStr(count, 2);
    }

    public void setAlarmCount(String alarmCount) {
        this.alarmCount = fixHexStr(alarmCount, 4);
    }

    public void setFaultCount(String faultCount) {
        this.faultCount = fixHexStr(faultCount, 4);
    }

    public void setDetectorCount(String detectorCount) {
        this.detectorCount = fixHexStr(detectorCount, 4);
    }

    private String fixHexStr(String hexStr, int targetLength) {
        if (hexStr == null) {
            return "0".repeat(Math.max(0, targetLength));
        } else if (hexStr.length() < targetLength) {
            return "0".repeat(Math.max(0, targetLength - hexStr.length())) + hexStr;
        }
        return hexStr.substring(0, targetLength);
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public Map<String, String> getOtherData() {
        return otherData;
    }

    public void setOtherData(Map<String, String> otherData) {
        this.otherData = otherData;
    }
}
