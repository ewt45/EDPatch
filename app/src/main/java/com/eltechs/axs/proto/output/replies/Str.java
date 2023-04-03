package com.eltechs.axs.proto.output.replies;

import com.eltechs.axs.proto.output.POD;
import java.nio.charset.Charset;

@POD({"length", "str"})
/* loaded from: classes.dex */
public class Str {
    public final byte length;
    public final byte[] str;

    public Str(String str) {
        this.length = (byte) str.length();
        this.str = str.getBytes(Charset.forName("latin1"));
    }
}
