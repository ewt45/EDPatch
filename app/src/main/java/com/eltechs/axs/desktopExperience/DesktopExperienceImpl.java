package com.eltechs.axs.desktopExperience;

import com.eltechs.axs.desktopExperience.ICCCM.WMStateProperty;
import com.eltechs.axs.desktopExperience.ICCCM.WMStateValues;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.AtomsManager;
import com.eltechs.axs.xserver.DesktopExperience;
import com.eltechs.axs.xserver.FocusManager;
import com.eltechs.axs.xserver.LocksManager;
import com.eltechs.axs.xserver.Pointer;
import com.eltechs.axs.xserver.PointerListener;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.WindowLifecycleListener;
import com.eltechs.axs.xserver.WindowPropertiesManager;
import com.eltechs.axs.xserver.WindowProperty;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.helpers.WindowHelpers;

import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

/* loaded from: classes.dex */
public class DesktopExperienceImpl implements DesktopExperience, PointerListener, WindowLifecycleListener {
    private XServer xServer;

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerButtonReleased(int i) {
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerMoved(int i, int i2) {
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerWarped(int i, int i2) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowCreated(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowDestroyed(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowReparented(Window window, Window window2) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowUnmapped(Window window) {
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowZOrderChange(Window window) {
    }

    @Override // com.eltechs.axs.xserver.DesktopExperience
    public void attachToXServer(XServer xServer) {
        this.xServer = xServer;

        try (LocksManager.XLock ignored = xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.DESKTOP_EXPERIENCE, LocksManager.Subsystem.INPUT_DEVICES, LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.ATOMS_MANAGER})) {
            xServer.desktopExperienceAttached(this);
            xServer.getPointer().addListener(this);
            xServer.getWindowsManager().addWindowLifecycleListener(this);
            initXResources();
        }
    }

    @Override // com.eltechs.axs.xserver.DesktopExperience
    public void detachFromXServer() {
        try (LocksManager.XLock ignored = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.DESKTOP_EXPERIENCE, LocksManager.Subsystem.INPUT_DEVICES, LocksManager.Subsystem.WINDOWS_MANAGER})) {
            this.xServer.getPointer().removeListener(this);
            this.xServer.getWindowsManager().removeWindowLifecycleListener(this);
            this.xServer.desktopExperienceDetached(this);
        }
    }

    @Override // com.eltechs.axs.xserver.PointerListener
    public void pointerButtonPressed(int i) {
        try (LocksManager.XLock ignored = this.xServer.getLocksManager().lock(new LocksManager.Subsystem[]{LocksManager.Subsystem.WINDOWS_MANAGER, LocksManager.Subsystem.FOCUS_MANAGER, LocksManager.Subsystem.INPUT_DEVICES})) {
            FocusManager focusManager = this.xServer.getFocusManager();
            Window focusedWindow = focusManager.getFocusedWindow();
            Pointer pointer = this.xServer.getPointer();
            Window rootWindow = this.xServer.getWindowsManager().getRootWindow();
            Window directMappedSubWindowByCoords = WindowHelpers.getDirectMappedSubWindowByCoords(rootWindow, pointer.getX(), pointer.getY());
            if (directMappedSubWindowByCoords == null && focusManager.getFocusedWindow() != rootWindow) {
                focusManager.setFocus(rootWindow, FocusManager.FocusReversionPolicy.NONE);
            } else if (directMappedSubWindowByCoords != focusedWindow) {
                focusManager.setFocus(directMappedSubWindowByCoords, focusManager.getFocusReversionPolicy());
            }
        }
    }

    @Override // com.eltechs.axs.xserver.WindowLifecycleListener
    public void windowMapped(Window window) {
        if (window.getParent() == this.xServer.getWindowsManager().getRootWindow()) {
            this.xServer.getFocusManager().setFocus(window, FocusManager.FocusReversionPolicy.POINTER_ROOT);
            ICCCMHelpers.setWMState(this.xServer, window, new WMStateProperty(WMStateValues.NORMAL, null));
        }

    }

    private void initXResources() {
        setXResourceToWindow(this.xServer.getWindowsManager().getRootWindow(), new XResourceCursor(24, "dmz", true));
    }

    private void setXResourceToWindow(Window window, XResource xResource) {
        AtomsManager atomsManager = this.xServer.getAtomsManager();
        Atom atomResourceManager = atomsManager.getAtom("RESOURCE_MANAGER");
        Assert.state(atomResourceManager != null, "Atom RESOURCE_MANAGER must be predefined");
        Atom atomString = atomsManager.getAtom("STRING");
        Assert.state(atomString != null, "Atom STRING must be predefined");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : xResource.getKeyValPairs().entrySet()) {
            sb.append(xResource.getName());
            sb.append(FilenameUtils.EXTENSION_SEPARATOR);
            sb.append((Object) entry.getKey());
            sb.append(':');
            sb.append('\t');
            sb.append((Object) entry.getValue());
            sb.append('\n');
        }
        window.getPropertiesManager().modifyProperty(atomResourceManager, atomString, WindowProperty.ARRAY_OF_BYTES, WindowPropertiesManager.PropertyModification.APPEND, sb.toString().getBytes(Charset.forName("latin1")));
    }
}