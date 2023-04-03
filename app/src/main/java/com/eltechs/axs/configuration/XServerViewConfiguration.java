package com.eltechs.axs.configuration;

/* loaded from: classes.dex */
public class XServerViewConfiguration {
    public static XServerViewConfiguration DEFAULT = new XServerViewConfiguration();
    private boolean showCursor = false;
    private FitStyleHorizontal fitStyleH = FitStyleHorizontal.CENTER;
    private FitStyleVertical fitStyleV = FitStyleVertical.CENTER;

    /* loaded from: classes.dex */
    public enum FitStyleHorizontal {
        LEFT,
        CENTER,
        RIGHT,
        STRETCH
    }

    /* loaded from: classes.dex */
    public enum FitStyleVertical {
        TOP,
        CENTER,
        BOTTOM,
        STRETCH
    }

    public synchronized void setShowCursor(boolean z) {
        this.showCursor = z;
    }

    public synchronized boolean isCursorShowNeeded() {
        return this.showCursor;
    }

    public synchronized void setFitStyleHorizontal(FitStyleHorizontal fitStyleHorizontal) {
        this.fitStyleH = fitStyleHorizontal;
    }

    public synchronized FitStyleHorizontal getFitStyleHorizontal() {
        return this.fitStyleH;
    }

    public synchronized void setFitStyleVertical(FitStyleVertical fitStyleVertical) {
        this.fitStyleV = fitStyleVertical;
    }

    public synchronized FitStyleVertical getFitStyleVertical() {
        return this.fitStyleV;
    }
}