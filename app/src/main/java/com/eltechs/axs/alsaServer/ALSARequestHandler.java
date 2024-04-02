package com.eltechs.axs.alsaServer;

import static com.eltechs.axs.alsaServer.ClientOpcodes.Close;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Drain;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Pointer;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Prepare;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Start;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Stop;
import static com.eltechs.axs.alsaServer.ClientOpcodes.Write;

import android.support.constraint.motion.utils.StopLogic;

import com.eltechs.axs.proto.input.ProcessingResult;
import com.eltechs.axs.xconnectors.RequestHandler;
import com.eltechs.axs.xconnectors.XInputStream;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XStreamLock;
import java.io.IOException;

/* loaded from: classes.dex */
public class ALSARequestHandler implements RequestHandler<ALSAClient> {
    public static final int HEADER_SIZE = 8;

    @Override // com.eltechs.axs.xconnectors.RequestHandler
    public ProcessingResult handleRequest(ALSAClient aLSAClient, XInputStream xInputStream, XOutputStream xOutputStream) throws IOException {
        if (xInputStream.getAvailableBytesCount() < 8) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        int opcode = xInputStream.getInt();
        int len = xInputStream.getInt();
        if (xInputStream.getAvailableBytesCount() < len) {
            return ProcessingResult.INCOMPLETE_BUFFER;
        }
        switch (opcode) {
            case Close:
                aLSAClient.reset();
                break;
            case Prepare:
                aLSAClient.reset();
                boolean successful = aLSAClient.setChannels(xInputStream.getInt())
                        && (aLSAClient.setFormat(xInputStream.getInt()))
                        && aLSAClient.setRate(xInputStream.getInt());
                if (!successful) {
                    return ProcessingResult.PROCESSED_KILL_CONNECTION;
                }
                break;
            case Start:
                aLSAClient.start();
                break;
            case Write:
                aLSAClient.writeDataToTrack(xInputStream.getAsByteBuffer(len), len);
                break;
            case Stop:
                aLSAClient.stop();
                break;
            case Pointer:
                try (XStreamLock ignored = xOutputStream.lock()) {
                    xOutputStream.writeInt(aLSAClient.pointer());
                }
                break;
            case Drain:
                aLSAClient.drain();
                break;
        }
        return ProcessingResult.PROCESSED;
    }
}
