package com.eltechs.axs.payments;

import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;

public interface PurchasableComponent {
    int getInfoResId();

    String getName();

    XServerDisplayActivityInterfaceOverlay getUiOverlay();
}
