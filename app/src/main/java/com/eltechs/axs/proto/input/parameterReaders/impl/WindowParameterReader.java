package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadWindow;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class WindowParameterReader extends ReferenceToObjectParameterReader {
    public WindowParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Window window = xServer.getWindowsManager().getWindow(i);
        if (window == null) {
            throw new BadWindow(i);
        }
        return window;
    }
}
