package com.eltechs.axs.helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.eltechs.axs.Globals;

public class AndroidHelpers {
    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) Globals.getAppContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }


    public static int dpToPx(int i) {
        return (int) ((i * Globals.getAppContext().getResources().getDisplayMetrics().density) + 0.5f);
    }


}
