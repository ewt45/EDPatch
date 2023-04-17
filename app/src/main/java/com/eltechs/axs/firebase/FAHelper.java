package com.eltechs.axs.firebase;

import android.content.Context;
import android.os.Bundle;
import com.eltechs.axs.AppConfig;
import com.eltechs.axs.helpers.DateHelper;
import com.eltechs.axs.helpers.GAHelpers;
import com.eltechs.ed.guestContainers.GuestContainerConfig;
//import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Calendar;

/* loaded from: classes.dex */
public class FAHelper {
    public static void logEvent(Context context, String str, Bundle bundle) {
//        FirebaseAnalytics.getInstance(context).logEvent(str, bundle);
    }

    public static void logNotificationClickEvent(Context context, String str) {
//        Bundle bundle = new Bundle();
//        bundle.putString(GuestContainerConfig.KEY_NAME, str);
//        logEvent(context, "NOTIFICATION_CLICK", bundle);
//        GAHelpers.GASendEvent(context, "NOTIFICATION_CLICK", null, str, null);
    }

    public static void logExeFoundEvent(Context context) {
//        AppConfig appConfig = AppConfig.getInstance(context);
//        long diffDays = DateHelper.getDiffDays(Calendar.getInstance().getTime(), appConfig.getFirstRunTime());
//        Bundle bundle = new Bundle();
//        if (appConfig.isRunAfterNotification()) {
//            bundle.putString("NOTIF_NAME", appConfig.getNotificationName());
//            logEvent(context, "EXE_FOUND_NOTIF", bundle);
//            GAHelpers.GASendEvent(context, "EXE_FOUND_NOTIF", null, appConfig.getNotificationName(), null);
//            return;
//        }
//        bundle.putLong("TIME_DAYS", diffDays);
//        logEvent(context, "EXE_FOUND", bundle);
//        GAHelpers.GASendEvent(context, "EXE_FOUND", null, Long.toString(diffDays), null);
    }

    public static void logGotTrialEvent(Context context) {
//        AppConfig appConfig = AppConfig.getInstance(context);
//        long diffDays = DateHelper.getDiffDays(Calendar.getInstance().getTime(), appConfig.getExeFoundTime());
//        Bundle bundle = new Bundle();
//        if (appConfig.isRunAfterNotification()) {
//            bundle.putString("NOTIF_NAME", appConfig.getNotificationName());
//            logEvent(context, "GOT_TRIAL_NOTIF", bundle);
//            GAHelpers.GASendEvent(context, "GOT_TRIAL_NOTIF", null, appConfig.getNotificationName(), null);
//            return;
//        }
//        bundle.putLong("TIME_DAYS", diffDays);
//        logEvent(context, "GOT_TRIAL", bundle);
//        GAHelpers.GASendEvent(context, "GOT_TRIAL", null, Long.toString(diffDays), null);
    }

    private static void logIabEvent(Context context, String str) {
//        AppConfig appConfig = AppConfig.getInstance(context);
//        long diffDays = DateHelper.getDiffDays(Calendar.getInstance().getTime(), appConfig.getExeFoundTime());
//        Bundle bundle = new Bundle();
//        if (appConfig.isRunAfterNotification()) {
//            bundle.putString("NOTIF_NAME", appConfig.getNotificationName());
//            logEvent(context, str + "_NOTIF", bundle);
//            GAHelpers.GASendEvent(context, str + "_NOTIF", null, appConfig.getNotificationName(), null);
//            return;
//        }
//        bundle.putLong("TIME_DAYS", diffDays);
//        logEvent(context, str, bundle);
//        GAHelpers.GASendEvent(context, str, null, Long.toString(diffDays), null);
    }

    public static void logTapBuyPromoEvent(Context context) {
//        logIabEvent(context, "TAP_BUY_PROMO");
    }

    public static void logCompleteBuyPromoEvent(Context context) {
//        logIabEvent(context, "COMPLETE_BUY_PROMO");
    }

    public static void logTapBuyEvent(Context context) {
//        logIabEvent(context, "TAP_BUY");
    }

    public static void logCompleteBuyEvent(Context context) {
//        logIabEvent(context, "COMPLETE_BUY");
    }

    public static void logTapSubscribeEvent(Context context) {
//        logIabEvent(context, "TAP_SUBSCRIBE");
    }

    public static void logCompleteSubscribeEvent(Context context) {
//        logIabEvent(context, "COMLPETE_SUBSCRIBE");
    }

    public static void logXServerFirstConnectEvent(Context context) {
//        logEvent(context, "XSERVER_FIRST_CONNECT", null);
//        GAHelpers.GASendEvent(context, "XSERVER_FIRST_CONNECT", null, null, null);
    }

    public static void logRemindActionsCrashEvent(Context context, String str, String str2) {
//        Bundle bundle = new Bundle();
//        bundle.putString("DESCR", str);
//        bundle.putString("STACKELEM", str2);
//        logEvent(context, "REMIND_ACTIONS_CRASH", bundle);
//        GAHelpers.GASendEvent(context, "REMIND_ACTIONS_CRASH", str, str2, null);
    }
}