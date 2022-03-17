package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Description: Netty 简单客户端示例
 * <br/>
 * Date: 2019/12/27 1:48
 *
 * @author ALion
 */
public class SimpleClient {

    public static void main(String[] args) throws Exception {
        new SimpleClient().sendToServer(new BigInteger("10000000000000", 10));
        // linux 下建议使用 EpollEventLoopGroup
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleClientHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 23333).sync();
            Channel channel = future.channel();

            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < 10; i++) {
                String s = scanner.nextLine();
                ByteBuf byteBuf = Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
                channel.writeAndFlush(byteBuf);
            }

            future.channel().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    private void sendToServer(BigInteger binaryCode) throws Exception {
        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket ds = new DatagramSocket();

        InetAddress ip = InetAddress.getByName("127.0.0.1");

        // convert the String input into the byte array.
        byte buf[] = binaryCode.toByteArray();

        // Step 2 : Create the datagramPacket for sending
        // the data.
        DatagramPacket DpSend =
                new DatagramPacket(buf, buf.length, ip, 688);

        // Step 3 : invoke the send call to actually send
        // the data.
        ds.send(DpSend);
    }
}
