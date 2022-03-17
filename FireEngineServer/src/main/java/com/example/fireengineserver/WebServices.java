package com.example.fireengineserver;

import com.example.fireengineserver.fireEngineServer.ServerStatus;
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
        Map<String, ArrayList<String[]>> map = new HashMap<>();
        ArrayList<String[]> dataList = new ArrayList<>();
        String[] tmp = new String[2];
        for (String status : ServerStatus.receivedStatusCode) {
            tmp[0] = "localhost"; // IP
            tmp[1] = status;

            dataList.add(tmp);
        }

        map.put("data", dataList);
        return map;
    }

}

