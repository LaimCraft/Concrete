package ru.laimcraft.concrete.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import ru.laimcraft.concrete.VarInt;
import ru.laimcraft.concrete.VarLong;
import ru.laimcraft.concrete.VarString;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class MinecraftConnection extends SimpleChannelInboundHandler<Object> {

    public Channel channel;
    public SocketAddress address;
    public SocketAddress haProxyAddress;
    public boolean preparing;

    public MinecraftConnection() {

    }

    public void configurePacketHandler(ChannelPipeline pipeline) {
        System.out.println("Connection init");
        pipeline.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
            public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
                super.write(ctx, obj, promise);
            }
        }).addLast("packet_handler", this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();
        this.preparing = false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("disconnect");
        this.disconnect();
    }

    String test_response = "{\n" +
            "  \"version\": {\n" +
            "    \"name\": \"1.21.6\",\n" +
            "    \"protocol\": 763\n" +
            "  },\n" +
            "  \"players\": {\n" +
            "    \"max\": 100,\n" +
            "    \"online\": 1000000,\n" +
            "    \"sample\": []\n" +
            "  },\n" +
            "  \"description\": {\n" +
            "    \"text\": \"АХХАХАХА\"\n" +
            "  }\n" +
            "}\n";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf buf) {
            //int readableBytes = buf.readableBytes();
            //byte[] bytes = new byte[readableBytes];
            //buf.readBytes(bytes);
            int length = VarInt.read(buf);
            int id = VarInt.read(buf);
            System.out.println("Получен пакет: " + id + ", размер пакета: " + length);

            if(id == 0) {
                if(buf.isReadable()) {
                    int protocolVersion = VarInt.read(buf);
                    String serverAddress = VarString.read(buf);
                    int serverPort = buf.readUnsignedShort();
                    int nextState = VarInt.read(buf);

                    System.out.println("Данные об клиенте версия протокола: " + protocolVersion +
                            ", подключился через: " + serverAddress + ":" + serverPort + ", следующая стадия: " + nextState);
                } return;
            } if(id == 1) {
                if(buf.isReadable()) {
                    long time = VarLong.read(buf);
                    System.out.println(time);
                    ByteBuf responseBuf = ctx.alloc().buffer();
                    VarInt.write(responseBuf, 1);
                    VarLong.write(responseBuf, time);

                    ByteBuf finalBuf = ctx.alloc().buffer();
                    VarInt.write(finalBuf, responseBuf.readableBytes());
                    finalBuf.writeBytes(responseBuf);

                    ctx.writeAndFlush(finalBuf);
                    System.out.println("pong sending");
                    return;
                } return;
            }

            // Генерация ответа для списка сервера
            ByteBuf responseBuf = ctx.alloc().buffer();
            VarInt.write(responseBuf, 0);
            byte[] jsonBytes = test_response.getBytes(StandardCharsets.UTF_8);
            VarInt.write(responseBuf, jsonBytes.length);
            responseBuf.writeBytes(jsonBytes);

            ByteBuf finalBuf = ctx.alloc().buffer();
            VarInt.write(finalBuf, responseBuf.readableBytes());
            finalBuf.writeBytes(responseBuf);

            ctx.writeAndFlush(finalBuf);
        }
    }

    public void disconnect() {
    }
}
