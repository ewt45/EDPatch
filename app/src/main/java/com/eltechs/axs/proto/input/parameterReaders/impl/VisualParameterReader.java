package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadMatch;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.drawables.Visual;

/* loaded from: classes.dex */
public class VisualParameterReader extends ReferenceToObjectParameterReader {
    public VisualParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Visual visual = xServer.getDrawablesManager().getVisual(i);
        if (visual == null) {
            throw new BadMatch();
        }
        return visual;
    }
}
