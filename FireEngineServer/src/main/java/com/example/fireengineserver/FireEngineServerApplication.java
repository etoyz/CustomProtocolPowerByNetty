package com.example.fireengineserver;

import com.example.fireengineserver.fireEngineServer.FireEngineServer;
import com.example.fireengineserver.webService.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FireEngineServerApplication {

    public static void main(String[] args) {
        new Thread(WebSocketServer::startServer).start();
        new Thread(FireEngineServer::startServer).start();
        SpringApplication.run(FireEngineServerApplication.class, args);
    }
}
