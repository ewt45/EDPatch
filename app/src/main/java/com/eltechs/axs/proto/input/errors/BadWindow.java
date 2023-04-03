package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadWindow extends XProtocolError {
    public BadWindow(int i) {
        super((byte) 3, i, String.format("Bad window id %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getErrorCode();
    }
}
