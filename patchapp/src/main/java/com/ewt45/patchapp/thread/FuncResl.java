package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

import java.io.File;

/**
 * 版本号改为2. 添加了中英双语 */
public class FuncResl implements Func {
    private static final String TAG = "FuncResl";
    @Override
    public int getLatestVersion() {
        return 2;
    }
    @Override
    public int getInstalledVersion() {
        int version = SmaliFile.findVersionInClass("com.eltechs.ed.fragments.ContainerSettingsFragment");
        if(version!=INVALID_VERSION)
            return version;

        try {
            int a = PatcherFile.getAddedFuncVer(getClass().getSimpleName());
            if (a == INVALID_VERSION && isPatchedOldWay())
                return 1;
            else return a;
        } catch (Exception e) {
            e.printStackTrace();
            return INVALID_VERSION;
        }
    }

    private boolean isPatchedOldWay() {
        SmaliFile fragment = new SmaliFile().findSmali("com.eltechs.ed.fragments.ContainerSettingsFragment");
        boolean patched = fragment.containsLine("RadioGroup");
        fragment.close();
        return patched;
    }

    @Override
    public Integer call() throws Exception {


        PatcherFile.copy(TYPE_SMALI, new String[]{
                "/com/eltechs/ed/fragments/ContainerSettingsFragment.smali",
                "/com/example/datainsert/exagear/QH.smali",
                "/com/example/datainsert/exagear/RR.smali"});


        return R.string.actmsg_funcresl;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcresl;
    }
}
