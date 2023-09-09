package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_LIB_ARMV7;
import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

import java.io.File;

/**
 * 版本号改为2. 添加了中英双语
 */
public class FuncResl implements Func {
    private static final String TAG = "FuncResl";

    @Override
    public int getLatestVersion() {
        return 2;
    }

    @Override
    public int getInstalledVersion() {
        int version = SmaliFile.findVersionInClass("com.example.datainsert.exagear.containerSettings.ConSetResolution");
        if (version != INVALID_VERSION)
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

//        PatcherFile.copy(TYPE_SMALI,
//                new String[]{"/com/eltechs/ed/fragments/ContainerSettingsFragment.smali"},
//                new String[]{"enable_different_renderers"},
//                ".field private static final enable_custom_resolution:Z = true");

        PatcherFile.copy(TYPE_SMALI, new String[]{
                "/com/eltechs/ed/fragments/ContainerSettingsFragment.smali",
                "/com/example/datainsert/exagear/containerSettings/ConSetResolution.smali", //复制标识类
                "/com/example/datainsert/exagear/QH.smali",
                "/com/example/datainsert/exagear/RR.smali"});

//        //测试xegw v2
//        PatcherFile.copy(TYPE_SMALI, new String[]{
//                "/com/termux/x11",
//                "/com/eltechs/axs/widgets/viewOfXServer/ViewOfXServer.smali",
//                "/com/eltechs/axs/xserver/Keyboard.smali",
//                "/com/eltechs/axs/Keyboard.smali",
//                "/com/eltechs/axs/xserver/Pointer.smali",
//        });
//        PatcherFile.copy(TYPE_LIB_ARMV7,new String[]{
//                "/libexec-helper.so",
//                "/libxkbcomp.so",
//                "/libXlorie.so",
//        });


        return R.string.actmsg_funcresl;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcresl;
    }
}
