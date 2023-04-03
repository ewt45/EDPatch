package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadAtom extends XProtocolError {
    public BadAtom(int i) {
        super((byte) 5, i, String.format("Bad atom id %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getErrorCode();
    }
}
