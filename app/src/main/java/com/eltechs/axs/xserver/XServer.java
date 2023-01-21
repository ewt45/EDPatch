package com.eltechs.axs.xserver;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.impl.DrawablesManagerImpl;
import com.eltechs.axs.xserver.impl.EventsInjectorImpl;
import com.eltechs.axs.xserver.impl.KeyboardModelManagerImpl;
import com.eltechs.axs.xserver.impl.LocksManagerImpl;
import com.eltechs.axs.xserver.impl.WindowsManagerImpl;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactory;
import com.eltechs.axs.xserver.rendering.RenderingEngine;
import com.eltechs.axs.xserver.sysvipc.SHMEngine;

public class XServer {
    private final LocksManagerImpl locksManager = new LocksManagerImpl();
    private final EventsInjector eventsInjector;
    private final Keyboard keyboard;
    private final KeyboardModelManager keyboardModelManager;
    private final PointerEventSender pointerEventSender;
    private final Pointer pointer;
    private final ScreenInfo screenInfo;
    private final WindowsManager windowsManager;
    private final DrawablesManager drawablesManager;




    public XServer(ScreenInfo screenInfo, KeyboardModel keyboardModel, DrawablesFactory drawablesFactory, SHMEngine sHMEngine, RenderingEngine renderingEngine, int i) {
        this.keyboardModelManager = new KeyboardModelManagerImpl(keyboardModel);
        this.screenInfo = screenInfo;
        this.keyboard = new Keyboard(this);
        this.eventsInjector = new EventsInjectorImpl(this);
        this.pointer = new Pointer(this);
        this.pointerEventSender = new PointerEventSender(this);
        this.drawablesManager = new DrawablesManagerImpl(drawablesFactory);
        this.windowsManager = new WindowsManagerImpl(screenInfo, this.drawablesManager);



    }


    public LocksManager getLocksManager() {
        return this.locksManager;
    }

    public EventsInjector getEventsInjector() {
        return this.eventsInjector;
    }

    public Keyboard getKeyboard() {
        Assert.state(this.locksManager.isLocked(LocksManager.Subsystem.INPUT_DEVICES), "Access to the keyboard must be protected with a lock of type INPUT_DEVICES");
        return this.keyboard;
    }

    public KeyboardModelManager getKeyboardModelManager() {
        Assert.state(this.locksManager.isLocked(LocksManager.Subsystem.KEYBOARD_MODEL_MANAGER), "Access to the keyboard model manager must be protected with a lock of type KEYBOARD_MODEL_MANAGER.");
        return this.keyboardModelManager;
    }

    public PointerEventSender getPointerEventSender() {
        return this.pointerEventSender;
    }

    public Pointer getPointer() {
        Assert.state(this.locksManager.isLocked(LocksManager.Subsystem.INPUT_DEVICES), "Access to the pointer must be protected with a lock of type INPUT_DEVICES");
        return this.pointer;
    }

    public ScreenInfo getScreenInfo() {
        return this.screenInfo;
    }


    public WindowsManager getWindowsManager() {
        Assert.state(this.locksManager.isLocked(LocksManager.Subsystem.WINDOWS_MANAGER), "Access to the windows manager must be protected with a lock of type WINDOWS_MANAGER.");
        return this.windowsManager;
    }

}
