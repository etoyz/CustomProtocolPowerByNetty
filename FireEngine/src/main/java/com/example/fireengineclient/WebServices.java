package com.example.fireengineclient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/")
public class WebServices {

    @RequestMapping("uploadRawData")
    public String uploadRawData(@RequestParam Map<String, String> map) {
        try {
            // 初始化消防主机
            FireEngine fireEngine = new FireEngine();
            fireEngine.setFunctionCode(map.get("functionCode"));
            fireEngine.setIsReadSuccess(map.get("isReadSuccess"));
//        fireEngine.dataFormat = map.get("dataFormat");
            fireEngine.setCount(getHexStrFromMap(map, "count"));
            fireEngine.setAlarmCount(getHexStrFromMap(map, "alarmCount"));
            fireEngine.setFaultCount(getHexStrFromMap(map, "faultCount"));
            fireEngine.setDetectorCount(getHexStrFromMap(map, "detectorCount"));


            // 输出日志信息
            String statusCodeString = fireEngine.getHexStatusCodeString();
            String statusCodeStringAll = fireEngine.addHeadAndTail(statusCodeString);
            BigInteger statusCodeBinaryAll = new BigInteger(statusCodeStringAll, 16);
            System.out.println("\n\n模拟消费主机...");
            System.out.println("生成部分报文内容：\t\t" + statusCodeString + "H");
            System.out.println("生成全部报文数据：\t\t" + statusCodeStringAll + "H");
            System.out.println("十进制表示：\t\t\t" + statusCodeBinaryAll.toString(10));
            System.out.println("上传二进制串到工控主机：\t" + statusCodeBinaryAll.toString(2));

            // 发送二进制串到远程主机 TODO
            fireEngine.sendToServer(statusCodeBinaryAll, map.get("ip"));

            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getHexStrFromMap(Map<String, String> map, String key) {
        if (map.get(key) == null || map.get(key).equals(""))
            return "0";
        else
            return Integer.toHexString(Integer.parseInt(map.get(key)));
    }
}

