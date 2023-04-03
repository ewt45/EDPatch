package com.eltechs.axs.xserver.client;

import com.eltechs.axs.proto.output.XEventSender;
import com.eltechs.axs.xconnectors.XOutputStream;
import com.eltechs.axs.xconnectors.XResponse;
import com.eltechs.axs.xserver.Colormap;
import com.eltechs.axs.xserver.ColormapLifecycleListener;
import com.eltechs.axs.xserver.Cursor;
import com.eltechs.axs.xserver.CursorLifecycleListener;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.GraphicsContext;
import com.eltechs.axs.xserver.GraphicsContextLifecycleListener;
import com.eltechs.axs.xserver.IdInterval;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.Pixmap;
import com.eltechs.axs.xserver.PixmapLifecycleListener;
import com.eltechs.axs.xserver.ShmSegment;
import com.eltechs.axs.xserver.ShmSegmentLifecycleListener;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowLifecycleAdapter;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.masks.Mask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class XClient {
    private boolean authenticated;
    private final ColormapLifecycleListener colormapLifecycleListener;
    private final CursorLifecycleListener cursorLifecycleListener;
    private final GraphicsContextLifecycleListener graphicsContextLifecycleListener;
    private final IdInterval idInterval;
    private final XOutputStream outputStream;
    private final PixmapLifecycleListener pixmapLifecycleListener;
    private final ShmSegmentLifecycleListener shmSegmentLifecycleListener;
    private final WindowLifecycleListener windowLifecycleListener;
    private final XServer xServer;
    private int sequenceNumber = 0;
    private final Map<Window, XClientWindowListener> windowListeners = new HashMap();
    private final Collection<Window> clientWindows = new ArrayList();
    private final Collection<Pixmap> clientPixmaps = new ArrayList();
    private final Collection<GraphicsContext> clientGraphicsContexts = new ArrayList();
    private final Collection<Cursor> clientCursors = new ArrayList();
    private final Collection<Colormap> clientColormaps = new ArrayList();
    private final Collection<ShmSegment> clientShmSegments = new ArrayList();

    public XClient(XServer xServer, XOutputStream xOutputStream) {
        this.outputStream = xOutputStream;
        this.xServer = xServer;
        LocksManager.XLock lockAll = xServer.getLocksManager().lockAll();
        try {
            this.windowLifecycleListener = new WindowLifecycleAdapter() { // from class: com.eltechs.axs.xserver.client.XClient.1
                @Override // com.eltechs.axs.xserver.WindowLifecycleAdapter, com.eltechs.axs.xserver.WindowLifecycleListener
                public void windowDestroyed(Window window) {
                    XClient.this.windowListeners.remove(window);
                    XClient.this.clientWindows.remove(window);
                }
            };
            this.xServer.getWindowsManager().addWindowLifecycleListener(this.windowLifecycleListener);
            this.pixmapLifecycleListener = new PixmapLifecycleListener() { // from class: com.eltechs.axs.xserver.client.XClient.2
                @Override // com.eltechs.axs.xserver.PixmapLifecycleListener
                public void pixmapCreated(Pixmap pixmap) {
                }

                @Override // com.eltechs.axs.xserver.PixmapLifecycleListener
                public void pixmapFreed(Pixmap pixmap) {
                    XClient.this.clientPixmaps.remove(pixmap);
                }
            };
            this.xServer.getPixmapsManager().addPixmapLifecycleListener(this.pixmapLifecycleListener);
            this.cursorLifecycleListener = new CursorLifecycleListener() { // from class: com.eltechs.axs.xserver.client.XClient.3
                @Override // com.eltechs.axs.xserver.CursorLifecycleListener
                public void cursorCreated(Cursor cursor) {
                }

                @Override // com.eltechs.axs.xserver.CursorLifecycleListener
                public void cursorFreed(Cursor cursor) {
                    XClient.this.clientCursors.remove(cursor);
                }
            };
            this.xServer.getCursorsManager().addCursorLifecycleListener(this.cursorLifecycleListener);
            this.graphicsContextLifecycleListener = new GraphicsContextLifecycleListener() { // from class: com.eltechs.axs.xserver.client.XClient.4
                @Override // com.eltechs.axs.xserver.GraphicsContextLifecycleListener
                public void graphicsContextCreated(GraphicsContext graphicsContext) {
                }

                @Override // com.eltechs.axs.xserver.GraphicsContextLifecycleListener
                public void graphicsContextFreed(GraphicsContext graphicsContext) {
                    XClient.this.clientGraphicsContexts.remove(graphicsContext);
                }
            };
            this.xServer.getGraphicsContextsManager().addGraphicsContextsLifecycleListener(this.graphicsContextLifecycleListener);
            this.colormapLifecycleListener = new ColormapLifecycleListener() { // from class: com.eltechs.axs.xserver.client.XClient.5
                @Override // com.eltechs.axs.xserver.ColormapLifecycleListener
                public void colormapCreated(Colormap colormap) {
                }

                @Override // com.eltechs.axs.xserver.ColormapLifecycleListener
                public void colormapFreed(Colormap colormap) {
                    XClient.this.clientColormaps.remove(colormap);
                }
            };
            this.xServer.getColormapsManager().addColormapLifecycleListener(this.colormapLifecycleListener);
            this.shmSegmentLifecycleListener = new ShmSegmentLifecycleListener() { // from class: com.eltechs.axs.xserver.client.XClient.6
                @Override // com.eltechs.axs.xserver.ShmSegmentLifecycleListener
                public void segmentAttached(ShmSegment shmSegment) {
                }

                @Override // com.eltechs.axs.xserver.ShmSegmentLifecycleListener
                public void segmentDetached(ShmSegment shmSegment) {
                    XClient.this.clientShmSegments.remove(shmSegment);
                }
            };
            this.xServer.getShmSegmentsManager().addShmSegmentLifecycleListener(this.shmSegmentLifecycleListener);
            this.idInterval = this.xServer.getIdIntervalsManager().getInterval();
            if (lockAll != null) {
                lockAll.close();
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (lockAll != null) {
                    if (th != null) {
                        try {
                            lockAll.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        lockAll.close();
                    }
                }
                throw th2;
            }
        }
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean z) {
        this.authenticated = z;
    }

    public int getLastSequenceNumber() {
        return this.sequenceNumber;
    }

    public int generateSequenceNumber() {
        int i = this.sequenceNumber + 1;
        this.sequenceNumber = i;
        return i;
    }

    public IdInterval getIdInterval() {
        return this.idInterval;
    }

    public void installEventListener(Window window, Mask<EventName> mask) {
        XClientWindowListener xClientWindowListener = this.windowListeners.get(window);
        if (xClientWindowListener != null) {
            window.getEventListenersList().removeListener(xClientWindowListener);
        }
        if (mask.isEmpty()) {
            return;
        }
        XClientWindowListener xClientWindowListener2 = new XClientWindowListener(this, mask);
        this.windowListeners.put(window, xClientWindowListener2);
        window.getEventListenersList().addListener(xClientWindowListener2);
    }

    public boolean isInterestedIn(Window window, EventName eventName) {
        XClientWindowListener xClientWindowListener = this.windowListeners.get(window);
        if (xClientWindowListener != null) {
            return xClientWindowListener.isInterestedIn(eventName);
        }
        return false;
    }

    public Mask<EventName> getEventMask(Window window) {
        XClientWindowListener xClientWindowListener = this.windowListeners.get(window);
        if (xClientWindowListener != null) {
            return xClientWindowListener.getMask();
        }
        return Mask.emptyMask(EventName.class);
    }

    public XEventSender createEventSender() {
        return new XEventSender(new XResponse(this.sequenceNumber, this.outputStream));
    }

    public void registerAsOwnerOfWindow(Window window) {
        this.clientWindows.add(window);
    }

    public void registerAsOwnerOfPixmap(Pixmap pixmap) {
        this.clientPixmaps.add(pixmap);
    }

    public void registerAsOwnerOfGraphicsContext(GraphicsContext graphicsContext) {
        this.clientGraphicsContexts.add(graphicsContext);
    }

    public void registerAsOwnerOfCursor(Cursor cursor) {
        this.clientCursors.add(cursor);
    }

    public void registerAsOwnerOfColormap(Colormap colormap) {
        this.clientColormaps.add(colormap);
    }

    public void freeAssociatedResources() {
        LocksManager.XLock lockAll = this.xServer.getLocksManager().lockAll();
        while (!this.clientWindows.isEmpty()) {
            try {
                this.xServer.getWindowsManager().destroyWindow(this.clientWindows.iterator().next());
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (lockAll != null) {
                        if (th != null) {
                            try {
                                lockAll.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            lockAll.close();
                        }
                    }
                    throw th2;
                }
            }
        }
        while (!this.clientPixmaps.isEmpty()) {
            this.xServer.getPixmapsManager().freePixmap(this.clientPixmaps.iterator().next());
        }
        while (!this.clientGraphicsContexts.isEmpty()) {
            this.xServer.getGraphicsContextsManager().removeGraphicsContext(this.clientGraphicsContexts.iterator().next());
        }
        while (!this.clientCursors.isEmpty()) {
            this.xServer.getCursorsManager().freeCursor(this.clientCursors.iterator().next());
        }
        while (!this.clientColormaps.isEmpty()) {
            this.xServer.getColormapsManager().freeColormap(this.clientColormaps.iterator().next());
        }
        while (!this.clientShmSegments.isEmpty()) {
            this.xServer.getShmSegmentsManager().detachSegment(this.clientShmSegments.iterator().next());
        }
        for (Map.Entry<Window, XClientWindowListener> entry : this.windowListeners.entrySet()) {
            entry.getKey().getEventListenersList().removeListener(entry.getValue());
        }
        this.xServer.getWindowsManager().removeWindowLifecycleListener(this.windowLifecycleListener);
        this.xServer.getPixmapsManager().removePixmapLifecycleListener(this.pixmapLifecycleListener);
        this.xServer.getGraphicsContextsManager().removeGraphicsContextLifecycleListener(this.graphicsContextLifecycleListener);
        this.xServer.getCursorsManager().removeCursorLifecycleListener(this.cursorLifecycleListener);
        this.xServer.getColormapsManager().removeColormapLifecycleListener(this.colormapLifecycleListener);
        this.xServer.getShmSegmentsManager().removeShmSegmentLifecycleListener(this.shmSegmentLifecycleListener);
        this.xServer.getIdIntervalsManager().freeInterval(this.idInterval);
        if (lockAll != null) {
            lockAll.close();
        }
    }
}