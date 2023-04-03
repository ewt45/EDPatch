package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.ExtensionRequestHandler;
import com.eltechs.axs.proto.input.annotations.ParamLength;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.impl.RootXRequestHandler;
import com.eltechs.axs.proto.output.replies.ExtensionsList;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.XServer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ExtensionInquiries extends HandlerObjectBase {
    private final RootXRequestHandler rootXRequestsDispatcher;

    public ExtensionInquiries(XServer xServer, RootXRequestHandler rootXRequestHandler) {
        super(xServer);
        this.rootXRequestsDispatcher = rootXRequestHandler;
    }

    @RequestHandler(opcode = 98)
    public void QueryExtension(XResponse xResponse, @RequestParam @ParamName("nameLength") short s, @RequestParam short s2, @ParamLength("nameLength") @RequestParam String str) throws IOException {
        final ExtensionRequestHandler findExtensionByName = findExtensionByName(str);
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.ExtensionInquiries.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                int i = findExtensionByName != null ? 1 : 0;
                byteBuffer.put((byte) i);
                if (i != 0) {
                    byteBuffer.put(findExtensionByName.getAssignedMajorOpcode());
                    byteBuffer.put(findExtensionByName.getFirstAssignedEventId());
                    byteBuffer.put(findExtensionByName.getFirstAssignedErrorId());
                }
            }
        });
    }

    private ExtensionRequestHandler findExtensionByName(String str) {
        for (ExtensionRequestHandler extensionRequestHandler : this.rootXRequestsDispatcher.getInstalledExtensionHandlers()) {
            if (str.equals(extensionRequestHandler.getName())) {
                return extensionRequestHandler;
            }
        }
        return null;
    }

    @RequestHandler(opcode = 99)
    public void ListExtensions(XResponse xResponse) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (ExtensionRequestHandler extensionRequestHandler : this.rootXRequestsDispatcher.getInstalledExtensionHandlers()) {
            arrayList.add(extensionRequestHandler.getName());
        }
        xResponse.sendSuccessReply((byte) arrayList.size(), new ExtensionsList(arrayList));
    }
}
