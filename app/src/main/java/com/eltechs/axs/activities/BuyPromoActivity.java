package com.eltechs.axs.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.widget.TextView;
import cn.iwgang.countdownview.CountdownView;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.Globals;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.firebase.FAHelper;
import com.eltechs.axs.helpers.PromoHelper;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import com.eltechs.axs.payments.PurchaseCompletionCallback;
import java.util.Calendar;

/* loaded from: classes.dex */
public class BuyPromoActivity<StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    private static final int REQUEST_CODE_IAB_PURCHASE = 10001;
    public static final String TAG = "BuyPromoActivity";

    public BuyPromoActivity() {
        enableLogging(false);
    }

    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.buy_promo);
        resizeRootViewToStandardDialogueSize();
        init();
    }

    private void init() {
        PurchasableComponentsCollection purchasableComponentsCollection = getApplicationState().getPurchasableComponentsCollection();
        String unlimEffectivePriceString = purchasableComponentsCollection.getUnlimEffectivePriceString();
        String unlimNoPromoPriceString = purchasableComponentsCollection.getUnlimNoPromoPriceString();
        ((CountdownView) findViewById(R.id.buy_promo_timer)).start(PromoHelper.getMsecToEnd(this));
        if (unlimEffectivePriceString != null) {
            TextView textView = findViewById(R.id.buy_promo_old_price);
            textView.setText(unlimNoPromoPriceString);
            textView.setPaintFlags(textView.getPaintFlags() | 16);
            TextViewCompat.setAutoSizeTextTypeWithDefaults(textView, 1);
            TextView textView2 = findViewById(R.id.buy_promo_new_price);
            textView2.setText(unlimEffectivePriceString);
            TextViewCompat.setAutoSizeTextTypeWithDefaults(textView2, 1);
            return;
        }
        (findViewById(R.id.buy_promo_old_price)).setVisibility(View.INVISIBLE);
        TextView textView3 = findViewById(R.id.buy_promo_new_price);
        textView3.setText(getString(R.string.buy_promo_no_price));
        textView3.setTextColor(Color.rgb(0, 0, 0));
        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView3, 1);
    }

    public void onBuyButtonClicked(View view) {
        FAHelper.logTapBuyPromoEvent(this);
        getApplicationState().getPurchasableComponentsCollection().buyUnlim(this, REQUEST_CODE_IAB_PURCHASE, () -> {
            if (getApplicationState().getPurchasableComponentsCollection().isPrepaidPeriodActive()) {
                AppConfig.getInstance(BuyPromoActivity.this).setBuyOrSubscribeTime(Calendar.getInstance().getTime());
                FAHelper.logCompleteBuyPromoEvent(BuyPromoActivity.this);
            }
            finish();
        });
    }

    @Override // com.eltechs.axs.activities.FrameworkActivity, com.eltechs.axs.activities.AxsActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        PurchasableComponentsCollectionAware aware;
        PurchasableComponentsCollection coll;
        logDebug("onActivityResult(" + requestCode + "," + resultCode + "," + intent + ")");
        if (requestCode != REQUEST_CODE_IAB_PURCHASE || (aware = Globals.getApplicationState()) == null || (coll = aware.getPurchasableComponentsCollection()) == null || !coll.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}