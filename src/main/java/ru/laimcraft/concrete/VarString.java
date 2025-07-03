package ru.laimcraft.concrete;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class VarString {

    public static String read(ByteBuf buf) {
        int length = VarInt.read(buf);
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
