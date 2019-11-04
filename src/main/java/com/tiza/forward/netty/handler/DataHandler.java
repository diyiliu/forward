package com.tiza.forward.netty.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: DataHandler
 * Author: DIYILIU
 * Update: 2019-11-04 10:09
 */

@Slf4j
public class DataHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("与服务器建立连接... ");
        this.context = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    }

    public boolean isActive(){
        if (context == null){
            return false;
        }

        return this.context.channel().isWritable();
    }

    public void write(byte[] bytes){

        this.context.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }
}
