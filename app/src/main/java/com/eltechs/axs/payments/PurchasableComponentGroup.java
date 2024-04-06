package com.eltechs.axs.payments;

import android.app.Activity;
import android.text.TextUtils;
import com.eltechs.axs.helpers.Assert;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class PurchasableComponentGroup {
    private PurchasableComponentsCollection host;
    private final String name;
    private final RemoteSubscription remoteSubscription;
    private final String subscriptionSkuName;

    /* loaded from: classes.dex */
    public static class RemoteSubscription {
        public final String packageName;
        public final String skuName;

        public RemoteSubscription(String packageName, String skuName) {
            this.packageName = packageName;
            this.skuName = skuName;
        }
    }

    public PurchasableComponentGroup(String name, String subscriptionSkuName, RemoteSubscription remoteSubscription) {
        Assert.state(!TextUtils.isEmpty(subscriptionSkuName));
        this.name = name;
        this.subscriptionSkuName = subscriptionSkuName;
        this.remoteSubscription = remoteSubscription;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getSkuList() {
        return Collections.singletonList(this.subscriptionSkuName);
    }

    public RemoteSubscription getRemoteSubscription() {
        return this.remoteSubscription;
    }

    public void attach(PurchasableComponentsCollection purchasableComponentsCollection) {
        this.host = purchasableComponentsCollection;
    }

    public boolean isSubscriptionActive() {
        if (this.host.getIabInventory().hasPurchase(this.subscriptionSkuName)) {
            return true;
        }
        return this.remoteSubscription != null
                && host.getRemotePackageOwnedSkuList(remoteSubscription.packageName).contains(remoteSubscription.skuName);
    }

    public void buyMonthlySubscription(Activity activity, int requestCode, final PurchaseCompletionCallback callback) {
        Assert.state(!isSubscriptionActive());
        this.host.addGooglePlayInteractionCompletionCallbackOnlyChange(() -> {
            if (callback != null) {
                callback.completed();
            }
        });
        this.host.buySubscription(activity, requestCode, this.subscriptionSkuName);
    }
}
