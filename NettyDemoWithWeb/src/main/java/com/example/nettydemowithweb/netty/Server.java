package com.example.nettydemowithweb.netty;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class Server {

    @ResponseBody
    @RequestMapping("/")
    public String test() {
        return "Ready!";
    }
}
