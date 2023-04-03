package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.KeyboardModel;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class KeyboardRelatedRequest extends HandlerObjectBase {
    public KeyboardRelatedRequest(XServer xServer) {
        super(xServer);
    }

    @Locks({"INPUT_DEVICES"})
    @RequestHandler(opcode = 44)
    public void QueryKeymap(XResponse xResponse) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new byte[32]);
    }

    @Locks({"KEYBOARD_MODEL_MANAGER"})
    @RequestHandler(opcode = 101)
    public void GetKeyboardMapping(XResponse xResponse, @RequestParam @Unsigned @Width(1) int i, @RequestParam @Unsigned @Width(1) int i2, @RequestParam short s) throws IOException {
        KeyboardModel keyboardModel = this.xServer.getKeyboardModelManager().getKeyboardModel();
        int layoutsCount = 2 * keyboardModel.getLayoutsCount();
        final int[] iArr = new int[i2 * layoutsCount];
        int[] iArr2 = new int[layoutsCount];
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            keyboardModel.getKeysymsForKeycode(i + i4, iArr2);
            System.arraycopy(iArr2, 0, iArr, i3, iArr2.length);
            i3 += iArr2.length;
        }
        xResponse.sendSuccessReplyWithPayload((byte) layoutsCount, null, iArr.length * 4, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.KeyboardRelatedRequest.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.asIntBuffer().put(iArr);
            }
        });
    }

    @Locks({"KEYBOARD_MODEL_MANAGER"})
    @RequestHandler(opcode = 119)
    public void GetModifierMapping(XResponse xResponse) throws IOException {
        xResponse.sendSuccessReply((byte) 1, new byte[8]);
    }
}
