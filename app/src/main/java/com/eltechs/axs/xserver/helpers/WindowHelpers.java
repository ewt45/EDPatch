package com.eltechs.axs.xserver.helpers;

import com.eltechs.axs.geom.Point;
import com.eltechs.axs.helpers.Predicate;
import com.eltechs.axs.xserver.Window;



public abstract class WindowHelpers {

    public static Window getLeafMappedSubWindowByCoords(Window window, final int i, final int i2) {
        return getLeafSubWindowByCondition(window, new Predicate<Window>() { // from class: com.eltechs.axs.xserver.helpers.WindowHelpers.10
            @Override // com.eltechs.axs.helpers.Predicate
            public boolean apply(Window window2) {
                return window2.getWindowAttributes().isMapped() && window2.getBoundingRectangle().containsInnerPoint(WindowHelpers.convertRootCoordsToWindow(window2, i, i2));
            }
        });
    }


    public static Point convertRootCoordsToWindow(Window window, int i, int i2) {
        while (window != null) {
            i -= window.getBoundingRectangle().x;
            i2 -= window.getBoundingRectangle().y;
            window = window.getParent();
        }
        return new Point(i, i2);
    }


    public static Window getLeafSubWindowByCondition(Window window, Predicate<Window> predicate) {
        if (predicate.apply(window)) {
            Window directSubWindowByCondition = getDirectSubWindowByCondition(window, predicate);
            return directSubWindowByCondition != null ? getLeafSubWindowByCondition(directSubWindowByCondition, predicate) : window;
        }
        return null;
    }


    public static Window getDirectSubWindowByCondition(Window window, Predicate<Window> predicate) {
        for (Window window2 : window.getChildrenTopToBottom()) {
            if (predicate.apply(window2)) {
                return window2;
            }
        }
        return null;
    }



}
