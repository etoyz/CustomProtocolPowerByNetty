package com.example.nettydemowithweb.netty;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class Index {

    @RequestMapping("uploadRawData")
    public String uploadRawData(@RequestParam Map<String, String> map) {
        FireEngineStatus status = new FireEngineStatus();
        status.functionCode = getFromMap(map, "functionCode");
        status.isReadSuccess = getFromMap(map, "isReadSuccess");
//        status.dataFormat = map.get("dataFormat");
        status.count = Integer.toHexString(Integer.parseInt(getFromMap(map, "count")));
        status.alarmCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "alarmCount")));
        status.faultCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "faultCount")));
        status.detectorCount = Integer.toHexString(Integer.parseInt(getFromMap(map, "detectorCount")));


        // Log
        System.out.println("\n\n模拟消费主机...");
        System.out.println("生成部分报文内容：\t" + status.getHexStatusCode());
        System.out.println("生成全部报文数据：\t" + status.addHeadAndTail(status.getHexStatusCode()));

        return "ok";
    }

    String getFromMap(Map<String, String> map, String key) {
        if (map.get(key) == null)
            return "0";
        else
            return map.get(key);
    }
}

