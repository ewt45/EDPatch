package com.eltechs.axs.xserver.impl;

import android.util.Log;

import com.eltechs.axs.xserver.EventsInjector;
import com.eltechs.axs.xserver.RealXServer;
import com.eltechs.axs.xserver.XServer;

public class EventsInjectorImpl implements EventsInjector {
    private  static final String TAG= "EventsInjectorImpl";
    private final XServer xServer;

    public EventsInjectorImpl(XServer xServer) {
        this.xServer = xServer;
    }

    public void injectKeyPress(byte keyCode, int keySym) {
        if (keyCode != 0 && keySym == 0)
            RealXServer.key(keyCode,1);
        else if (keyCode == 0 && keySym != 0)
            RealXServer.keySym(keyCode,keySym,1);
        else
            Log.e("EventsInjectorImpl", "injectKeyPress: Something is wrong. keyCode is " + keyCode + " and keySym is " + keySym);
    }

    public void injectKeyRelease(byte keyCode, int keySym) {
        if (keyCode != 0 && keySym == 0)
            RealXServer.key(keyCode,1);
        else if (keyCode == 0 && keySym != 0)
            RealXServer.keySym(keyCode,keySym, 0);
        else
            Log.e("EventsInjectorImpl", "injectKeyRelease: Something is wrong. keyCode is " + keyCode + " and keySym is " + keySym);
    }

    public void injectPointerMove(int x, int y) {
        Log.d(TAG, String.format("injectPointerMove: 移动坐标 (%d,%d)",x,y));

        RealXServer.motion(x, y);
    }

    public void injectPointerButtonPress(int i) {
        RealXServer.click(i, 1);
    }

    public void injectPointerButtonRelease(int i) {
        RealXServer.click(i, 0);
    }
}
