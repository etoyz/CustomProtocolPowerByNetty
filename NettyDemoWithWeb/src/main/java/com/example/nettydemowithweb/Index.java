package com.example.nettydemowithweb;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/")
public class Index {

    @RequestMapping("uploadRawData")
    public String uploadRawData(@RequestParam Map<String, String> map) {
        FireEngine fireEngine = new FireEngine();
        fireEngine.functionCode = getFromMap(map, "functionCode");
        fireEngine.isReadSuccess = getFromMap(map, "isReadSuccess");
//        fireEngine.dataFormat = map.get("dataFormat");
        fireEngine.count = Integer.toHexString(Integer.parseInt(getFromMap(map, "count")));
        fireEngine.alarmCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "alarmCount")));
        fireEngine.faultCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "faultCount")));
        fireEngine.detectorCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "detectorCount")));


        // Log
        String statusCodeString = fireEngine.getHexStatusCodeString();
        String statusCodeStringAll = fireEngine.addHeadAndTail(statusCodeString);
        BigInteger statusCodeBinaryAll = fireEngine.toBinary(statusCodeStringAll);
        System.out.println("\n\n模拟消费主机...");
        System.out.println("生成部分报文内容：\t\t" + statusCodeString + "H");
        System.out.println("生成全部报文数据：\t\t" + statusCodeStringAll + "H");
        System.out.println("十进制表示：\t\t\t" + statusCodeBinaryAll.toString(10));
        System.out.println("上传二进制串到工控主机：\t" + statusCodeBinaryAll.toString(2));

        return "ok";
    }

    String getFromMap(Map<String, String> map, String key) {
        if (map.get(key) == null)
            return "0";
        else
            return map.get(key);
    }
}

