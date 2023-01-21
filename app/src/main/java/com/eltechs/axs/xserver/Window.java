package com.eltechs.axs.xserver;

import com.eltechs.axs.geom.Rectangle;

public interface Window {
    Window[] getChildrenTopToBottom();

    WindowAttributes getWindowAttributes();

    Rectangle getBoundingRectangle();

    Window getParent();
}
