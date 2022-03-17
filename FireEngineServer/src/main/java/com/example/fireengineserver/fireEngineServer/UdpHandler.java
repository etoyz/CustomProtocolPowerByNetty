package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;


public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        System.out.println("收到UDP数据报：\t" + msg.toString());
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);

        System.out.println("提取到数据部分：\t" + new String(bytes, StandardCharsets.UTF_8));
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        ctx.fireChannelRead(byteBuf);
    }
}
