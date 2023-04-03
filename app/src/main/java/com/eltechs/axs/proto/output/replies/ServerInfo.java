package com.eltechs.axs.proto.output.replies;

import com.eltechs.axs.proto.X11ImplementationVendor;
import com.eltechs.axs.proto.input.impl.ProtoHelpers;
import com.eltechs.axs.proto.output.POD;
import com.eltechs.axs.proto.output.PODWriter;
import com.eltechs.axs.xserver.IdInterval;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.drawables.ImageFormat;
import java.util.Collection;

@POD({"success", "unused0", "majorProtocolVersion", "minorProtocolVersion", "additionalDataLength", "releaseNumber", "resourceIdBase", "resourceIdMask", "motionBufferSize", "vendorNameLength", "maximumRequestLength", "screensCount", "pixmapFormatsCount", "imageByteOrder", "bitmapFormatBitOrder", "bitmapFormatScanlineUnit", "bitmapFormatScanlinePad", "minKeycode", "maxKeycode", "unused1", "vendorName", "pixmapFormats", "roots"})
/* loaded from: classes.dex */
public class ServerInfo {
    public final short additionalDataLength;
    public final PixmapFormat[] pixmapFormats;
    public final byte pixmapFormatsCount;
    public final int resourceIdBase;
    public final int resourceIdMask;
    public final Screen[] roots;
    public final byte success = 1;
    public final byte unused0 = 0;
    public final short majorProtocolVersion = 11;
    public final short minorProtocolVersion = 0;
    public final int releaseNumber = 1;
    public final int motionBufferSize = 256;
    public final short maximumRequestLength = -1;
    public final byte screensCount = 1;
    public final byte imageByteOrder = 0;
    public final byte bitmapFormatBitOrder = 0;
    public final byte bitmapFormatScanlineUnit = 32;
    public final byte bitmapFormatScanlinePad = 32;
    public final byte minKeycode = 8;
    public final byte maxKeycode = -1;
    public final int unused1 = 0;
    public final String vendorName = X11ImplementationVendor.VENDOR_NAME;
    public final short vendorNameLength = (short) this.vendorName.length();

    public ServerInfo(XServer xServer, IdInterval idInterval) {
        int i = 0;
        this.resourceIdBase = idInterval.getIdBase();
        this.resourceIdMask = idInterval.getIdMask();
        this.roots = new Screen[]{new Screen(xServer)};
        Collection<ImageFormat> supportedImageFormats = xServer.getDrawablesManager().getSupportedImageFormats();
        this.pixmapFormatsCount = (byte) supportedImageFormats.size();
        this.pixmapFormats = new PixmapFormat[this.pixmapFormatsCount];
        for (ImageFormat imageFormat : supportedImageFormats) {
            this.pixmapFormats[i] = new PixmapFormat((byte) imageFormat.getDepth(), (byte) imageFormat.getBitsPerPixel(), (byte) imageFormat.getScanlinePad());
            i++;
        }
        this.additionalDataLength = (short) (8 + (2 * this.pixmapFormatsCount) + ProtoHelpers.calculateLengthInWords(ProtoHelpers.roundUpLength4(this.vendorNameLength)) + ProtoHelpers.calculateLengthInWords(PODWriter.getOnWireLength(this.roots)));
    }
}
