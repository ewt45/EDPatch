package com.eltechs.axs.proto.output.replies;

import com.eltechs.axs.proto.input.impl.ProtoHelpers;
import com.eltechs.axs.proto.output.POD;

@POD({"zero", "messageLength", "majorProtocolVersion", "minorProtocolVersion", "dataLength", "reason"})
/* loaded from: classes.dex */
public class AuthDenial {
    public final short dataLength;
    public final byte messageLength;
    public final String reason;
    public final byte zero = 0;
    public final short majorProtocolVersion = 11;
    public final short minorProtocolVersion = 0;

    public AuthDenial(String str) {
        this.messageLength = (byte) str.length();
        this.dataLength = (short) (ProtoHelpers.roundUpLength4(str.length()) / 4);
        this.reason = str;
    }
}
