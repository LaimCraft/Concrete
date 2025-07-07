package ru.laimcraft.concrete;

import io.netty.buffer.ByteBuf;

public class VarLong {

    public static long read(ByteBuf buf) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            if(!buf.isReadable()) {
                throw new RuntimeException("read VarLong Exception: недостаточно байт");
            }

            read = buf.readByte();
            long value = (read & 0b01111111L);
            result |= (value << (7 * numRead));

            numRead++;
            if(numRead > 10) {
                throw new RuntimeException("VarLong слишком большой (более 10 байт)");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void write(ByteBuf buf, long value) {
        do {
            byte temp = (byte) (value & 0b01111111L);
            value >>>= 7;
            if (value != 0) {
                temp |= (byte) 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

}
