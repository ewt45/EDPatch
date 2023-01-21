package com.eltechs.axs.xserver;


import com.eltechs.axs.geom.Rectangle;

public class PlacedDrawable {
    private final Drawable drawable;
    private final Rectangle location;

    public PlacedDrawable(Drawable drawable, Rectangle rectangle) {
        this.drawable = drawable;
        this.location = rectangle;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public Rectangle getLocation() {
        return this.location;
    }
}