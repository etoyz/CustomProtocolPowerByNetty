package com.example.fireengineserver.fireEngineServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class FireEngineServer {

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
                                .addLast(new FireEngineDecoder())
                                .addLast(new FireEngineEncoder())
                                .addLast(new CustomLogicHandler());
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
