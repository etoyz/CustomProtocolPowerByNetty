package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Random;

public class FireEngineEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        // CRC校验
        String crc = msg.substring(msg.length() - 4, msg.length() - 2);
        if (!verifyCRC(msg, crc)) { // 若校验失败
            String ID = msg.substring(8, 10);
            int len = 10;
            String resCrc = "00";
            ctx.writeAndFlush("514E" + Integer.toHexString(len) + ID + resCrc + "7F00E0XX45");
            return;
        }
        //提取数据有效部分 ID--数据内容
        msg = msg.substring(8); // 去头
        msg = msg.substring(0, msg.length() - 4); // 去尾

        //

        System.out.println(msg);
    }

    // TODO
    private boolean verifyCRC(String msg, String crc) {

        return new Random().nextBoolean();
    }
}
