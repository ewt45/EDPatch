package com.eltechs.axs.proto.input;

/* loaded from: classes.dex */
public abstract class XProtocolError extends Exception {
    private final byte code;
    private final int data;

    /* JADX INFO: Access modifiers changed from: protected */
    public XProtocolError(byte b, int i) {
        this.code = b;
        this.data = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public XProtocolError(byte b, int i, String str) {
        super(str);
        this.code = b;
        this.data = i;
    }

    public byte getErrorCode() {
        return this.code;
    }

    public int getData() {
        return this.data;
    }
}
