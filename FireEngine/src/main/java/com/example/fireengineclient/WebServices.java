package com.example.fireengineclient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class WebServices {

    @RequestMapping("uploadRawData")
    public String uploadRawData(@RequestParam Map<String, String> map) {
        try {
            // 初始化消防主机
            FireEngine fireEngine = new FireEngine(map);
            // 输出日志信息
            fireEngine.printLog();
            // 上传数据到远程主机
            fireEngine.sendToServer(map.get("ip"));

            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("getRemoteResponse")
    public String getRemoteResponse() {
        return FireEngine.remoteResponse;
    }
}

