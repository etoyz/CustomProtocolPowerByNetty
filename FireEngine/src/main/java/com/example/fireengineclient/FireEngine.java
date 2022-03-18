package com.example.fireengineclient;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FireEngine {
    public static String currentStatus = "暂未初始化！";

    // 自定义构造方法，初始化消防主机
    public FireEngine(Map<String, String> map) {
        setFunctionCode(map.get("functionCode"));
        setIsReadSuccess(map.get("isReadSuccess"));
//        fireEngine.dataFormat = map.get("dataFormat");
        setCount(getHexStrFromMap(map, "count"));
        setAlarmCount(getHexStrFromMap(map, "alarmCount"));
        setFaultCount(getHexStrFromMap(map, "faultCount"));
        setDetectorCount(getHexStrFromMap(map, "detectorCount"));
    }

    // 向控制台输出信息
    public void printLog() {
        String statusCodeString = getHexStatusCodeString().toUpperCase(Locale.ROOT);
        String statusCodeStringAll = addHeadAndTail(statusCodeString).toUpperCase(Locale.ROOT);
        BigInteger statusCodeBinaryAll = new BigInteger(statusCodeStringAll, 16);
        System.out.println("\n\n模拟消费主机...");
        System.out.println("生成部分报文内容：\t\t" + statusCodeString + "H");
        System.out.println("生成全部报文数据：\t\t" + statusCodeStringAll + "H");
        System.out.println("十进制表示：\t\t\t" + statusCodeBinaryAll.toString(10));
        System.out.println("上传二进制串到工控主机：\t" + statusCodeBinaryAll.toString(2));
    }

    // 上传数据到特定主机
    public void sendToServer(String ipStr) throws Exception {
        BigInteger binaryCode = new BigInteger(addHeadAndTail(getHexStatusCodeString()), 16);

        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getByName(ipStr);

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
    }

    // 阻塞监听服务器响应
    public void listenToServer(String ipStr) throws Exception {
        int SERVICE_PORT = 688;

      /* Instantiate client socket.
      No need to bind to a specific port */
        DatagramSocket clientSocket = new DatagramSocket(788);

        InetAddress IPAddress = InetAddress.getByName(ipStr);

        byte[] receivingDataBuffer = new byte[1024];

        // Get the server response
        DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        updateStatus("等待服务器响应...");
        clientSocket.receive(receivingPacket);

        // Printing the received data
        String receivedData = new String(receivingPacket.getData()).trim();
        updateStatus("收到服务器响应信息: " + receivedData);

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

    private String addHeadAndTail(String statusCode) {
        int length = 2 + 2 + 1 + statusCode.length() / 2 + 1 + 1; // 长度单位是字节
        int id = (new Random()).nextInt((int) Math.pow(2, 8));
        String crc = "00"; // TODO
        return "514E" + fixHexStr(Integer.toHexString(length), 4)
                + fixHexStr(Integer.toHexString(id), 2)
                + statusCode + crc + "45";
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
        this.functionCode = fixHexStr(functionCode, 2);
    }

    public void setIsReadSuccess(String isReadSuccess) {
        this.isReadSuccess = fixHexStr(isReadSuccess, 2);
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
}
