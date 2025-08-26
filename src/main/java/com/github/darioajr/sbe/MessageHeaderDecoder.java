package com.github.darioajr.sbe;

import org.agrona.DirectBuffer;

public class MessageHeaderDecoder {
    public static final int ENCODED_LENGTH = 8;
    
    private DirectBuffer buffer;
    private int offset;

    public MessageHeaderDecoder wrap(final DirectBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.offset = offset;
        return this;
    }

    public int blockLength() {
        return buffer.getShort(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
    }

    public int templateId() {
        return buffer.getShort(offset + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
    }

    public int schemaId() {
        return buffer.getShort(offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
    }

    public int version() {
        return buffer.getShort(offset + 6, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF;
    }

    public int encodedLength() {
        return ENCODED_LENGTH;
    }
}
