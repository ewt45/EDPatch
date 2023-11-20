package com.ewt45.patchapp.thread;


import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

/**
 * 容器设置，额外参数。
 *
 * 1: 初版。
 * 添加cpu核心选项：是否启用核心设置，以及8个（？）核心的选择。
 * 启动 ib
 * 禁用 service.exe
 */
public class FuncOtherArgv implements Func {
    @Override
    public int getStartMessage() {
        return R.string.funcname_otherargv;
    }

    @Override
    public int getInstalledVersion() {
        return SmaliFile.findVersionInClass("com.example.datainsert.exagear.containerSettings.ConSetOtherArgv");
    }

    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    public Integer call() throws Exception {


        //启动容器时添加环境变量
        new FuncAddEnvs().call();

        PatcherFile.copy(TYPE_SMALI, new String[]{
                "/com/eltechs/ed/fragments/ContainerSettingsFragment.smali",
                "/com/example/datainsert/exagear/containerSettings/ConSetOtherArgv.smali", //复制标识类
                "/com/example/datainsert/exagear/containerSettings/otherargv",
                "/com/example/datainsert/exagear/QH.smali",
                "/com/example/datainsert/exagear/RR.smali"});


        return R.string.funcname_otherargv;
    }
}
