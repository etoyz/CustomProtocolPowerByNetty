package com.example.fireengineserver.fireEngineServer;

import java.util.Map;

/**
 * 客户端主动上传的信息类
 */
public class RequestMsg {
    private String ip;
    private String rawMsg;
    private String convertedMsg;
    private String requestDate;

    private Map convertedData;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRawMsg() {
        return rawMsg;
    }

    public void setRawMsg(String rawMsg) {
        this.rawMsg = rawMsg;
    }

    public String getConvertedMsg() {
        return convertedMsg;
    }

    public void setConvertedMsg(String convertedMsg) {
        this.convertedMsg = convertedMsg;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public Map getConvertedData() {
        return convertedData;
    }

    public void setConvertedData(Map convertedData) {
        this.convertedData = convertedData;
    }
}
