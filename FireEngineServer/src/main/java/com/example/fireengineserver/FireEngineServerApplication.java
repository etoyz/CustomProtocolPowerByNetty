package com.example.fireengineserver;

import com.example.fireengineserver.fireEngineServer.FireEngineServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FireEngineServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FireEngineServerApplication.class, args);
        FireEngineServer.startServer();
    }
}
