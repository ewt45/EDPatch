package com.example.datainsert.exagear.FAB.dialogfragment;

import static com.example.datainsert.exagear.RR.dimen.margin8Dp;
import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.ZipInstallerAssets;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

public class PulseAudio extends BaseFragment {
    private final static File paWorkDir = new File(QH.Files.edPatchDir(), "pulseaudio-xsdl");
    private final static File logFile = new File(paWorkDir, "logs/palog.txt");

    /**
     * 是否在启动容器时启动pulse。默认为PREF_VAL_PULSE_AUTORUN(true)
     */
    private static final String PREF_KEY_PULSE_AUTORUN = "PULSE_AUTORUN";
    private static boolean PREF_DEF_VAL_PULSE_AUTORUN = true;
    /**
     * 使用自定义参数启动pulse，为“”则使用默认参数。
     */
    private static final String PREF_KEY_PULSE_LAUNCH_PARAMS = "PULSE_CUSTOM_PARAMS";
    private static final String DEFAULT_LAUNCH_PARAMS =
            "./pulseaudio --start --exit-idle-time=-1 -n -F ./pulseaudio.conf --daemonize=true";
//            "./pulseaudio --start -n -F ./pulseaudio.conf --exit-idle-time=-1 --disable-shm --daemonize=true --dl-search-path=" + paWorkDir.getAbsolutePath() + " --log-target=stderr --log-level=debug";

    /**
     * 是否输出日志，默认为false
     */
    private static final String PREF_KEY_PULSE_ENABLE_LOG = "PULSE_ENABLE_LOG";
    private static final String TAG = "PulseAudio";


    /**
     * 启动pulseaudio （貌似多次启动会导致失效，要么就启动一次，要么就先停止再启动）
     * @param isStart 停止还是运行
     */
    public static void installAndRun(boolean isStart) {
        //解压要求paDir不存在
        if (paWorkDir.exists() && (!paWorkDir.isDirectory() || paWorkDir.list().length == 0) && !paWorkDir.delete())
            return;
        ZipInstallerAssets.installIfNecessary(Globals.getAppContext(), new ZipInstallerAssets.InstallCallback() {
            @Override
            public void installationFailed(String str) {
                Log.e(TAG, "installationFailed: pulseaudio-xsdl.zip解压失败： ", new Exception(str));
                boolean b = paWorkDir.delete();
            }

            @Override
            public void installationFinished(String str) {
                killAndStartPulseaudio(isStart);//设置pulseaudio路径并启动pulseaudio （即使不勾选启用，也要先调用这个执行停止，否则停不下来了）
            }

        }, paWorkDir, "pulseaudio-xsdl.zip");
    }

    /**
     * 确保so文件存在，新建java进程 停止pulse。
     * 若勾选启用，则运行pulseaudio
     * @param isStart 停止还是运行
     */
    private static void killAndStartPulseaudio(boolean isStart) {
        Log.d(TAG, "startPulseaudio: 停止pulseaudio");

        try {
            //ProcessBulder可以设置环境变量，stdout err重定向等
            assert paWorkDir.exists();
            assert new File(paWorkDir, "pulseaudio").exists();
            String dir = paWorkDir.getAbsolutePath();

            ProcessBuilder builder = new ProcessBuilder(
                    "./pulseaudio",
                    "--kill"
            );

            builder.environment().put("HOME", dir);
            builder.environment().put("TMPDIR", dir);
            builder.environment().put("LD_LIBRARY_PATH", dir);
            builder.directory(paWorkDir);
            if (getPreference().getBoolean(PREF_KEY_PULSE_ENABLE_LOG, true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.redirectErrorStream(true);
                boolean b = logFile.getParentFile().mkdirs();
                builder.redirectOutput(logFile);
            }
            long startTime = System.currentTimeMillis();

            builder.start().waitFor();
            Log.d(TAG, "startPulseaudio: 停止pulseaudio用了多长时间：" + (System.currentTimeMillis() - startTime));

            //删除残留.config文件夹和pulse-xxxx文件夹，防止pa_pid_file_create() failed.?
            for(File subFile :paWorkDir.listFiles())
                if(subFile.isDirectory() &&  subFile.getName().startsWith("pulse-"))
                    FileUtils.deleteDirectory(subFile);
                else if(subFile.isDirectory() && subFile.getName().contains(".config")){
                    //config不知道要不要删啊，留着daemon.conf 其他的删了吧
                    for(File subInConfig:subFile.listFiles())
                        if(!subInConfig.getName().equals("daemon.conf") && !subInConfig.getName().equals("daemon.conf.d"))
                            FileUtils.deleteQuietly(subFile);
                }

            //如果设置不开启pulse，直接返回
            if (!isStart)
                return;

//            builder.command(
//                    "./pulseaudio",
//                    "--start",
//                    "-n",
//                    "-F", "./pulseaudio.conf", //启动时执行特定脚本，和 -n 一起使用（禁止读取default.pa）
//                    "--exit-idle-time=-1",
//                    "--disable-shm",
//                    "--daemonize=true",
//                    "--dl-search-path=" + dir,
////                    "--use-pid-file=false", //默认为true，true不允许多服务器同时运行，可以使用--kill
//                    "--log-target=stderr",
//                    "--log-level=debug"
//            );
            //分割字符串，不能识别引号
            String storedParams = getPreference().getString(PREF_KEY_PULSE_LAUNCH_PARAMS, DEFAULT_LAUNCH_PARAMS);
            assert storedParams != null;
            String[] splitParams = storedParams.trim().split(" ");
            Log.d(TAG, "startPulseaudio: 启动pulseaudio，启动参数为：" + Arrays.toString(splitParams));

            builder.command(splitParams);
            if (getPreference().getBoolean(PREF_KEY_PULSE_ENABLE_LOG, true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            }
            builder.start();
        } catch (IOException | InterruptedException e) {
            try (PrintWriter printWriter = new PrintWriter(logFile);) {
                e.printStackTrace(printWriter);
            } catch (FileNotFoundException ignored) {

            }
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected ViewGroup buildUI() {
        Context c = requireContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paddingParams = new LinearLayout.LayoutParams(-1, -2);
        paddingParams.topMargin = margin8Dp();

        //顶端简介
        TextView tvShortInfo = new TextView(c);
        tvShortInfo.setText(getS(RR.pa_explain));
        linearRoot.addView(tvShortInfo, paddingParams);

        //立即停止和开始
//        Button btnRunNow = new Button(c);
//        btnRunNow.setText("Run Now");
//        btnRunNow.setOnClickListener(v->{
//            installAndRun(true);
//            dismiss();
//        });
//        Button btnStopNow = new Button(c);
//        btnStopNow.setText("stop now");
//        btnStopNow.setOnClickListener(v->{
//            installAndRun(false);
//            dismiss();
//        });
//        LinearLayout linearRunOrStop = new LinearLayout(c);
//        linearRunOrStop.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(0,-2);
//        weightParams.weight=1;
//        linearRunOrStop.addView(btnRunNow,weightParams);
//        linearRunOrStop.addView(btnStopNow,weightParams);
//        linearRoot.addView(linearRunOrStop,paddingParams);

        //是否启动pulse服务
        String[] checkRunStr = getS(RR.pa_checkRun).split("\\$");
        CheckBox checkAutorun = new CheckBox(c);
        checkAutorun.setText(checkRunStr[0]);
        linearRoot.addView(checkAutorun, paddingParams);
        linearRoot.addView(getDescriptionTextView(checkRunStr[1]));

        //输出日志（java的报错也放到这个文件里吧）
        String[] checkLogStr = getS(RR.pa_checkLog).split("\\$");
        CheckBox checkEnableLog = new CheckBox(c);
        checkEnableLog.setText(checkLogStr[0]);
        checkEnableLog.setOnCheckedChangeListener((buttonView, isChecked) -> getPreference().edit().putBoolean(PREF_KEY_PULSE_ENABLE_LOG, isChecked).apply());
        checkEnableLog.setChecked(getPreference().getBoolean(PREF_KEY_PULSE_ENABLE_LOG, true));
        checkEnableLog.setEnabled(getPreference().getBoolean(PREF_KEY_PULSE_AUTORUN, PREF_DEF_VAL_PULSE_AUTORUN));
        linearRoot.addView(checkEnableLog, paddingParams);
        linearRoot.addView(getDescriptionTextView(String.format(checkLogStr[1], requireContext().getPackageName())));

        checkAutorun.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPreference().edit().putBoolean(PREF_KEY_PULSE_AUTORUN, isChecked).apply();
            checkEnableLog.setEnabled(isChecked);
        });
        checkAutorun.setChecked(getPreference().getBoolean(PREF_KEY_PULSE_AUTORUN, PREF_DEF_VAL_PULSE_AUTORUN));


        //修改启动参数
        EditText editPars = new EditText(c);
        editPars.setMaxWidth(AndroidHelpers.dpToPx(400));
        editPars.setSingleLine(true);
        editPars.setText(getPreference().getString(PREF_KEY_PULSE_LAUNCH_PARAMS, DEFAULT_LAUNCH_PARAMS));
        editPars.addTextChangedListener((QH.SimpleTextWatcher) s -> getPreference().edit().putString(PREF_KEY_PULSE_LAUNCH_PARAMS, s.toString()).apply());

        Button btnReset = new Button(c);
        btnReset.setText(getS(RR.cmCtrl_reset));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btnReset.setTextAppearance(android.R.style.TextAppearance_Material_Widget_Button_Borderless_Colored);
            btnReset.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44444444), null, btnReset.getBackground()));
            btnReset.setMinWidth(0);
            btnReset.setMinimumWidth(0);
        }
        btnReset.setOnClickListener(v -> editPars.setText(DEFAULT_LAUNCH_PARAMS));
        LinearLayout linearEditLine = new LinearLayout(c);
        linearEditLine.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(-2, -2);
        editParams.weight = 1;
        linearEditLine.addView(editPars, editParams);
        linearEditLine.addView(btnReset);
        linearEditLine.setVisibility(View.GONE);


        String[] btnParamsStr = getS(RR.pa_btnParam).split("\\$");
        Button btnEditPars = new Button(c);
        btnEditPars.setText(btnParamsStr[0]);
        QH.setButtonBorderless(btnEditPars);
        btnEditPars.setOnClickListener(v -> {
            boolean shouldExit = linearEditLine.getVisibility() == View.VISIBLE;
            linearEditLine.setVisibility(shouldExit ? View.GONE : View.VISIBLE);
        });
        btnEditPars.setAllCaps(false);//禁止全大写
        linearRoot.addView(btnEditPars, paddingParams);
        linearRoot.addView(linearEditLine, new LinearLayout.LayoutParams(-1, -2));
        linearRoot.addView(getDescriptionTextView(btnParamsStr[1]));


        //强制重启

        //故障排查
        String[] trShStrs = getS(RR.pa_troubleShooting).split("\\$");
        String trShTitleStr = trShStrs[0];
        String trShTxt1 = trShStrs[1];
        String trShTxt2 = trShStrs[2];

        TextView tvTbSt = new TextView(c);
        tvTbSt.setLineSpacing(0, 1.2f);
        tvTbSt.setTextIsSelectable(true);
        tvTbSt.setText(trShTxt1);

        //包裹故障排查的全部文字内容
        LinearLayout linearTrShText = new LinearLayout(c);
        linearTrShText.setOrientation(LinearLayout.VERTICAL);
        linearTrShText.addView(tvTbSt);
        LinearLayout.LayoutParams trShTxtParams = new LinearLayout.LayoutParams(-1, -2);
        trShTxtParams.topMargin = AndroidHelpers.dpToPx(8);

        //每一项太长了，先默认缩成一行，点击展开
        for (String oneStr : trShTxt2.split("\n")) {
            TextView tvTbStSub = new TextView(c);
//            QH.setRippleBackground(tvTbStSub);
//            tvTbStSub.setTextIsSelectable(true);
            tvTbStSub.setLineSpacing(0, 1.2f);
            tvTbStSub.setSingleLine(true);
            tvTbStSub.setEllipsize(TextUtils.TruncateAt.END);
            tvTbStSub.setText(oneStr);
            tvTbStSub.setOnClickListener(v -> tvTbStSub.setSingleLine(tvTbStSub.getMaxLines() != 1));
            linearTrShText.addView(tvTbStSub, trShTxtParams);
        }

        LinearLayout linearTblShooting = getOneLineWithTitle(requireContext(), trShTitleStr, linearTrShText, true);//"故障排查 ⓘ"
        linearTrShText.setVisibility(View.GONE);
        linearTblShooting.getChildAt(0).setOnClickListener(v -> linearTrShText.setVisibility(linearTrShText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));

        linearRoot.addView(linearTblShooting, paddingParams);

        return linearRoot;
    }


    /**
     * 创建一个对应某个选项的说明文字
     *
     * @param text 文字
     * @return textview
     */
    private TextView getDescriptionTextView(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextIsSelectable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMarginStart(AndroidHelpers.dpToPx(16));
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * 是否应该启动pulseaudio。从pref读取
     */
    public static boolean shouldStartByPref(){
        return QH.getPreference().getBoolean(PREF_KEY_PULSE_AUTORUN, PREF_DEF_VAL_PULSE_AUTORUN);
    }

    @Override
    public void callWhenFirstStart(AppCompatActivity activity) {

    }

    @Override
    public String getTitle() {
        return getS(RR.pa_title);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
