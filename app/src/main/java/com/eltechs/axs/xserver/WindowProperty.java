package com.eltechs.axs.xserver;

import com.eltechs.axs.proto.input.errors.CoreErrorCodes;

/* loaded from: classes.dex */
public interface WindowProperty<T> {
    public static final Format<byte[]> ARRAY_OF_BYTES = new Format<>((byte) 8);
    public static final Format<short[]> ARRAY_OF_SHORTS = new Format<>(CoreErrorCodes.LENGTH);
    public static final Format<int[]> ARRAY_OF_INTS = new Format<>((byte) 32);

    Format<T> getFormat();

    int getSizeInBytes();

    Atom getType();

    T getValues();

    /* loaded from: classes.dex */
    public static class Format<T> {
        private final byte formatValue;

        public Format(byte b) {
            this.formatValue = b;
        }

        public byte getFormatValue() {
            return this.formatValue;
        }
    }
}