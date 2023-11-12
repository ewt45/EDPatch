package com.example.datainsert.exagear.containerSettings;

import static android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE;
import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.service_exe_disable_tip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.v7.preference.EditTextPreference;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.fragments.ContainerSettingsFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 设置处理器核心
 */
public class ConSetOtherArgv {
    public static final String KEY_OTHER_ARGV_STUB = "OTHER_ARGV_STUB";
    public static final String KEY_TASKSET = "KEY_TASKSET";
    public static final String KEY_RUN_IB = "KEY_RUN_IB";
    public static final String KEY_DISABLE_SERVICE = "KEY_DISABLE_SERVICE";
    /**
     * 1: 初版。
     * 添加cpu核心选项：是否启用核心设置，以及8个核心的选择。
     * 启动 ib
     * 禁用 service.exe
     */
    private static final int VERSION_FOR_EDPATCH = 1;
    private static final String TAG = "ConSetOtherArgv";
    private static final int[] defaultAvailableCores = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static String VAL_OTHER_ARGV_STUB = getS(RR.conSet_otherArgv_hint);
    public static String VAL_TASKSET_DEFAULT = "";
    public static boolean VAL_RUN_IB_DEFAULT = false;
    public static boolean VAL_DISABLE_SERVICE_DEFAULT = false;

    public static void buildTasksetPref(ContainerSettingsFragment fragment) {

        //新建preference时的context需要为 从已构建的preference获取的contextwrapper，否则样式会不同
        EditTextPreference tasksetPref = new EditTextPreference(fragment.getPreferenceManager().getContext());
        tasksetPref.setTitle(getS(RR.conSet_otherArgv_title));
        tasksetPref.setDialogTitle(getS(RR.conSet_otherArgv_title));
        tasksetPref.setKey(KEY_OTHER_ARGV_STUB);
        tasksetPref.setSummary("%s");
        tasksetPref.setOrder(3);
        tasksetPref.setDefaultValue(VAL_OTHER_ARGV_STUB);
        tasksetPref.setSummary(VAL_OTHER_ARGV_STUB);
        tasksetPref.setText(VAL_OTHER_ARGV_STUB);
        fragment.getPreferenceScreen().addPreference(tasksetPref);

    }

    public static void buildDialog(EditTextPreference preference) {
        Context c = preference.getContext();
//        ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity();
        SharedPreferences prefMgr = preference.getSharedPreferences();

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        int px = QH.px(c, RR.attr.dialogPaddingDp);
        linearRoot.setPadding(px, 0, px, 0);

        LinearLayout.LayoutParams wrapWidthParams = new LinearLayout.LayoutParams(-2, -2);
        wrapWidthParams.topMargin = px;


        LinearLayout linearBtns = new LinearLayout(c);
        linearBtns.setOrientation(LinearLayout.HORIZONTAL);
        Button btnAdd = new Button(c);
        btnAdd.setText("添加");
        QH.setButtonBorderless(btnAdd);
        btnAdd.setOnClickListener(v->{
            new AlertDialog
                    .Builder(c)
                    .setMultiChoiceItems(new String[]{}, new boolean[]{}, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        }
                    })
                    .create()
                    .show();
        });

        Button btnEditPool = new Button(c);
        btnEditPool.setText("编辑参数库");
        QH.setButtonBorderless(btnEditPool);
        btnEditPool.setOnClickListener(v->{
            
        });

        linearBtns.addView(btnAdd);
        linearBtns.addView(btnEditPool);
        linearRoot.addView(linearBtns);




        ScrollView dialogView = new ScrollView(c);
        dialogView.addView(linearRoot);
        new AlertDialog.Builder(c)
                .setView(dialogView)
                .setTitle(preference.getDialogTitle())
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                })
                .show();
    }
    @Deprecated
    public static void buildDialogOld(EditTextPreference preference) {
        Context c =
                preference.getContext();
//        ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity();
        SharedPreferences prefMgr = preference.getSharedPreferences();

        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        int px = QH.px(c, RR.attr.dialogPaddingDp);
        linearRoot.setPadding(px, 0, px, 0);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(300,px);
        drawable.setAlpha(0);
        linearRoot.setDividerDrawable(drawable);
        linearRoot.setShowDividers(SHOW_DIVIDER_MIDDLE);

        LinearLayout.LayoutParams wrapWidthParams = new LinearLayout.LayoutParams(-2, -2);
        wrapWidthParams.topMargin = px;

        TextView tvInfo = new TextView(c);
        tvInfo.setText(getS(RR.conSet_otherArgv_info));
        tvInfo.setLineSpacing(0,1.1f);
        linearRoot.addView(tvInfo);

        //# 1: 设置cpu核心
        LinearLayout linearTaskSetRoot = new LinearLayout(c);
        linearTaskSetRoot.setOrientation(VERTICAL);

        Switch switchUseCustom = new Switch(c);
        linearTaskSetRoot.addView(QH.getOnePrefLine(switchUseCustom, getS(RR.taskset_useCustom), getS(RR.taskset_info), KEY_TASKSET));

        GridLayout gridCores = new GridLayout(c);
        gridCores.setColumnCount(4);
        gridCores.setRowCount(100);
        gridCores.setUseDefaultMargins(true);
        linearTaskSetRoot.addView(gridCores, wrapWidthParams);

        //获取全部可用的核心，以及当前设置的核心
        int[] availableCores = defaultAvailableCores; //既然armv9在任务管理器里也能手动勾选，那就不限制了，默认0-7都可以选择把
        List<Integer> currCores = new ArrayList<>();
        String currTaskSetStr = prefMgr.getString(KEY_TASKSET, VAL_TASKSET_DEFAULT);
        for (String s : currTaskSetStr.split(","))
            if (s.matches("[0-9]+")) currCores.add(Integer.parseInt(s));//可能有空字符串的情况，所以需要判断

        //注意checkbox的index不一定等于cpu核心的index。应以checkbox的text为准
        for (int i : availableCores) {
            CheckBox checkBox = new CheckBox(c);
            checkBox.setText("" + i);
            checkBox.setChecked(currCores.contains(i));
            checkBox.setOnCheckedChangeListener((btn, isChecked) -> setCoresToPref(prefMgr, true, gridCores));
            gridCores.addView(checkBox);
        }

        //初次显示/switch被点击/check被点击 -> 调用setCoresToPref -> 更新pref
        boolean isCustom = !"".equals(currTaskSetStr);
        switchUseCustom.setChecked(isCustom);//初始化可能不会变化导致没有触发listener，所以手动触发一次。防止混乱，触发之后再设置listener
        setCoresToPref(prefMgr, isCustom, gridCores);
        switchUseCustom.setOnCheckedChangeListener((buttonView, isChecked) -> setCoresToPref(prefMgr, isChecked, gridCores));

        linearRoot.addView(linearTaskSetRoot);

        //# 2: 设置 ib 自启动
        LinearLayout linearRunIB = new LinearLayout(c);
        linearRunIB.setOrientation(VERTICAL);

        Switch switchRunIb = new Switch(c);
        switchRunIb.setText(getS(RR.ib_autorun));
        switchRunIb.setChecked(prefMgr.getBoolean(KEY_RUN_IB, VAL_RUN_IB_DEFAULT)); //默认都为false吧
        switchRunIb.setOnCheckedChangeListener((buttonView, isChecked) -> prefMgr.edit().putBoolean(KEY_RUN_IB, isChecked).apply());
        linearRunIB.addView(QH.addInfoTrail(switchRunIb, getS(RR.ib_autorun_tip)));
        linearRoot.addView(linearRunIB);

        //# 3: 设置 禁用 service.exe
        LinearLayout linearDisableServ = new LinearLayout(c);
        linearDisableServ.setOrientation(VERTICAL);

        Switch switchDisableServ = new Switch(c);
        switchDisableServ.setText(getS(RR.service_exe_disable));
        switchDisableServ.setChecked(prefMgr.getBoolean(KEY_DISABLE_SERVICE, VAL_DISABLE_SERVICE_DEFAULT)); //默认都为false吧
        switchDisableServ.setOnCheckedChangeListener((buttonView, isChecked) -> prefMgr.edit().putBoolean(KEY_DISABLE_SERVICE, isChecked).apply());
        linearDisableServ.addView(QH.addInfoTrail(switchDisableServ, getS(service_exe_disable_tip)));
        linearRoot.addView(linearDisableServ);

        ScrollView dialogView = new ScrollView(c);
        dialogView.addView(linearRoot);
        new AlertDialog.Builder(c)
                .setView(dialogView)
                .setTitle(preference.getDialogTitle())
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//                    ((ContextThemeWrapper)((AlertDialog)dialog).mContext).mThemeResource = 16974551(0x10302D7)
                    //是 Theme_Material_DayNight_Dialog_Alert 根据https://android.googlesource.com/platform/frameworks/base/+/15d48a16f645509cb0c6a1f3abf52ddd233cd8b5%5E%21/
                    //在exa的dex中搜索id搜不到，但是搜DayNight_Dialog_Alert能搜到 Theme_AppCompat_DayNight_Dialog_Alert 0x7f0e0124
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        try {
//                            AlertDialog alertDialog = (AlertDialog) dialog;
//                            ContextThemeWrapper contextThemeWrapper = (ContextThemeWrapper) alertDialog.getContext();
////                            ContextThemeWrapper contextThemeWrapper = (ContextThemeWrapper) QH.getFieldReflectInst(android.support.v7.app.AlertDialog.class,alertDialog,"mContext",true );
//                            Integer themeRes = (Integer) QH.getFieldReflectInst(ContextThemeWrapper.class,contextThemeWrapper,"mThemeResource",true);
//                            Log.d(TAG, "buildDialog: 获取到所用的主题："+themeRes);
//                            Log.d(TAG, "buildDialog: 看看背景色是啥？"+((GradientDrawable)((InsetDrawable)linearRoot.getRootView().getBackground()).getDrawable()).getColor());
//                        }catch (Throwable e){
//                            e.printStackTrace();
//                        }
//                    }
                })
                .show();

    }

    /**
     * switch 或check 被点击后，调用该函数
     */
    private static void setCoresToPref(SharedPreferences prefMgr, boolean useCustom, GridLayout gridLayout) {
        StringBuilder currCores = new StringBuilder();
        //更新checkbox 禁用或启用。获取已勾选的核心
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) gridLayout.getChildAt(i);
            if (useCustom && checkBox.isChecked())
                currCores.append(",").append(checkBox.getText());
        }
        if (currCores.length() > 1)
            currCores.deleteCharAt(0);

//        preference.setText(currCores.toString());//设置这里，会自动修改preference吗 （好像会）
        prefMgr.edit().putString(KEY_TASKSET, currCores.toString()).apply();
        gridLayout.setVisibility(useCustom ? View.VISIBLE : View.GONE);
    }

    private static int[] getAvailableCpuCores() {

        try {
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                List<Integer> availableCores = new ArrayList<>();

                String proc = null;
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "getAvailableCpuCores: " + line);

                    //怎么突然正则匹配需要完全匹配了。。。末尾不加.* 前面都匹配也返回false。。。
                    if (line.trim().matches("^processor\\s*:.*")) {
                        proc = line.split(":", 2)[1].trim();
                    } else if (proc != null && line.trim().matches("^CPU architecture\\s*:.*")) {
                        int arch = Integer.parseInt(line.split(":", 2)[1].trim());
                        if (arch <= 8)
                            availableCores.add(Integer.parseInt(proc));
                        //找到了一个完整的proc和arch信息之后，置为null
                        proc = null;
                    }
                }
                Log.d(TAG, "getAvailableCpuCores: 总共读取的cpu核心：" + availableCores);
                int[] returnedArr = new int[availableCores.size()];
                for (int i = 0; i < returnedArr.length; i++)
                    returnedArr[i] = availableCores.get(i);
                return returnedArr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultAvailableCores;
        }
    }

}
