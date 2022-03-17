package com.example.fireengineserver;

import com.example.fireengineserver.fireEngineServer.FireEngineServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class WebServices {

    @RequestMapping("getServerStatus")
    public Map<String, ArrayList<String[]>> uploadRawData() {
        ArrayList<String[]> dataList = new ArrayList<>(FireEngineServer.receivedStatus);

        Map<String, ArrayList<String[]>> map = new HashMap<>();

        map.put("data", dataList);
        return map;
    }

}

