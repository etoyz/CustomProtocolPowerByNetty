package com.example.fireengineserver.webService;

import com.example.fireengineserver.fireEngineServer.FireEngineServer;
import com.example.fireengineserver.fireEngineServer.RequestMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class WebServices {

    @RequestMapping("getServerStatus")
    public Map<String, ArrayList<RequestMsg>> uploadRawData() {
        Map<String, ArrayList<RequestMsg>> map = new HashMap<>();

//        ArrayList<String[]> dataList = new ArrayList<>(FireEngineServer.receivedStatus);

        map.put("data", FireEngineServer.receivedStatus);
        return map;
    }

}

