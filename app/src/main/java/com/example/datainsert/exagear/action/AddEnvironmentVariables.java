package com.example.datainsert.exagear.action;


import static com.eltechs.ed.fragments.ContainerSettingsFragment.renderEntries.Turnip_DXVK;
import static com.eltechs.ed.fragments.ContainerSettingsFragment.renderEntries.VirGL_Overlay;
import static com.eltechs.ed.fragments.ContainerSettingsFragment.renderEntries.VirGL_built_in;
import static com.eltechs.ed.fragments.ContainerSettingsFragment.renderEntries.VirtIO_GPU;
import static com.eltechs.ed.guestContainers.GuestContainerConfig.CONTAINER_CONFIG_FILE_KEY_PREFIX;
import static com.example.datainsert.exagear.mutiWine.MutiWine.KEY_WINE_INSTALL_PATH;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.Mcat;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.applicationState.UBTLaunchConfigurationAware;
import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.configuration.startup.actions.AbstractStartupAction;
import com.eltechs.ed.fragments.ContainerSettingsFragment;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.example.datainsert.exagear.QH;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 作为action，在startguest启动时添加环境变量。
 * <p>
 * 插入代码位置：紧跟在 arrayList.add(new CreateLaunchConfiguration<> 后面 ,StartEnvironmentService之前
 */
public class AddEnvironmentVariables<StateClass extends UBTLaunchConfigurationAware & EnvironmentAware & ExagearImageAware>
        extends AbstractStartupAction<StateClass> {
    private static final String TAG = "AddEnvironmentVariables";
    /**
     * 可能多个地方用到的，放到成员变量里，最后再加入。每次拼接的时候，路径前带冒号(e.g.  LD_LIBRARY_PATH.insert(0,ldPath).insert(0,':'); )
     */
    private StringBuilder LD_LIBRARY_PATH = new StringBuilder(":/usr/lib/i386-linux-gnu");

    @Override
    public void execute() {
        UBTLaunchConfiguration ubtConfig = getApplicationState().getUBTLaunchConfiguration();

        //根据xdroid的真实路径确定容器id
        File xdroidFile = new File(getApplicationState().getExagearImage().getPath(), "home/xdroid");
        long contId = 0;
        try {
            contId = Long.parseLong(xdroidFile.getCanonicalFile().getName().replace("xdroid_", ""));
        } catch (IOException e) {
            e.printStackTrace();
            sendError(e.getMessage(), e);

        }

        SharedPreferences sp = getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + contId, Context.MODE_PRIVATE);

        //添加多个盘符

        //添加pulseaudio环境变量

        //wine程序位置的环境变量
        try {
            Class.forName("com.example.datainsert.exagear.mutiWine.MutiWine");
            addWinePath(sp, ubtConfig, contId, xdroidFile);
        } catch (Exception e) {
            Log.w(TAG, "execute: 功能未安装：多wine共存 " + e.getMessage());
        }

        //添加渲染对应的ld_library_path环境变量 (改成在容器设置里修改
        try {
            Field field = ContainerSettingsFragment.class.getField("KEY_RENDERER"); //直接获取成员，抛出的是NoSuchFieldError 不属于exception
            addRendererPath(sp, ubtConfig);
        } catch (Exception | NoSuchFieldError e) {
            Log.w(TAG, "execute: 功能未安装：环境设置-渲染器新选择" + e.getMessage());
        }
        //最后再添加动态库路径环境变量到ubt
        ubtConfig.addEnvironmentVariable("LD_LIBRARY_PATH", LD_LIBRARY_PATH.toString());

        sendDone();
    }

    private void addRendererPath(SharedPreferences sp, UBTLaunchConfiguration ubtConfig) {

        ContainerSettingsFragment.renderEntries[] entries = ContainerSettingsFragment.renderEntries.values();

        String rendererName = sp.getString(ContainerSettingsFragment.KEY_RENDERER, entries[0].name);
        String ldPath = "";
        for (ContainerSettingsFragment.renderEntries entry : entries)
            if (entry.name.equals(rendererName))
                ldPath = entry.path;

        //一些渲染的额外设置

        if (testRenderExist("VirGL_Overlay") && VirGL_Overlay.name.equals(rendererName)) {
            //默认取消悬浮窗
            ubtConfig.addEnvironmentVariable("VTEST_WIN", "1");
            ubtConfig.addEnvironmentVariable("VTEST_SOCK", "");
        } else if (testRenderExist("VirGL_built_in") && VirGL_built_in.name.equals(rendererName)) {
            //调用so
            File virglServerFile = new File(getAppContext().getApplicationInfo().nativeLibraryDir, "libvirgl_test_server.so");
            try {
                ProcessBuilder builder = new ProcessBuilder(virglServerFile.getAbsolutePath());
                builder.environment().put("TMPDIR", getApplicationState().getExagearImage().getPath().getAbsolutePath() + "/tmp");
//                builder.environment().put("VTEST_SOCK", "");
//                builder.directory(paDir);
                builder.redirectErrorStream(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    File logDir = new File(Globals.getAppContext().getExternalFilesDir(null),"logs");
                    logDir.mkdirs();
                    builder.redirectOutput(new File(logDir,"virglLog.txt"));
                }
                builder.start();

//                Runtime.getRuntime().exec(virglServerFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (testRenderExist("VirtIO_GPU") && VirtIO_GPU.name.equals(rendererName)) {
            if (QH.classExist("com.eltechs.axs.MCat"))
                new Mcat().start();
        } else if (testRenderExist("Turnip_DXVK") && Turnip_DXVK.name.equals(rendererName)) {
            ubtConfig.addEnvironmentVariable("GALLIUM_DRIVER", "zink");
            ubtConfig.addEnvironmentVariable("MESA_VK_WSI_DEBUG", "sw");
        }

        if (!ldPath.equals(""))
            LD_LIBRARY_PATH.insert(0, ldPath).insert(0, ":");

        Log.d(TAG, "getEnvVarBin: 渲染路径为" + ldPath + ", 模式为" + rendererName);

//        String[] rendererValues = {""};
//        try {
//            rendererValues = getAppContext().getResources().getStringArray(QH.rslvID(R.array.cont_pref_renderer_values, 0x7f030009));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String renderer = sp.getString(KEY_RENDERER, rendererValues[0]);
//        String ldPath = ""; //不同渲染模式选择不同链接库路径
//        if ("llvmpipe".equals(renderer))
//            ldPath = "/usr/bin/llvmpipe";
//        else if ("virgloverlay".equals(renderer)) {
//            //如果是vo，顺便添加一下vtest_win
//            ubtConfig.addEnvironmentVariable("VTEST_WIN","1");
//            ubtConfig.addEnvironmentVariable("VTEST_SOCK","");
//            ldPath = "/usr/bin/virgloverlay";
//        } else if ("virpipe".equals(renderer))
//            //这里 三合一 vtest叫virpipe，ubt启动设置那个类里，只有判断是virpipe的时候才会new MCat().改动时注意
//            ldPath = "/usr/bin/vtest";
//        else if ("gallium_xlib_zink_turnip".equals(renderer))
//            ldPath = "/usr/bin/zink";
//
//        LD_LIBRARY_PATH.insert(0,':').insert(0,ldPath);
//        Log.d(TAG, "getEnvVarBin: 渲染路径为" + ldPath + ", 模式为" + renderer);
    }

    /**
     * enum可能被修改，所以最好加个判断其是否存在
     */
    private boolean testRenderExist(String name) {
        try {
            ContainerSettingsFragment.renderEntries.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void addWinePath(SharedPreferences sp, UBTLaunchConfiguration ubtConfig, long contId, File xdroidFile) {
        //网上查，环境变量中路径即使带空格也无所谓。只通过冒号分割。
        String winePath = null;//wine执行路径，从xdroid_n/envp.txt中读取
        File envpFile = new File(xdroidFile, "envp.txt");
        try {
            List<String> lines = FileUtils.readLines(envpFile);
            for (String s : lines) {
                if (s.startsWith(KEY_WINE_INSTALL_PATH))
                    winePath = s.substring((KEY_WINE_INSTALL_PATH + "=").length());
            }
        } catch (IOException e) {
            e.printStackTrace();
            //如果不存在v2的txt，考虑读取v1的pref
            winePath = Globals.getAppContext().getSharedPreferences(CONTAINER_CONFIG_FILE_KEY_PREFIX + contId, Context.MODE_PRIVATE).getString(KEY_WINE_INSTALL_PATH, "/usr");
        }
        if (winePath == null) {
            winePath = "/usr";
        }

        //发现貌似用环境变量的方法，/usr/bin/wine会被首先执行。。。所以只好删掉了
        File globalWineBin = new File(getApplicationState().getExagearImage().getPath(), "usr/bin/wine");
        File globalWineBinEscape = new File(getApplicationState().getExagearImage().getPath(), "usr/bin/wine0");
        if (!winePath.equals("/usr") && globalWineBin.exists()) {
            globalWineBinEscape.delete();
            globalWineBin.renameTo(globalWineBinEscape);//重命名为wine0吧
            Log.d(TAG, "addEnvVars: /usr/bin/wine已被重命名为wine0，以防默认wine被启动");
        } else if (winePath.equals("/usr") && globalWineBinEscape.exists() && !globalWineBin.exists()) {
            globalWineBinEscape.renameTo(globalWineBin);//重命名回wine
            Log.d(TAG, "addEnvVars: /usr/bin/wine0已被重命名回wine，因为当前希望的wine二进制路径为/usr/bin");
        }

        ubtConfig.addEnvironmentVariable(
                "PATH",
                winePath + "/bin:" +
                        "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games");

        //先拼接，最后一起加
        LD_LIBRARY_PATH.append(':').append(sp.getString(KEY_WINE_INSTALL_PATH, "/usr")).append("/lib");

        Log.d(TAG, "getEnvVarBin: wine文件夹路径为" + winePath);
    }


}
