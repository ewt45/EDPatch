package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

import java.io.File;

public class FuncMultiWine implements Func{
    private static final String TAG ="FuncMultiWine";
    @Override
    public int getStartMessage() {
        return R.string.funcname_mw;
    }

    @Override
    public int getInstalledVersion() {
        int version = SmaliFile.findVersionInClass("com.example.datainsert.exagear.mutiWine.MutiWine");
        if(version==INVALID_VERSION){
            SmaliFile file = new SmaliFile().findSmali("com.example.datainsert.exagear.mutiWine.MutiWine");
            if(file!=null){
                version =1;
                file.close();
            }
        }
        return version;
    }

    @Override
    public int getLatestVersion() {
        return 2;
    }

    @Override
    public Integer call() throws Exception {
        Log.d(TAG, "call: 开始添加多wine共存功能");
        //首次安装
        if(getInstalledVersion()==INVALID_VERSION){
            //ManageContainerFragment
            new SmaliFile()
                    .findSmali("com.eltechs.ed.fragments.ManageContainersFragment")
                    //删除onOptionsItemSelected方法
                    .deleteMethod(".method public onOptionsItemSelected")
                    //修改onCreateOptionsMenu内容
                    .deleteMethod(".method public onCreateOptionsMenu")
                    .addMethod(PatcherFile.getSmaliMethod("com/eltechs/ed/fragments/ManageContainersFragment.smali",".method public onCreateOptionsMenu"))
                    .close();
        }

        //StartGuest 添加环境变量
        new FuncAddEnvs().call();

        //复制文件夹
        PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                "/com/google/gson", //json依赖
                "/org/tukaani/xz", //xz解压依赖
                "/com/example/datainsert/exagear/mutiWine",
                "/com/example/datainsert/exagear/RR.smali",
                "/com/example/datainsert/exagear/QH.smali",
        });

        return R.string.funcname_mw;
    }
}
