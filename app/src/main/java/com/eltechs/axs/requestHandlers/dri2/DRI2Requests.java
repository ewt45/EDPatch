package com.eltechs.axs.requestHandlers.dri2;

import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class DRI2Requests extends HandlerObjectBase {
    public DRI2Requests(XServer xServer) {
        super(xServer);
    }

    @RequestHandler(opcode = 0)
    public void QueryVersion(XResponse xResponse, @RequestParam int i, @RequestParam int i2) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.dri2.DRI2Requests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(1);
                byteBuffer.putInt(3);
            }
        });
    }
}
