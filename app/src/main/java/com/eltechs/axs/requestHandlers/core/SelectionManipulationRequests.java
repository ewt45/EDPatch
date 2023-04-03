package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SelectionManipulationRequests extends HandlerObjectBase {
    public SelectionManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER", "SELECTIONS_MANAGER"})
    @RequestHandler(opcode = 22)
    public void SetSelectionOwner(XClient xClient, XResponse xResponse, @SpecialNullValue(0) @RequestParam Window window, @RequestParam Atom atom, @RequestParam int i) throws XProtocolError, IOException {
        this.xServer.getSelectionsManager().setSelectionOwner(atom, window, xClient, i);
    }

    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER", "SELECTIONS_MANAGER"})
    @RequestHandler(opcode = 23)
    public void GetSelectionOwner(XClient xClient, XResponse xResponse, @RequestParam Atom atom) throws XProtocolError, IOException {
        final Window selectionOwner = this.xServer.getSelectionsManager().getSelectionOwner(atom);
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.SelectionManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(selectionOwner != null ? selectionOwner.getId() : 0);
            }
        });
    }

    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER", "SELECTIONS_MANAGER"})
    @RequestHandler(opcode = 24)
    public void ConvertSelection(XClient xClient, XResponse xResponse, @RequestParam Window window, @RequestParam Atom atom, @RequestParam Atom atom2, @SpecialNullValue(0) @RequestParam Atom atom3, @RequestParam int i) throws XProtocolError, IOException {
        this.xServer.getSelectionsManager().convertSelection(window, xClient, atom, atom2, atom3, i);
    }
}
