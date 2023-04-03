package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadCursor extends XProtocolError {
    public BadCursor(int i) {
        super((byte) 6, i, String.format("Bad cursor id %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getData();
    }
}
