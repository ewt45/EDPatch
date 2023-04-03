package com.eltechs.axs.requestHandlers.mitshm;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Signed;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadAccess;
import com.eltechs.axs.proto.input.errors.BadImplementation;
import com.eltechs.axs.proto.input.errors.BadMatch;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.requestHandlers.IncomingImageFormat;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.Painter;
import com.eltechs.axs.xserver.ShmSegment;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MITShmRequests extends HandlerObjectBase {
    public static final byte SHARED_PIXMAPS_AVAILABLE = 0;

    public MITShmRequests(XServer xServer) {
        super(xServer);
    }

    @RequestHandler(opcode = 0)
    public void QueryVersion(XResponse xResponse) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.mitshm.MITShmRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putShort((short) 1);
                byteBuffer.putShort((short) 1);
                byteBuffer.putShort((short) 0);
                byteBuffer.putShort((short) 0);
                byteBuffer.put((byte) 0);
            }
        });
    }

    @Locks({"SHM_SEGMENTS_MANAGER"})
    @RequestHandler(opcode = 1)
    public void Attach(@RequestParam @NewXId int i, @RequestParam int i2, @RequestParam boolean z, @RequestParam byte b, @RequestParam short s) throws XProtocolError {
        this.xServer.getShmSegmentsManager().attachSegment(i, i2, !z);
    }

    @Locks({"SHM_SEGMENTS_MANAGER"})
    @RequestHandler(opcode = 2)
    public void Detach(@RequestParam ShmSegment shmSegment) throws XProtocolError {
        this.xServer.getShmSegmentsManager().detachSegment(shmSegment);
    }

    @Locks({"SHM_SEGMENTS_MANAGER", "DRAWABLES_MANAGER", "WINDOWS_MANAGER", "PIXMAPS_MANAGER", "GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 3)
    public void PutImage(@RequestParam Drawable drawable, @RequestParam GraphicsContext graphicsContext, @RequestParam @Unsigned @Width(2) int i, @RequestParam @Unsigned @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4, @RequestParam @Unsigned @Width(2) int i5, @RequestParam @Unsigned @Width(2) int i6, @RequestParam @Signed @Width(2) int i7, @RequestParam @Signed @Width(2) int i8, @RequestParam byte b, @RequestParam IncomingImageFormat incomingImageFormat, @RequestParam boolean z, @RequestParam byte b2, @RequestParam ShmSegment shmSegment, @RequestParam int i9) throws XProtocolError {
        if (z) {
            Assert.notImplementedYet();
        }
        if (graphicsContext.getFunction() != PixelCompositionRule.COPY && incomingImageFormat != IncomingImageFormat.Z_PIXMAP) {
            Assert.notImplementedYet("Drawing with GC::Function values other than COPY is not supported yet.");
        }
        Painter painter = drawable.getPainter();
        switch (incomingImageFormat) {
            case BITMAP:
                if (b != 1) {
                    throw new BadMatch();
                }
                Assert.notImplementedYet();
                return;
            case XY_PIXMAP:
                if (drawable.getVisual().getDepth() != b) {
                    throw new BadMatch();
                }
                Assert.notImplementedYet();
                return;
            case Z_PIXMAP:
                if (drawable.getVisual().getDepth() != b) {
                    throw new BadMatch();
                }
                painter.drawZPixmap(graphicsContext.getFunction(), b, i7, i8, i3, i4, i5, i6, shmSegment.getContent(), i, i2);
                return;
            default:
                Assert.state(false, String.format("Unknown IncomingImageFormat %s.", incomingImageFormat));
                return;
        }
    }

    @Locks({"DRAWABLES_MANAGER", "PIXMAPS_MANAGER"})
    @RequestHandler(opcode = 4)
    public void GetImage(XResponse xResponse, @RequestParam Drawable drawable, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam @Unsigned @Width(2) int i4, @RequestParam int i5, @RequestParam IncomingImageFormat incomingImageFormat, @RequestParam byte b, @RequestParam byte b2, @RequestParam byte b3, @RequestParam ShmSegment shmSegment, @RequestParam int i6) throws XProtocolError, IOException {
        if (incomingImageFormat == IncomingImageFormat.BITMAP) {
            throw new BadValue(incomingImageFormat.ordinal());
        }
        Rectangle rectangle = new Rectangle(i, i2, i3, i4);
        final int i7 ;
        boolean z = this.xServer.getPixmapsManager().getPixmap(drawable.getId()) != null;
        byte depth = (byte) drawable.getVisual().getDepth();
        if (z) {
            i7=0;
            if (!new Rectangle(0, 0, drawable.getWidth(), drawable.getHeight()).containsInnerRectangle(rectangle)) {
                throw new BadMatch();
            }
        } else {
            i7 = drawable.getVisual().getId();
        }
        if (!shmSegment.isWritable()) {
            throw new BadAccess();
        }
        Assert.notImplementedYet();
        xResponse.sendSimpleSuccessReply(depth, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.mitshm.MITShmRequests.2
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(i7);
                byteBuffer.putInt(0);
            }
        });
    }

    @Locks({"PIXMAPS_MANAGER", "DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 5)
    public void CreatePixmap(@RequestParam @NewXId int i, @RequestParam Drawable drawable, @RequestParam @Unsigned @Width(2) int i2, @RequestParam @Unsigned @Width(2) int i3, @RequestParam byte b, @RequestParam byte b2, @RequestParam byte b3, @RequestParam byte b4, @RequestParam ShmSegment shmSegment, @RequestParam int i4) throws XProtocolError {
        if (!shmSegment.isWritable()) {
            throw new BadAccess();
        }
        Assert.state(true);
        throw new BadImplementation();
    }
}
