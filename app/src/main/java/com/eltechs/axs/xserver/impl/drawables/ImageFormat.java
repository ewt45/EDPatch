package com.eltechs.axs.xserver.impl.drawables;

public class ImageFormat {
    private final int bitsPerPixel;
    private final int depth;
    private final int scanlinePad;

    public ImageFormat(int depth, int bitsPerPixel, int scanlinePad) {
        this.depth = depth;
        this.bitsPerPixel = bitsPerPixel;
        this.scanlinePad = scanlinePad;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public int getScanlinePad() {
        return this.scanlinePad;
    }
}
