package com.github.darioajr.sbe;

import org.agrona.MutableDirectBuffer;

public class MessageHeaderEncoder {
    public static final int ENCODED_LENGTH = 8;
    
    private MutableDirectBuffer buffer;
    private int offset;

    public MessageHeaderEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public MessageHeaderEncoder blockLength(final int value) {
        buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MessageHeaderEncoder templateId(final int value) {
        buffer.putShort(offset + 2, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MessageHeaderEncoder schemaId(final int value) {
        buffer.putShort(offset + 4, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MessageHeaderEncoder version(final int value) {
        buffer.putShort(offset + 6, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public int encodedLength() {
        return ENCODED_LENGTH;
    }
}
