package com.eltechs.axs.activities;

import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;

public class CustomizationPackageInformerActivity extends FrameworkActivity {
    public static final String AVAILABLE_PLAY_EXPIRED = "AVAILABLE_PLAY_EXPIRED";
    private static final int REQUEST_CODE_IAB_PURCHASE = 10001;
    private static final int REQUEST_CODE_LIFETIME_INFO = 10003;
    private static final int REQUEST_CODE_SELECT_CP = 10005;
    private static final int REQUEST_CODE_SUBS_INFO = 10004;
    private static final int REQUEST_CODE_TRIAL_RULES = 10002;
    public static final String TAG = "CustomizationPackageInformerActivity";
    boolean availablePlayExpired = false;
    private View.OnTouchListener rootViewOnTouchListener = new View.OnTouchListener() { // from class: com.eltechs.axs.activities.CustomizationPackageInformerActivity.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

}
