package Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.BitSet;

/**
 * Description: Netty 简单服务器的处理器
 * <br/>
 * Date: 2019/12/27 1:44
 *
 * @author ALion
 */
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server.SimpleServerHandler.channelRead");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("Server received: " + buf.toString(CharsetUtil.UTF_8));
        System.out.println("\n\n\n");
        System.out.println("Server received: " + buf.toString());
        System.out.println("\n\n\n");
        BitSet bitSet = BitSet.valueOf(buf.asByteBuf().nioBuffer()); // array??
        System.out.println(bitSet.get(0, 5));

        ByteBuf byteBuf = Unpooled.copiedBuffer("happy", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
