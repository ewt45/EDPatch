package com.eltechs.axs.proto.input.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.ExtensionRequestHandler;
import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.errors.BadRequest;
import com.eltechs.axs.requestHandlers.X11ProtocolExtensionIds;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XRequest;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xconnectors.impl.XInputStreamImpl;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class RootXRequestHandler implements RequestHandler<XClient> {
    private static final int SIZE_OF_INT = 4;
    private static final int X_REQUEST_PROLOGUE_LEN = 4;
    private final ExtensionRequestHandler[] extensionHandlers = new ExtensionRequestHandler[256];
    private final HandshakeHandler handshakeHandler;
    private final XServer target;

    public RootXRequestHandler(XServer xServer) {
        this.target = xServer;
        this.handshakeHandler = new HandshakeHandler(xServer);
        installExtensionHandler(X11ProtocolExtensionIds.BIGREQ, new BigReqExtensionHandler());
    }

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(XClient xClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xClient.isAuthenticated()) {
            return handleNormalRequest(xClient, xInputStream, xOutputStream);
        }
        return this.handshakeHandler.handleAuthRequest(xClient, xInputStream, xOutputStream);
    }

    private ProcessingResult handleNormalRequest(XClient xClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        int i;
        if (xInputStream.getAvailableBytesCount() < 4) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        byte majorOpCode = xInputStream.getByte();
//        if(majorOpCode==49){
//            byte[] tmpbytes = new byte[xInputStream.getAvailableBytesCount()];
//            xInputStream.get(tmpbytes);
//            Log.e("TAG", "handleNormalRequest: 调用listFonts了，看看字节都是啥"+ Arrays.toString(tmpbytes), null);
//        }
        byte minorOpCode = xInputStream.getByte();
        int extendAsUnsigned = ArithHelpers.extendAsUnsigned(xInputStream.getShort());
        if (extendAsUnsigned != 0) {
            i = 4;
        } else if (xInputStream.getAvailableBytesCount() < 4) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        } else {
            extendAsUnsigned = xInputStream.getInt();
            i = 8;
        }
        int length = (extendAsUnsigned * 4) - i;
        if (length > xInputStream.getAvailableBytesCount()) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        XRequest xRequest = new XRequest(xClient.generateSequenceNumber(), xInputStream, length);
        XResponse xResponse = new XResponse(xRequest, xOutputStream);
        xRequest.setMajorOpcode(majorOpCode);
        dispatchRequest(majorOpCode, minorOpCode, length, xClient, xRequest, xResponse);
        if(xRequest.getRemainingBytesCount()!=0){
            Log.d("TAG", "handleNormalRequest: "+xRequest.getRemainingBytesCount());
            StringBuilder builder = new StringBuilder();
            while (xRequest.getRemainingBytesCount()>0)
                builder.append(" ").append(xRequest.readByte());
            Log.d("TAG", "handleNormalRequest: "+builder.toString());
            int stop = 1;
        }
        Assert.state(xRequest.getRemainingBytesCount() == 0, "Request has not been parsed fully.");
        return ProcessingResult.PROCESSED;
    }

    private void dispatchRequest(byte majorOpCode, byte minorOpCode, int length, XClient xClient, XRequest xRequest, XResponse xResponse) throws IOException {
        ExtensionRequestHandler extensionRequestHandler = this.extensionHandlers[majorOpCode >= 0 ? 0 : ArithHelpers.extendAsUnsigned(majorOpCode)];
//        if(majorOpCode==49){
//            Log.e("TAG", "dispatchRequest: 调用listFonts了,看看用的哪个handler"+extensionRequestHandler);
//        }
        try {
            if (length < 0 || extensionRequestHandler == null) {
                throw new BadRequest();
            }
            extensionRequestHandler.handleRequest(xClient, majorOpCode, minorOpCode, length, xRequest, xResponse);
        } catch (XProtocolError e) {
            e.printStackTrace();
            xRequest.skipRequest();
            xResponse.sendError(e);
        }
    }

    @SuppressLint("DefaultLocale")
    public void installExtensionHandler(int i, ExtensionRequestHandler extensionRequestHandler) {
        Assert.state(this.extensionHandlers[i] == null, String.format("A handler for the protocol extension %d is already installed.", Integer.valueOf(i)));
        this.extensionHandlers[i] = extensionRequestHandler;
    }

    public List<ExtensionRequestHandler> getInstalledExtensionHandlers() {
        ArrayList<ExtensionRequestHandler> arrayList = new ArrayList<>();
        int length = this.extensionHandlers.length;
        for (int i = 1; i < length; i++) {
            if (this.extensionHandlers[i] != null) {
                arrayList.add(this.extensionHandlers[i]);
            }
        }
        return arrayList;
    }
}
