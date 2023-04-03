package com.eltechs.axs.proto.input.annotations.impl;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.errors.BadRequest;

/* loaded from: classes.dex */
public class NormalRequestDataReader implements RequestDataReader {
    public static final RequestDataReader INSTANCE = new NormalRequestDataReader();

    private NormalRequestDataReader() {
    }

    @Override // com.eltechs.axs.proto.input.annotations.impl.RequestDataReader
    public byte readByte(RequestDataRetrievalContext requestDataRetrievalContext) throws XProtocolError {
        updateRemainingBytesCount(requestDataRetrievalContext, 1);
        return requestDataRetrievalContext.req.readByte();
    }

    @Override // com.eltechs.axs.proto.input.annotations.impl.RequestDataReader
    public short readShort(RequestDataRetrievalContext requestDataRetrievalContext) throws XProtocolError {
        updateRemainingBytesCount(requestDataRetrievalContext, 2);
        return requestDataRetrievalContext.req.readShort();
    }

    @Override // com.eltechs.axs.proto.input.annotations.impl.RequestDataReader
    public int readInt(RequestDataRetrievalContext requestDataRetrievalContext) throws XProtocolError {
        updateRemainingBytesCount(requestDataRetrievalContext, 4);
        return requestDataRetrievalContext.req.readInt();
    }

    @Override // com.eltechs.axs.proto.input.annotations.impl.RequestDataReader
    public byte[] read(RequestDataRetrievalContext requestDataRetrievalContext, int i) throws XProtocolError {
        updateRemainingBytesCount(requestDataRetrievalContext, i);
        byte[] bArr = new byte[i];
        requestDataRetrievalContext.req.read(bArr);
        return bArr;
    }

    @Override // com.eltechs.axs.proto.input.annotations.impl.RequestDataReader
    public void skip(RequestDataRetrievalContext requestDataRetrievalContext, int i) throws XProtocolError {
        updateRemainingBytesCount(requestDataRetrievalContext, i);
        requestDataRetrievalContext.req.skip(i);
    }

    private void updateRemainingBytesCount(RequestDataRetrievalContext requestDataRetrievalContext, int i) throws XProtocolError {
        if (requestDataRetrievalContext.remainingBytesCount < i) {
            throw new BadRequest();
        }
        requestDataRetrievalContext.remainingBytesCount -= i;
    }
}
