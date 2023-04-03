package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowLifecycleAdapter;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.client.XClient;
import com.eltechs.axs.xserver.events.SelectionClear;
import com.eltechs.axs.xserver.events.SelectionNotify;
import com.eltechs.axs.xserver.events.SelectionRequest;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SelectionsManagerImpl {
    private final Map<Atom, SelectionInfo> selections = new HashMap();
    private final WindowLifecycleListener windowDestroyListener = new WindowLifecycleAdapter() { // from class: com.eltechs.axs.xserver.impl.SelectionsManagerImpl.1
        @Override // com.eltechs.axs.xserver.WindowLifecycleAdapter, com.eltechs.axs.xserver.WindowLifecycleListener
        public void windowDestroyed(Window window) {
            for (SelectionInfo selectionInfo : SelectionsManagerImpl.this.selections.values()) {
                if (selectionInfo.owner == window) {
                    selectionInfo.owner = null;
                    selectionInfo.client = null;
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelectionInfo {
        public XClient client;
        public int lastChangeTime;
        public Window owner;

        private SelectionInfo() {
        }
    }

    private SelectionInfo getSelectionInfo(Atom atom) {
        SelectionInfo selectionInfo = this.selections.get(atom);
        if (selectionInfo == null) {
            SelectionInfo selectionInfo2 = new SelectionInfo();
            this.selections.put(atom, selectionInfo2);
            return selectionInfo2;
        }
        return selectionInfo;
    }

    public SelectionsManagerImpl(XServer xServer) {
        xServer.getWindowsManager().addWindowLifecycleListener(this.windowDestroyListener);
    }

    public void setSelectionOwner(Atom atom, Window window, XClient xClient, int i) {
        if (i == 0) {
            i = (int) System.currentTimeMillis();
        }
        SelectionInfo selectionInfo = getSelectionInfo(atom);
        if (selectionInfo.owner != null && (window == null || selectionInfo.client != xClient)) {
            selectionInfo.client.createEventSender().sendEvent(new SelectionClear(i, selectionInfo.owner, atom));
        }
        selectionInfo.owner = window;
        selectionInfo.client = xClient;
        selectionInfo.lastChangeTime = i;
    }

    public Window getSelectionOwner(Atom atom) {
        return getSelectionInfo(atom).owner;
    }

    public void convertSelection(Window window, XClient xClient, Atom atom, Atom atom2, Atom atom3, int i) {
        SelectionInfo selectionInfo = getSelectionInfo(atom);
        if (selectionInfo.owner != null) {
            selectionInfo.client.createEventSender().sendEvent(new SelectionRequest(i, selectionInfo.owner, window, atom, atom2, atom3));
            return;
        }
        xClient.createEventSender().sendEvent(new SelectionNotify(i, window, atom, atom2, null));
    }
}
