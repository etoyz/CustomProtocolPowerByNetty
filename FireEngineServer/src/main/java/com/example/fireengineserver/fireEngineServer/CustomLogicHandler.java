package com.example.fireengineserver.fireEngineServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CustomLogicHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String hexStr = (String) msg;

        ServerStatus.receivedStatusCode.add(hexStr);
    }
}
