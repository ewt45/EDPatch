package com.example.datainsert.exagear.FAB.dialogfragment;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.IOException;

/**
 * 关于。关于右下齿轮按钮的介绍。
 * 也可以用于一些首次安装应用后启动时的操作
 */
public class AboutFab extends BaseFragment {
    private static final String TAG = "AboutFab";

    /**
     * 首次安装应用后显示提示。默认false表示还没显示过提示。
     */
    private static final String PREF_FIRST_LAUNCH_INFO_SHOWN = "PREF_FIRST_LAUNCH_INFO_SHOWN";



//    static {
//        System.loadLibrary("some-helper");
//    }

    //    public native int startPulseaudio();
//    public native int setEnv(String s);



    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView tvInfo = new TextView(c);
        tvInfo.setLineSpacing(0, 1.2f);
        tvInfo.setText(Html.fromHtml(getS(RR.abtFab_info)));
        tvInfo.setClickable(true);
        tvInfo.setTextIsSelectable(true);
        LinearLayout.LayoutParams tvInfoParams = new LinearLayout.LayoutParams(-2, -2);
        tvInfoParams.topMargin = 20;

        linearLayout.addView(tvInfo, tvInfoParams);

        return linearLayout;
    }

    /**
     * 首次安装后启动，显示snackbar提示用户使用右下角齿轮按钮
     */
    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {
        boolean firstLaunch = !QH.isTesting() && getPreference().getBoolean(PREF_FIRST_LAUNCH_INFO_SHOWN, false);
        if (!firstLaunch) {
            getPreference().edit().putBoolean(PREF_FIRST_LAUNCH_INFO_SHOWN, true).apply();
            Snackbar snackbar = Snackbar.make(FabMenu.getMainFrameView(activity), getS(RR.firstLaunch_snack), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.yes, v -> snackbar.dismiss());
            snackbar.show();
        }

        //重启activity时刷新locale
        RR.locale = RR.refreshLocale();

       //尝试重定向logcat到文件中：
//        try {
//            System.out.println("callWhenFirstStart: 不执行吗？");
//             Runtime.getRuntime().exec("logcat -f /sdcard/logcat.txt *:V" );// "*:S",
////            Process process = Runtime.getRuntime().exec(new String[]{"killall","-9","logat;","/system/bin/logat","-c;","/system/bin/logcat","-f",filePath });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public String getTitle() {
        return getS(RR.abtFab_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
