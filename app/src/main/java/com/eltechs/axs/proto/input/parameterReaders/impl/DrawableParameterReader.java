package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadDrawable;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class DrawableParameterReader extends ReferenceToObjectParameterReader {
    public DrawableParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Drawable drawable = xServer.getDrawablesManager().getDrawable(i);
        if (drawable == null) {
            throw new BadDrawable(i);
        }
        return drawable;
    }
}
