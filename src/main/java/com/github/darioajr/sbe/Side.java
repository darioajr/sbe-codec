package com.github.darioajr.sbe;

public enum Side {
    BUY((byte)'B'),
    SELL((byte)'S');

    private final byte value;

    Side(final byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static Side get(final byte value) {
        switch (value) {
            case (byte)'B': return BUY;
            case (byte)'S': return SELL;
            default: throw new IllegalArgumentException("Unknown Side value: " + value);
        }
    }
}
