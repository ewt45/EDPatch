package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;

import java.io.File;

public class FuncResl implements Func {

    @Override
    public boolean funcAdded() {
        File smaliFile = new File(PatchUtils.getPatchTmpDir() + "/tmp/smali/"
                +PatchUtils.getPackageName()+"/fragments/ContainerSettingsFragment$3.smali");

        return smaliFile.exists();
    }

    @Override
    public Integer call() throws Exception {
        PatcherFile.copy(TYPE_SMALI, new String[]{
                "/com/eltechs/ed/fragments/ContainerSettingsFragment.smali",
                "/com/eltechs/ed/fragments/ContainerSettingsFragment$1.smali",
                "/com/eltechs/ed/fragments/ContainerSettingsFragment$2.smali",
                "/com/eltechs/ed/fragments/ContainerSettingsFragment$3.smali",
                "/com/example/datainsert/exagear/RSIDHelper.smali",
                "/com/example/datainsert/exagear/RR.smali"});
        return R.string.actmsg_funcresl;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcresl;
    }
}
