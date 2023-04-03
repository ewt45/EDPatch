package com.eltechs.axs.xserver.events;


import android.util.Log;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

public abstract class InputDeviceEvent extends Event {
    private final Window child;
    /** 标准keycode的byte值+8 */
    private final byte detail;
    //xy是坐标，event和root不知道是什么窗口
    private final Window event;
    private final short eventX;
    private final short eventY;
    private final Window root;
    private final short rootX;
    private final short rootY;
    private final Mask<KeyButNames> state;
    private final int timestamp;


    public InputDeviceEvent(int id, byte detail, int timestamp,
                            Window rootWindow, Window eventWindow, Window childWindow,
                            short rootX, short rootY, short eventX, short eventY,
                            Mask<KeyButNames> mask) {
        super(id);
        this.detail = detail;
        this.timestamp = timestamp;
        this.root = rootWindow;
        this.event = eventWindow;
        this.child = childWindow;
        this.rootX = rootX;
        this.rootY = rootY;
        this.eventX = eventX;
        this.eventY = eventY;
        this.state = mask;
        Log.d("InputDeviceEvent", logToString());
    }
    public byte getDetail() {
        return this.detail;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public Window getRoot() {
        return this.root;
    }

    public Window getEvent() {
        return this.event;
    }

    public Window getChild() {
        return this.child;
    }

    public short getRootX() {
        return this.rootX;
    }

    public short getRootY() {
        return this.rootY;
    }

    public short getEventX() {
        return this.eventX;
    }

    public short getEventY() {
        return this.eventY;
    }

    public Mask<KeyButNames> getState() {
        return this.state;
    }

    /** 输出一下，看看都是什么东西，有没有字符相关的 */
    public String logToString() {
        return "InputDeviceEvent{" +
                "child=" + child +
                ", detail=" + detail +
                ", event=" + event +
                ", eventX=" + eventX +
                ", eventY=" + eventY +
                ", root=" + root +
                ", rootX=" + rootX +
                ", rootY=" + rootY +
                ", state=" + state +
                ", timestamp=" + timestamp +
                '}';
    }
}
