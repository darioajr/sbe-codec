package com.github.darioajr.sbe;

import org.agrona.DirectBuffer;
import java.nio.charset.StandardCharsets;

public class MarketDataDecoder {
    public static final int TEMPLATE_ID = 3;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int BLOCK_LENGTH = 64;
    
    private DirectBuffer buffer;
    private int offset;
    private int blockLength;
    private int version;

    public MarketDataDecoder wrap(final DirectBuffer buffer, final int offset, final int blockLength, final int version) {
        this.buffer = buffer;
        this.offset = offset;
        this.blockLength = blockLength;
        this.version = version;
        return this;
    }

    public String symbol() {
        final byte[] dst = new byte[8];
        buffer.getBytes(offset + 0, dst, 0, 8);
        int end = 0;
        for (; end < 8 && dst[end] != 0; ++end);
        return new String(dst, 0, end, StandardCharsets.US_ASCII);
    }

    public long timestamp() {
        return buffer.getLong(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long bidPrice() {
        return buffer.getLong(offset + 16, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long bidSize() {
        return buffer.getLong(offset + 24, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long askPrice() {
        return buffer.getLong(offset + 32, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long askSize() {
        return buffer.getLong(offset + 40, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long lastPrice() {
        return buffer.getLong(offset + 48, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public long lastSize() {
        return buffer.getLong(offset + 56, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

    public LevelsDecoder levels() {
        final int position = offset + blockLength;
        final int blockLength = buffer.getShort(position, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
        final int count = buffer.getShort(position + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
        return new LevelsDecoder(buffer, position + 4, blockLength, count);
    }

    public int encodedLength() {
        final LevelsDecoder levelsDecoder = levels();
        return blockLength + 4 + (levelsDecoder.count * 17);
    }

    public static class LevelsDecoder {
        private final DirectBuffer buffer;
        private final int initialPosition;
        private int position;
        private final int blockLength;
        private final int count;
        private int index = 0;

        LevelsDecoder(final DirectBuffer buffer, final int position, final int blockLength, final int count) {
            this.buffer = buffer;
            this.initialPosition = position;
            this.position = position;
            this.blockLength = blockLength;
            this.count = count;
        }

        public boolean hasNext() {
            return index < count;
        }

        public LevelsDecoder next() {
            if (!hasNext()) {
                throw new IllegalStateException("Index " + index + " out of range for count " + count);
            }
            position = initialPosition + (index * 17);
            index++;
            return this;
        }

        public long price() {
            return buffer.getLong(position + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public long size() {
            return buffer.getLong(position + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public Side side() {
            return Side.get(buffer.getByte(position + 16));
        }
    }
}
