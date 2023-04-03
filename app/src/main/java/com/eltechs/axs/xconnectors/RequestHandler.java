package com.eltechs.axs.xconnectors;

import com.eltechs.axs.proto.input.ProcessingResult;
import java.io.IOException;

/* loaded from: classes.dex */
public interface RequestHandler<Context> {
    ProcessingResult handleRequest(Context context, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException;
}