package com.example.fireengineserver.fireEngineServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class FireEngineDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        // convert the byte array into BigInteger.
        BigInteger integer = new BigInteger(new String(bytes, StandardCharsets.UTF_8), 2);
        String hexStr = integer.toString(16);
        System.out.println("十六进制表示：\t\t" + hexStr.toUpperCase(Locale.ROOT) + "H");

        ctx.fireChannelRead(hexStr);
    }
}
