package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.DrawablesManager;
import com.eltechs.axs.xserver.EventName;
import com.eltechs.axs.xserver.PlacedDrawable;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.StackMode;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowAttributes;
import com.eltechs.axs.xserver.WindowChangeListener;
import com.eltechs.axs.xserver.WindowChangeListenersList;
import com.eltechs.axs.xserver.WindowContentModificationListener;
import com.eltechs.axs.xserver.WindowContentModificationListenersList;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.WindowLifecycleListenersList;
import com.eltechs.axs.xserver.WindowListenersList;
import com.eltechs.axs.xserver.WindowsManager;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.events.DestroyNotify;
import com.eltechs.axs.xserver.events.Expose;
import com.eltechs.axs.xserver.events.MapNotify;
import com.eltechs.axs.xserver.events.MapRequest;
import com.eltechs.axs.xserver.events.ResizeRequest;
import com.eltechs.axs.xserver.events.UnmapNotify;
import com.eltechs.axs.xserver.helpers.WindowHelpers;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class WindowsManagerImpl implements WindowsManager {
    private final DrawablesManager drawablesManager;
    private final Window rootWindow;
    private final Map<Integer, Window> windows = new HashMap();
    private final WindowContentModificationListenersList windowContentModificationListenersList = new WindowContentModificationListenersList();
    private final WindowLifecycleListenersList windowLifecycleListenersList = new WindowLifecycleListenersList();
    private final WindowChangeListenersList windowChangeListenersList = new WindowChangeListenersList();

    public WindowsManagerImpl(ScreenInfo screenInfo, DrawablesManager drawablesManager) {
        this.drawablesManager = drawablesManager;
        int generateId = SmallIdsGenerator.generateId();
        Drawable createDrawable = drawablesManager.createDrawable(generateId, null, screenInfo.widthInPixels, screenInfo.heightInPixels, drawablesManager.getPreferredVisual());
        this.rootWindow = new WindowImpl(generateId, createDrawable, null, this.windowContentModificationListenersList, this.windowChangeListenersList, null);
        this.rootWindow.setBoundingRectangle(new Rectangle(0, 0, screenInfo.widthInPixels, screenInfo.heightInPixels));
        this.rootWindow.getWindowAttributes().setMapped(true);
        this.windows.put(createDrawable.getId(), this.rootWindow);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public Window getRootWindow() {
        return this.rootWindow;
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public Window getWindow(int i) {
        return this.windows.get(i);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public Window createWindow(int i, Window window, int x, int y, int width, int height, Visual visual, boolean z, XClient xClient) {
        Drawable frontBuffer;
        if (this.windows.containsKey(i)) {
            return null;
        }
        if (z) {
            Drawable createDrawable = this.drawablesManager.createDrawable(i, WindowHelpers.getRootWindowOf(window), width, height, visual);
            if (createDrawable == null) {
                return null;
            }
            frontBuffer = createDrawable;
        } else {
            frontBuffer = null;
        }
        WindowImpl windowImpl = new WindowImpl(i, frontBuffer, null, this.windowContentModificationListenersList, this.windowChangeListenersList, xClient);
        this.windows.put(i, windowImpl);
        window.getChildrenList().add(windowImpl);
        windowImpl.setBoundingRectangle(new Rectangle(x, y, width, height));
        this.windowLifecycleListenersList.sendWindowCreated(windowImpl);
        return windowImpl;
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void destroyWindow(Window window) {
        if (getRootWindow().getId() == window.getId()) {
            return;
        }
        unmapWindow(window);
        deleteSubtreeAndWindow(window);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void destroySubwindows(Window window) {
        unmapSubwindows(window);
        for (Window window2 : window.getChildrenBottomToTop()) {
            deleteSubtreeAndWindow(window2);
        }
    }

    private void deleteSubtreeAndWindow(Window window) {
        Assert.isFalse(getRootWindow().getId() == window.getId(), "Root window can't be destroyed.");
        ArrayList<Window> arrayList = new ArrayList();
        for (Window window2 : window.getChildrenBottomToTop()) {
            arrayList.add(window2);
        }
        for (Window window3 : arrayList) {
            deleteSubtreeAndWindow(window3);
        }
        Window parent = window.getParent();
        WindowListenersList eventListenersList = window.getEventListenersList();
        WindowListenersList eventListenersList2 = parent.getEventListenersList();
        eventListenersList.sendEventForEventName(new DestroyNotify(window, window), EventName.STRUCTURE_NOTIFY);
        eventListenersList2.sendEventForEventName(new DestroyNotify(parent, window), EventName.SUBSTRUCTURE_NOTIFY);
        this.windows.remove(Integer.valueOf(window.getId()));
        if (window.isInputOutput()) {
            this.drawablesManager.removeDrawable(window.getFrontBuffer());
        }
        this.windowLifecycleListenersList.sendWindowDestroyed(window);
        parent.getChildrenList().remove(window);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void mapWindow(Window window) {
        WindowAttributes windowAttributes = window.getWindowAttributes();
        if (windowAttributes.isMapped()) {
            return;
        }
        Window parent = window.getParent();
        if (parent.getEventListenersList().isListenerInstalledForEvent(EventName.SUBSTRUCTURE_REDIRECT) && !window.getWindowAttributes().isOverrideRedirect()) {
            parent.getEventListenersList().sendEventForEventName(new MapRequest(window.getParent(), window), EventName.SUBSTRUCTURE_REDIRECT);
            return;
        }
        WindowListenersList eventListenersList = window.getEventListenersList();
        WindowListenersList eventListenersList2 = parent.getEventListenersList();
        windowAttributes.setMapped(true);
        eventListenersList.sendEventForEventName(new MapNotify(window, window), EventName.STRUCTURE_NOTIFY);
        eventListenersList2.sendEventForEventName(new MapNotify(parent, window), EventName.SUBSTRUCTURE_NOTIFY);
        eventListenersList.sendEventForEventName(new Expose(window), EventName.EXPOSURE);
        this.windowLifecycleListenersList.sendWindowMapped(window);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void mapSubwindows(Window window) {
        for (Window window2 : window.getChildrenTopToBottom()) {
            mapWindow(window2);
        }
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void unmapWindow(Window window) {
        WindowAttributes windowAttributes = window.getWindowAttributes();
        if (getRootWindow().getId() != window.getId() && windowAttributes.isMapped()) {
            windowAttributes.setMapped(false);
            Window parent = window.getParent();
            WindowListenersList eventListenersList = window.getEventListenersList();
            WindowListenersList eventListenersList2 = parent.getEventListenersList();
            eventListenersList.sendEventForEventName(new UnmapNotify(window, window, false), EventName.STRUCTURE_NOTIFY);
            eventListenersList2.sendEventForEventName(new UnmapNotify(parent, window, false), EventName.SUBSTRUCTURE_NOTIFY);
            this.windowLifecycleListenersList.sendWindowUnmapped(window);
        }
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void unmapSubwindows(Window window) {
        for (Window window2 : window.getChildrenBottomToTop()) {
            unmapWindow(window2);
        }
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void changeWindowZOrder(Window window, Window window2, StackMode stackMode) {
        switch (stackMode) {
            case ABOVE:
                window.getParent().getChildrenList().moveAbove(window, window2);
                break;
            case BELOW:
                window.getParent().getChildrenList().moveBelow(window, window2);
                break;
        }
        this.windowLifecycleListenersList.sendWindowZOrderChange(window);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void changeRelativeWindowGeometry(Window window, int x, int y, int width, int height) {
        if (window.getParent() == null) {
            return;
        }
        Rectangle boundingRectangle = window.getBoundingRectangle();
        WindowAttributes windowAttributes = window.getWindowAttributes();
        boolean z = (boundingRectangle.width == width && boundingRectangle.height == height) ? false : true;
        if (window.getEventListenersList().isListenerInstalledForEvent(EventName.RESIZE_REDIRECT) && z) {
            window.getEventListenersList().sendEventForEventName(new ResizeRequest(window, width, height), EventName.SUBSTRUCTURE_REDIRECT);
            width = boundingRectangle.width;
            height = boundingRectangle.height;
            z = false;
        }
        if (z && window.isInputOutput()) {
            Drawable frontBuffer = window.getFrontBuffer();
            this.drawablesManager.removeDrawable(frontBuffer);
            window.replaceBackingStores(this.drawablesManager.createDrawable(frontBuffer.getId(), frontBuffer.getRoot(), width, height, frontBuffer.getVisual()), null);
        }
        if (z || x != boundingRectangle.x || y != boundingRectangle.y) {
            window.setBoundingRectangle(new Rectangle(x, y, width, height));
            this.windowChangeListenersList.sendWindowGeometryChanged(window);
        }
        if (z && window.isInputOutput() && windowAttributes.isMapped()) {
            window.getEventListenersList().sendEvent(new Expose(window));
        }
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void addWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        this.windowLifecycleListenersList.addListener(windowLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void removeWindowLifecycleListener(WindowLifecycleListener windowLifecycleListener) {
        this.windowLifecycleListenersList.removeListener(windowLifecycleListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void addWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener) {
        this.windowContentModificationListenersList.addListener(windowContentModificationListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void removeWindowContentModificationListner(WindowContentModificationListener windowContentModificationListener) {
        this.windowContentModificationListenersList.removeListener(windowContentModificationListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void addWindowChangeListener(WindowChangeListener windowChangeListener) {
        this.windowChangeListenersList.addListener(windowChangeListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public void removeWindowChangeListener(WindowChangeListener windowChangeListener) {
        this.windowChangeListenersList.removeListener(windowChangeListener);
    }

    @Override // com.eltechs.axs.xserver.WindowsManager
    public List<PlacedDrawable> getDrawablesForOutput() {
        ArrayList arrayList = new ArrayList();
        addDrawablesInWindow(this.rootWindow, 0, 0, arrayList);
        return arrayList;
    }

    private void addDrawablesInWindow(Window window, int i, int i2, List<PlacedDrawable> list) {
        if (window.getWindowAttributes().isMapped() && window.isInputOutput()) {
            for (Window window2 : window.getChildrenBottomToTop()) {
                Rectangle boundingRectangle = window2.getBoundingRectangle();
                addDrawablesInWindow(window2, boundingRectangle.x + i, boundingRectangle.y + i2, list);
            }
            if (window != this.rootWindow) {
                Drawable activeBackingStore = window.getActiveBackingStore();
                list.add(new PlacedDrawable(activeBackingStore, new Rectangle(i, i2, activeBackingStore.getWidth(), activeBackingStore.getHeight())));
            }
        }
    }
}