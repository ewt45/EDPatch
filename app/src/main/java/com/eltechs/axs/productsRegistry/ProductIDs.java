package com.eltechs.axs.productsRegistry;

import com.eltechs.axs.helpers.Assert;

/* loaded from: classes.dex */
public abstract class ProductIDs {
    public static final int DOOM = 0;
    public static final int ARCANUM_DEMO = 1;
    public static final int OFFICE_DEMO = 2;
    public static final int HERETIC = 3;
    public static final int HEROES3 = 4;
    public static final int ENGLISH123 = 5;
    public static final int PETKA = 6;
    public static final int HEROES2 = 7;
    public static final int CIV3 = 8;
    public static final int STRATEGIES = 9;
    public static final int RPG = 10;
    public static final int SHTIRLITZ1 = 11;
    public static final int WDESKTOP = 12;

    private ProductIDs() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static final String getPackageName(int i) {
        switch (i) {
            case DOOM:
                return "com.eltechs.doombyeltechs";
            case ARCANUM_DEMO:
                return "com.eltechs.arcanum";
            case OFFICE_DEMO:
                return "com.eltechs.msoffice";
            case HERETIC:
                return "com.eltechs.hereticbyeltechs";
            case HEROES3:
            case HEROES2:
            case CIV3:
                Assert.state(false, String.format("The product %d has been discontinued.", i));
                break;
            case ENGLISH123:
                return "com.eltechs.english123";
            case PETKA:
                return "ru.buka.petka1";
            case STRATEGIES:
                return "com.eltechs.es";
            case RPG:
                return "com.eltechs.erpg";
            case SHTIRLITZ1:
                return "ru.buka.shtirlitz_1";
            case WDESKTOP:
                return "com.eltechs.ed";
        }
        Assert.isTrue(false, "Invalid product ID");
        return null;
    }
}