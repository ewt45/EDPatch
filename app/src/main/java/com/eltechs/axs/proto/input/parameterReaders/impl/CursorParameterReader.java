package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadCursor;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class CursorParameterReader extends ReferenceToObjectParameterReader {
    public CursorParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Cursor cursor = xServer.getCursorsManager().getCursor(i);
        if (cursor == null) {
            throw new BadCursor(i);
        }
        return cursor;
    }
}
