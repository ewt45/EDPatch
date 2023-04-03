package com.eltechs.axs.requestHandlers.core;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.Optional;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.GraphicsContextsManager;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.graphicsContext.ArcMode;
import com.eltechs.axs.xserver.graphicsContext.FillRule;
import com.eltechs.axs.xserver.graphicsContext.FillStyle;
import com.eltechs.axs.xserver.graphicsContext.GraphicsContextParts;
import com.eltechs.axs.xserver.graphicsContext.JoinStyle;
import com.eltechs.axs.xserver.graphicsContext.LineStyle;
import com.eltechs.axs.xserver.graphicsContext.PixelCompositionRule;
import com.eltechs.axs.xserver.graphicsContext.SubwindowMode;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class GraphicsContextManipulationRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    public enum ClipRectanglesOrdering {
        UNSORTED,
        Y_SORTED,
        YX_SORTED,
        YX_BANDED
    }

    @Locks({"GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 59)
    public void SetClipRectangles(@OOBParam @RequestParam ClipRectanglesOrdering clipRectanglesOrdering, @RequestParam GraphicsContext graphicsContext, @RequestParam short s, @RequestParam short s2, @RequestParam ByteBuffer byteBuffer) {
    }

    @Locks({"GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 58)
    public void SetDashes(@RequestParam GraphicsContext graphicsContext, @RequestParam short s, @RequestParam short s2, @RequestParam ByteBuffer byteBuffer) {
    }

    public GraphicsContextManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"GRAPHICS_CONTEXTS_MANAGER", "DRAWABLES_MANAGER", "PIXMAPS_MANAGER"})
    @RequestHandler(opcode = 55)
    public void CreateGC(XClient xClient, XResponse xResponse, @RequestParam @NewXId int i, @RequestParam Drawable drawable, @RequestParam @ParamName("mask") Mask<GraphicsContextParts> mask, @RequestParam @Optional(bit = "FUNCTION") @Width(4) PixelCompositionRule pixelCompositionRule, @RequestParam @Optional(bit = "PLANE_MASK") Integer num, @RequestParam @Optional(bit = "FOREGROUND") Integer num2, @RequestParam @Optional(bit = "BACKGROUND") Integer num3, @RequestParam @Optional(bit = "LINE_WIDTH") Integer num4, @RequestParam @Optional(bit = "LINE_STYLE") @Width(4) LineStyle lineStyle, @RequestParam @Optional(bit = "CAP_STYLE") Integer num5, @RequestParam @Optional(bit = "JOIN_STYLE") @Width(4) JoinStyle joinStyle, @RequestParam @Optional(bit = "FILL_STYLE") @Width(4) FillStyle fillStyle, @RequestParam @Optional(bit = "FILL_RULE") @Width(4) FillRule fillRule, @RequestParam @Optional(bit = "TILE") Pixmap pixmap, @RequestParam @Optional(bit = "STIPPLE") Pixmap pixmap2, @RequestParam @Optional(bit = "TILE_STIPPLE_X_ORIGIN") Integer num6, @RequestParam @Optional(bit = "TILE_STIPPLE_Y_ORIGIN") Integer num7, @RequestParam @Optional(bit = "FONT") Integer num8, @RequestParam @Optional(bit = "SUBWINDOW_MODE") @Width(4) SubwindowMode subwindowMode, @RequestParam @Optional(bit = "GRAPHICS_EXPOSURES") @Width(4) Boolean bool, @RequestParam @Optional(bit = "CLIP_X_ORIGIN") Integer num9, @RequestParam @Optional(bit = "CLIP_Y_ORIGIN") Integer num10, @SpecialNullValue(0) @RequestParam @Optional(bit = "CLIP_MASK") Pixmap pixmap3, @RequestParam @Optional(bit = "DASH_OFFSET") Integer num11, @RequestParam @Optional(bit = "DASHES") Integer num12, @RequestParam @Optional(bit = "ARC_MODE") @Width(4) ArcMode arcMode) throws IOException, XProtocolError {
        GraphicsContextsManager graphicsContextsManager = this.xServer.getGraphicsContextsManager();
        GraphicsContext createGraphicsContext = graphicsContextsManager.createGraphicsContext(i, drawable);
        if (createGraphicsContext == null) {
            throw new BadIdChoice(i);
        }
        xClient.registerAsOwnerOfGraphicsContext(createGraphicsContext);
        graphicsContextsManager.updateGraphicsContext(createGraphicsContext, mask, pixelCompositionRule, num, num2, num3, num4, lineStyle, num5, joinStyle, fillStyle, fillRule, pixmap, pixmap2, num6, num7, num8, subwindowMode, bool, num9, num10, pixmap3, num11, num12, arcMode);
    }

    @Locks({"GRAPHICS_CONTEXTS_MANAGER", "PIXMAPS_MANAGER"})
    @RequestHandler(opcode = 56)
    public void ChangeGC(@RequestParam GraphicsContext graphicsContext, @RequestParam @ParamName("mask") Mask<GraphicsContextParts> mask, @RequestParam @Optional(bit = "FUNCTION") @Width(4) PixelCompositionRule pixelCompositionRule, @RequestParam @Optional(bit = "PLANE_MASK") Integer num, @RequestParam @Optional(bit = "FOREGROUND") Integer num2, @RequestParam @Optional(bit = "BACKGROUND") Integer num3, @RequestParam @Optional(bit = "LINE_WIDTH") Integer num4, @RequestParam @Optional(bit = "LINE_STYLE") @Width(4) LineStyle lineStyle, @RequestParam @Optional(bit = "CAP_STYLE") Integer num5, @RequestParam @Optional(bit = "JOIN_STYLE") @Width(4) JoinStyle joinStyle, @RequestParam @Optional(bit = "FILL_STYLE") @Width(4) FillStyle fillStyle, @RequestParam @Optional(bit = "FILL_RULE") @Width(4) FillRule fillRule, @RequestParam @Optional(bit = "TILE") Pixmap pixmap, @RequestParam @Optional(bit = "STIPPLE") Pixmap pixmap2, @RequestParam @Optional(bit = "TILE_STIPPLE_X_ORIGIN") Integer num6, @RequestParam @Optional(bit = "TILE_STIPPLE_Y_ORIGIN") Integer num7, @RequestParam @Optional(bit = "FONT") Integer num8, @RequestParam @Optional(bit = "SUBWINDOW_MODE") @Width(4) SubwindowMode subwindowMode, @RequestParam @Optional(bit = "GRAPHICS_EXPOSURES") @Width(4) Boolean bool, @RequestParam @Optional(bit = "CLIP_X_ORIGIN") Integer num9, @RequestParam @Optional(bit = "CLIP_Y_ORIGIN") Integer num10, @SpecialNullValue(0) @RequestParam @Optional(bit = "CLIP_MASK") Pixmap pixmap3, @RequestParam @Optional(bit = "DASH_OFFSET") Integer num11, @RequestParam @Optional(bit = "DASHES") Integer num12, @RequestParam @Optional(bit = "ARC_MODE") @Width(4) ArcMode arcMode) throws IOException, XProtocolError {
        this.xServer.getGraphicsContextsManager().updateGraphicsContext(graphicsContext, mask, pixelCompositionRule, num, num2, num3, num4, lineStyle, num5, joinStyle, fillStyle, fillRule, pixmap, pixmap2, num6, num7, num8, subwindowMode, bool, num9, num10, pixmap3, num11, num12, arcMode);
    }

    @Locks({"GRAPHICS_CONTEXTS_MANAGER"})
    @RequestHandler(opcode = 60)
    public void FreeGC(XResponse xResponse, @RequestParam GraphicsContext graphicsContext) throws IOException {
        this.xServer.getGraphicsContextsManager().removeGraphicsContext(graphicsContext);
    }
}
