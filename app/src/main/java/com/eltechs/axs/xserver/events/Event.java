package com.eltechs.axs.xserver.events;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class Event {
    private final int id;

    /* JADX INFO: Access modifiers changed from: protected */
    protected Event(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    private static final Map<Integer,String> idMap = new HashMap<>();
    static {
        //有关各个event的参数具体说明https://www.x.org/releases/current/doc/xproto/x11protocol.html#events
        idMap.put(2,"InputDeviceEvent/KeyPress");
        idMap.put(3,"InputDeviceEvent/KeyRelease");
        idMap.put(4,"InputDeviceEvent/ButtonPress");
        idMap.put(5,"InputDeviceEvent/ButtonRelease");
        idMap.put(6,"InputDeviceEvent/MotionNotify");
        idMap.put(7,"InputDeviceEvent/EnterNotify");
        idMap.put(8,"InputDeviceEvent/LeaveNotify");
        idMap.put(12,"Expose");
        idMap.put(16,"CreateNotify");
        idMap.put(17,"DestroyNotify");
        idMap.put(18,"UnmapNotify");
        idMap.put(19,"MapNotify");
        idMap.put(20,"MapRequest");
        idMap.put(22,"ConfigureNotify");
        idMap.put(23,"ConfigureRequest");
        idMap.put(25,"ResizeRequest");
        idMap.put(28,"PropertyNotify");
        idMap.put(29,"SelectionClear");
        idMap.put(30,"SelectionRequest");
        idMap.put(31,"SelectionNotify");
        idMap.put(33,"ConfigureNotify");
        idMap.put(34,"MappingNotify");

    }
    public static String idToClassName(int id){
        return idMap.get(id);
    }
}