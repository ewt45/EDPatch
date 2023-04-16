package com.eltechs.axs.xserver.impl;

import android.util.Log;

import com.eltechs.axs.xserver.EventsInjector;
import com.eltechs.axs.xserver.XServer;

public class EventsInjectorImpl implements EventsInjector {
    private  static final String TAG= "EventsInjectorImpl";
    private final XServer xServer;

    public EventsInjectorImpl(XServer xServer) {
        this.xServer = xServer;
    }

    @Override // com.eltechs.axs.xserver.EventsInjector
    public void injectKeyPress(byte b, int i) {

        this.xServer.getKeyboard().keyPressed(b, i);
    }

    @Override // com.eltechs.axs.xserver.EventsInjector
    public void injectKeyRelease(byte b, int i) {
        Log.d(TAG, "injectKeyRelease: 松开按键：keycode="+b+", keysym="+i);
        this.xServer.getKeyboard().keyReleased(b, i);
    }

    @Override // com.eltechs.axs.xserver.EventsInjector
    public void injectPointerMove(int x, int y) {
        this.xServer.getPointer().setCoordinates(x, y);
    }

    @Override // com.eltechs.axs.xserver.EventsInjector
    public void injectPointerButtonPress(int i) {
        this.xServer.getPointer().setButton(i, true);
    }

    @Override // com.eltechs.axs.xserver.EventsInjector
    public void injectPointerButtonRelease(int i) {
        this.xServer.getPointer().setButton(i, false);
    }
}
