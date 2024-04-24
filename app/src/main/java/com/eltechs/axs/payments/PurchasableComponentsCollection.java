package com.eltechs.axs.payments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.container.annotations.PreRemove;
import com.eltechs.axs.firebase.FAHelper;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.Base64;
import com.eltechs.axs.helpers.PromoHelper;
import com.eltechs.axs.helpers.ReflectionHelpers;
import com.eltechs.axs.helpers.iab.IabHelper;
import com.eltechs.axs.helpers.iab.IabResult;
import com.eltechs.axs.helpers.iab.Inventory;
import com.eltechs.axs.payments.impl.GooglePlayInteractionState;
import com.eltechs.axs.payments.ipc.IabIpc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PurchasableComponentsCollection {
    private static final long DEFAULT_TEST_RUN_DURATION_MILLIS = 600000;
    private static final long DEFAULT_TRIAL_PERIOD_MILLIS = 259200000;
    private static final String LOG_TAG = "IabManager";
    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private static final long MILLIS_IN_MINUTE = 60000;
    private static final long MONTH_MILLIS = 2678400000L;
    private Context applicationContext;
    private String errorMsg;
    private GooglePlayInteractionState googlePlayInteractionState;
    private IabHelper iabHelper;
    private Inventory iabInventory;
    boolean iabSetupDone;
    private final List<GooglePlayInteractionCompletionCallback> interactionCompletionCallbacks;
    private IabIpc.Request ipcRequest;
    private boolean isFake;
    private boolean logEnabled;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    IabHelper.QueryInventoryFinishedListener mGotInventoryWithPricesListener;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    private PurchasableComponentGroup purchasableComponentGroup;
    private final LinkedHashMap<String, PurchasableComponent> purchasableComponentsMap;
    private Map<String, List<String>> remoteOwnedSkus;
    private ArrayList<String> skuList;
    private long testRunDurationExpireTime;
    private final long testRunDurationMillis;
    private long testRunDurationStartTime;
    private long trialPeriodExpireTime;
    private final long trialPeriodMillis;
    private long trialPeriodStartTime;
    private SparseArray<String> unlimPromoSkus;
    private ArrayList<String> unlimSkus;

    public PurchasableComponentsCollection(Context context) {
        this(context, false);
    }

    public PurchasableComponentsCollection(Context context, boolean isFake) {
        this.logEnabled = false;
        this.purchasableComponentsMap = new LinkedHashMap<>();
        this.iabSetupDone = false;
        this.skuList = new ArrayList<>();
        this.unlimSkus = new ArrayList<>();
        this.unlimPromoSkus = new SparseArray<>();
        this.remoteOwnedSkus = new HashMap<>();
        this.ipcRequest = null;
        this.googlePlayInteractionState = GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE;
        this.interactionCompletionCallbacks = new ArrayList<>();
        this.trialPeriodStartTime = 0L;
        this.trialPeriodMillis = DEFAULT_TRIAL_PERIOD_MILLIS;
        this.testRunDurationStartTime = 0L;
        this.testRunDurationMillis = DEFAULT_TEST_RUN_DURATION_MILLIS;
        this.mGotInventoryWithPricesListener = (iabResult, inventory) -> {
            PurchasableComponentsCollection.this.logDebug("Query IAB inventory finished.");
            PurchasableComponentsCollection.this.checkUiThread();
            Assert.notNull(iabHelper);
            Assert.state(googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
            if (iabHelper == null) {
                return;
            }
            if (iabResult.isFailure()) {
                iabHelper.queryInventoryAsync(false, skuList, mGotInventoryListener);
                return;
            }
            logDebug("Query IAB inventory with prices was successful.");
            iabInventory = inventory;
            changeGooglePlayInteractionState(GooglePlayInteractionState.HAVE_DATA_LOCALLY);
        };
        this.mGotInventoryListener = (iabResult, inventory) -> {
            logDebug("Query IAB inventory finished.");
            checkUiThread();
            Assert.notNull(iabHelper);
            Assert.state(googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
            if (iabHelper == null) {
                return;
            }
            if (!iabResult.isFailure()) {
                logDebug("Query IAB inventory was successful.");
                iabInventory = inventory;
                changeGooglePlayInteractionState(GooglePlayInteractionState.HAVE_DATA_LOCALLY);
                return;
            }
            errorMsg = "Failed to query in-app billing inventory: " + iabResult;
            logDebug(errorMsg);
            changeGooglePlayInteractionState(GooglePlayInteractionState.ERROR_OCCURRED);
        };
        this.mPurchaseFinishedListener = (iabResult, purchase) -> {
            logDebug("IAB purchase finished: " + iabResult + ", purchase: " + purchase);
            checkUiThread();
            Assert.notNull(iabHelper);
            Assert.state(googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
            if (iabHelper == null) {
                return;
            }
            if (!iabResult.isFailure()) {
                logDebug("IAB purchase successful.");
            } else {
                logDebug("Error IAB purchasing: " + iabResult);
            }
            iabInventory = null;
            iabHelper.queryInventoryAsync(true, skuList, mGotInventoryWithPricesListener);
        };
        this.applicationContext = context;
        this.isFake = isFake;
        checkUiThread();
    }

    public boolean isFake() {
        return this.isFake;
    }

    public static PurchasableComponentsCollection create(Class<?> cls, Context context) {
        return create(cls, context, false);
    }

    public static PurchasableComponentsCollection create(Class<?> cls, Context context, boolean isFake) {
        ArrayList<String> unlimSkus = new ArrayList<>();
        final ArrayList<PurchasableComponent> purchasableList = new ArrayList<>();
        final ArrayList<PurchasableComponentGroup> purchasableGroupList = new ArrayList<>();
        SparseArray<String> unlimPromoSkus = new SparseArray<>();
        ReflectionHelpers.FieldCallback fieldCallback = field -> purchasableList.add((PurchasableComponent) field.get(null));
        ReflectionHelpers.FieldCallback fieldCallback2 = field -> purchasableGroupList.add((PurchasableComponentGroup) field.get(null));
        try {
            ReflectionHelpers.doWithFields(cls, fieldCallback, ReflectionHelpers.Filters.publicStaticFields(PurchasableComponent.class));
            ReflectionHelpers.doWithFields(cls, fieldCallback2, ReflectionHelpers.Filters.publicStaticFields(PurchasableComponentGroup.class));
            try {
                unlimSkus = new ArrayList<>((List<String>) cls.getDeclaredField("unlimSkus").get(null));
                try {
                    unlimPromoSkus = ((SparseArray) cls.getDeclaredField("unlimPromoSkus").get(null)).clone();
                } catch (NoSuchFieldException ignored) {
                } catch (Exception unused2) {
                    Assert.state(false, "Enumeration of public static fields in a public class has failed. How come?");
                }
            } catch (NoSuchFieldException ignored) {
            }
        } catch (Exception ignored) {
        }
        PurchasableComponentsCollection coll = new PurchasableComponentsCollection(context, isFake);
        coll.setPurchasableComponents(purchasableList, purchasableGroupList, unlimSkus, unlimPromoSkus);
        return coll;
    }

    public void setPurchasableComponents(List<PurchasableComponent> purchasableList, List<PurchasableComponentGroup> purchasableGroupList, List<String> unlimSkus, SparseArray<String> unlimPromoSkus) {
        Assert.state(this.purchasableComponentsMap.isEmpty(), "setPurchasableComponents() can be called only once.");
        for (PurchasableComponent purchasableComponent : purchasableList) {
            String name = purchasableComponent.getName();
            Assert.state(!this.purchasableComponentsMap.containsKey(name), String.format("There are several PurchasableComponent instances with the same name '%s'.", name));
            this.purchasableComponentsMap.put(name, purchasableComponent);
        }
        this.skuList.addAll(unlimSkus);
        for (int i = 0; i < unlimPromoSkus.size(); i++) {
            this.skuList.add(unlimPromoSkus.get(unlimPromoSkus.keyAt(i)));
        }
        for (PurchasableComponentGroup group : purchasableGroupList) {
            Assert.state(this.purchasableComponentGroup == null);
            this.purchasableComponentGroup = group;
            group.attach(this);
            this.skuList.addAll(group.getSkuList());
        }
        this.unlimSkus = new ArrayList<>(unlimSkus);
        this.unlimPromoSkus = unlimPromoSkus.clone();
        makeInitialRetrievalOfGooglePlayData();
    }

    public Collection<PurchasableComponent> getPurchasableComponents() {
        return this.purchasableComponentsMap.values();
    }

    private void makeInitialRetrievalOfGooglePlayData() {
        if (this.isFake) {
            this.iabInventory = new Inventory();
            this.remoteOwnedSkus.put(getRemoteSubsPackageName(), Collections.emptyList());
            changeGooglePlayInteractionState(GooglePlayInteractionState.HAVE_DATA_LOCALLY);
            return;
        }
        startObtainingRemotePaymentsData();
        makeIabSetupAndInitialRetrievalOfGooglePlayData();
    }

    public int getPurchasableComponentsCount() {
        checkUiThread();
        return this.purchasableComponentsMap.size();
    }

    public PurchasableComponent getPurchasableComponent(int i) {
        checkUiThread();
        return (PurchasableComponent) this.purchasableComponentsMap.values().toArray()[i];
    }

    public PurchasableComponent getPurchasableComponent(String str) {
        checkUiThread();
        return this.purchasableComponentsMap.get(str);
    }

    public String getErrorMsg() {
        checkUiThread();
        Assert.state(this.googlePlayInteractionState == GooglePlayInteractionState.ERROR_OCCURRED);
        return this.errorMsg;
    }

    public GooglePlayInteractionState getGooglePlayInteractionState() {
        checkUiThread();
        return this.googlePlayInteractionState;
    }

    public void addGooglePlayInteractionCompletionCallback(GooglePlayInteractionCompletionCallback callback) {
        checkUiThread();
        this.interactionCompletionCallbacks.add(callback);
        callGooglePlayInteractionCompletionCallbacksIfNeed();
    }

    public void addGooglePlayInteractionCompletionCallbackOnlyChange(GooglePlayInteractionCompletionCallback callback) {
        checkUiThread();
        Assert.state(this.googlePlayInteractionState != GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        Assert.state(this.ipcRequest == null);
        this.interactionCompletionCallbacks.add(callback);
    }

    private void callGooglePlayInteractionCompletionCallbacksIfNeed() {
        checkUiThread();
        if ((this.googlePlayInteractionState == GooglePlayInteractionState.HAVE_DATA_LOCALLY || this.googlePlayInteractionState == GooglePlayInteractionState.ERROR_OCCURRED) && this.ipcRequest == null) {
            for (GooglePlayInteractionCompletionCallback callback : this.interactionCompletionCallbacks) {
                callback.googlePlayInteractionCompleted();
            }
            this.interactionCompletionCallbacks.clear();
        }
    }

    private void changeGooglePlayInteractionState(GooglePlayInteractionState googlePlayInteractionState) {
        this.googlePlayInteractionState = googlePlayInteractionState;
        callGooglePlayInteractionCompletionCallbacksIfNeed();
    }

    @PreRemove
    public void clear() {
        checkUiThread();
        if (this.iabHelper != null) {
            this.iabHelper.dispose();
            this.iabHelper = null;
        }
        this.applicationContext = null;
        this.iabInventory = null;
        this.iabSetupDone = false;
        if (this.ipcRequest != null) {
            this.ipcRequest.cleanup();
            this.ipcRequest = null;
        }
    }

    private String getRemoteSubsPackageName() {
        PurchasableComponentGroup.RemoteSubscription remoteSubscription;
        if (this.purchasableComponentGroup == null || (remoteSubscription = this.purchasableComponentGroup.getRemoteSubscription()) == null) {
            return null;
        }
        return remoteSubscription.packageName;
    }

    private void startObtainingRemotePaymentsData() {
        Assert.state(this.ipcRequest == null);
        final String remoteSubsPackageName = getRemoteSubsPackageName();
        if (remoteSubsPackageName == null) {
            return;
        }
        this.ipcRequest = new IabIpc.Request();
        this.ipcRequest.sendRequest(this.applicationContext, remoteSubsPackageName, list -> {
            checkUiThread();
            Assert.state(ipcRequest != null);
            ipcRequest = null;
            remoteOwnedSkus.put(remoteSubsPackageName, list);
            callGooglePlayInteractionCompletionCallbacksIfNeed();
        });
    }

    private void makeIabSetupAndInitialRetrievalOfGooglePlayData() {
        Assert.state(this.iabHelper == null);
        Assert.state(this.googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        logDebug("Creating IAB helper.");
        this.iabHelper = new IabHelper(this.applicationContext, "");
        this.iabHelper.enableDebugLogging(this.logEnabled);
        logDebug("Starting IAB setup.");
        this.iabHelper.startSetup(iabResult -> {
            logDebug("IAB setup finished.");
            checkUiThread();
            Assert.notNull(iabHelper);
            Assert.state(!iabSetupDone);
            if (iabHelper == null || iabSetupDone) {
                return;
            }
            Assert.state(googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
            if (!iabResult.isSuccess()) {
                errorMsg = "Problem setting up in-app billing: " + iabResult;
                logDebug(errorMsg);
                changeGooglePlayInteractionState(GooglePlayInteractionState.ERROR_OCCURRED);
                iabHelper.dispose();
                iabHelper = null;
                return;
            }
            iabSetupDone = true;
            logDebug("IAB setup successful. Querying inventory.");
            iabHelper.queryInventoryAsync(true, skuList, mGotInventoryWithPricesListener);
        });
    }

    private void checkUiThread() {
        Assert.state(Thread.currentThread() == Looper.getMainLooper().getThread(), "Functions of PurchasableComponentsCollection must be called from the GUI thread only.");
    }

    private void logDebug(String str) {
        if (this.logEnabled) {
            Log.d(LOG_TAG, str);
        }
    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public Inventory getIabInventory() {
        return this.iabInventory;
    }

    public List<String> getRemotePackageOwnedSkuList(String str) {
        return this.remoteOwnedSkus.get(str);
    }

    public void buyItem(Activity activity, int requestCode, String sku) {
        checkUiThread();
        Assert.state(this.googlePlayInteractionState != GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        changeGooglePlayInteractionState(GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        this.iabHelper.launchPurchaseFlow(activity, sku, requestCode, this.mPurchaseFinishedListener, "");
    }

    public void buySubscription(Activity activity, int requestCode, String sku) {
        checkUiThread();
        Assert.state(this.googlePlayInteractionState != GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        changeGooglePlayInteractionState(GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        this.iabHelper.launchSubscriptionPurchaseFlow(activity, sku, requestCode, this.mPurchaseFinishedListener, "");
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent intent) {
        if (this.iabHelper == null || !this.iabHelper.handleActivityResult(requestCode, resultCode, intent)) {
            return false;
        }
        Assert.state(this.googlePlayInteractionState == GooglePlayInteractionState.WAITING_FOR_GPLAY_RESPONSE);
        return true;
    }

    public PurchasableComponentGroup getPurchasableComponentGroup() {
        return this.purchasableComponentGroup;
    }

    public String getUnlimNoPromoPriceString() {
        Inventory iabInventory = getIabInventory();
        String sku = this.unlimSkus.get(0);
        return iabInventory.hasDetails(sku) ? iabInventory.getSkuDetails(sku).getPrice() : null;
    }

    public String getUnlimEffectivePriceString() {
        String price = null;
        Inventory iabInventory = getIabInventory();
        if (PromoHelper.isActive(getApplicationContext())) {
            String unlimSku = this.unlimPromoSkus.get(PromoHelper.getDiscount(getApplicationContext()));
            if (iabInventory.hasDetails(unlimSku))
                price = iabInventory.getSkuDetails(unlimSku).getPrice();
        }
        if (price != null)
            return price;

        String unlimSku0 = this.unlimSkus.get(0);
        return iabInventory.hasDetails(unlimSku0) ? iabInventory.getSkuDetails(unlimSku0).getPrice() : null;

    }

    public void buyUnlim(Activity activity, int requestCode, final PurchaseCompletionCallback callback) {
        addGooglePlayInteractionCompletionCallbackOnlyChange(() -> {
            if (callback != null) {
                callback.completed();
            }
        });
        String sku = PromoHelper.isActive(getApplicationContext())
                ? unlimPromoSkus.get(PromoHelper.getDiscount(getApplicationContext()))
                : unlimSkus.get(0);
        buyItem(activity, requestCode, sku);
    }

    public boolean isPrepaidPeriodActive() {
        Inventory iabInventory = getIabInventory();
        for (String skus : this.unlimSkus)
            if (iabInventory.hasPurchase(skus))
                return true;

        for (int i = 0; i < this.unlimPromoSkus.size(); i++)
            if (iabInventory.hasPurchase(this.unlimPromoSkus.get(this.unlimPromoSkus.keyAt(i))))
                return true;

        return this.purchasableComponentGroup.isSubscriptionActive();
    }

    public long getTrialPeriodExpirationTime() {
        initTrialTimes();
        initTestRunDurations();
        return Math.max(this.trialPeriodExpireTime, this.testRunDurationExpireTime);
    }

    public boolean isTrialPeriodActive() {
        initTrialTimes();
        initTestRunDurations();
        long currentTimeMillis = System.currentTimeMillis();
        return (currentTimeMillis >= this.trialPeriodStartTime && currentTimeMillis < this.trialPeriodExpireTime)
                || (currentTimeMillis >= this.testRunDurationStartTime && currentTimeMillis < this.testRunDurationExpireTime)
                || isFake();
    }

    private void initTestRunDurations() {
        if (this.testRunDurationStartTime != 0) {
            return;
        }
        this.testRunDurationStartTime = System.currentTimeMillis();
        this.testRunDurationExpireTime = this.testRunDurationStartTime + DEFAULT_TEST_RUN_DURATION_MILLIS;
    }

    private void initTrialTimes() {
        long j;
        if (this.trialPeriodStartTime != 0) {
            return;
        }
        AppConfig appConfig = AppConfig.getInstance(getApplicationContext());
        try {
            Context ctx = getApplicationContext();
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            if (appConfig.getTrialBeginTime().getTime() == 0) {
                this.trialPeriodStartTime = Calendar.getInstance().getTimeInMillis();
            } else {
                this.trialPeriodStartTime = packageInfo.lastUpdateTime;
            }
            this.trialPeriodExpireTime = this.trialPeriodStartTime + DEFAULT_TRIAL_PERIOD_MILLIS;
            deleteOldVersionInstallDataFile(packageInfo.versionCode);
            File file = new File(getMagicDirPath(), getMagicFileName(packageInfo.versionCode));
            try {
                if (!file.exists()) {
                    writeFile(file, Long.toString(this.trialPeriodStartTime));
                }
                j = Long.parseLong(readFile(file));
            } catch (IOException | NumberFormatException unused) {
                j = 0;
            }
            if (j != 0) {
                this.trialPeriodStartTime = Math.max(this.trialPeriodStartTime, j);
                this.trialPeriodExpireTime = Math.min(this.trialPeriodExpireTime, j + DEFAULT_TRIAL_PERIOD_MILLIS);
            }
            if (appConfig.getTrialBeginTime().getTime() == 0) {
                appConfig.setTrialBeginTime(new Date(this.trialPeriodStartTime));
                appConfig.setTrialExpireTime(new Date(this.trialPeriodExpireTime));
                FAHelper.logGotTrialEvent(getApplicationContext());
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String readFile(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        byte[] bArr = new byte[(int) randomAccessFile.length()];
        randomAccessFile.readFully(bArr);
        randomAccessFile.close();
        return new String(bArr);
    }

    private static void writeFile(File file, String str) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(str.getBytes());
        fileOutputStream.close();
    }

    private String getMagicDirPath() {
        try {
            return getApplicationContext().getExternalFilesDir(null).getParentFile().getParentFile().getAbsolutePath();
        } catch (NullPointerException unused) {
            return "/";
        }
    }

    private String getMagicFileName(int i) {
        Context applicationContext = getApplicationContext();
        String str = applicationContext.getPackageName() + "." + i;
        return Base64.encodeWebSafe(str.getBytes(), false) + ".pm";
    }

    private String getMagicFileNameObsolete(int i) {
        String str;
        Context applicationContext = getApplicationContext();
        if (i == -1) {
            str = applicationContext.getPackageName();
        } else {
            str = applicationContext.getPackageName() + "." + i;
        }
        return Base64.encode(str.getBytes()) + ".pm";
    }

    private void deleteOldVersionInstallDataFile(int i) {
        for (int i2 = i - 1; i2 >= Math.max(i - 6, 0); i2--) {
            File file = new File(getMagicDirPath(), getMagicFileName(i2));
            if (file.exists()) {
                file.delete();
            }
            File file2 = new File(getMagicDirPath(), getMagicFileNameObsolete(i2));
            if (file2.exists()) {
                file2.delete();
            }
        }
        File file3 = new File(getMagicDirPath(), getMagicFileNameObsolete(-1));
        if (file3.exists()) {
            file3.delete();
        }
    }
}
