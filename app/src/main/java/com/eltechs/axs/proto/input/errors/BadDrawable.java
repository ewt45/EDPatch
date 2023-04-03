package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadDrawable extends XProtocolError {
    public BadDrawable(int i) {
        super((byte) 9, i, String.format("Bad drawable %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getErrorCode();
    }
}
