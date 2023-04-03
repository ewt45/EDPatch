package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadValue extends XProtocolError {
    public BadValue(int i) {
        super((byte) 2, i);
    }

    public int getBadValue() {
        return getData();
    }
}
