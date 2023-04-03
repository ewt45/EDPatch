package com.eltechs.axs.proto.input.errors;

import com.eltechs.axs.proto.input.XProtocolError;

/* loaded from: classes.dex */
public class BadIdChoice extends XProtocolError {
    public BadIdChoice(int i) {
        super(CoreErrorCodes.ID_CHOICE, i);
    }

    public int getBadId() {
        return getData();
    }
}
