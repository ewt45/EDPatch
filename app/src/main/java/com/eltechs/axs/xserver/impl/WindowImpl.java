package com.eltechs.axs.xserver.impl;

import android.support.annotation.NonNull;

import com.eltechs.axs.geom.Rectangle;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowAttributes;
import com.eltechs.axs.xserver.WindowChangeListenersList;
import com.eltechs.axs.xserver.WindowContentModificationListenersList;
import com.eltechs.axs.xserver.WindowListenersList;
import com.eltechs.axs.xserver.WindowPropertiesManager;
import com.eltechs.axs.xserver.client.XClient;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/* loaded from: classes.dex */
public class WindowImpl implements Window {
    private final WindowAttributes attributes;
    private Drawable backBuffer;
    private Rectangle boundingRectangle;
    private final WindowChildrenList children;
    private final WindowContentModificationListenersList contentModificationListenersList;
    private final XClient creator;
    private final WindowListenersList eventListenersList;
    private Drawable frontBuffer;
    private final int id;
    private Window parent;
    private final WindowPropertiesManager propertiesManager;

    public WindowImpl(int id, Drawable frontBuffer, Drawable backBuffer, WindowContentModificationListenersList windowContentModificationListenersList, WindowChangeListenersList windowChangeListenersList, XClient xClient) {
        this.id = id;
        if (frontBuffer != null) {
            this.frontBuffer = frontBuffer;
            this.backBuffer = backBuffer;
            installFrontBufferModificationListener();
        } else {
            Assert.isTrue(backBuffer == null, "Can't create a window with a back buffer only.");
            this.frontBuffer = null;
            this.backBuffer = null;
        }
        this.children = new WindowChildrenList(this);
        this.propertiesManager = new WindowPropertiesManagerImpl(this);
        this.attributes = new WindowAttributes(WindowAttributes.WindowClass.INPUT_OUTPUT, windowChangeListenersList, this);
        this.eventListenersList = new WindowListenersList(this);
        this.contentModificationListenersList = windowContentModificationListenersList;
        this.creator = xClient;
    }

    @Override // com.eltechs.axs.xserver.Window
    public int getId() {
        return this.id;
    }

    @Override // com.eltechs.axs.xserver.Window
    public Window getParent() {
        return this.parent;
    }

    @Override // com.eltechs.axs.xserver.Window
    public Iterable<Window> getChildrenBottomToTop() {
        return this.children.getChildren();
    }

    @Override // com.eltechs.axs.xserver.Window
    public Iterable<Window> getChildrenTopToBottom() {
        return new Iterable<Window>() { // from class: com.eltechs.axs.xserver.impl.WindowImpl.1
            @Override // java.lang.Iterable
            public Iterator<Window> iterator() {
                List<Window> children = WindowImpl.this.children.getChildren();
                final ListIterator<Window> listIterator = children.listIterator(children.size());
                return new Iterator<Window>() { // from class: com.eltechs.axs.xserver.impl.WindowImpl.1.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return listIterator.hasPrevious();
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    /* renamed from: next */
                    public Window next() {
                        return (Window) listIterator.previous();
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        listIterator.remove();
                    }
                };
            }
        };
    }

    @Override // com.eltechs.axs.xserver.Window
    public void setParent(Window window) {
        if (window != null) {
            Assert.state(this.parent == null, String.format("The window %s already has a parent.", this));
        }
        this.parent = window;
    }

    @Override // com.eltechs.axs.xserver.Window
    public boolean isInputOutput() {
        return this.frontBuffer != null;
    }

    @Override // com.eltechs.axs.xserver.Window
    public Rectangle getBoundingRectangle() {
        return this.boundingRectangle;
    }

    @Override // com.eltechs.axs.xserver.Window
    public void setBoundingRectangle(Rectangle rectangle) {
        this.boundingRectangle = rectangle;
    }

    @Override // com.eltechs.axs.xserver.Window
    public WindowPropertiesManager getPropertiesManager() {
        return this.propertiesManager;
    }

    @Override // com.eltechs.axs.xserver.Window
    public WindowAttributes getWindowAttributes() {
        return this.attributes;
    }

    @Override // com.eltechs.axs.xserver.Window
    public WindowChildrenList getChildrenList() {
        return this.children;
    }

    @Override // com.eltechs.axs.xserver.Window
    public WindowListenersList getEventListenersList() {
        return this.eventListenersList;
    }

    @Override // com.eltechs.axs.xserver.Window
    public Drawable getActiveBackingStore() {
        return this.frontBuffer;
    }

    @Override // com.eltechs.axs.xserver.Window
    public XClient getCreator() {
        return this.creator;
    }

    @Override // com.eltechs.axs.xserver.Window
    public Drawable getFrontBuffer() {
        return this.frontBuffer;
    }

    public Drawable getBackBuffer() {
        return this.backBuffer;
    }

    @Override // com.eltechs.axs.xserver.Window
    public void replaceBackingStores(Drawable drawable, Drawable drawable2) {
        boolean z = true;
        Assert.state(isInputOutput(), String.format("replaceBackingStores has been called for the window %d which is input-only.", Integer.valueOf(this.id)));
        Assert.notNull(drawable, "replaceBackingStores() can't be used to turn a window into an input-only one.");
        if (drawable.getVisual() != this.frontBuffer.getVisual()) {
            z = false;
        }
        Assert.isTrue(z, "replaceBackingStores() can't be used to change the image format of a window.");
        this.frontBuffer = drawable;
        this.backBuffer = drawable2;
        installFrontBufferModificationListener();
        this.contentModificationListenersList.sendFrontBufferReplaced(this);
    }

    private void installFrontBufferModificationListener() {
        this.frontBuffer.installModificationListener(new Drawable.ModificationListener() { // from class: com.eltechs.axs.xserver.impl.WindowImpl.2
            @Override // com.eltechs.axs.xserver.Drawable.ModificationListener
            public void changed(int i, int i2, int i3, int i4) {
                WindowImpl.this.contentModificationListenersList.sendWindowContentChanged(WindowImpl.this, i, i2, i3, i4);
            }
        });
    }

    @NonNull
    @Override
    public String toString() {
        return "WindowImpl{" +
                ", id=" + id +
                ", frontBuffer=" + frontBuffer +
                ", backBuffer=" + backBuffer +
                ", boundingRectangle=" + boundingRectangle +
                ", parent=" + parent +
                ", children=" + children +
                ", creator=" + creator +
                "attributes=" + attributes +
                ", contentModificationListenersList=" + contentModificationListenersList +

                ", eventListenersList=" + eventListenersList +
                ", propertiesManager=" + propertiesManager +
                '}';
    }
}
