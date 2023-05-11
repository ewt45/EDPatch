package com.eltechs.axs.xserver.impl.drawables;

public class Visual {
    private final int bitsPerRgbValue;
    private final int blueMask;
    private final int depth;
    private boolean displayable;
    private final int greenMask;
    private final int id;
    private final int redMask;

    private Visual(int id, boolean displayable, int depth, int bitsPerRgbValue, int redMask, int greenMask, int blueMask) {
        this.id = id;
        this.displayable = displayable;
        this.depth = depth;
        this.bitsPerRgbValue = bitsPerRgbValue;
        this.redMask = redMask;
        this.greenMask = greenMask;
        this.blueMask = blueMask;
    }

    public static Visual makeDisplayableVisual(int id, int depth, int bitsPerRgbValue, int redMask, int greenMask, int blueMask) {
        return new Visual(id, true, depth, bitsPerRgbValue, redMask, greenMask, blueMask);
    }

    public static Visual makeNonDisplayableVisual(int id, int depth) {
        return new Visual(id, false, depth, depth, 0, 0, 0);
    }

    public int getId() {
        return this.id;
    }

    public boolean isDisplayable() {
        return this.displayable;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getBitsPerRgbValue() {
        return this.bitsPerRgbValue;
    }

    public VisualClass getVisualClass() {
        return VisualClass.TRUE_COLOR;
    }

    public int getRedMask() {
        return this.redMask;
    }

    public int getGreenMask() {
        return this.greenMask;
    }

    public int getBlueMask() {
        return this.blueMask;
    }
}
