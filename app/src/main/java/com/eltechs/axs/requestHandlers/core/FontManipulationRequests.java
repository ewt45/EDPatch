package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.annotations.ParamLength;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class FontManipulationRequests extends HandlerObjectBase {
    @RequestHandler(opcode = 46)
    public void CloseFont(@RequestParam int i) {
    }

    public FontManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @RequestHandler(opcode = 45)
    public void OpenFont(
            @RequestParam int i,
            @RequestParam @ParamName("nameLength") short s,
            @RequestParam short s2,
            @ParamLength("nameLength") @RequestParam String str) {
        if (!str.equals("cursor")) {
            Assert.notImplementedYet(String.format("OpenFont supports only font='cursor', but got '%s'.", str));
        }
    }

    @RequestHandler(opcode = 49)
    public void ListFonts(
            XClient xClient,
            XResponse xResponse ,
            @RequestParam @Unsigned @Width(2) int maxNames,
            @RequestParam @ParamName("patternLength") short length,
            @ParamLength("patternLength") @RequestParam String pattern
            ) throws IOException {
        Log.d("TAG", "看看要求的pattern是啥？"+pattern);
        String[] fontLists = new String[0];
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.AtomManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {

            }
        });
    }
}
