package com.eltechs.axs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.firebase.FAHelper;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.PromoHelper;
import com.eltechs.axs.payments.PurchasableComponent;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import com.eltechs.axs.payments.PurchaseCompletionCallback;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/* loaded from: classes.dex */
public class CustomizationPackageInformerActivity<StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    public static final String AVAILABLE_PLAY_EXPIRED = "AVAILABLE_PLAY_EXPIRED";
    private static final int REQUEST_CODE_IAB_PURCHASE = 10001;
    private static final int REQUEST_CODE_TRIAL_RULES = 10002;
    private static final int REQUEST_CODE_LIFETIME_INFO = 10003;
    private static final int REQUEST_CODE_SUBS_INFO = 10004;
    private static final int REQUEST_CODE_SELECT_CP = 10005;
    public static final String TAG = "CustomizationPackageInformerActivity";
    boolean availablePlayExpired = false;
    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener rootViewOnTouchListener = (view, motionEvent) -> true;

    public CustomizationPackageInformerActivity() {
        enableLogging(false);
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customization_package_informer);
        resizeRootViewToStandardDialogueSize();
        String str = getExtraParameter();
        if (str != null && str.equals(AVAILABLE_PLAY_EXPIRED)) {
            this.availablePlayExpired = true;
        }
        findViewById(R.id.cpi_root_view).setOnTouchListener(this.rootViewOnTouchListener);
        init();
    }

    private PurchasableComponent getCurrentPurchasableComponent() {
        return getApplicationState().getPurchasableComponentsCollection().getPurchasableComponent(0);
    }

    private void init() {
        PurchasableComponentsCollection purchasableComponentsCollection = getApplicationState().getPurchasableComponentsCollection();
        if (!purchasableComponentsCollection.isTrialPeriodActive()) {
            findViewById(R.id.cpi_run_trial_button).setEnabled(false);
        }
        ((Button) findViewById(R.id.cpi_run_trial_button)).setText(String.format(getString(R.string.cpi_run_trial_button_label), DateFormat.getDateTimeInstance(2, 2).format(new Date(purchasableComponentsCollection.getTrialPeriodExpirationTime()))));
        Button button = findViewById(R.id.cpi_buy_cp_button);
        String unlimEffectivePriceString = purchasableComponentsCollection.getUnlimEffectivePriceString();
        if (unlimEffectivePriceString != null) {
            button.setText(String.format(getString(R.string.cpi_buy_lifetime_button_with_price_label), unlimEffectivePriceString));
        } else {
            button.setText(getString(R.string.cpi_buy_lifetime_button_label));
        }
        if (PromoHelper.isActive(this)) {
            ((ImageView) findViewById(R.id.cpi_buy_cp_promo_image)).setImageResource(PromoHelper.getDiscountImageRes(this));
        }
    }

    public void onRunTrialButtonClicked(View view) {
        DetectedExecutableFile<StateClass> selectedExecutableFile = getApplicationState().getSelectedExecutableFile();
        if (selectedExecutableFile == null) {
            finish();
        } else if (selectedExecutableFile.getEffectiveCustomizationPackage() != null) {
            signalUserInteractionFinished();
        } else {
            startActivityForResult(REQUEST_CODE_SELECT_CP, SelectCustomizationPackageActivity.class);
        }
    }

    public void onBuyLifetimeButtonClicked(View view) {
        FAHelper.logTapBuyEvent(this);
        getApplicationState().getPurchasableComponentsCollection().buyUnlim(this, REQUEST_CODE_IAB_PURCHASE, () -> {
            if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
                AppConfig.getInstance(CustomizationPackageInformerActivity.this).setBuyOrSubscribeTime(Calendar.getInstance().getTime());
                FAHelper.logCompleteBuyEvent(CustomizationPackageInformerActivity.this);
            }
            CustomizationPackageInformerActivity.this.finish();
        });
    }

    public void onBuySubscriptionButtonClicked(View view) {
        FAHelper.logTapSubscribeEvent(this);
        getApplicationState().getPurchasableComponentsCollection().getPurchasableComponentGroup().buyMonthlySubscription(this, REQUEST_CODE_IAB_PURCHASE, () -> {
            if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
                AppConfig.getInstance(CustomizationPackageInformerActivity.this).setBuyOrSubscribeTime(Calendar.getInstance().getTime());
                FAHelper.logCompleteSubscribeEvent(CustomizationPackageInformerActivity.this);
            }
            finish();
        });
    }

    public void onInfoButtonClicked(View view) {
        String tag = (String) view.getTag();
        switch (tag) {
            case "run_trial":
                startActivityForResult(REQUEST_CODE_TRIAL_RULES, AddGameWizard.class, R.string.cpi_trial_rules);
                return;
            case "buy_lifetime":
                startActivityForResult(REQUEST_CODE_LIFETIME_INFO, AddGameWizard.class, R.string.cpi_lifetime_rules);
                return;
            case "buy_subscription":
                startActivityForResult(REQUEST_CODE_SUBS_INFO, AddGameWizard.class, R.string.cpi_subs_info);
                return;
            default:
                Assert.unreachable("unexpected tag: " + tag);
                return;
        }
    }
    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        PurchasableComponentsCollectionAware aware;
        PurchasableComponentsCollection coll;
        logDebug("onActivityResult(" + requestCode + "," + resultCode + "," + intent + ")");
        if (requestCode == REQUEST_CODE_TRIAL_RULES || requestCode == REQUEST_CODE_LIFETIME_INFO || requestCode == REQUEST_CODE_SUBS_INFO) {
            return;
        }
        if (requestCode == REQUEST_CODE_SELECT_CP && resultCode != 0) {
            signalUserInteractionFinished();
        } else if (requestCode != REQUEST_CODE_IAB_PURCHASE || (aware = Globals.getApplicationState()) == null || (coll = aware.getPurchasableComponentsCollection()) == null || !coll.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}