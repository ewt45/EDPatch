package com.eltechs.axs.xserver;

public class WindowAttributes {
    private boolean isMapped = false;
    private Cursor cursor;

    public boolean isMapped() {
        return this.isMapped;
    }

    public void setMapped(boolean z) {
        this.isMapped = z;
    }
    public Cursor getCursor() {
        return this.cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

}
