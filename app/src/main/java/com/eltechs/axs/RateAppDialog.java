package com.eltechs.axs;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import com.eltechs.axs.helpers.DateHelper;
import java.util.Calendar;

/* loaded from: classes.dex */
public class RateAppDialog {
    private static final int MIN_GUEST_LAUNCHES = 10;
    private static final int SHOW_INTERVAL_DAYS = 2;

    public static void show(final Context context) {
        final AppConfig appConfig = AppConfig.getInstance(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rate this app");
        builder.setMessage("If you enjoy this app, please take a moment to rate it. It won't take more than a minute. Thank you for your support!");
        builder.setPositiveButton("Rate now", new DialogInterface.OnClickListener() { // from class: com.eltechs.axs.RateAppDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                String packageName = context.getPackageName();
                try {
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                } catch (ActivityNotFoundException unused) {
                    Context context2 = context;
                    context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
                appConfig.setRateAppDontShowAgain(true);
            }
        });
        builder.setNeutralButton("No, thanks", new DialogInterface.OnClickListener() { // from class: com.eltechs.axs.RateAppDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                appConfig.setRateAppDontShowAgain(true);
            }
        });
        builder.setNegativeButton("Remind later", new DialogInterface.OnClickListener() { // from class: com.eltechs.axs.RateAppDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        appConfig.setRateAppLastShowTime(Calendar.getInstance().getTime());
        builder.show();
    }

    public static void checkCondAndShow(Context context) {
        AppConfig appConfig = AppConfig.getInstance(context);
        if (!appConfig.getRateAppDontShowAgain() && appConfig.getGuestLaunchesCount() >= 10 && DateHelper.getDiffDays(Calendar.getInstance().getTime(), appConfig.getRateAppLastShowTime()) >= 2) {
            show(context);
        }
    }
}