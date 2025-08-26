package com.github.darioajr.sbe;

import org.agrona.DirectBuffer;
import java.nio.charset.StandardCharsets;

public class TradeDecoder {
    public static final int TEMPLATE_ID = 2;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int BLOCK_LENGTH = 49;
    
    private DirectBuffer buffer;
    private int offset;
    private int blockLength;
    private int version;

    public TradeDecoder wrap(final DirectBuffer buffer, final int offset, final int blockLength, final int version) {
        this.buffer = buffer;
        this.offset = offset;
        this.blockLength = blockLength;
        this.version = version;
        return this;
    }

    public long tradeId() {
        return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long orderId() {
        return buffer.getLong(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public String symbol() {
        final byte[] dst = new byte[8];
        buffer.getBytes(offset + 16, dst, 0, 8);
        int end = 0;
        for (; end < 8 && dst[end] != 0; ++end);
        return new String(dst, 0, end, StandardCharsets.US_ASCII);
    }

    public Side side() {
        return Side.get(buffer.getByte(offset + 24));
    }

    public long quantity() {
        return buffer.getLong(offset + 25, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long price() {
        return buffer.getLong(offset + 33, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long timestamp() {
        return buffer.getLong(offset + 41, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public int venueLength() {
        final int position = offset + blockLength;
        return buffer.getInt(position, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public int getVenue(final byte[] dst, final int dstOffset, final int length) {
        final int headerLength = 4;
        final int position = offset + blockLength;
        final int dataLength = buffer.getInt(position, java.nio.ByteOrder.LITTLE_ENDIAN);
        final int bytesToCopy = Math.min(length, dataLength);
        buffer.getBytes(position + headerLength, dst, dstOffset, bytesToCopy);
        return bytesToCopy;
    }

    public int encodedLength() {
        return blockLength + 4 + venueLength();
    }
}
