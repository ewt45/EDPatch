package com.eltechs.axs.helpers;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.analytics.tracking.android.MapBuilder;

/* loaded from: classes.dex */
public class GAHelpers {
    private static final String CAMPAIGN_SOURCE_PARAM = "utm_source";

    public static void GAStart(Activity activity) {
//        EasyTracker.getInstance(activity).activityStart(activity);
    }

    public static void GAStop(Activity activity) {
//        EasyTracker.getInstance(activity).activityStop(activity);
    }

    public static void GASendAndroidVersion(Activity activity) {
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("ANDROID_VERSION", Build.VERSION.RELEASE, "", null).build());
    }

    public static void GASendDeviceInfo(Activity activity) {
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("DEVICE_MODEL", Build.MODEL, "", null).build());
    }

    public static void GASendLinuxVersion(Activity activity) {
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("LINUX_VERSION", System.getProperty("os.version"), "", null).build());
    }

    public static void GASendScreenParameters(Activity activity) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("DISPLAY", String.format("%dx%d", Integer.valueOf(displayMetrics.widthPixels), Integer.valueOf(displayMetrics.heightPixels)), "", null).build());
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("DISPLAY_PHYS", String.format("%fx%f", Float.valueOf((displayMetrics.widthPixels / displayMetrics.densityDpi) * 2.54f), Float.valueOf((displayMetrics.heightPixels / displayMetrics.densityDpi) * 2.54f)), "", null).build());

    }

    public static void GASendReferrer(Activity activity) {
//        Uri data = activity.getIntent().getData();
//        if (data == null || data.getQueryParameter(CAMPAIGN_SOURCE_PARAM) == null) {
//            return;
//        }
//        MapBuilder createAppView = MapBuilder.createAppView();
//        createAppView.setCampaignParamsFromUrl(data.toString());
//        EasyTracker.getInstance(activity).send(createAppView.build());
    }

    public static void GASendEvent(Context context, String str, String str2, String str3, Long l) {
//        EasyTracker.getInstance(context).send(MapBuilder.createEvent(str, str2, str3, l).build());
    }

    public static void GASendKeepalive(Activity activity) {
//        EasyTracker.getInstance(activity).send(MapBuilder.createEvent("KEEPALIVE", "", "", null).build());
    }

    public static void GASendCpuNotSupported(Context context) {
//        EasyTracker.getInstance(context).send(MapBuilder.createEvent("CPUNOTSUPPORTED", Build.MODEL, context.getPackageName(), null).build());
    }
}