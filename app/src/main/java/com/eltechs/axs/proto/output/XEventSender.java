package com.eltechs.axs.proto.output;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowAttributes;
import com.eltechs.axs.xserver.events.ButtonPress;
import com.eltechs.axs.xserver.events.ButtonRelease;
import com.eltechs.axs.xserver.events.ConfigureNotify;
import com.eltechs.axs.xserver.events.ConfigureRequest;
import com.eltechs.axs.xserver.events.CreateNotify;
import com.eltechs.axs.xserver.events.DestroyNotify;
import com.eltechs.axs.xserver.events.EnterNotify;
import com.eltechs.axs.xserver.events.Event;
import com.eltechs.axs.xserver.events.Expose;
import com.eltechs.axs.xserver.events.InputDeviceEvent;
import com.eltechs.axs.xserver.events.KeyPress;
import com.eltechs.axs.xserver.events.KeyRelease;
import com.eltechs.axs.xserver.events.LeaveNotify;
import com.eltechs.axs.xserver.events.MapNotify;
import com.eltechs.axs.xserver.events.MapRequest;
import com.eltechs.axs.xserver.events.MappingNotify;
import com.eltechs.axs.xserver.events.MotionNotify;
import com.eltechs.axs.xserver.events.PointerWindowEvent;
import com.eltechs.axs.xserver.events.PropertyNotify;
import com.eltechs.axs.xserver.events.ResizeRequest;
import com.eltechs.axs.xserver.events.SelectionClear;
import com.eltechs.axs.xserver.events.SelectionNotify;
import com.eltechs.axs.xserver.events.SelectionRequest;
import com.eltechs.axs.xserver.events.UnmapNotify;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class XEventSender {
    private static final Map<Class<? extends Event>, EventWriter<?>> eventWriters = new HashMap();
    private final XResponse response;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface EventWriter<E extends Event> {
        void sendEvent(XResponse xResponse, E e) throws IOException;
    }

    static {
        eventWriters.put(MapNotify.class, new EventWriter<MapNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.1
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final MapNotify mapNotify) throws IOException {
                xResponse.sendEvent((byte) mapNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.1.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(mapNotify.getOriginatedAt().getId());
                        byteBuffer.putInt(mapNotify.getMappedWindow().getId());
                        byteBuffer.put(mapNotify.getMappedWindow().getWindowAttributes().isOverrideRedirect() ? (byte) 1 : (byte) 0);
                    }
                });
            }
        });
        eventWriters.put(UnmapNotify.class, new EventWriter<UnmapNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.2
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final UnmapNotify unmapNotify) throws IOException {
                xResponse.sendEvent((byte) unmapNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.2.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(unmapNotify.getOriginatedAt().getId());
                        byteBuffer.putInt(unmapNotify.getUnmappedWindow().getId());
                        byteBuffer.put(unmapNotify.isFromConfigure() ? (byte) 1 : (byte) 0);
                    }
                });
            }
        });
        eventWriters.put(CreateNotify.class, new EventWriter<CreateNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.3
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final CreateNotify createNotify) throws IOException {
                xResponse.sendEvent((byte) createNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.3.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        Window window = createNotify.getWindow();
                        Rectangle boundingRectangle = window.getBoundingRectangle();
                        WindowAttributes windowAttributes = window.getWindowAttributes();
                        byteBuffer.putInt(createNotify.getParent().getId());
                        byteBuffer.putInt(window.getId());
                        byteBuffer.putShort((short) boundingRectangle.x);
                        byteBuffer.putShort((short) boundingRectangle.y);
                        byteBuffer.putShort((short) boundingRectangle.width);
                        byteBuffer.putShort((short) boundingRectangle.height);
                        byteBuffer.putShort((short) windowAttributes.getBorderWidth());
                        byteBuffer.put(windowAttributes.isOverrideRedirect() ? (byte) 1 : (byte) 0);
                    }
                });
            }
        });
        eventWriters.put(DestroyNotify.class, new EventWriter<DestroyNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.4
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final DestroyNotify destroyNotify) throws IOException {
                xResponse.sendEvent((byte) destroyNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.4.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(destroyNotify.getOriginatedAt().getId());
                        byteBuffer.putInt(destroyNotify.getDeletedWindow().getId());
                    }
                });
            }
        });
        eventWriters.put(PropertyNotify.class, new EventWriter<PropertyNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.5
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final PropertyNotify propertyNotify) throws IOException {
                xResponse.sendEvent((byte) propertyNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.5.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(propertyNotify.getWindow().getId());
                        byteBuffer.putInt(propertyNotify.getName().getId());
                        byteBuffer.putInt(propertyNotify.getTimestamp());
                        byteBuffer.put(propertyNotify.isDelete() ? (byte) 1 : (byte) 0);
                    }
                });
            }
        });
        eventWriters.put(Expose.class, new EventWriter<Expose>() { // from class: com.eltechs.axs.proto.output.XEventSender.6
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final Expose expose) throws IOException {
                xResponse.sendEvent((byte) expose.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.6.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(expose.getWindow().getId());
                        byteBuffer.putShort((short) expose.getX());
                        byteBuffer.putShort((short) expose.getY());
                        byteBuffer.putShort((short) expose.getWidth());
                        byteBuffer.putShort((short) expose.getHeight());
                        byteBuffer.putShort((short) 0);
                    }
                });
            }
        });
        eventWriters.put(ResizeRequest.class, new EventWriter<ResizeRequest>() { // from class: com.eltechs.axs.proto.output.XEventSender.7
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final ResizeRequest resizeRequest) throws IOException {
                xResponse.sendEvent((byte) resizeRequest.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.7.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(resizeRequest.getWindow().getId());
                        byteBuffer.putShort((short) resizeRequest.getWidth());
                        byteBuffer.putShort((short) resizeRequest.getHeight());
                    }
                });
            }
        });
        eventWriters.put(MapRequest.class, new EventWriter<MapRequest>() { // from class: com.eltechs.axs.proto.output.XEventSender.8
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final MapRequest mapRequest) throws IOException {
                xResponse.sendEvent((byte) mapRequest.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.8.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(mapRequest.getParentWindow().getId());
                        byteBuffer.putInt(mapRequest.getMappedWindow().getId());
                    }
                });
            }
        });
        eventWriters.put(ConfigureNotify.class, new EventWriter<ConfigureNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.9
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final ConfigureNotify configureNotify) throws IOException {
                xResponse.sendEvent((byte) configureNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.9.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(configureNotify.getEvent().getId());
                        byteBuffer.putInt(configureNotify.getWindow().getId());
                        Window aboveSibling = configureNotify.getAboveSibling();
                        byteBuffer.putInt(aboveSibling == null ? 0 : aboveSibling.getId());
                        byteBuffer.putShort((short) configureNotify.getX());
                        byteBuffer.putShort((short) configureNotify.getY());
                        byteBuffer.putShort((short) configureNotify.getWidth());
                        byteBuffer.putShort((short) configureNotify.getHeight());
                        byteBuffer.putShort((short) configureNotify.getBorderWidth());
                        byteBuffer.putShort(configureNotify.isOverrideRedirect() ? (short) 1 : (short) 0);
                    }
                });
            }
        });
        eventWriters.put(ConfigureRequest.class, new EventWriter<ConfigureRequest>() { // from class: com.eltechs.axs.proto.output.XEventSender.10
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final ConfigureRequest configureRequest) throws IOException {
                xResponse.sendEvent((byte) configureRequest.getId(), (byte) configureRequest.getStackMode().ordinal(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.10.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(configureRequest.getParent().getId());
                        byteBuffer.putInt(configureRequest.getWindow().getId());
                        Window sibling = configureRequest.getSibling();
                        byteBuffer.putInt(sibling == null ? 0 : sibling.getId());
                        byteBuffer.putShort((short) configureRequest.getX());
                        byteBuffer.putShort((short) configureRequest.getY());
                        byteBuffer.putShort((short) configureRequest.getWidth());
                        byteBuffer.putShort((short) configureRequest.getHeight());
                        byteBuffer.putShort((short) configureRequest.getBorderWidth());
                        byteBuffer.putShort((short) configureRequest.getParts().getRawMask());
                    }
                });
            }
        });
        EventWriter<InputDeviceEvent> eventWriter = new EventWriter<InputDeviceEvent>() { // from class: com.eltechs.axs.proto.output.XEventSender.11
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final InputDeviceEvent inputDeviceEvent) throws IOException {
                xResponse.sendEvent((byte) inputDeviceEvent.getId(), inputDeviceEvent.getDetail(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.11.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        Window child = inputDeviceEvent.getChild();
                        byteBuffer.putInt(inputDeviceEvent.getTimestamp());
                        byteBuffer.putInt(inputDeviceEvent.getRoot().getId());
                        byteBuffer.putInt(inputDeviceEvent.getEvent().getId());
                        byteBuffer.putInt(child != null ? child.getId() : 0);
                        byteBuffer.putShort(inputDeviceEvent.getRootX());
                        byteBuffer.putShort(inputDeviceEvent.getRootY());
                        byteBuffer.putShort(inputDeviceEvent.getEventX());
                        byteBuffer.putShort(inputDeviceEvent.getEventY());
                        byteBuffer.putShort((short) inputDeviceEvent.getState().getRawMask());
                        byteBuffer.put((byte) 1);
                    }
                });
            }
        };
        eventWriters.put(MotionNotify.class, eventWriter);
        eventWriters.put(ButtonPress.class, eventWriter);
        eventWriters.put(ButtonRelease.class, eventWriter);
        eventWriters.put(KeyPress.class, eventWriter);
        eventWriters.put(KeyRelease.class, eventWriter);
        EventWriter<PointerWindowEvent> eventWriter2 = new EventWriter<PointerWindowEvent>() { // from class: com.eltechs.axs.proto.output.XEventSender.12
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final PointerWindowEvent pointerWindowEvent) throws IOException {
                xResponse.sendEvent((byte) pointerWindowEvent.getId(), (byte) pointerWindowEvent.getDetail().ordinal(), new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.12.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        Window child = pointerWindowEvent.getChild();
                        byteBuffer.putInt(pointerWindowEvent.getTimestamp());
                        byteBuffer.putInt(pointerWindowEvent.getRoot().getId());
                        byteBuffer.putInt(pointerWindowEvent.getEvent().getId());
                        byteBuffer.putInt(child != null ? child.getId() : 0);
                        byteBuffer.putShort(pointerWindowEvent.getRootX());
                        byteBuffer.putShort(pointerWindowEvent.getRootY());
                        byteBuffer.putShort(pointerWindowEvent.getEventX());
                        byteBuffer.putShort(pointerWindowEvent.getEventY());
                        byteBuffer.putShort((short) pointerWindowEvent.getState().getRawMask());
                        byteBuffer.put((byte) pointerWindowEvent.getMode().ordinal());
                        byteBuffer.put(pointerWindowEvent.getSameScreenAndFocus());
                    }
                });
            }
        };
        eventWriters.put(EnterNotify.class, eventWriter2);
        eventWriters.put(LeaveNotify.class, eventWriter2);
        eventWriters.put(SelectionClear.class, new EventWriter<SelectionClear>() { // from class: com.eltechs.axs.proto.output.XEventSender.13
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final SelectionClear selectionClear) throws IOException {
                xResponse.sendEvent((byte) selectionClear.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.13.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.putInt(selectionClear.getTimestamp());
                        byteBuffer.putInt(selectionClear.getOwner().getId());
                        byteBuffer.putInt(selectionClear.getSelection().getId());
                    }
                });
            }
        });
        eventWriters.put(SelectionRequest.class, new EventWriter<SelectionRequest>() { // from class: com.eltechs.axs.proto.output.XEventSender.14
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final SelectionRequest selectionRequest) throws IOException {
                xResponse.sendEvent((byte) selectionRequest.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.14.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        Atom property = selectionRequest.getProperty();
                        byteBuffer.putInt(selectionRequest.getTimestamp());
                        byteBuffer.putInt(selectionRequest.getOwner().getId());
                        byteBuffer.putInt(selectionRequest.getRequestor().getId());
                        byteBuffer.putInt(selectionRequest.getSelection().getId());
                        byteBuffer.putInt(selectionRequest.getTarget().getId());
                        byteBuffer.putInt(property != null ? property.getId() : 0);
                    }
                });
            }
        });
        eventWriters.put(SelectionNotify.class, new EventWriter<SelectionNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.15
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final SelectionNotify selectionNotify) throws IOException {
                xResponse.sendEvent((byte) selectionNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.15.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        Atom property = selectionNotify.getProperty();
                        byteBuffer.putInt(selectionNotify.getTimestamp());
                        byteBuffer.putInt(selectionNotify.getRequestor().getId());
                        byteBuffer.putInt(selectionNotify.getSelection().getId());
                        byteBuffer.putInt(selectionNotify.getTarget().getId());
                        byteBuffer.putInt(property != null ? property.getId() : 0);
                    }
                });
            }
        });
        eventWriters.put(MappingNotify.class, new EventWriter<MappingNotify>() { // from class: com.eltechs.axs.proto.output.XEventSender.16
            @Override // com.eltechs.axs.proto.output.XEventSender.EventWriter
            public void sendEvent(XResponse xResponse, final MappingNotify mappingNotify) throws IOException {
                xResponse.sendEvent((byte) mappingNotify.getId(), (byte) 0, new XResponse.ResponseDataWriter() { // from class: com.eltechs.axs.proto.output.XEventSender.16.1
                    @Override // com.eltechs.axs.xconnectors.BufferFiller
                    public void write(ByteBuffer byteBuffer) {
                        byteBuffer.put((byte) mappingNotify.getRequest().ordinal());
                        byteBuffer.put((byte) mappingNotify.getFirstKeycode());
                        byteBuffer.put((byte) mappingNotify.getCount());
                    }
                });
            }
        });
    }

    public XEventSender(XResponse xResponse) {
        this.response = xResponse;
    }

    public void sendEvent(Event event) {
        EventWriter eventWriter = eventWriters.get(event.getClass());
        if (eventWriter == null) {
            Assert.notImplementedYet();
        }
        try {
            eventWriter.sendEvent(this.response, event);
        } catch (IOException unused) {
        }
    }
}
