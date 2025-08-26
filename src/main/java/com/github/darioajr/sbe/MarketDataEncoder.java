package com.github.darioajr.sbe;

import org.agrona.MutableDirectBuffer;
import java.nio.charset.StandardCharsets;

public class MarketDataEncoder {
    public static final int TEMPLATE_ID = 3;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int BLOCK_LENGTH = 64;
    
    private MutableDirectBuffer buffer;
    private int offset;
    private int position;

    public MarketDataEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.position = offset;
        return this;
    }

    public int sbeBlockLength() { return BLOCK_LENGTH; }
    public int sbeTemplateId() { return TEMPLATE_ID; }
    public int sbeSchemaId() { return SCHEMA_ID; }
    public int sbeSchemaVersion() { return SCHEMA_VERSION; }

    public MarketDataEncoder symbol(final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
        final int length = Math.min(bytes.length, 8);
        buffer.putBytes(position + 0, bytes, 0, length);
        for (int i = length; i < 8; i++) {
            buffer.putByte(position + 0 + i, (byte)0);
        }
        return this;
    }

    public MarketDataEncoder timestamp(final long value) {
        buffer.putLong(position + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder bidPrice(final long value) {
        buffer.putLong(position + 16, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder bidSize(final long value) {
        buffer.putLong(position + 24, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder askPrice(final long value) {
        buffer.putLong(position + 32, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder askSize(final long value) {
        buffer.putLong(position + 40, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder lastPrice(final long value) {
        buffer.putLong(position + 48, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MarketDataEncoder lastSize(final long value) {
        buffer.putLong(position + 56, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public LevelsEncoder levelsCount(final int count) {
        buffer.putShort(position + BLOCK_LENGTH, (short)17, java.nio.ByteOrder.LITTLE_ENDIAN); // blockLength
        buffer.putShort(position + BLOCK_LENGTH + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN); // numInGroup
        return new LevelsEncoder(buffer, position + BLOCK_LENGTH + 4, count);
    }

    public int encodedLength() {
        return BLOCK_LENGTH;
    }

    public static class LevelsEncoder {
        private final MutableDirectBuffer buffer;
        private final int initialPosition;
        private int position;
        private final int count;
        private int index = 0;

        LevelsEncoder(final MutableDirectBuffer buffer, final int position, final int count) {
            this.buffer = buffer;
            this.initialPosition = position;
            this.position = position;
            this.count = count;
        }

        public LevelsEncoder next() {
            if (index >= count) {
                throw new IllegalStateException("Index " + index + " out of range for count " + count);
            }
            position = initialPosition + (index * 17);
            index++;
            return this;
        }

        public LevelsEncoder price(final long value) {
            buffer.putLong(position + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        public LevelsEncoder size(final long value) {
            buffer.putLong(position + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        public LevelsEncoder side(final Side value) {
            buffer.putByte(position + 16, value.value());
            return this;
        }
    }
}
