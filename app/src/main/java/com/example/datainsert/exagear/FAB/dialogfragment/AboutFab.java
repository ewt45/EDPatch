package com.example.datainsert.exagear.FAB.dialogfragment;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.Globals;
import com.eltechs.axs.guestApplicationsTracker.impl.ProcessHelpers;
import com.eltechs.axs.helpers.ZipInstallerAssets;
import com.example.datainsert.exagear.FAB.FabMenu;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

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
    /**
     * 虽然多次启动好像也无所谓。。。就启动一次吧
     */
    private static boolean pulseStarted = false;


    static {
        System.loadLibrary("some-helper");
    }

    //    public native int startPulseaudio();
    public native int setEnv(String s);

    private int startPulseaudio() {
        Log.d(TAG, "startPulseaudio: 启动pulseaudio");

        try {
            //ProcessBulder可以设置环境变量，stdout err重定向等
            File logFile = new File(Environment.getExternalStorageDirectory(), "palog.txt");
            File paDir = new File(Globals.getAppContext().getFilesDir(), "pulseaudio-xsdl");
            assert paDir.exists();
            assert new File(paDir, "pulseaudio").exists();
            String dir = paDir.getAbsolutePath();
            Process process = null;

//            if(process==null)
//                Log.d(TAG, "startPulseaudio: 初次启动");
//            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !process.isAlive())
//                Log.d(TAG, "startPulseaudio: 第二次往后启动，pulseaudio的进程已经被终止了？！重新启动！");
//            else
//                Log.d(TAG, "startPulseaudio: 第二次往后启动，pulseaudio的进程还活着");


            ProcessBuilder builder = new ProcessBuilder(
                    dir + "/pulseaudio",
                    "--kill"
            );
            builder.environment().put("HOME", dir);
            builder.environment().put("TMPDIR", dir);
            builder.environment().put("LD_LIBRARY_PATH", dir);
            builder.directory(paDir);
            builder.redirectErrorStream(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.redirectOutput(logFile);
            }
            process = builder.start();
            long startTime = System.currentTimeMillis();
            process.waitFor();
            Log.d(TAG, "startPulseaudio: 停止pulseaudio用了多长时间：" + (System.currentTimeMillis() - startTime));

            builder.command(
                    dir + "/pulseaudio",
                    "--start",
                    "--exit-idle-time=-1",
                    "--disable-shm",
                    "-n",
                    "-F", dir + "/pulseaudio.conf",
                    "--dl-search-path=" + dir,
                    "--daemonize=true",
//                    "--use-pid-file=false",
                    "--log-target=stderr",
                    "--log-level=debug"
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            }
            process = builder.start();


//            Runtime.getRuntime().exec(
//                    dir + "/pulseaudio --disable-shm -n -F " + dir + "/pulseaudio.conf --dl-search-path="+dir+" --daemonize=false --use-pid-file=false --log-target=stderr --log-level=debug",
//                    new String[]{"HOME=" + dir, "TMPDIR=" + dir, "LD_LIBRARY_PATH=" + dir},
//                    paDir
//            );

            return -1;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

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

//       //尝试重定向logcat到文件中：
//        String filePath = Environment.getExternalStorageDirectory() + "/logcat.txt";
//        try {
//            System.out.println("callWhenFirstStart: 不执行吗？");
//             Runtime.getRuntime().exec("logcat -f /sdcard/logcat.txt *:V" );// "*:S",
////            Process process = Runtime.getRuntime().exec(new String[]{"killall","-9","logat;","/system/bin/logat","-c;","/system/bin/logcat","-f",filePath });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        //启动pulseaudio （貌似多次启动会导致失效，要么就启动一次，要么就先停止再启动）
//        if (!pulseStarted) {
//            Log.d(TAG, "callWhenFirstStart: pulseaudio未启动，启动pulseaudio");
//            //解压必要文件
//            File paDir = new File(Globals.getAppContext().getFilesDir(), "pulseaudio-xsdl");
//            if (paDir.exists() && (!paDir.isDirectory() || paDir.list().length == 0))
//                paDir.delete();//解压要求paDir不存在
//            ZipInstallerAssets.installIfNecessary(Globals.getAppContext(), new ZipInstallerAssets.InstallCallback() {
//                @Override
//                public void installationFailed(String str) {
//                    Log.e(TAG, "installationFailed: ", new Exception(str));
//                    paDir.delete();
//                }
//
//                @Override
//                public void installationFinished(String str) {
//                    Log.d(TAG, "installationFinished: " + str);
//                    //设置pulseaudio路径并启动pulseaudio
//                    setEnv(paDir.getAbsolutePath());
//                    pulseStarted = startPulseaudio() == 0;
//                }
//            }, paDir, "pulseaudio-xsdl.zip");
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
