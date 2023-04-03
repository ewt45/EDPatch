package com.eltechs.axs.proto.input.parameterReaders.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.impl.ParameterDescriptor;
import com.eltechs.axs.proto.input.annotations.impl.RequestDataReader;
import com.eltechs.axs.proto.input.errors.BadAtom;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.XServer;

/* loaded from: classes.dex */
public class AtomParameterReader extends ReferenceToObjectParameterReader {
    public AtomParameterReader(RequestDataReader requestDataReader, ParameterDescriptor parameterDescriptor) {
        super(requestDataReader, parameterDescriptor);
    }

    @Override // com.eltechs.axs.proto.input.parameterReaders.impl.ReferenceToObjectParameterReader
    protected Object getReference(XServer xServer, int i) throws XProtocolError {
        Atom atom = xServer.getAtomsManager().getAtom(i);
        if (atom == null) {
            throw new BadAtom(i);
        }
        return atom;
    }
}
