package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_ASSETS;
import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import android.app.Application;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncCursorV2 implements Func {
    private static final String TAG = "FuncCursorV2";

    @Override
    public int getInstalledVersion() {
        try {
            return PatcherFile.getAddedFuncVer(getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            return INVALID_VERSION;
        }
    }

    @Override
    public int getLatestVersion() {
        return 1;
    }



    @Override
    public Integer call() throws Exception {
        //如果首次安装，修改ex的dex (version=1用的是png图片方法，也需要首次安装）
        int installedVersion  = getInstalledVersion();
        if (installedVersion == INVALID_VERSION || installedVersion==1)
            firstPatch();
        //换新的方法，改WindowAttributes
        new SmaliFile()
                .findSmali("com.eltechs.axs.xserver.WindowAttributes")
                .deleteMethod(".method public getCursor()Lcom/eltechs/axs/xserver/Cursor;")
                .addMethod(PatcherFile.getSmaliMethod("/com/eltechs/axs/xserver/WindowAttributes.smali", ".method public getCursor()Lcom/eltechs/axs/xserver/Cursor;"))
                .close();
        return R.string.funcname_cursorimg;

    }

    private void firstPatch() throws Exception {


    }

    @Override
    public int getStartMessage() {
        return R.string.funcname_cursorimg;
    }
}
