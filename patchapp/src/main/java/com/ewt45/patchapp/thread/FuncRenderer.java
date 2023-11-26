package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncRenderer implements Func {
    @Override
    public int getStartMessage() {
        return R.string.funcname_renderer;
    }

    @Override
    public int getInstalledVersion() {
        return SmaliFile.findVersionInClass("com.example.datainsert.exagear.containerSettings.ConSetRenderer");
    }

    @Override
    public int getLatestVersion() {
        return 5;
    }

    @Override
    public Integer call() throws Exception {

        //Mcat可能在别的类(UBTLaunchConfiguration)有调用，尝试寻找并删除
        SmaliFile ubtLanConfig = new SmaliFile().findSmali("com.eltechs.axs.configuration.UBTLaunchConfiguration");
        for(int i=0; i<ubtLanConfig.getAllLines().size(); i++){
            if(ubtLanConfig.getAllLines().get(i).contains(" Lcom/eltechs/axs/Mcat;->start()V")){
                ubtLanConfig.getAllLines().remove(i);
                i--;
            }
        }
        ubtLanConfig.close();

        //启动容器时添加环境变量
        new FuncAddEnvs().call();

        PatcherFile.copy(TYPE_SMALI, new String[]{
                "/com/eltechs/ed/fragments/ContainerSettingsFragment.smali",
                "/com/example/datainsert/exagear/containerSettings/ConSetRenderer.smali", //复制标识类
                "/com/example/datainsert/exagear/QH.smali",
                "/com/example/datainsert/exagear/RR.smali"});


        return R.string.funcname_renderer;
    }
}
