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
            // 监听响应
            fireEngine.listenToServer(map.get("ip"));

            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            FireEngine.currentStatus = "出现错误！";
            return e.getMessage();
        }
    }

    @RequestMapping("getFireEngineStatus")
    public String getRemoteResponse() {
        return FireEngine.currentStatus;
    }
}

