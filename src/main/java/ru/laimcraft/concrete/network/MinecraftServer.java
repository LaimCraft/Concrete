package ru.laimcraft.concrete.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MinecraftServer extends Thread {

    ServerBootstrap serverBootstrap;
    EventLoopGroup eventLoopGroup;

    @Override
    public void run() {
        try {
            serverBootstrap = new ServerBootstrap();

            Class clazz;

            if (Epoll.isAvailable()) {
                clazz = EpollServerSocketChannel.class;
                eventLoopGroup = new EpollEventLoopGroup();
            } else {
                clazz = NioServerSocketChannel.class;
                eventLoopGroup = new NioEventLoopGroup();
            }

            serverBootstrap.channel(clazz);

            serverBootstrap.childHandler(new MinecraftChannelInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 25555).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
