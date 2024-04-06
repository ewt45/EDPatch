package com.eltechs.axs.ExagearImageConfiguration;

import android.os.Process;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.MemsplitConfigurationAware;
import com.eltechs.axs.environmentService.components.NativeLibsConfiguration;
import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.helpers.SafeFileHelpers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public class ExagearImageConfigurationHelpers {
    private final ExagearImage image;

    public ExagearImageConfigurationHelpers(ExagearImage exagearImage) {
        this.image = exagearImage;
    }

    /**
     * 创建 /etc/passwd文件 （用于存储用户信息），向其中写入一行用户信息
     * @param username 用户名
     * @param home 家目录
     */
    public void createEtcPasswd(String username, String home) throws IOException {
        File file = new File(this.image.getPath(), ExagearImagePaths.ETC_PASSWD);
        int myUid = Process.myUid();
        FileHelpers.touch(file.getPath());
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
        printWriter.printf("%s:x:%d:%d::%s:/bin/sh\n", username, myUid, myUid, home);//用户名，用户标识号，组标识号，家目录
        printWriter.close();
    }

    public void createTmp() throws IOException {
        FileHelpers.createDirectory(new File(this.image.getPath(), "/tmp"));
    }

    public void createWineDisks(String str, WineDiskInfo[] wineDiskInfoArr) throws IOException {
        File file = new File(new File(this.image.getPath(), str), "dosdevices");
        file.mkdirs();
        for (WineDiskInfo wineDiskInfo : wineDiskInfoArr) {
            FileHelpers.createFakeSymlink(file.getPath(), FileHelpers.fixPathForVFAT(String.format("%s:", wineDiskInfo.diskLetter)), wineDiskInfo.diskTargetPath);
        }
    }

    public void createFakeSymlink(String str, String str2, String str3) throws IOException {
        FileHelpers.createFakeSymlink(new File(this.image.getPath(), str).getPath(), str2, str3);
    }

    public void createVpathsList(String... strArr) throws IOException {
        FileHelpers.createDirectory(this.image.getConfigurationDir());
        File vpathsList = this.image.getVpathsList();
        vpathsList.createNewFile();
        PrintWriter printWriter = new PrintWriter(vpathsList);
        for (String str : strArr) {
            if (str.endsWith("/")) {
                printWriter.printf("%s\n", str);
                FileHelpers.createDirectory(new File(this.image.getPath(), str));
            } else {
                printWriter.printf("%s\n", str);
                FileHelpers.touch(new File(this.image.getPath(), str).toString());
            }
        }
        printWriter.close();
    }

    public void recreateX11SocketDir() throws IOException {
        try {
            SafeFileHelpers.removeDirectory(new File(this.image.getPath(), "/tmp/.X11-unix"));
            FileHelpers.createDirectory(new File(this.image.getPath(), "/tmp/.X11-unix"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recreateSoundSocketDir() throws IOException {
        try {
            SafeFileHelpers.removeDirectory(new File(this.image.getPath(), "/tmp/.sound"));
            FileHelpers.createDirectory(new File(this.image.getPath(), "/tmp"), ".sound");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareWineForCurrentMemoryConfiguration(NativeLibsConfiguration nativeLibsConfiguration) throws IOException {
        if (!((MemsplitConfigurationAware) Globals.getApplicationState()).getMemsplitConfiguration().isMemsplit3g()) {
            File path = this.image.getPath();
            FileHelpers.copyFile(new File(path, "usr/lib/i386-linux-gnu/wine/kernel32.dll.so_2g"), new File(path, "usr/lib/i386-linux-gnu/wine/kernel32.dll.so"));
            FileHelpers.copyFile(new File(path, "usr/lib/i386-linux-gnu/wine/ntdll.dll.so_2g"), new File(path, "usr/lib/i386-linux-gnu/wine/ntdll.dll.so"));
            FileHelpers.copyFile(new File(path, "usr/bin/wine_2g"), new File(path, "usr/bin/wine"));
            FileHelpers.copyFile(new File(path, "usr/bin/wine-preloader_2g"), new File(path, "usr/bin/wine-preloader"));
        }
    }
}
