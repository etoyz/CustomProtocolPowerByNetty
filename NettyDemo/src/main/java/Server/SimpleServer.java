package Server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Description: Netty 简单服务端示例
 * <br/>
 * Date: 2019/12/27 1:42
 *
 * @author ALion
 */
public class SimpleServer {

    public static void main(String[] args) {
        // linux 下建议使用 EpollEventLoopGroup
        EventLoopGroup acceptGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(acceptGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleServerHandler());
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
