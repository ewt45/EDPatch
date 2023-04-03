package com.eltechs.axs.proto.output.replies;

import android.support.v4.view.ViewCompat;
import com.eltechs.axs.proto.output.POD;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.XServer;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

@POD({"root", "colormap", "whitePixel", "blackPixel", "currentInputMasks", "widthInPixels", "heightInPixels", "widthInMillimeters", "heightInMillimeters", "minInstalledMaps", "maxInstalledMaps", "rootVisual", "backingStores", "saveUnders", "rootDepth", "allowedDepthsCount", "allowedDepths"})
/* loaded from: classes.dex */
public class Screen {
    public final Depth[] allowedDepths;
    public final byte allowedDepthsCount;
    public final int currentInputMasks;
    public final short heightInMillimeters;
    public final short heightInPixels;
    public final int root;
    public final byte rootDepth;
    public final int rootVisual;
    public final short widthInMillimeters;
    public final short widthInPixels;
    public final int colormap = 0;
    public final int whitePixel = ViewCompat.MEASURED_SIZE_MASK;
    public final int blackPixel = 0;
    public final short minInstalledMaps = 1;
    public final short maxInstalledMaps = 1;
    public final byte backingStores = 0;
    public final byte saveUnders = 0;

    public Screen(XServer xServer) {
        int i = 0;
        Window rootWindow = xServer.getWindowsManager().getRootWindow();
        this.root = rootWindow.getId();
        this.currentInputMasks = rootWindow.getEventListenersList().calculateAllEventsMask().getRawMask();
        ScreenInfo screenInfo = xServer.getScreenInfo();
        this.widthInPixels = (short) screenInfo.widthInPixels;
        this.heightInPixels = (short) screenInfo.heightInPixels;
        this.widthInMillimeters = (short) screenInfo.widthInMillimeters;
        this.heightInMillimeters = (short) screenInfo.heightInMillimeters;
        TreeMap<Integer,Collection<Visual>> treeMap = new TreeMap<>();
        for (Visual visual : xServer.getDrawablesManager().getSupportedVisuals()) {
            Collection<Visual> collection = treeMap.get(visual.getDepth());
            if (collection == null) {
                collection = new LinkedList<>();
                treeMap.put(visual.getDepth(), collection);
            }
            collection.add(visual);
        }
        this.allowedDepthsCount = (byte) treeMap.size();
        this.allowedDepths = new Depth[treeMap.size()];
        for (Map.Entry<Integer,Collection<Visual>> entry : treeMap.entrySet()) {
            this.allowedDepths[i] = new Depth(entry.getKey(), entry.getValue());
            i++;
        }
        Visual visual2 = rootWindow.getActiveBackingStore().getVisual();
        this.rootVisual = visual2.getId();
        this.rootDepth = (byte) visual2.getDepth();
    }
}
