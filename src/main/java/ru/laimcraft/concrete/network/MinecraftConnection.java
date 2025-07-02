package ru.laimcraft.concrete.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MinecraftConnection extends SimpleChannelInboundHandler {

    public final Channel channel;

    public MinecraftConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

    }
}
