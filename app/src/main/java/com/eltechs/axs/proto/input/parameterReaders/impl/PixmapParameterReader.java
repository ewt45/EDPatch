package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadPixmap;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class PixmapParameterReader extends ReferenceToObjectParameterReader {
    public PixmapParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Pixmap pixmap = xServer.getPixmapsManager().getPixmap(i);
        if (pixmap == null) {
            throw new BadPixmap(i);
        }
        return pixmap;
    }
}
