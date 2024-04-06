package com.eltechs.axs.payments.impl;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.payments.PurchasableComponent;

/* loaded from: classes.dex */
public class SimplePurchasableComponent implements PurchasableComponent {
    private final int infoResId;
    private final String name;
    private final XServerDisplayActivityInterfaceOverlay uiOverlay;

    public SimplePurchasableComponent(String name, XServerDisplayActivityInterfaceOverlay uiOverlay, int infoResId) {
        this.name = name;
        this.uiOverlay = uiOverlay;
        this.infoResId = infoResId;
    }

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public String getName() {
        return this.name;
    }

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public XServerDisplayActivityInterfaceOverlay getUiOverlay() {
        return this.uiOverlay;
    }

    @Override // com.eltechs.axs.payments.PurchasableComponent
    public int getInfoResId() {
        return this.infoResId;
    }

    public String toString() {
        return String.format("[purchase: %s]", this.name);
    }
}
