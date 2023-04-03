package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SystemRequests extends HandlerObjectBase {
    private final short DEFAULT_SCREEN_SAVER_INTERVAL_SEC;
    private final short DEFAULT_SCREEN_SAVER_TIME_SEC;

    /* loaded from: classes.dex */
    public enum NoYesDefault {
        NO,
        YES,
        DEFAULT
    }

    @RequestHandler(opcode = 104)
    public void Bell(@OOBParam @RequestParam byte b) {
    }

    @RequestHandler(opcode = Opcodes.NoOperation)
    public void NoOperation(@RequestParam ByteBuffer byteBuffer) {
    }

    @RequestHandler(opcode = 107)
    public void SetScreenSaver(@RequestParam short s, @RequestParam short s2, @RequestParam NoYesDefault noYesDefault, @RequestParam NoYesDefault noYesDefault2, @RequestParam short s3) throws IOException {
    }

    public SystemRequests(XServer xServer) {
        super(xServer);
        this.DEFAULT_SCREEN_SAVER_TIME_SEC = (short) 600;
        this.DEFAULT_SCREEN_SAVER_INTERVAL_SEC = (short) 600;
    }

    @RequestHandler(opcode = 108)
    public void GetScreenSaver(XResponse xResponse) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.SystemRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putShort((short) 600);
                byteBuffer.putShort((short) 600);
                byteBuffer.put((byte) NoYesDefault.YES.ordinal());
                byteBuffer.put((byte) NoYesDefault.YES.ordinal());
            }
        });
    }
}
