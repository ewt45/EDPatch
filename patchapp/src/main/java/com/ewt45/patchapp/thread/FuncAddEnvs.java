package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

import java.util.concurrent.Callable;

/**
 * 在启动容器时添加环境变量。不会显示在添加功能的选项上，而是根据需要，如果其他功能需要用到这个， 就尝试安装
 *
 * 调用该函数应该在每次升级时都调用，而不是只有初次安装时调用，因为这个类可能会变化
 */
public class FuncAddEnvs implements Callable<Void> {
    private static final String TAG ="FuncAddEnvs";

    @Override
    public Void call() throws Exception {
        //检查功能是否存在
        SmaliFile startGuest = new SmaliFile().findSmali("com.eltechs.ed.startupActions.StartGuest");
        if (!startGuest.containsLine("AddEnvironmentVariables")) {
            Log.d(TAG, "call: 在Startguest不存在插入语句，开始插入");
            //将功能添加到StartGuest，StartEnvironmentService之前
            startGuest
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public execute()V")
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                            new String[]{"Lcom/eltechs/axs/configuration/startup/actions/StartEnvironmentService;"},
                            new String[]{"new-instance v3, Lcom/example/datainsert/exagear/action/AddEnvironmentVariables;" ,
                                    "invoke-direct {v3}, Lcom/example/datainsert/exagear/action/AddEnvironmentVariables;-><init>()V" ,
                                    "invoke-interface {v2, v3}, Ljava/util/List;->add(Ljava/lang/Object;)Z"}
                    );
        }else{
            Log.d(TAG, "call: 在Startguest已存在插入语句，跳过插入");
        }
        startGuest.close();


        //复制自己的类
        PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                "/com/example/datainsert/exagear/action",
                "/com/example/datainsert/exagear/RR.smali",
                "/com/example/datainsert/exagear/QH.smali",
        });
        Log.d(TAG, "call: AddEnvironmentVariables action 添加成功");

        return null;
    }
}
