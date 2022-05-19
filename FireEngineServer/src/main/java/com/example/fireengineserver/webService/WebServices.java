package com.example.fireengineserver.webService;

import com.example.fireengineserver.fireEngineServer.FireEngineServer;
import com.example.fireengineserver.fireEngineServer.RequestMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/")
public class WebServices {

    @RequestMapping("getServerStatus")
    public Map<String, ArrayList<RequestMsg>> uploadRawData() {
        Map<String, ArrayList<RequestMsg>> map = new HashMap<>();

//        ArrayList<String[]> dataList = new ArrayList<>(FireEngineServer.receivedStatus);

        map.put("data", FireEngineServer.receivedData);
        return map;
    }

    @RequestMapping("clearData")
    public String clearData() {
        FireEngineServer.receivedData.clear();
        return "ok";
    }

    @RequestMapping("viewLog")
    public String viewLog() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(Paths.get("server.log"));
        stringBuilder.append("<h1>服务器全部运行日志</h1>");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.endsWith("info"))
                stringBuilder
                        .append("<hr style='height:2px;background-color:gray'><strong>")
                        .append(line)
                        .append("</strong>");
            else if (line.endsWith("warning")) {
                stringBuilder
                        .append("<hr style='height:2px;background-color:gray'><strong style='color:red'>")
                        .append(line)
                        .append("</strong>");
            } else
                stringBuilder.append("<div>").append(line).append("</div>");
        }
        scanner.close();
        return stringBuilder.toString();
    }
}

