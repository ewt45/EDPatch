package com.example.datainsert.exagear.FAB.dialogfragment;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
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
    private static boolean appFirstLaunching = true;



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

        //显示一个可以点击的颜文字
        String fulltext = "( ˡ ᴗ ˡ ) ";//⩊ ᴗ

//            AnimationUtils.loadAnimation(requireContext(),R.anim.text_scale)
        ScaleAnimation scale = new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0.5f);
        scale.setDuration(150);
        scale.setInterpolator(requireContext(), android.R.anim.overshoot_interpolator);

        LinearLayout linearFun = new LinearLayout(c);
        linearFun.setPadding(40,40,40,40);
        linearFun.setOrientation(LinearLayout.HORIZONTAL);
        View.OnClickListener aniListener = new View.OnClickListener() {
            private int animIndex = 0;
            private final int viewCount = fulltext.length() / 2;
            @Override
            public void onClick(View v) {
                //设置部分文字可见
                for(int i=0; i<linearFun.getChildCount(); i++){
                    linearFun.getChildAt(i).setVisibility(i<=animIndex ?View.VISIBLE : View.GONE);
                }
                //开始单个文字动画
                Log.d(TAG, "onClick: 第几个视图开始动画："+animIndex+", 当前点击视图为"+(String) v.getTag());
                linearFun.getChildAt(animIndex).startAnimation(scale);

                animIndex = (animIndex+1)%viewCount;
            }
        };

        linearFun.setOnClickListener(aniListener);
        for(int i=0; i+2<=fulltext.length(); i+=2){
            TextView tv = new TextView(requireContext());
            tv.setText(fulltext.substring(i,i+2));
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tv.setTypeface(Typeface.MONOSPACE);
            tv.setTag(String.valueOf(i));
            tv.setOnClickListener(aniListener);
            linearFun.addView(tv);
        }

        linearLayout.addView(linearFun);

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


        //尝试重定向logcat到文件中：
        redirectLogcat();




    }

    /**
     * 将logat输出到本地文本中。
     * 要求：app为第一次启动（否则每次退出容器就被清空了） 并且 d盘对应目录存在名为logcat文件夹
     */
    private void redirectLogcat() {
        if(!appFirstLaunching)
            return;
        appFirstLaunching = false;

        File logcatDir = new File(DriveD.getDriveDDir(),"logcat");
        if(!logcatDir.exists())
            return;
        try {
            System.out.println("callWhenFirstStart: 不执行吗？");
            Runtime.getRuntime().exec("logcat -f "+logcatDir.getAbsolutePath()+"/logcat.txt"+" *:V" );// "*:S",
//            Process process = Runtime.getRuntime().exec(new String[]{"killall","-9","logat;","/system/bin/logat","-c;","/system/bin/logcat","-f",filePath });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return getS(RR.abtFab_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
