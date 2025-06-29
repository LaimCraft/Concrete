package ru.laimcraft.concrete.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class MinecraftServer extends Thread {

    ServerBootstrap serverBootstrap;
    EventLoopGroup eventLoopGroup1 = new NioEventLoopGroup();
    EventLoopGroup eventLoopGroup2 = new NioEventLoopGroup();

    @Override
    public void run() {
        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup1, eventLoopGroup2);

            Class clazz;

            if(Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
            }

            //serverBootstrap.childHandler(new ChannelHandler());

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 25555).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //Proxy.logger.info(e.getMessage());
            return;
        } finally {
            eventLoopGroup1.shutdownGracefully();
            eventLoopGroup2.shutdownGracefully();
        }
    }
}
