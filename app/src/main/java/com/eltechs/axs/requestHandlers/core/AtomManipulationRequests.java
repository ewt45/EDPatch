package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.ParamLength;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.AtomsManager;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public class AtomManipulationRequests extends HandlerObjectBase {
    private static final Charset latin1 = Charset.forName("latin1");

    public AtomManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"ATOMS_MANAGER"})
    @RequestHandler(opcode = 16)
    public void InternAtom(
            XResponse xResponse,
            @OOBParam @RequestParam boolean z,
            @RequestParam @ParamName("nameLength") short s,
            @RequestParam short s2,
            @ParamLength("nameLength") @RequestParam String str
    ) throws IOException {
        final int internAtom;
        AtomsManager atomsManager = this.xServer.getAtomsManager();
        if (z) {
            internAtom = atomsManager.getAtomId(str);
        } else {
            internAtom = atomsManager.internAtom(str);
        }
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.AtomManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(internAtom);
            }
        });
    }

    @Locks({"ATOMS_MANAGER"})
    @RequestHandler(opcode = 17)
    public void GetAtomName(XResponse xResponse, @RequestParam final Atom atom) throws IOException {
        final short length = (short) atom.getName().length();
        xResponse.sendSuccessReplyWithPayload(
                (byte) 0,
                new XResponse.ResponseDataWriter() {
                    @Override
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putShort(length);
                    }
                },
                length,
                new XResponse.ResponseDataWriter() {
                    @Override
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.put(atom.getName().getBytes(AtomManipulationRequests.latin1));
                    }
                }
        );
    }


}
