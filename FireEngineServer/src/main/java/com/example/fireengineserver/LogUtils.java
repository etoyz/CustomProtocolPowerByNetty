package com.example.fireengineserver;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtils {
    private static final Logger serverLog = Logger.getLogger("serverLog");
    static FileHandler fh;

    static {
        try {
            fh = new FileHandler("server.log", true);
            serverLog.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warning(String msg) {
        serverLog.warning(msg);
        fh.flush();
    }

    public static void info(String msg) {
        serverLog.info(msg);
        fh.flush();
    }
}
