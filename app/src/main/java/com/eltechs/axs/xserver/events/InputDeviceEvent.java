package com.eltechs.axs.xserver.events;


import android.util.Log;

import com.eltechs.axs.xserver.KeyButNames;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.masks.Mask;

/**
 * https://www.x.org/releases/current/doc/xproto/x11protocol.html#events:input
 * same-screen:exa没定义这个属性，大概默认是true
 * source：指针所在窗口
 * root: source的根窗口
 * rootX,rootY: 此时指针相对于root原点的坐标
 * event: 此事件实际报告到的窗口。确定event的方法：从source开始，在结构树中查找窗口，如果该窗口的对该事件感兴趣，则返回
 * eventX,eventY: 若event和root在同一screen上，则二者为此时指针相对于event原点的坐标。 否则二者为0.
 * child: 若source为event的子窗口，则child为event子窗口，source父窗口（或source本身）。 否则为None。
 * state:{ Shift, Lock, Control, Mod1, Mod2, Mod3, Mod4, Mod5 } 或 { Button1, Button2, Button3, Button4, Button5 }
 * detail: KeyPress, KeyRelease:KEYCODE; ButtonPress, ButtonRelease:BUTTON; MotionNotify:{ Normal Hint }
 */
public abstract class InputDeviceEvent extends Event {
    //same-screen:exa没定义这个属性，大概默认是true
    //source：指针所在窗口
    /**
     * source的根窗口
     */
    private final Window root;
    /**
     * 此时指针相对于root原点的坐标
     */
    private final short rootX;
    /**
     * 此时指针相对于root原点的坐标
     */
    private final short rootY;
    /**
     * 此事件实际报告到的窗口。确定event的方法：从source开始，在结构树中查找窗口，如果该窗口的对该事件感兴趣，则返回
     */
    private final Window event;
    /**
     * 若event和root在同一screen上，则二者为此时指针相对于event原点的坐标。 否则二者为0.
     */
    private final short eventX;
    /**
     * 若event和root在同一screen上，则二者为此时指针相对于event原点的坐标。 否则二者为0.
     */
    private final short eventY;
    /**
     * 若source为event的子窗口，则child为event子窗口，且source父窗口（或source本身）。 否则为None。
     */
    private final Window child;
    /**
     * 标准keycode的byte值+8
     * KeyPress, KeyRelease:KEYCODE; ButtonPress, ButtonRelease:BUTTON; MotionNotify:{ Normal Hint }
     */
    private final byte detail;
    /**
     * { Shift, Lock, Control, Mod1, Mod2, Mod3, Mod4, Mod5 } 或 { Button1, Button2, Button3, Button4, Button5 }
     */
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
//        Log.d("InputDeviceEvent", logToString());
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

    /**
     * 输出一下，看看都是什么东西，有没有字符相关的
     */
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
