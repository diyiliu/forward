package com.tiza.forward.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * Description: CodecAdapter
 * Author: DIYILIU
 * Update: 2019-11-04 09:50
 */
public class CodecAdapter extends ByteToMessageCodec {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {

        if (o instanceof ByteBuf) {
            byteBuf.writeBytes((ByteBuf) o);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) {

    }
}
