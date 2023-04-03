package com.eltechs.axs.activities;

import com.eltechs.axs.widgets.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class XServerDisplayActivityConfiguration {
    private List<Action> menuItems = new ArrayList<>();
    private boolean horizontalStretchEnabled = false;

    public List<Action> getMenuItems() {
        return this.menuItems;
    }

    public void setMenuItems(List<Action> list) {
        this.menuItems = list;
    }

    public void addMenuItem(Action action) {
        this.menuItems.add(action);
    }

    public boolean isHorizontalStretchEnabled() {
        return this.horizontalStretchEnabled;
    }

    public void setHorizontalStretchEnabled(boolean z) {
        this.horizontalStretchEnabled = z;
    }
}