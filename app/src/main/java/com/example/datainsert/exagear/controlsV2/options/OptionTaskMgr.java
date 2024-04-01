package com.example.datainsert.exagear.controlsV2.options;

//TODO 打开任务管理器

import static com.eltechs.ed.guestContainers.GuestContainerConfig.CONTAINER_CONFIG_FILE_KEY_PREFIX;
import static com.example.datainsert.exagear.controlsV2.Const.OPTION_TASKMGR_START_SH_ENV;
import static com.example.datainsert.exagear.mutiWine.MutiWine.KEY_WINE_INSTALL_PATH;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.MemoryFile;
import android.os.SharedMemory;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.configuration.UBTLaunchConfiguration;
import com.eltechs.axs.configuration.startup.EnvironmentCustomisationParameters;
import com.eltechs.axs.environmentService.components.GuestApplicationsTrackerComponent;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.ed.EDApplicationState;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 试试 GuestApplicationsTrackerComponent.startGuestApplication()
 * 传入的参数为 UBTLaunchConfiguration 。其构建参考 CreateLaunchConfiguration.execute()
 * 多wine的话还要获取一下wine路径。
 *
 * 如果winecfg里开启虚拟桌面的话，在wine explorer /desktop=shell 参数中
 * 不指明分辨率则会用winecfg里写的，导致出现两个桌面。所以需要关闭winecfg里的那个选项
 * 啊不对好像也没问题，第一次启动会多出一个默认分辨率的空桌面而已
 */
public class OptionTaskMgr extends AbstractOption{
    private static final String DEFAULT_CMD = "";
    @Override
    public void run() {
        EDApplicationState eDApplicationState = (EDApplicationState) Globals.getApplicationState();
        assert eDApplicationState!=null;
        UBTLaunchConfiguration oldConfig = eDApplicationState.getUBTLaunchConfiguration();
        String altCmd = Const.Pref.getRunTaskmgrAlt();
        if(altCmd.trim().isEmpty())
            altCmd = OPTION_TASKMGR_START_SH_ENV + //"$ANOTHER_SH"
                    "\neval \"wine explorer /desktop=shell taskmgr\"";
        String[] newArgs = altCmd.replace(OPTION_TASKMGR_START_SH_ENV,getShPath()).split("\n");

//        EnvironmentCustomisationParameters environmentCustomisationParameters = eDApplicationState.getSelectedExecutableFile().getEnvironmentCustomisationParameters();
        UBTLaunchConfiguration newConfig = new UBTLaunchConfiguration();
        newConfig.setFsRoot(oldConfig.getFsRoot());

        // /data/user/0/com.eltechs.ed/files/image/home/xdroid/.wine/dosdevices/z:/opt
        newConfig.setGuestExecutablePath(oldConfig.getGuestExecutablePath());
        // /home/xdroid/.wine/run.sh
        newConfig.setGuestExecutable(
                newArgs[0]
//                oldConfig.getGuestExecutable() //一定可以
//                "/bin/bash" //找不到 eval \"wine explorer /desktop=shell taskmgr\"
                //(winePath+"/bin/wine"); //可以但是wine路径要自己找太麻烦
        );
        // /home/xdroid/.wine/run.sh, eval "TU_DEBUG=noconform env ib GALLIUM_DRIVER=zink mesa_glthread=true ZINK_CONTEXT_MODE=base MESA_EXTENSION_MAX_YEAR=2002 MESA_GL_VERSION_OVERRIDE=4.6 WINEDEBUG=-all MESA_NO_ERROR=1 MESA_DEBUG=silent ZINK_DESCRIPTORS=auto  __GL_THREADED_OPTIMIZATION=1 MESA_VK_WSI_DEBUG=sw DXVK_HUD=version,fps,scale=0.8 GALLIUM_HUD=simple,fps WINEDEBUG=-all WINEPREFIX="/home/xdroid/.wine" taskset -c 4,5,6,7 wine explorer /desktop=shell,640x480 /opt/TFM.exe  "
        newConfig.setGuestArguments(
                newArgs
//                new String[]{oldConfig.getGuestExecutable(),"eval \"wine explorer /desktop=shell taskmgr\""} //一定可以
//                new String[]{"/bin/bash","eval \"wine explorer /desktop=shell taskmgr\""} //找不到 eval \"wine explorer /desktop=shell taskmgr\"
//                new String[]{winePath+"/bin/wine","explorer","/desktop=shell","taskmgr"} //可以但是wine路径要自己找太麻烦
        );

        newConfig.setGuestEnvironmentVariables(oldConfig.getGuestEnvironmentVariables());
        newConfig.setVfsHacks(EnumSet.of(UBTLaunchConfiguration.VFSHacks.TREAT_LSTAT_SOCKET_AS_STATTING_WINESERVER_SOCKET, UBTLaunchConfiguration.VFSHacks.TRUNCATE_STAT_INODE, UBTLaunchConfiguration.VFSHacks.SIMPLE_PASS_DEV));
        newConfig.setSocketPathSuffix(oldConfig.getSocketPathSuffix());
//        newConfig.addEnvironmentVariable(
//                "PATH",
//                winePath + "/bin:" +
//                        "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games");



//        eDApplicationState.setUBTLaunchConfiguration(newConfig);

        GuestApplicationsTrackerComponent component = eDApplicationState.getEnvironment().getComponent(GuestApplicationsTrackerComponent.class);
        component.startGuestApplication(newConfig);

    }

    private String getWinePath() {
        //网上查，环境变量中路径即使带空格也无所谓。只通过冒号分割。
        String winePath = null;//wine执行路径，从xdroid_n/envp.txt中读取
        File envpFile = new File(getImagePath(), "home/xdroid/envp.txt");
        try {
            List<String> lines = FileUtils.readLines(envpFile);
            for (String s : lines) {
                if (s.startsWith(KEY_WINE_INSTALL_PATH))
                    winePath = s.substring((KEY_WINE_INSTALL_PATH + "=").length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (winePath == null) {
            winePath = "/usr";
        }
        return winePath;
    }

    private String getShPath(){
        File shFile = new File(Const.Files.workDir,"run-another-ubt.sh");
        shFile.delete();
        if(!shFile.exists()){
            try {
                FileUtils.writeStringToFile(shFile,"#!/bin/bash\neval \"$@\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shFile.getAbsolutePath().replace(getImagePath().getAbsolutePath(), "");
    }

    private File getImagePath(){
        return ((EDApplicationState) Objects.requireNonNull(Globals.getApplicationState())).getExagearImage().getPath();
    }
    @Override
    public String getName() {
        return RR.getS(RR.ctr2_option_taskmgr);
    }
}
