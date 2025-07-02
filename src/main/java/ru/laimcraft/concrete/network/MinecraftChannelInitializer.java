package ru.laimcraft.concrete.network;

import io.netty.channel.*;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.Connection;

public class MinecraftChannelInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        // Убирает алгоритм Нейгла который объединяет маленькие пакеты и отправляет их вместе
        try {channel.config().setOption(ChannelOption.TCP_NODELAY, true);} catch (Exception e) {}

        // Если за 30 секунд не было пакетов игрока выкидывает с сервера
        ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));

        // хз
        channel.pipeline().addAfter("timeout", "haproxy-decoder", new HAProxyMessageDecoder());
        channel.pipeline().addAfter("haproxy-decoder", "haproxy-handler", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if(msg instanceof final HAProxyMessage message) {
                    if(message.command() == HAProxyCommand.PROXY) {
                        String realAddress = message.sourceAddress();
                        int realPort = message.sourcePort();
                        SocketAddress socketAddress = new InetSocketAddress(realAddress, realPort);
                        MinecraftConnection minecraftConnection = (MinecraftConnection) channel.pipeline().get("packet_handler");
                    }
                } else {
                    super.channelRead(ctx, msg);
                }
            }
        });
    }
}
