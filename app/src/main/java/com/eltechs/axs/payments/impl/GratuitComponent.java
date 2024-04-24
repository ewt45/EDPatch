package com.eltechs.axs.payments.impl;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.payments.PurchasableComponent;

/* loaded from: classes.dex */
public class GratuitComponent implements PurchasableComponent {
    private final int infoResId;
    private final String name;

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public XServerDisplayActivityInterfaceOverlay getUiOverlay() {
        return null;
    }

    public GratuitComponent(String str) {
        this(str, -1);
    }

    public GratuitComponent(String name, int infoResId) {
        this.name = name;
        this.infoResId = infoResId;
    }

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public String getName() {
        return this.name;
    }

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public int getInfoResId() {
        return this.infoResId;
    }
}
