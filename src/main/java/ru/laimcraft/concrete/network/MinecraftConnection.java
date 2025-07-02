package ru.laimcraft.concrete.network;

import io.netty.channel.*;

import java.net.SocketAddress;

public class MinecraftConnection extends SimpleChannelInboundHandler {

    public Channel channel;
    public SocketAddress address;
    public SocketAddress haProxyAddress;

    public MinecraftConnection() {

    }

    public void configurePacketHandler(ChannelPipeline pipeline) {
        pipeline.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
            public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
                super.write(ctx, obj, promise);
            }
        }).addLast("packet_handler", this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

    }
}
