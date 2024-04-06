package com.eltechs.axs.helpers;

import android.content.Context;
import com.eltechs.axs.AppConfig;
import com.eltechs.ed.R;
import java.util.Calendar;
import java.util.Date;

/* loaded from: classes.dex */
public class PromoHelper {
    public static boolean isActive(Context context) {
        AppConfig appConfig = AppConfig.getInstance(context);
        if (appConfig.getBuyOrSubscribeTime().getTime() != 0) {
            return false;
        }
        Date time = Calendar.getInstance().getTime();
        return time.getTime() < appConfig.getPromoEndTime().getTime() && time.getTime() >= appConfig.getPromoStartTime().getTime();
    }

    public static long getMsecToEnd(Context context) {
        return AppConfig.getInstance(context).getPromoEndTime().getTime() - Calendar.getInstance().getTimeInMillis();
    }

    public static int getDiscount(Context context) {
        return AppConfig.getInstance(context).getPromoDiscount();
    }

    public static int getDiscountImageRes(Context context) {
        int discount = getDiscount(context);
        if(discount == 10)
            return R.drawable.discount10;
        if(discount == 15)
            return R.drawable.discount15;
        if (discount == 20)
            return R.drawable.discount20;
        if (discount == 25)
            return R.drawable.discount25;
        if (discount == 30)
            return R.drawable.discount30;
        if (discount == 35)
            return R.drawable.discount35;
        if (discount == 40)
            return R.drawable.discount40;
        if (discount == 45)
            return R.drawable.discount45;
        if (discount == 50)
            return R.drawable.discount50;

        Assert.state(false);
        return 0;

    }
}