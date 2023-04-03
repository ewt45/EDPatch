package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadColormap extends XProtocolError {
    public BadColormap(int i) {
        super(CoreErrorCodes.COLORMAP, i, String.format("Bad colormap id %d.", Integer.valueOf(i)));
    }

    public int getId() {
        return getData();
    }
}
