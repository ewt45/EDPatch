package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadPixmap extends XProtocolError {
    public BadPixmap(int i) {
        super((byte) 4, i, String.format("Bad pixmap id %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getData();
    }
}
