package com.example.fireengineserver.fireEngineServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.ArrayList;

public class FireEngineServer {
    public static ArrayList<RequestMsg> receivedData = new ArrayList<>(); // 存储本次运行接收到的客户端数据（运行时）

    public static void startServer() {

        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(acceptGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new FireEngineEncoder())
                                .addLast(new FireEngineDecoder());
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(688).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptGroup.shutdownGracefully();
        }
    }
}
