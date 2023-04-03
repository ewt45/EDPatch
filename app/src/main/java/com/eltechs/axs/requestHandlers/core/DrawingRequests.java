package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Signed;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadMatch;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.requestHandlers.IncomingImageFormat;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class DrawingRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    public enum CoordinateMode {
        ORIGIN,
        PREVIOUS
    }

    @Locks({"DRAWABLES_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 67)
    public void PolyRectangle(@RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam ByteBuffer byteBuffer) {
    }

    @Locks({"DRAWABLES_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 66)
    public void PolySegment(@RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam ByteBuffer byteBuffer) {
    }

    public DrawingRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"DRAWABLES_MANAGER", "WINDOWS_MANAGER", "PIXMAPS_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 72)
    public void PutImage(@OOBParam @RequestParam IncomingImageFormat incomingImageFormat, @RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam @Unsigned @Width(2) int i, @RequestParam @Unsigned @Width(2) int i2, @RequestParam @Signed @Width(2) int i3, @RequestParam @Signed @Width(2) int i4, @RequestParam byte b, @RequestParam byte b2, @RequestParam short s, @RequestParam ByteBuffer byteBuffer) throws XProtocolError {
        Painter painter = drawable.getPainter();
        if (graphicsContext.getFunction() != PixelCompositionRule.COPY && incomingImageFormat != IncomingImageFormat.Z_PIXMAP) {
            Assert.notImplementedYet("Drawing with GC::Function values other than COPY is not supported yet.");
        }
        switch (incomingImageFormat) {
            case BITMAP:
                if (b != 0) {
                    Assert.notImplementedYet("PutImage.leftPad != 0 not implemented yet");
                }
                if (b2 != 1) {
                    throw new BadMatch();
                }
                painter.drawBitmap(i3, i4, i, i2, byteBuffer);
                return;
            case XY_PIXMAP:
                if (drawable.getVisual().getDepth() == b2) {
                    return;
                }
                throw new BadMatch();
            case Z_PIXMAP:
                if (drawable.getVisual().getDepth() != b2 || b != 0) {
                    throw new BadMatch();
                }
                painter.drawZPixmap(graphicsContext.getFunction(), b2, i3, i4, 0, 0, i, i2, byteBuffer, i, i2);
                return;
            default:
                Assert.state(false, String.format("Unknown IncomingImageFormat %s.", incomingImageFormat));
                return;
        }
    }

    @Locks({"DRAWABLES_MANAGER", "PIXMAPS_MANAGER"})
    @RequestHandler(opcode = 73)
    public void GetImage(XResponse xResponse, @OOBParam @RequestParam IncomingImageFormat incomingImageFormat, @RequestParam Drawable drawable, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4, @RequestParam int i5) throws XProtocolError, IOException {
        final int id;
        if (incomingImageFormat == IncomingImageFormat.BITMAP) {
            throw new BadValue(incomingImageFormat.ordinal());
        }
        Rectangle rectangle = new Rectangle(i, i2, i3, i4);
        if (this.xServer.getPixmapsManager().getPixmap(drawable.getId()) != null) {
            if (!new Rectangle(0, 0, drawable.getWidth(), drawable.getHeight()).containsInnerRectangle(rectangle)) {
                throw new BadMatch();
            }
            id = 0;
        } else {
            id = drawable.getVisual().getId();
        }
        Painter painter = drawable.getPainter();
        final byte[] bArr ;
        switch (incomingImageFormat) {
            case XY_PIXMAP:
                bArr=null;
                Assert.notImplementedYet("Reading data as XY Pixmap is unimplemented yet.");
                break;
            case Z_PIXMAP:
                bArr = painter.getZPixmap(i, i2, i3, i4);
                break;
            default:
                bArr=null;
                Assert.state(false, String.format("Unknown IncomingImageFormat %s.", incomingImageFormat));
                break;
        }
        xResponse.sendSuccessReplyWithPayload((byte) drawable.getVisual().getDepth(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.DrawingRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(id);
            }
        }, bArr.length, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.DrawingRequests.2
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.put(bArr);
            }
        });
    }

    @Locks({"DRAWABLES_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 62)
    public void CopyArea(@RequestParam Drawable drawable, @RequestParam Drawable drawable2, @RequestParam GraphicsContext graphicsContext, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2, @RequestParam @Signed @Width(2) int i3, @RequestParam @Signed @Width(2) int i4, @RequestParam @Unsigned @Width(2) int i5, @RequestParam @Unsigned @Width(2) int i6) {
        drawable2.getPainter().copyArea(graphicsContext, drawable, i, i2, i3, i4, i5, i6);
    }

    @Locks({"WINDOWS_MANAGER"})
    @RequestHandler(opcode = 61)
    public void ClearArea(@OOBParam @RequestParam Boolean bool, @RequestParam Window window, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4) {
        if (i3 == 0 && i4 == 0) {
            return;
        }
        Assert.notImplementedYet("ClearArea is not implemented");
    }

    @Locks({"DRAWABLES_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 70)
    public void PolyFillRectangle(@RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam ByteBuffer byteBuffer) {
        if (graphicsContext.getFunction() != PixelCompositionRule.COPY) {
            return;
        }
        drawable.getPainter().drawFilledRectangles(byteBuffer, graphicsContext.getBackground());
    }

    @Locks({"DRAWABLES_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 65)
    public void PolyLine(@OOBParam @RequestParam CoordinateMode coordinateMode, @RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam ByteBuffer byteBuffer) {
        if (coordinateMode == CoordinateMode.ORIGIN && graphicsContext.getLineWidth() == 1 && graphicsContext.getFunction() == PixelCompositionRule.COPY) {
            drawable.getPainter().drawLines(byteBuffer, graphicsContext.getForeground(), graphicsContext.getLineWidth());
        }
    }
}
