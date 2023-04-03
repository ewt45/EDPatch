package com.eltechs.axs.requestHandlers.glx;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.proto.output.PODWriter;
import com.eltechs.axs.proto.output.replies.VisualConfig;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.GLXConstants;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

/* loaded from: classes.dex */
public class GLXRequests extends HandlerObjectBase {
    private static final Charset latin1 = Charset.forName("latin1");

    @RequestHandler(opcode = 35)
    public void SetClientInfo2ARB(@RequestParam int i, @RequestParam int i2, @RequestParam int i3, @RequestParam int i4, @RequestParam int i5, @RequestParam ByteBuffer byteBuffer) throws XProtocolError {
    }

    public GLXRequests(XServer xServer) {
        super(xServer);
    }

    private static void CheckGLXScreenValid(int i) throws XProtocolError {
        if (i != 0) {
            throw new BadValue(i);
        }
    }

    @RequestHandler(opcode = 7)
    public void QueryVersion(XResponse xResponse, @RequestParam int i, @RequestParam int i2) throws IOException {
        xResponse.sendSimpleSuccessReply((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(1);
                byteBuffer.putInt(4);
            }
        });
    }

    @Locks({"RENDERING_ENGINE"})
    @RequestHandler(opcode = 19)
    public void QueryServerString(XResponse xResponse, @RequestParam int i, @RequestParam int i2) throws IOException, XProtocolError {
        final String vendor;
        CheckGLXScreenValid(i);
        switch (i2) {
            case 1:
                vendor = this.xServer.getRenderingEngine().getVendor();
                break;
            case 2:
                vendor = "1.4";
                break;
            case 3:
                vendor = this.xServer.getRenderingEngine().getGLXExtensionsList();
                break;
            default:
                throw new BadValue(i2);
        }
        final int length = vendor.length() + 1;
        xResponse.sendSuccessReplyWithPayload((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.2
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(0);
                byteBuffer.putInt(length);
            }
        }, length, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.3
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.put(vendor.getBytes(GLXRequests.latin1));
            }
        });
    }

    @Locks({"DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 14)
    public void GetVisualConfigs(XResponse xResponse, @RequestParam int i) throws IOException, XProtocolError {
        CheckGLXScreenValid(i);
        final Collection<Visual> supportedVisuals = this.xServer.getDrawablesManager().getSupportedVisuals();
        final int onWireLength = PODWriter.getOnWireLength(new VisualConfig(supportedVisuals.iterator().next()));
        xResponse.sendSuccessReplyWithPayload((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.4
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(supportedVisuals.size());
                byteBuffer.putInt(onWireLength / 4);
            }
        }, onWireLength * supportedVisuals.size(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.5
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                for (Visual visual : supportedVisuals) {
                    PODWriter.write(byteBuffer, new VisualConfig(visual));
                }
            }
        });
    }

    @Locks({"DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 21)
    public void GetFBConfigs(XResponse xResponse, @RequestParam int i) throws IOException, XProtocolError {
        CheckGLXScreenValid(i);
        final int size = this.xServer.getDrawablesManager().getSupportedVisuals().size();
        xResponse.sendSuccessReplyWithPayload((byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.6
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                byteBuffer.putInt(size);
                byteBuffer.putInt(44);
            }
        }, size * 44 * 2 * 4, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.glx.GLXRequests.7
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                IntBuffer asIntBuffer = byteBuffer.asIntBuffer();
                for (Visual visual : GLXRequests.this.xServer.getDrawablesManager().getSupportedVisuals()) {
                    asIntBuffer.put(new int[]{GLXConstants.GLX_VISUAL_ID, visual.getId(), GLXConstants.GLX_FBCONFIG_ID, -1, GLXConstants.GLX_X_RENDERABLE, -1, 4, 1, GLXConstants.GLX_RENDER_TYPE, 1, 5, 1, 6, 0, 2, visual.getBitsPerRgbValue(), 3, 0, 7, 0, 8, Integer.bitCount(visual.getRedMask()), 9, Integer.bitCount(visual.getGreenMask()), 10, Integer.bitCount(visual.getBlueMask()), 11, visual.getDepth() > visual.getBitsPerRgbValue() ? visual.getDepth() - visual.getBitsPerRgbValue() : 0, 14, 0, 15, 0, 16, 0, 17, 0, 12, visual.getDepth(), 13, 0, 34, GLXConstants.GLX_TRUE_COLOR, 32, 32768, 35, 32768, 37, -1, 38, -1, 39, -1, 40, -1, 36, -1, GLXConstants.GLX_SWAP_METHOD_OML, 0, GLXConstants.GLX_SAMPLES_SGIS, 0, 100000, 0, GLXConstants.GLX_VISUAL_SELECT_GROUP_SGIX, 0, GLXConstants.GLX_DRAWABLE_TYPE, 7, GLXConstants.GLX_BIND_TO_TEXTURE_RGB_EXT, -1, GLXConstants.GLX_BIND_TO_TEXTURE_RGBA_EXT, -1, GLXConstants.GLX_BIND_TO_MIPMAP_TEXTURE_EXT, -1, GLXConstants.GLX_BIND_TO_TEXTURE_TARGETS_EXT, -1, GLXConstants.GLX_Y_INVERTED_EXT, -1, GLXConstants.GLX_MAX_PBUFFER_WIDTH, 0, GLXConstants.GLX_MAX_PBUFFER_HEIGHT, 0, GLXConstants.GLX_MAX_PBUFFER_PIXELS, 0, GLXConstants.GLX_OPTIMAL_PBUFFER_WIDTH_SGIX, 0, GLXConstants.GLX_OPTIMAL_PBUFFER_HEIGHT_SGIX, 0, 0, 0});
                }
                byteBuffer.position(byteBuffer.position() + (asIntBuffer.position() * 4));
            }
        });
    }

    @Locks({"DRAWABLES_MANAGER"})
    @RequestHandler(opcode = 3)
    public void CreateContext(@RequestParam int i, @RequestParam Visual visual, @RequestParam int i2, @RequestParam int i3, @RequestParam boolean z, @RequestParam byte b, @RequestParam short s) throws XProtocolError {
        CheckGLXScreenValid(i2);
    }
}
