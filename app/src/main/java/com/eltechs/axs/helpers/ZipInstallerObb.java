package com.eltechs.axs.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.example.datainsert.exagear.obb.ProcessInstallObb;
import com.example.datainsert.exagear.obb.SelectObbFragment;

import java.io.File;
import java.io.IOException;

public class ZipInstallerObb {
    private final Callbacks callbacks;
    private final Context context;
    private final ExagearImage exagearImage;
    private int foundObbVersion;
    private final boolean isMain;
    private final String[] keepOldFiles;
    private final boolean mayTakeFromSdcard;

    public void installImageFromObbIfNeeded() throws IOException {
        Log.d("ZipInstallerObb", "installImageFromObbIfNeeded: 此时开始原解压数据包操作");
    }

    /**
     * 新方法，UnpackExagearImageObb里调用这个。当数据包不存在时显示按钮让用户手动选取
     */
    public void installImageFromObbIfNeededNew() throws IOException {
//        ProcessInstallObb.start(this);
        if(findObbFile()==null && checkObbUnpackNeed() ){//草，必须先调用findObbFile，再调用check，否则check结果不对
//            Log.d("ZipInstallerObb", "显示fragment，need="+checkObbUnpackNeed()+",file="+findObbFile());
            ProcessInstallObb.start(this);
        }else{
//            Log.d("ZipInstallerObb", "正常走installImageFromObbIfNeeded，need="+checkObbUnpackNeed()+",file="+findObbFile());
            installImageFromObbIfNeeded();
        }


    }

    private boolean checkObbUnpackNeed() {
        return true;
    }

    private File findObbFile() {
        return SelectObbFragment.obbFile;
    }


    /* loaded from: classes.dex */
    public interface Callbacks extends ZipUnpacker.Callbacks {
        void error(String str);

        void noObbFound();

        void unpackingCompleted(File file);

        void unpackingInProgress();
    }

    public ZipInstallerObb(Context context, boolean z, boolean z2, ExagearImage exagearImage, Callbacks callbacks, String[] strArr) {
        this.context = context;
        this.isMain = z;
        this.mayTakeFromSdcard = z2;
        this.exagearImage = exagearImage;
        this.callbacks = callbacks;
        this.keepOldFiles = strArr;

    }



}
