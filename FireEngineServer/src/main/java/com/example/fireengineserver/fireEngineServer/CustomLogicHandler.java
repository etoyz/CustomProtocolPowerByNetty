package com.example.fireengineserver.fireEngineServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomLogicHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String hexStr = (String) msg;

        ServerStatus.receivedStatus.add(new String[]{
                "127.0.0.1",
                hexStr,
                new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date())
        });
    }
}
