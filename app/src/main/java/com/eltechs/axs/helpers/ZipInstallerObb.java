package com.eltechs.axs.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.ExagearImageConfiguration.ExagearImagePaths;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.example.datainsert.exagear.obb.ProcessInstallObb;
import com.example.datainsert.exagear.obb.SelectObbFragment;

import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class ZipInstallerObb {
    private final Callbacks callbacks;
    private final Context context;
    private final ExagearImage exagearImage;
    private final boolean isMain;
    private final String[] keepOldFiles;
    private final boolean mayTakeFromSdcard;
    private int foundObbVersion;

    public ZipInstallerObb(Context context, boolean isMain, boolean mayTakeFromSdcard, ExagearImage exagearImage, Callbacks callbacks, String[] keepOldFiles) {
        this.context = context;
        this.isMain = isMain;
        this.mayTakeFromSdcard = mayTakeFromSdcard;
        this.exagearImage = exagearImage;
        this.callbacks = callbacks;
        this.keepOldFiles = keepOldFiles;

    }


    @SuppressLint("StaticFieldLeak")
    public void installImageFromObbIfNeeded() throws IOException {
        Log.d("ZipInstallerObb", "installImageFromObbIfNeeded: 此时开始原解压数据包操作");

//        callbacks.unpackingCompleted(this.exagearImage.getPath());
        final File obbFile = findObbFile();
        boolean checkObbUnpackNeed = checkObbUnpackNeed();
        final File path = this.exagearImage.getPath();
        if (!checkObbUnpackNeed) {
            this.callbacks.unpackingCompleted(path);
        } else if (obbFile == null) {
            this.callbacks.noObbFound();
        } else {
            new AsyncTask<Object, Object, Object>() { // from class: com.eltechs.axs.helpers.ZipInstallerObb.1
                @Override // android.os.AsyncTask
                protected Object doInBackground(Object... objArr) {
                    try {
                        UiThread.post(callbacks::unpackingInProgress);
                        if (keepOldFiles.length == 0) {
                            SafeFileHelpers.removeDirectory(path);
                            FileHelpers.createDirectory(path);
                        } else {
                            if (!path.exists()) {
                                path.mkdir();
                            } else {
                                Assert.isTrue(path.isDirectory());
                            }
                            SafeFileHelpers.doWithFiles(path, 1, (file, str) -> {
                                File file2 = new File(file, str);
                                for (String str2 : ZipInstallerObb.this.keepOldFiles) {
                                    if (file2.getName().equals(str2)) {
                                        return false;
                                    }
                                }
                                return true;
                            }, (parent, fileName) -> {
                                File file = new File(parent, fileName);
                                if (file.isDirectory()) {
                                    SafeFileHelpers.removeDirectory(file);
                                } else {
                                    file.delete();
                                }
                            });
                        }
                        ZipUnpacker.unpackZip(obbFile, path, callbacks);
                        FileHelpers.createDirectory(path, ExagearImagePaths.DOT_EXAGEAR);
                        createFileWithObbVersion(path);
                        UiThread.post(() -> callbacks.unpackingCompleted(path));
                        return null;
                    } catch (IOException e) {
                        UiThread.post(() -> callbacks.error(e.getMessage()));
                        return null;
                    }
                }
            }.execute();
        }
    }


    /**
     * 新方法，UnpackExagearImageObb里调用这个。当数据包不存在时显示按钮让用户手动选取
     */
    public void installImageFromObbIfNeededNew() throws IOException {
//        ProcessInstallObb.start(this);
        if (findObbFile() == null && checkObbUnpackNeed()) {//草，必须先调用findObbFile，再调用check，否则check结果不对
//            Log.d("ZipInstallerObb", "显示fragment，need="+checkObbUnpackNeed()+",file="+findObbFile());
            ProcessInstallObb.start(this);
        } else {
//            Log.d("ZipInstallerObb", "正常走installImageFromObbIfNeeded，need="+checkObbUnpackNeed()+",file="+findObbFile());
            installImageFromObbIfNeeded();
        }
    }

    public void createFileWithObbVersion(File file) throws IOException {
        File file2 = new File(file, ExagearImagePaths.IMG_VERSION);
        file2.createNewFile();
        FileWriter fileWriter = new FileWriter(file2);
        fileWriter.write(String.format("%d\n", Integer.valueOf(this.foundObbVersion)));
        fileWriter.close();
    }


    private boolean checkObbUnpackNeed() throws FileNotFoundException, IOException {
        return !this.exagearImage.isValid() || this.exagearImage.getImageVersion() < this.foundObbVersion;
    }


    private File findObbFile() {
        try {
            this.foundObbVersion = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            this.foundObbVersion = 0;
        }
        while (this.foundObbVersion >= 0) {
            Object[] objArr = new Object[3];
            objArr[0] = this.isMain ? "main" : "patch";
            objArr[1] = this.foundObbVersion;
            objArr[2] = this.context.getPackageName();
            @SuppressLint("DefaultLocale") String format = String.format("%s.%d.%s.obb", objArr);
            File file = new File(this.context.getObbDir(), format);
            if (!file.exists() && this.mayTakeFromSdcard) {
                file = new File(AndroidHelpers.getMainSDCard(), format);
            }
            if (file.exists()) {
                return file;
            }
            this.foundObbVersion--;
        }
        this.foundObbVersion = -1;

        //如果都没找到，那就用自己复制到内部目录下的文件
        return SelectObbFragment.obbFile;
    }


    /* loaded from: classes.dex */
    public interface Callbacks extends ZipUnpacker.Callbacks {
        void error(String str);

        void noObbFound();

        void unpackingCompleted(File file);

        void unpackingInProgress();
    }


}
