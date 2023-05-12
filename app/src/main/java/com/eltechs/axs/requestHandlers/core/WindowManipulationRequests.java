package com.eltechs.axs.requestHandlers.core;

import android.util.Log;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.ArithHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.annotations.Locks;
import com.eltechs.axs.proto.input.annotations.NewXId;
import com.eltechs.axs.proto.input.annotations.OOBParam;
import com.eltechs.axs.proto.input.annotations.Optional;
import com.eltechs.axs.proto.input.annotations.ParamName;
import com.eltechs.axs.proto.input.annotations.RequestHandler;
import com.eltechs.axs.proto.input.annotations.RequestParam;
import com.eltechs.axs.proto.input.annotations.Signed;
import com.eltechs.axs.proto.input.annotations.SpecialNullValue;
import com.eltechs.axs.proto.input.annotations.Unsigned;
import com.eltechs.axs.proto.input.annotations.Width;
import com.eltechs.axs.proto.input.errors.BadAccess;
import com.eltechs.axs.proto.input.errors.BadIdChoice;
import com.eltechs.axs.proto.input.errors.BadLength;
import com.eltechs.axs.proto.input.errors.BadMatch;
import com.eltechs.axs.proto.input.errors.BadValue;
import com.eltechs.axs.proto.input.impl.ProtoHelpers;
import com.eltechs.axs.requestHandlers.HandlerObjectBase;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.BitGravity;
import com.eltechs.axs.xserver.ConfigureWindowParts;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.StackMode;
import com.eltechs.axs.xserver.WinGravity;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowAttributeNames;
import com.eltechs.axs.xserver.WindowAttributes;
import com.eltechs.axs.xserver.WindowPropertiesManager;
import com.eltechs.axs.xserver.WindowProperty;
import com.eltechs.axs.xserver.WindowsManager;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.events.ConfigureNotify;
import com.eltechs.axs.xserver.events.ConfigureRequest;
import com.eltechs.axs.xserver.events.CreateNotify;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class WindowManipulationRequests extends HandlerObjectBase {

    /* loaded from: classes.dex */
    public enum WindowClass {
        COPY_FROM_PARENT,
        INPUT_OUTPUT,
        INPUT_ONLY
    }

    public WindowManipulationRequests(XServer xServer) {
        super(xServer);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES", "KEYBOARD_MODEL_MANAGER", "ATOMS_MANAGER"})
    @RequestHandler(opcode = 8)
    public void MapWindow(@RequestParam Window window) {
        this.xServer.getWindowsManager().mapWindow(window);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES", "KEYBOARD_MODEL_MANAGER"})
    @RequestHandler(opcode = 9)
    public void MapSubwindows(@RequestParam Window window) {
        this.xServer.getWindowsManager().mapSubwindows(window);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES", "KEYBOARD_MODEL_MANAGER"})
    @RequestHandler(opcode = 10)
    public void UnmapWindow(@RequestParam Window window) {
        this.xServer.getWindowsManager().unmapWindow(window);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES", "KEYBOARD_MODEL_MANAGER"})
    @RequestHandler(opcode = 11)
    public void UnmapSubwindows(@RequestParam Window window) {
        this.xServer.getWindowsManager().unmapSubwindows(window);
    }

    @Locks({"WINDOWS_MANAGER", "DRAWABLES_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES"})
    @RequestHandler(opcode = 4)
    public void DestroyWindow(@RequestParam Window window) {
        this.xServer.getWindowsManager().destroyWindow(window);
    }

    @Locks({"WINDOWS_MANAGER", "DRAWABLES_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES"})
    @RequestHandler(opcode = 5)
    public void DestroySubwindows(@RequestParam Window window) {
        this.xServer.getWindowsManager().destroySubwindows(window);
    }

    @Locks({"WINDOWS_MANAGER", "FOCUS_MANAGER", "INPUT_DEVICES"})
    @RequestHandler(opcode = 12)
    public void ConfigureWindow(@RequestParam Window window, @RequestParam @ParamName("mask") @Width(2) Mask<ConfigureWindowParts> mask, @RequestParam short s, @RequestParam @Optional(bit = "X") @Width(4) Integer num, @RequestParam @Optional(bit = "Y") @Width(4) Integer num2, @RequestParam @Optional(bit = "WIDTH") @Width(4) Integer num3, @RequestParam @Optional(bit = "HEIGHT") @Width(4) Integer num4, @RequestParam @Optional(bit = "BORDER_WIDTH") @Width(4) Short sh, @RequestParam @Optional(bit = "SIBLING") Window window2, @RequestParam @Optional(bit = "STACK_MODE") @Width(4) StackMode stackMode) {
        int borderWidth;
        if (mask.isEmpty()) {
            return;
        }
        WindowsManager windowsManager = this.xServer.getWindowsManager();
        Window parent = window.getParent();
        Rectangle boundingRectangle = window.getBoundingRectangle();
        Integer valueOf = num == null ? Integer.valueOf(boundingRectangle.x) : num;
        Integer valueOf2 = num2 == null ? Integer.valueOf(boundingRectangle.y) : num2;
        Integer valueOf3 = num3 == null ? Integer.valueOf(boundingRectangle.width) : num3;
        Integer valueOf4 = num4 == null ? Integer.valueOf(boundingRectangle.height) : num4;
        if (parent.getEventListenersList().isListenerInstalledForEvent(EventName.SUBSTRUCTURE_REDIRECT) && !window.getWindowAttributes().isOverrideRedirect()) {
            if (sh != null) {
                borderWidth = sh.shortValue();
            } else {
                borderWidth = window.getWindowAttributes().getBorderWidth();
            }
            parent.getEventListenersList().sendEventForEventName(new ConfigureRequest(parent, window, window.getParent().getChildrenList().getPrevSibling(window), valueOf.intValue(), valueOf2.intValue(), valueOf3.intValue(), valueOf4.intValue(), borderWidth, stackMode, mask), EventName.SUBSTRUCTURE_REDIRECT);
            return;
        }
        if (mask.isSet(ConfigureWindowParts.X) || mask.isSet(ConfigureWindowParts.Y) || mask.isSet(ConfigureWindowParts.WIDTH) || mask.isSet(ConfigureWindowParts.HEIGHT)) {
            windowsManager.changeRelativeWindowGeometry(window, valueOf.intValue(), valueOf2.intValue(), valueOf3.intValue(), valueOf4.intValue());
        }
        if (sh != null) {
            window.getWindowAttributes().setBorderWidth(ArithHelpers.extendAsUnsigned(sh.shortValue()));
        }
        if (mask.isSet(ConfigureWindowParts.STACK_MODE)) {
            windowsManager.changeWindowZOrder(window, window2, stackMode);
        }
        Window prevSibling = parent.getChildrenList().getPrevSibling(window);
        Rectangle boundingRectangle2 = window.getBoundingRectangle();
        window.getEventListenersList().sendEventForEventName(new ConfigureNotify(window, window, prevSibling, boundingRectangle2.x, boundingRectangle2.y, boundingRectangle2.width, boundingRectangle2.height, window.getWindowAttributes().getBorderWidth(), window.getWindowAttributes().isOverrideRedirect()), EventName.STRUCTURE_NOTIFY);
        parent.getEventListenersList().sendEventForEventName(new ConfigureNotify(parent, window, prevSibling, boundingRectangle2.x, boundingRectangle2.y, boundingRectangle2.width, boundingRectangle2.height, window.getWindowAttributes().getBorderWidth(), window.getWindowAttributes().isOverrideRedirect()), EventName.SUBSTRUCTURE_NOTIFY);
    }

    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER"})
    @RequestHandler(opcode = 20)
    public void GetProperty(XResponse xResponse, @OOBParam @RequestParam boolean z, @RequestParam Window window, @RequestParam Atom atom, @SpecialNullValue(0) @RequestParam Atom atom2, @RequestParam int i, @RequestParam int i2) throws IOException, XProtocolError {
        WindowProperty<?> property = window.getPropertiesManager().getProperty(atom);
        if (property == null) {
            xResponse.sendSimpleSuccessReply((byte) 0, 0, 0, 0);
            return;
        }
        Atom type = property.getType();
        byte formatValue = property.getFormat().getFormatValue();
        if (atom2 != null && !type.equals(atom2)) {
            xResponse.sendSimpleSuccessReply(formatValue, type.getId(), property.getSizeInBytes(), 0);
            return;
        }
        Object obj = new byte[12];
        int sizeInBytes = property.getSizeInBytes();
        int i3 = i * 4;
        long min = Math.min(sizeInBytes - i3, ArithHelpers.extendAsUnsigned(4 * i2));
        if (min < 0) {
            throw new BadValue(i);
        }
        int i4 = (int) min;
        int i5 = sizeInBytes - (i3 + i4);
        if (formatValue == 8) {
            byte[] bArr = new byte[ProtoHelpers.calculatePad(i4) + i4];
            System.arraycopy((byte[]) property.getValues(), i3, bArr, 0, i4);
            xResponse.sendSimpleSuccessReply(formatValue, Integer.valueOf(type.getId()), Integer.valueOf(i5), Integer.valueOf(bArr.length), obj, bArr);
        } else if (formatValue == 16) {
            int i6 = i4 / 2;
            short[] sArr = new short[i6];
            System.arraycopy((short[]) property.getValues(), i3 / 2, sArr, 0, i6);
            xResponse.sendSimpleSuccessReply(formatValue, Integer.valueOf(type.getId()), Integer.valueOf(i5), Integer.valueOf(sArr.length), obj, sArr, new byte[ProtoHelpers.calculatePad(i4)]);
        } else if (formatValue != 32) {
            Assert.state(false, String.format("Strange format value (%d) in GetProperty method.", Byte.valueOf(formatValue)));
        } else {
            int i7 = i4 / 4;
            int[] iArr = new int[i7];
            System.arraycopy((int[]) property.getValues(), i3 / 4, iArr, 0, i7);
            xResponse.sendSimpleSuccessReply(formatValue, Integer.valueOf(type.getId()), Integer.valueOf(i5), Integer.valueOf(iArr.length), obj, iArr);
        }
        if (!z || i5 != 0) {
            return;
        }
        window.getPropertiesManager().deleteProperty(atom);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER"})
    @RequestHandler(opcode = 18)
    public void ChangeProperty(@OOBParam @RequestParam WindowPropertiesManager.PropertyModification propertyModification, @RequestParam Window window, @RequestParam Atom atom, @RequestParam Atom atom2, @RequestParam byte b, @RequestParam byte b2, @RequestParam byte b3, @RequestParam byte b4, @RequestParam int i, @RequestParam ByteBuffer byteBuffer) throws XProtocolError {
        WindowProperty.Format obj;
        short[] sArr=null;
        byte[] bArr=null;
        int[] iArr=null;
        if (b != 8) {
            int i2 = 0;
            if (b != 16) {
                if (b == 32) {
                    if (4 * i > byteBuffer.limit()) {
                        throw new BadLength();
                    }
                    iArr = new int[i];
                    while (i2 < i) {
                        iArr[i2] = byteBuffer.getInt();
                        i2++;
                    }
                    obj = WindowProperty.ARRAY_OF_INTS;
                } else {
                    throw new BadValue(b);
                }
            } else if (2 * i > byteBuffer.limit()) {
                throw new BadLength();
            } else {
                sArr = new short[i];
                while (i2 < i) {
                    sArr[i2] = byteBuffer.getShort();
                    i2++;
                }
                obj = WindowProperty.ARRAY_OF_SHORTS;
            }
        } else if (i > byteBuffer.limit()) {
            throw new BadLength();
        } else {
            bArr = new byte[i];
            byteBuffer.get(bArr);
            obj = WindowProperty.ARRAY_OF_BYTES;

        }
        if (!window.getPropertiesManager().modifyProperty(atom, atom2, obj, propertyModification, sArr!=null?sArr: bArr!=null?bArr:iArr)) {
            throw new BadMatch();
        }
    }

    @Locks({"WINDOWS_MANAGER", "ATOMS_MANAGER"})
    @RequestHandler(opcode = 19)
    public void DeleteProperty(@RequestParam Window window, @RequestParam Atom atom) throws XProtocolError {
        window.getPropertiesManager().deleteProperty(atom);
    }

    @Locks({"WINDOWS_MANAGER", "DRAWABLES_MANAGER", "INPUT_DEVICES", "COLORMAPS_MANAGER", "CURSORS_MANAGER", "FOCUS_MANAGER"})
    @RequestHandler(opcode = 1)
    public void CreateWindow(XClient xClient, @OOBParam @RequestParam byte b, @RequestParam @NewXId int i, @RequestParam Window window, @RequestParam @Signed @Width(2) int x, @RequestParam @Signed @Width(2) int y, @RequestParam @Unsigned @Width(2) int width, @RequestParam @Unsigned @Width(2) int height, @RequestParam @Unsigned @Width(2) int i6, @RequestParam @Width(2) WindowClass windowClass, @SpecialNullValue(0) @RequestParam Visual visual, @RequestParam @ParamName("mask") Mask<WindowAttributeNames> eventMask, @RequestParam @Optional(bit = "BACKGROUND_PIXMAP") Integer num, @RequestParam @Optional(bit = "BACKGROUND_PIXEL") Integer num2, @RequestParam @Optional(bit = "BORDER_PIXMAP") Integer num3, @RequestParam @Optional(bit = "BORDER_PIXEL") Integer num4, @RequestParam @Optional(bit = "BIT_GRAVITY") @Width(4) BitGravity bitGravity, @RequestParam @Optional(bit = "WIN_GRAVITY") @Width(4) WinGravity winGravity, @RequestParam @Optional(bit = "BACKING_STORE") @Width(4) WindowAttributes.BackingStore backingStore, @RequestParam @Optional(bit = "BACKING_PLANES") Integer backingPlanes, @RequestParam @Optional(bit = "BACKING_PIXEL") Integer backingPixel, @RequestParam @Optional(bit = "OVERRIDE_REDIRECT") @Width(4) Boolean overrideRedirect, @RequestParam @Optional(bit = "SAVE_UNDER") @Width(4) Boolean saveUnder, @RequestParam @Optional(bit = "EVENT_MASK") Mask<EventName> mask2, @RequestParam @Optional(bit = "DO_NOT_PROPAGATE_MASK") Mask<EventName> doNotPropagateMask, @RequestParam @Optional(bit = "COLORMAP") Integer num7, @SpecialNullValue(0) @RequestParam @Optional(bit = "CURSOR") Cursor cursor) throws XProtocolError {
        Log.e("TAG", "CreateWindow: ");
        byte depth;
        boolean z;
        Mask<EventName> emptyMask = mask2 == null ? Mask.emptyMask(EventName.class) : mask2;
        switch (windowClass) {
            case COPY_FROM_PARENT:
                boolean isInputOutput = window.isInputOutput();
                depth = (b != 0 || !window.isInputOutput()) ? b : (byte) window.getActiveBackingStore().getVisual().getDepth();
                z = isInputOutput;
                break;
            case INPUT_OUTPUT:
                if (!window.isInputOutput()) {
                    throw new BadMatch();
                }
                depth = b == 0 ? (byte) window.getActiveBackingStore().getVisual().getDepth() : b;
                z = true;
                break;
            case INPUT_ONLY:
                depth = b;
                z = false;
                break;
            default:
                Assert.state(false, String.format("Unsupported value %s of WindowClass.", windowClass));
                return;
        }
        if (z) {
            Visual visual3 = visual == null ? window.getActiveBackingStore().getVisual() : visual;
            if (depth != ((byte) visual3.getDepth())) {
                throw new BadMatch();
            }
            visual = visual3;
        }
        Window createWindow = xServer.getWindowsManager().createWindow(i, window, x, y, width, height, visual, z, xClient);
        if (createWindow == null) {
            throw new BadIdChoice(i);
        }
        xClient.installEventListener(createWindow, emptyMask);
        WindowAttributes windowAttributes = createWindow.getWindowAttributes();
        windowAttributes.setBorderWidth(i6);
        windowAttributes.update(eventMask, num3, num4, bitGravity, winGravity, backingStore, backingPlanes, backingPixel, overrideRedirect, saveUnder, doNotPropagateMask, num7, cursor);
        eventMask.isSet(WindowAttributeNames.BACKGROUND_PIXMAP);
        if (eventMask.isSet(WindowAttributeNames.BACKGROUND_PIXEL)) {
            createWindow.getActiveBackingStore().getPainter().fillWithColor(num2);
        }
        xClient.registerAsOwnerOfWindow(createWindow);
        window.getEventListenersList().sendEventForEventName(new CreateNotify(window, createWindow), EventName.SUBSTRUCTURE_NOTIFY);
    }

    @Locks({"WINDOWS_MANAGER", "COLORMAPS_MANAGER", "CURSORS_MANAGER"})
    @RequestHandler(opcode = 2)
    public void ChangeWindowAttributes(XClient xClient, @RequestParam Window window, @RequestParam @ParamName("mask") Mask<WindowAttributeNames> eventMask, @RequestParam @Optional(bit = "BACKGROUND_PIXMAP") Integer num, @RequestParam @Optional(bit = "BACKGROUND_PIXEL") Integer num2, @RequestParam @Optional(bit = "BORDER_PIXMAP") Integer num3, @RequestParam @Optional(bit = "BORDER_PIXEL") Integer num4, @RequestParam @Optional(bit = "BIT_GRAVITY") @Width(4) BitGravity bitGravity, @RequestParam @Optional(bit = "WIN_GRAVITY") @Width(4) WinGravity winGravity, @RequestParam @Optional(bit = "BACKING_STORE") @Width(4) WindowAttributes.BackingStore backingStore, @RequestParam @Optional(bit = "BACKING_PLANES") Integer backingPlanes, @RequestParam @Optional(bit = "BACKING_PIXEL") Integer backingPixel, @RequestParam @Optional(bit = "OVERRIDE_REDIRECT") @Width(4) Boolean overrideRedirect, @RequestParam @Optional(bit = "SAVE_UNDER") @Width(4) Boolean saveUnder, @RequestParam @Optional(bit = "EVENT_MASK") Mask<EventName> mask2, @RequestParam @Optional(bit = "DO_NOT_PROPAGATE_MASK") Mask<EventName> doNotPropagateMask, @RequestParam @Optional(bit = "COLORMAP") Integer num7, @SpecialNullValue(0) @RequestParam @Optional(bit = "CURSOR") Cursor cursor) throws XProtocolError {
        if (mask2 != null) {
            if ((mask2.isSet(EventName.SUBSTRUCTURE_REDIRECT) && willBeInConflict(xClient, window, EventName.SUBSTRUCTURE_REDIRECT)) || ((mask2.isSet(EventName.RESIZE_REDIRECT) && willBeInConflict(xClient, window, EventName.RESIZE_REDIRECT)) || (mask2.isSet(EventName.BUTTON_PRESS) && willBeInConflict(xClient, window, EventName.BUTTON_PRESS)))) {
                throw new BadAccess();
            }
            xClient.installEventListener(window, mask2);
        }
        window.getWindowAttributes().update(eventMask, num3, num4, bitGravity, winGravity, backingStore, backingPlanes, backingPixel, overrideRedirect, saveUnder, doNotPropagateMask, num7, cursor);
        eventMask.isSet(WindowAttributeNames.BACKGROUND_PIXMAP);
        if (eventMask.isSet(WindowAttributeNames.BACKGROUND_PIXEL)) {
            window.getActiveBackingStore().getPainter().fillWithColor(num2.intValue());
        }
    }

    private boolean willBeInConflict(XClient xClient, Window window, EventName eventName) {
        return window.getEventListenersList().isListenerInstalledForEvent(eventName) && !xClient.isInterestedIn(window, eventName);
    }

    @Locks({"WINDOWS_MANAGER"})
    @RequestHandler(opcode = 3)
    public void GetWindowAttributes(XClient xClient, XResponse xResponse, @RequestParam Window window) throws IOException {
        WindowAttributes windowAttributes = window.getWindowAttributes();
        xResponse.sendSimpleSuccessReply((byte) windowAttributes.getBackingStore().ordinal(), Integer.valueOf(window.isInputOutput() ? window.getActiveBackingStore().getVisual().getId() : 0), Short.valueOf((short) windowAttributes.getWindowClass().ordinal()), Byte.valueOf((byte) windowAttributes.getBitGravity().ordinal()), Byte.valueOf((byte) windowAttributes.getWinGravity().ordinal()), Integer.valueOf(windowAttributes.getBackingPlanes()), Integer.valueOf(windowAttributes.getBackingPixel()), Boolean.valueOf(windowAttributes.isSaveUnder()), true, Byte.valueOf((byte) WindowHelpers.getWindowMapState(window).ordinal()), Boolean.valueOf(windowAttributes.isOverrideRedirect()), 0, Integer.valueOf(window.getEventListenersList().calculateAllEventsMask().getRawMask()), Integer.valueOf(xClient.getEventMask(window).getRawMask()), Short.valueOf((short) windowAttributes.getDoNotPropagateMask().getRawMask()));
    }

    @Locks({"WINDOWS_MANAGER"})
    @RequestHandler(opcode = 40)
    public void TranslateCoordinates(XResponse xResponse, @RequestParam Window window, @RequestParam Window window2, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2) throws IOException, XProtocolError {
        Point convertWindowCoordsToRoot = WindowHelpers.convertWindowCoordsToRoot(window, i, i2);
        final Point convertRootCoordsToWindow = WindowHelpers.convertRootCoordsToWindow(window2, convertWindowCoordsToRoot.x, convertWindowCoordsToRoot.y);
        final Window directMappedSubWindowByCoords = WindowHelpers.getDirectMappedSubWindowByCoords(window2, convertWindowCoordsToRoot.x, convertWindowCoordsToRoot.y);
        xResponse.sendSimpleSuccessReply((byte) 1, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.requestHandlers.core.WindowManipulationRequests.1
            @Override // com.eltechs.axs.xconnectors.BufferFiller
            public void write(ByteBuffer byteBuffer) {
                if (directMappedSubWindowByCoords != null) {
                    byteBuffer.putInt(directMappedSubWindowByCoords.getId());
                } else {
                    byteBuffer.putInt(0);
                }
                byteBuffer.putShort((short) convertRootCoordsToWindow.x);
                byteBuffer.putShort((short) convertRootCoordsToWindow.y);
            }
        });
    }

    @Locks({"WINDOWS_MANAGER"})
    @RequestHandler(opcode = 7)
    public void ReparentWindow(XResponse xResponse, @RequestParam Window window, @RequestParam Window window2, @RequestParam @Signed @Width(2) int i, @RequestParam @Signed @Width(2) int i2) {
        Window parent = window.getParent();
        if (parent != null) {
            parent.getChildrenList().remove(window);
        }
        window.setParent(null);
        window2.getChildrenList().add(window);
    }


}
