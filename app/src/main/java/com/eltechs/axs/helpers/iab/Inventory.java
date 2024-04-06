package com.eltechs.axs.helpers.iab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Inventory {
    Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    Map<String, Purchase> mPurchaseMap = new HashMap<>();

    public SkuDetails getSkuDetails(String str) {
        return this.mSkuDetailsMap.get(str);
    }

    public Purchase getPurchase(String str) {
        return this.mPurchaseMap.get(str);
    }

    public boolean hasPurchase(String str) {
        return this.mPurchaseMap.containsKey(str);
    }

    public boolean hasDetails(String str) {
        return this.mSkuDetailsMap.containsKey(str);
    }

    public void erasePurchase(String str) {
        this.mPurchaseMap.remove(str);
    }

    public List<String> getAllOwnedSkus() {
        return new ArrayList<>(this.mPurchaseMap.keySet());
    }

    public List<String> getAllOwnedSkus(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Purchase purchase : this.mPurchaseMap.values()) {
            if (purchase.getItemType().equals(str)) {
                arrayList.add(purchase.getSku());
            }
        }
        return arrayList;
    }

    public List<Purchase> getAllPurchases() {
        return new ArrayList<>(this.mPurchaseMap.values());
    }

    void addSkuDetails(SkuDetails skuDetails) {
        this.mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
    }

    void addPurchase(Purchase purchase) {
        this.mPurchaseMap.put(purchase.getSku(), purchase);
    }
}
