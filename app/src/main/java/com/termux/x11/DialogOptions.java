package com.termux.x11;

import static com.termux.x11.CmdEntryPoint.PREF_KEY_LEGACY_DRAW;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

class DialogOptions {
    static final String PREF_KEY_GPU_CLOCK = "GPU_CLOCK_MAX_TEST";
    static final String PREF_KEY_GPU_CLOCK_SERVER = "GPU_CLOCK_MAX_SERVER_TEST";

    static LinearLayout getView(BaseFragment baseFragment) {
        Context c = baseFragment.getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paddingParams = new LinearLayout.LayoutParams(-2, -2);
        paddingParams.topMargin = AndroidHelpers.dpToPx(20);

        //https://4pda.to/forum/index.php?showtopic=992239&st=25880#entry121709333
        TextView tv1 = new TextView(c);
        tv1.setText(RR.getS(RR.xegw_info));
        linearRoot.addView(tv1);

//        CheckBox checkLegacyDraw = new CheckBox(c);
//        checkLegacyDraw.setText("-legacy-drawing");
//        checkLegacyDraw.setOnCheckedChangeListener((buttonView, isChecked) -> QH.getPreference().edit().putBoolean(PREF_KEY_LEGACY_DRAW, isChecked).apply());
//        checkLegacyDraw.setChecked(QH.getPreference().getBoolean(PREF_KEY_LEGACY_DRAW, false));
//        linearRoot.addView(checkLegacyDraw, paddingParams);
//        linearRoot.addView(getDescriptionTextView(c, RR.getS(RR.xegw_legacyDraw_tip)));

        linearRoot.addView(
                QH.getOnePrefLine(new Switch(c),"-legacy-drawing",RR.getS(RR.xegw_legacyDraw_tip),PREF_KEY_LEGACY_DRAW),
                paddingParams);

        //电池优化
        String[] btyOptStrs = RR.getS(RR.xegw_btyOpt).split("\\$");
        LinearLayout linearBtyOpt = QH.getOnePrefLine(new Button(c),btyOptStrs[0],btyOptStrs[1],null);
        linearBtyOpt.getChildAt(0).setOnClickListener(v->{
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                    Globals.getAppContext().startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        linearRoot.addView(linearBtyOpt,paddingParams);

        //需要manifest里声明权限的方法，但是只需要点确定即可
        //还有一个 Settings.ACTION_BATTERY_SAVER_SETTINGS 不知道是什么的界面
//        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//        intent.setData(Uri.parse("package:" + Globals.getAppContext().getPackageName()));
//        Globals.getAppContext().startActivity(intent);

//         //测试锁定gpu最大频率
//         CheckBox  checkMaxGpu = new CheckBox(c);
//         checkMaxGpu.setText("gpu max clock (test)");
//         checkMaxGpu.setOnCheckedChangeListener((v,check)->{QH.getPreference().edit().putBoolean(PREF_KEY_GPU_CLOCK,check).apply();});
//         checkMaxGpu.setChecked(QH.getPreference().getBoolean(PREF_KEY_GPU_CLOCK,true));
//         linearRoot.addView(checkMaxGpu);
        return linearRoot;
    }


    /**
     * 创建一个对应某个选项的说明文字
     *
     * @param text 文字
     * @return textview
     */
    static TextView getDescriptionTextView(Context c, String text) {
        TextView textView = new TextView(c);
        textView.setText(text);
        textView.setTextIsSelectable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMarginStart(AndroidHelpers.dpToPx(16));
        textView.setLayoutParams(params);
        return textView;
    }

}
