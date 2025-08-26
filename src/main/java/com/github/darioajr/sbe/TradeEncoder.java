package com.github.darioajr.sbe;

import org.agrona.MutableDirectBuffer;
import java.nio.charset.StandardCharsets;

public class TradeEncoder {
    public static final int TEMPLATE_ID = 2;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int BLOCK_LENGTH = 49;
    
    private MutableDirectBuffer buffer;
    private int offset;
    private int position;

    public TradeEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.position = offset;
        return this;
    }

    public int sbeBlockLength() { return BLOCK_LENGTH; }
    public int sbeTemplateId() { return TEMPLATE_ID; }
    public int sbeSchemaId() { return SCHEMA_ID; }
    public int sbeSchemaVersion() { return SCHEMA_VERSION; }

    public TradeEncoder tradeId(final long value) {
        buffer.putLong(position + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public TradeEncoder orderId(final long value) {
        buffer.putLong(position + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public TradeEncoder symbol(final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
        final int length = Math.min(bytes.length, 8);
        buffer.putBytes(position + 16, bytes, 0, length);
        for (int i = length; i < 8; i++) {
            buffer.putByte(position + 16 + i, (byte)0);
        }
        return this;
    }

    public TradeEncoder side(final Side value) {
        buffer.putByte(position + 24, value.value());
        return this;
    }

    public TradeEncoder quantity(final long value) {
        buffer.putLong(position + 25, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public TradeEncoder price(final long value) {
        buffer.putLong(position + 33, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public TradeEncoder timestamp(final long value) {
        buffer.putLong(position + 41, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public TradeEncoder putVenue(final byte[] src, final int srcOffset, final int length) {
        final int headerLength = 4;
        final int limit = position + BLOCK_LENGTH + headerLength + length;
        buffer.putInt(position + BLOCK_LENGTH, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(position + BLOCK_LENGTH + headerLength, src, srcOffset, length);
        position = limit;
        return this;
    }

    public int encodedLength() {
        return position - offset;
    }
}
