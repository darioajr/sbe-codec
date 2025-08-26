package com.github.darioajr.sbe;

public enum BooleanType {
    FALSE((byte)0),
    TRUE((byte)1);

    private final byte value;

    BooleanType(final byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static BooleanType get(final byte value) {
        switch (value) {
            case (byte)0: return FALSE;
            case (byte)1: return TRUE;
            default: throw new IllegalArgumentException("Unknown BooleanType value: " + value);
        }
    }
}
