package com.example.fireengineserver.webService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
    public static void startServer() {

        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(acceptGroup, clientGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("logging", new LoggingHandler("DEBUG"))//设置log监听器，并且日志级别为debug，方便观察运行流程
                                .addLast("http-codec", new HttpServerCodec())//设置解码器
                                .addLast("aggregator", new HttpObjectAggregator(65536))//聚合器，使用websocket会用到
                                .addLast("http-chunked", new ChunkedWriteHandler())//用于大数据的分区传输
                                .addLast("handler", new WebSocketHandler());//自定义的业务handler
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(88).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptGroup.shutdownGracefully();
            clientGroup.shutdownGracefully();
        }
    }
}
