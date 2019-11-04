package com.tiza.forward.netty;

import com.tiza.forward.netty.handler.DataHandler;
import com.tiza.forward.netty.handler.codec.CodecAdapter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: ForwardClient
 * Author: DIYILIU
 * Update: 2019-11-04 09:48
 */

@Slf4j
public class ForwardClient extends Thread {
    private static ConcurrentLinkedQueue<byte[]> pool = new ConcurrentLinkedQueue();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private String host;

    private int port;

    private int retry = 0;

    private boolean isOpen = false;

    private static ForwardClient client = new ForwardClient();

    private ForwardClient() {

    }

    public static ForwardClient getInstance() {
        return client;
    }

    private DataHandler handler;


    public void init(String host, int port) {
        this.host = host;
        this.port = port;
        this.start();
    }

    @Override
    public void run() {
        connectServer(host, port);
    }

    private void connectServer(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        handler = new DataHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new CodecAdapter())
                                .addLast(handler);
                    }
                });
        try {
            final ChannelFuture future = bootstrap.connect(host, port).
                    addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) {
                            if (channelFuture.isSuccess()) {
                                retry = 0;
                                isOpen = true;
                                toSend();
                            }
                        }
                    }).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            isOpen = false;
            group.shutdownGracefully();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("客户端, 尝试第{}次重连...", ++retry);
            connectServer(host, port);
        }
    }

    /**
     * 连接服务成功
     */
    private void toSend() {
        log.info("启动转发线程...");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (isOpen) {
                    while (handler.isActive() && !pool.isEmpty()) {
                        byte[] bytes = pool.poll();
                        handler.write(bytes);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("转发线程退出...");
            }
        });
    }

    public static void sendMsg(byte[] bytes) {
        pool.add(bytes);
    }
}