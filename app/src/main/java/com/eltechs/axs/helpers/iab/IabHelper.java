package com.eltechs.axs.helpers.iab;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import com.eltechs.axs.helpers.Assert;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;

/* loaded from: classes.dex */
public class IabHelper {
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
    public static final int IABHELPER_BAD_RESPONSE = -1002;
    public static final int IABHELPER_ERROR_BASE = -1000;
    public static final int IABHELPER_INVALID_CONSUMPTION = -1010;
    public static final int IABHELPER_MISSING_TOKEN = -1007;
    public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
    public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
    public static final int IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE = -1009;
    public static final int IABHELPER_UNKNOWN_ERROR = -1008;
    public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int IABHELPER_USER_CANCELLED = -1005;
    public static final int IABHELPER_VERIFICATION_FAILED = -1003;
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";
    static final int NO_REQUEST_CODE = -1;
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    Context mContext;
    OnIabPurchaseFinishedListener mPurchaseListener;
    String mPurchasingItemType;
    int mRequestCodeOld;
    String mSignatureBase64;
    boolean mDebugLog = false;
    String mDebugTag = "IabHelper";
    boolean mSetupDone = false;
    boolean mDisposed = false;
    boolean mSubscriptionsSupported = false;
    IInAppBillingService mService = null;
    ServiceConnection mServiceConn = null;
    boolean mAsyncInProgress = false;
    ExecutorService mAsyncRunner = Executors.newFixedThreadPool(1);
    String mAsyncOperation = "";
    int mRequestCode = -1;

    public interface OnConsumeFinishedListener {
        void onConsumeFinished(Purchase purchase, IabResult iabResult);
    }

    public interface OnConsumeMultiFinishedListener {
        void onConsumeMultiFinished(List<Purchase> list, List<IabResult> list2);
    }

    public interface OnIabPurchaseFinishedListener {
        void onIabPurchaseFinished(IabResult iabResult, Purchase purchase);
    }

    public interface OnIabSetupFinishedListener {
        void onIabSetupFinished(IabResult iabResult);
    }

    public interface QueryInventoryFinishedListener {
        void onQueryInventoryFinished(IabResult iabResult, Inventory inventory);
    }

    public IabHelper(Context context, String str) {
        this.mSignatureBase64 = null;
        this.mContext = context.getApplicationContext();
        this.mSignatureBase64 = str;
        logDebug("IAB helper created.");
    }

    public void enableDebugLogging(boolean enable, String tag) {
        checkNotDisposed();
        this.mDebugLog = enable;
        this.mDebugTag = tag;
    }

    public void enableDebugLogging(boolean enable) {
        checkNotDisposed();
        this.mDebugLog = enable;
    }

    public boolean isSubscriptionsSupported() {
        checkNotDisposed();
        checkSetupDone("isSubscriptionsSupported");
        return this.mSubscriptionsSupported;
    }

    public boolean isSetupDone() {
        return this.mSetupDone;
    }

    public void startSetup(final OnIabSetupFinishedListener listener) {
        checkNotDisposed();
        if (this.mSetupDone) {
            throw new IllegalStateException("IAB helper is already set up.");
        }
        if (listener == null) {
            throw new NullPointerException("null listener");
        }
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        if (!queryIntentServices.isEmpty()) {
            logDebug("Starting in-app billing setup.");
            this.mServiceConn = new ServiceConnection() { // from class: com.eltechs.axs.helpers.iab.IabHelper.1
                @Override // android.content.ServiceConnection
                public void onServiceDisconnected(ComponentName componentName) {
                    logDebug("Billing service disconnected.");
                    mService = null;
                }

                @Override // android.content.ServiceConnection
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    if (mDisposed) {
                        return;
                    }
                    logDebug("Billing service connected.");
                    mService = IInAppBillingService.Stub.asInterface(iBinder);
                    String packageName = IabHelper.this.mContext.getPackageName();
                    try {
                        IabHelper.this.logDebug("Checking for in-app billing 3 support.");
                        int isInAppSupport = mService.isBillingSupported(3, packageName, IabHelper.ITEM_TYPE_INAPP);
                        if (isInAppSupport != 0) {
                            listener.onIabSetupFinished(new IabResult(isInAppSupport, "Error checking for billing v3 support."));
                            mSubscriptionsSupported = false;
                            return;
                        }
                        logDebug("In-app billing version 3 supported for " + packageName);
                        int isSubsSupport = mService.isBillingSupported(3, packageName, IabHelper.ITEM_TYPE_SUBS);
                        if (isSubsSupport == 0) {
                            logDebug("Subscriptions AVAILABLE.");
                            mSubscriptionsSupported = true;
                        } else {
                            logDebug("Subscriptions NOT AVAILABLE. Response: " + isSubsSupport);
                        }
                        if (mSetupDone) {
                            return;
                        }
                        mSetupDone = true;
                        listener.onIabSetupFinished(new IabResult(0, "Setup successful."));
                    } catch (RemoteException e) {
                        listener.onIabSetupFinished(new IabResult(IabHelper.IABHELPER_REMOTE_EXCEPTION, "RemoteException while setting up in-app billing."));
                        e.printStackTrace();
                    }
                }
            };
            Assert.isTrue(this.mContext.bindService(intent, this.mServiceConn, Context.BIND_AUTO_CREATE));
            return;
        }
        listener.onIabSetupFinished(new IabResult(3, "Billing service unavailable on device."));
    }

    public void dispose() {
        checkUiThread();
        this.mAsyncRunner.shutdown();
        while (!this.mAsyncRunner.isTerminated()) {
            try {
                this.mAsyncRunner.awaitTermination(1L, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
        logDebug("Disposing.");
        this.mSetupDone = false;
        if (this.mServiceConn != null) {
            logDebug("Unbinding from service.");
            if (this.mContext != null) {
                this.mContext.unbindService(this.mServiceConn);
            }
        }
        this.mDisposed = true;
        this.mContext = null;
        this.mServiceConn = null;
        this.mService = null;
        this.mAsyncRunner = null;
        this.mPurchaseListener = null;
    }

    public void launchPurchaseFlow(Activity activity, String sku, int requestCode, OnIabPurchaseFinishedListener finishListener) {
        launchPurchaseFlow(activity, sku, requestCode, finishListener, "");
    }

    public void launchPurchaseFlow(Activity activity, String sku, int requestCode, OnIabPurchaseFinishedListener finishListener, String developerPayload) {
        launchPurchaseFlow(activity, sku, ITEM_TYPE_INAPP, requestCode, finishListener, developerPayload);
    }

    public void launchSubscriptionPurchaseFlow(Activity activity, String sku, int requestCode, OnIabPurchaseFinishedListener finishListener) {
        launchSubscriptionPurchaseFlow(activity, sku, requestCode, finishListener, "");
    }

    public void launchSubscriptionPurchaseFlow(Activity activity, String sku, int requestCode, OnIabPurchaseFinishedListener finishListener, String developerPayload) {
        launchPurchaseFlow(activity, sku, ITEM_TYPE_SUBS, requestCode, finishListener, developerPayload);
    }

    public void launchPurchaseFlow(Activity activity, String sku, String itemType, int requestCode, OnIabPurchaseFinishedListener finishListener, String developerPayload) {
        Assert.state(requestCode != -1);
        Assert.state(this.mRequestCode == -1);
        checkNotDisposed();
        checkSetupDone("launchPurchaseFlow");
        flagStartAsync("launchPurchaseFlow");
        if (itemType.equals(ITEM_TYPE_SUBS) && !this.mSubscriptionsSupported) {
            IabResult iabResult = new IabResult(IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE, "Subscriptions are not available.");
            flagEndAsync();
            if (finishListener != null)
                finishListener.onIabPurchaseFinished(iabResult, null);
            return;
        }
        try {
            logDebug("Constructing buy intent for " + sku + ", item type: " + itemType);
            Bundle buyIntent = this.mService.getBuyIntent(3, this.mContext.getPackageName(), sku, itemType, developerPayload);
            int responseCodeFromBundle = getResponseCodeFromBundle(buyIntent);
            if (responseCodeFromBundle != 0) {
                logError("Unable to buy item, Error response: " + getResponseDesc(responseCodeFromBundle));
                flagEndAsync();
                IabResult iabResult2 = new IabResult(responseCodeFromBundle, "Unable to buy item");
                if (finishListener != null)
                    finishListener.onIabPurchaseFinished(iabResult2, null);
                return;
            } else {
                logDebug("Launching buy intent for " + sku + ". Request code: " + requestCode);
                this.mRequestCode = requestCode;
                this.mRequestCodeOld = requestCode;
                this.mPurchaseListener = finishListener;
                this.mPurchasingItemType = itemType;
                IntentSender intentSender = ((PendingIntent) buyIntent.getParcelable(RESPONSE_BUY_INTENT)).getIntentSender();
                activity.startIntentSenderForResult(intentSender, requestCode, new Intent(), 0, 0, 0);
                return;
            }
        } catch (IntentSender.SendIntentException e) {
            logError("SendIntentException while launching purchase flow for sku " + sku);
            e.printStackTrace();
            flagEndAsync();
            this.mRequestCode = -1;
            IabResult iabResult = new IabResult(IABHELPER_SEND_INTENT_FAILED, "Failed to send intent.");
            if (finishListener != null)
                finishListener.onIabPurchaseFinished(iabResult, null);
        } catch (RemoteException e) {
            logError("RemoteException while launching purchase flow for sku " + sku);
            e.printStackTrace();
            flagEndAsync();
            this.mRequestCode = -1;
            IabResult iabResult = new IabResult(IABHELPER_REMOTE_EXCEPTION, "Remote exception while starting purchase flow");
            if (finishListener != null) {
                finishListener.onIabPurchaseFinished(iabResult, null);
            }
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent intent) {
        Assert.state(requestCode != -1);
        Assert.state(this.mRequestCode != -1 || requestCode != this.mRequestCodeOld, "not correct check, but we use it for temporary control");
        if(mRequestCode == -1 || requestCode != mRequestCode)
            return false;

        checkNotDisposed();
        checkSetupDone("handleActivityResult");
        flagEndAsync();
        this.mRequestCode = -1;
        if (intent == null) {
            logError("Null data in IAB activity result.");
            IabResult iabResult = new IabResult(IABHELPER_BAD_RESPONSE, "Null data in IAB result");
            if (this.mPurchaseListener != null)
                this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
            return true;
        }
        int resonpseCode = getResponseCodeFromIntent(intent);
        String purchaseData = intent.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String signature = intent.getStringExtra(RESPONSE_INAPP_SIGNATURE);
        if (resultCode == -1 && resonpseCode == 0) {
            logDebug("Successful resultcode from purchase activity.");
            logDebug("Purchase data: " + purchaseData);
            logDebug("Data signature: " + signature);
            logDebug("Extras: " + intent.getExtras());
            logDebug("Expected item type: " + this.mPurchasingItemType);
            if (purchaseData == null || signature == null) {
                logError("BUG: either purchaseData or dataSignature is null.");
                logDebug("Extras: " + intent.getExtras().toString());
                IabResult iabResult2 = new IabResult(IABHELPER_UNKNOWN_ERROR, "IAB returned null purchaseData or dataSignature");
                if (this.mPurchaseListener != null)
                    this.mPurchaseListener.onIabPurchaseFinished(iabResult2, null);
                return true;
            }
            try {
                Purchase purchase = new Purchase(this.mPurchasingItemType, purchaseData, signature);
                String sku = purchase.getSku();
                if (!Security.verifyPurchase(this.mSignatureBase64, purchaseData, signature)) {
                    logError("Purchase signature verification FAILED for sku " + sku);
                    if (this.mPurchaseListener != null)
                        this.mPurchaseListener.onIabPurchaseFinished(new IabResult(IABHELPER_VERIFICATION_FAILED, "Signature verification failed for sku " + sku), purchase);
                    return true;
                }
                logDebug("Purchase signature successfully verified.");
                if (this.mPurchaseListener != null)
                    this.mPurchaseListener.onIabPurchaseFinished(new IabResult(0, "Success"), purchase);
            } catch (JSONException e) {
                logError("Failed to parse purchase data.");
                e.printStackTrace();
                if (this.mPurchaseListener != null)
                    this.mPurchaseListener.onIabPurchaseFinished( new IabResult(IABHELPER_BAD_RESPONSE, "Failed to parse purchase data."), null);
                return true;
            }
        } else if (resultCode == -1) {
            logDebug("Result code was OK but in-app billing response was not OK: " + getResponseDesc(resonpseCode));
            if (this.mPurchaseListener != null)
                this.mPurchaseListener.onIabPurchaseFinished(new IabResult(resonpseCode, "Problem purchashing item."), null);
        } else if (resultCode == 0) {
            logDebug("Purchase canceled - Response: " + getResponseDesc(resonpseCode));
            if (this.mPurchaseListener != null)
                this.mPurchaseListener.onIabPurchaseFinished(new IabResult(IABHELPER_USER_CANCELLED, "User canceled."), null);
        } else {
            logError("Purchase failed. Result code: " + resultCode + ". Response: " + getResponseDesc(resonpseCode));
            if (this.mPurchaseListener != null)
                this.mPurchaseListener.onIabPurchaseFinished(new IabResult(IABHELPER_UNKNOWN_PURCHASE_RESPONSE, "Unknown purchase response."), null);
        }
        return true;
    }

    public Inventory queryInventory(boolean z, List<String> list, List<String> list2) throws IabException {
        int subsable;
        int purchasable;
        checkNotDisposed();
        checkSetupDone("queryInventory");
        try {
            Inventory inventory = new Inventory();
            int purchased = queryPurchases(inventory, ITEM_TYPE_INAPP);
            if (purchased != 0) {
                throw new IabException(purchased, "Error refreshing inventory (querying owned items).");
            }
            if (z && (purchasable = querySkuDetails(ITEM_TYPE_INAPP, inventory, list)) != 0) {
                throw new IabException(purchasable, "Error refreshing inventory (querying prices of items).");
            }
            if (this.mSubscriptionsSupported) {
                int subs = queryPurchases(inventory, ITEM_TYPE_SUBS);
                if (subs != 0) {
                    throw new IabException(subs, "Error refreshing inventory (querying owned subscriptions).");
                }
                if (z && (subsable = querySkuDetails(ITEM_TYPE_SUBS, inventory, list)) != 0) {
                    throw new IabException(subsable, "Error refreshing inventory (querying prices of subscriptions).");
                }
            }
            return inventory;
        } catch (RemoteException e) {
            throw new IabException(IABHELPER_REMOTE_EXCEPTION, "Remote exception while refreshing inventory.", e);
        } catch (JSONException e2) {
            throw new IabException(IABHELPER_BAD_RESPONSE, "Error parsing JSON response while refreshing inventory.", e2);
        }
    }

    public void queryInventoryAsync(final boolean z, final List<String> list, final QueryInventoryFinishedListener queryInventoryFinishedListener) {
        final Handler handler = new Handler();
        checkNotDisposed();
        checkSetupDone("queryInventory");
        checkUiThread();
        flagStartAsync("refresh inventory");
        this.mAsyncRunner.submit(() -> {
            IabResult iabResult = new IabResult(0, "Inventory refresh successful.");
            Inventory inventory = null;
            try {
                inventory = queryInventory(z, list, null);
            } catch (IabException e) {
                iabResult = e.getResult();
            }
            flagEndAsync();
            if (mDisposed || queryInventoryFinishedListener == null) {
                return;
            }
            IabResult finalIabResult = iabResult;
            Inventory finalInventory = inventory;
            handler.post(() -> {
                if (IabHelper.this.mDisposed)
                    return;
                queryInventoryFinishedListener.onQueryInventoryFinished(finalIabResult, finalInventory);
            });
        });
    }

    public void queryInventoryAsync(QueryInventoryFinishedListener queryInventoryFinishedListener) {
        queryInventoryAsync(true, null, queryInventoryFinishedListener);
    }

    public void queryInventoryAsync(boolean z, QueryInventoryFinishedListener queryInventoryFinishedListener) {
        queryInventoryAsync(z, null, queryInventoryFinishedListener);
    }

    void consume(Purchase purchase) throws IabException {
        checkNotDisposed();
        checkSetupDone("consume");
        if (!purchase.mItemType.equals(ITEM_TYPE_INAPP)) {
            throw new IabException((int) IABHELPER_INVALID_CONSUMPTION, "Items of type '" + purchase.mItemType + "' can't be consumed.");
        }
        try {
            String token = purchase.getToken();
            String sku = purchase.getSku();
            if (token != null && !token.equals("")) {
                logDebug("Consuming sku: " + sku + ", token: " + token);
                int consumePurchase = this.mService.consumePurchase(3, this.mContext.getPackageName(), token);
                if (consumePurchase == 0) {
                    logDebug("Successfully consumed sku: " + sku);
                    return;
                }
                logDebug("Error consuming consuming sku " + sku + ". " + getResponseDesc(consumePurchase));
                throw new IabException(consumePurchase, "Error consuming sku " + sku);
            }
            logError("Can't consume " + sku + ". No token.");
            throw new IabException((int) IABHELPER_MISSING_TOKEN, "PurchaseInfo is missing token for sku: " + sku + " " + purchase);
        } catch (RemoteException e) {
            throw new IabException(IABHELPER_REMOTE_EXCEPTION, "Remote exception while consuming. PurchaseInfo: " + purchase, e);
        }
    }

    public void consumeAsync(Purchase purchase, OnConsumeFinishedListener onConsumeFinishedListener) {
        checkNotDisposed();
        checkSetupDone("consume");
        ArrayList arrayList = new ArrayList();
        arrayList.add(purchase);
        consumeAsyncInternal(arrayList, onConsumeFinishedListener, null);
    }

    public void consumeAsync(List<Purchase> list, OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) {
        checkNotDisposed();
        checkSetupDone("consume");
        consumeAsyncInternal(list, null, onConsumeMultiFinishedListener);
    }

    public static String getResponseDesc(int i) {
        String[] split = "0:OK/1:User Canceled/2:Unknown/3:Billing Unavailable/4:Item unavailable/5:Developer Error/6:Error/7:Item Already Owned/8:Item not owned".split("/");
        String[] split2 = "0:OK/-1001:Remote exception during initialization/-1002:Bad response received/-1003:Purchase signature verification failed/-1004:Send intent failed/-1005:User cancelled/-1006:Unknown purchase response/-1007:Missing token/-1008:Unknown error/-1009:Subscriptions not available/-1010:Invalid consumption attempt".split("/");
        if (i <= -1000) {
            int i2 = (-1000) - i;
            if (i2 < 0 || i2 >= split2.length) {
                return i + ":Unknown IAB Helper Error";
            }
            return split2[i2];
        } else if (i < 0 || i >= split.length) {
            return i + ":Unknown";
        } else {
            return split[i];
        }
    }

    int getResponseCodeFromBundle(Bundle bundle) {
        Object obj = bundle.get(RESPONSE_CODE);
        if (obj == null) {
            logDebug("Bundle with null response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            logError("Unexpected type for bundle response code.");
            logError(obj.getClass().getName());
            throw new RuntimeException("Unexpected type for bundle response code: " + obj.getClass().getName());
        }
    }

    int getResponseCodeFromIntent(Intent intent) {
        Object obj = intent.getExtras().get(RESPONSE_CODE);
        if (obj == null) {
            logError("Intent with no response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            logError("Unexpected type for intent response code.");
            logError(obj.getClass().getName());
            throw new RuntimeException("Unexpected type for intent response code: " + obj.getClass().getName());
        }
    }

    int queryPurchases(Inventory inventory, String type) throws JSONException, RemoteException {
        logDebug("Querying owned items, item type: " + type);
        logDebug("Package name: " + this.mContext.getPackageName());
        String continuationToken = null;
        boolean failed = false;
        while (true) {
            logDebug("Calling getPurchases with continuation token: " + continuationToken);
            Bundle purchases = this.mService.getPurchases(3, this.mContext.getPackageName(), type, continuationToken);
            int responseCode = getResponseCodeFromBundle(purchases);
            logDebug("Owned items response: " + responseCode);
            if (responseCode != 0) {
                logDebug("getPurchases() failed: " + getResponseDesc(responseCode));
                return responseCode;
            } else if (!purchases.containsKey(RESPONSE_INAPP_ITEM_LIST)
                    || !purchases.containsKey(RESPONSE_INAPP_PURCHASE_DATA_LIST)
                    || !purchases.containsKey(RESPONSE_INAPP_SIGNATURE_LIST)) {
                logError("Bundle returned from getPurchases() doesn't contain required fields.");
                return IABHELPER_BAD_RESPONSE;
            }

            ArrayList<String> stringArrayList = purchases.getStringArrayList(RESPONSE_INAPP_ITEM_LIST);
            ArrayList<String> stringArrayList2 = purchases.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
            ArrayList<String> stringArrayList3 = purchases.getStringArrayList(RESPONSE_INAPP_SIGNATURE_LIST);
            for (int i = 0; i < stringArrayList2.size(); i++) {
                String str3 = stringArrayList2.get(i);
                String str4 = stringArrayList3.get(i);
                String str5 = stringArrayList.get(i);
                if (Security.verifyPurchase(this.mSignatureBase64, str3, str4)) {
                    logDebug("Sku is owned: " + str5);
                    Purchase purchase = new Purchase(type, str3, str4);
                    if (TextUtils.isEmpty(purchase.getToken())) {
                        logWarn("BUG: empty/null token!");
                        logDebug("Purchase data: " + str3);
                    }
                    inventory.addPurchase(purchase);
                } else {
                    logWarn("Purchase signature verification **FAILED**. Not adding item.");
                    logDebug("   Purchase data: " + str3);
                    logDebug("   Signature: " + str4);
                    failed = true;
                }
            }
            continuationToken = purchases.getString(INAPP_CONTINUATION_TOKEN);
            logDebug("Continuation token: " + continuationToken);

            if (TextUtils.isEmpty(continuationToken))
                return failed ? IABHELPER_VERIFICATION_FAILED : 0;
        }
    }

    int querySkuDetails(String str, Inventory inventory, List<String> list) throws RemoteException, JSONException {
        logDebug("Querying SKU details.");
        ArrayList<String> arrayList = new ArrayList<>(inventory.getAllOwnedSkus(str));
        if (list != null) {
            for (String str2 : list) {
                if (!arrayList.contains(str2)) {
                    arrayList.add(str2);
                }
            }
        }
        if (arrayList.isEmpty()) {
            logDebug("querySkuDetails: nothing to do because there are no SKUs.");
            return 0;
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, arrayList);
        Bundle skuDetails = this.mService.getSkuDetails(3, this.mContext.getPackageName(), str, bundle);
        if (!skuDetails.containsKey(RESPONSE_GET_SKU_DETAILS_LIST)) {
            int responseCode = getResponseCodeFromBundle(skuDetails);
            if (responseCode != 0) {
                logDebug("getSkuDetails() failed: " + getResponseDesc(responseCode));
                return responseCode;
            }
            logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
            return IABHELPER_BAD_RESPONSE;
        }
        for (String s : skuDetails.getStringArrayList(RESPONSE_GET_SKU_DETAILS_LIST)) {
            SkuDetails skuDetails2 = new SkuDetails(str, s);
            logDebug("Got sku details: " + skuDetails2);
            inventory.addSkuDetails(skuDetails2);
        }
        return 0;
    }

    void consumeAsyncInternal(final List<Purchase> list, final OnConsumeFinishedListener onConsumeFinishedListener, final OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) {
        final Handler handler = new Handler();
        checkUiThread();
        flagStartAsync("consume");
        this.mAsyncRunner.submit(() -> {
            final ArrayList<IabResult> arrayList = new ArrayList<>();
            for (Purchase purchase : list) {
                try {
                    consume(purchase);
                    arrayList.add(new IabResult(0, "Successful consume of sku " + purchase.getSku()));
                } catch (IabException e) {
                    arrayList.add(e.getResult());
                }
            }
            flagEndAsync();
            if (!mDisposed && onConsumeFinishedListener != null) {
                handler.post(() -> {
                    if (!mDisposed)
                        onConsumeFinishedListener.onConsumeFinished(list.get(0), arrayList.get(0));
                });
            }
            if (!mDisposed && onConsumeMultiFinishedListener != null) {
                handler.post(() -> {
                    if (!mDisposed)
                        onConsumeMultiFinishedListener.onConsumeMultiFinished(list, arrayList);
                });
            }

        });
    }

    private void checkUiThread() {
        Assert.isTrue(Looper.getMainLooper().getThread() == Thread.currentThread());
    }

    private void checkSetupDone(String str) {
        if (this.mSetupDone) {
            return;
        }
        logError("Illegal state for operation (" + str + "): IAB helper is not set up.");
        throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + str);
    }

    private void checkNotDisposed() {
        if (this.mDisposed) {
            throw new IllegalStateException("IabHelper was disposed of, so it cannot be used.");
        }
    }

    void flagStartAsync(String str) {
        if (this.mAsyncInProgress) {
            throw new IllegalStateException("Can't start async operation (" + str + ") because another async operation(" + this.mAsyncOperation + ") is in progress.");
        }
        this.mAsyncOperation = str;
        this.mAsyncInProgress = true;
        logDebug("Starting async operation: " + str);
    }

    void flagEndAsync() {
        logDebug("Ending async operation: " + this.mAsyncOperation);
        if (!this.mAsyncInProgress) {
            throw new IllegalStateException("Can't end async operation because async operation is NOT in progress.");
        }
        this.mAsyncOperation = "";
        this.mAsyncInProgress = false;
    }

    void logDebug(String str) {
        if (this.mDebugLog) {
            Log.d(this.mDebugTag, str);
        }
    }

    void logError(String str) {
        String str2 = this.mDebugTag;
        Log.e(str2, "In-app billing error: " + str);
    }

    void logWarn(String str) {
        String str2 = this.mDebugTag;
        Log.w(str2, "In-app billing warning: " + str);
    }
}
