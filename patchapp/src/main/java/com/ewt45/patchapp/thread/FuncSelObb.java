package com.ewt45.patchapp.thread;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncSelObb implements Func {
    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcselobb;
    }

    @Override
    public boolean funcAdded() {
        String[] origin = new String[]{"invoke-virtual {v8}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->installImageFromObbIfNeeded()V"};
        SmaliFile smaliFile = new SmaliFile();
        boolean patched = smaliFile.findSmali("com.eltechs.axs.configuration.startup.actions", "UnpackExagearImageObb")
                .patchedEarlier(".method public execute()V", origin);
        smaliFile.close();
        return patched;
    }

    @Override
    public Integer call() throws Exception {
        String[] origin = new String[]{"invoke-virtual {v8}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->installImageFromObbIfNeeded()V"};

        String[] dst = new String[]{"invoke-virtual {v8}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->installImageFromObbIfNeededNew()V"};
        //        invoke-virtual {v8}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->installImageFromObbIfNeeded()V

        //UnpackExagearImageObb调用新的install方法
        new SmaliFile().findSmali("com.eltechs.axs.configuration.startup.actions", "UnpackExagearImageObb")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public execute()V")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT, origin, dst)
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, origin, origin)
                .close();

        //ZipInstallerObb 添加新的install方法；获取obb新增一条路径
        new SmaliFile().findSmali("com.eltechs.axs.helpers", "ZipInstallerObb")
                .addMethod(PatcherFile.getSmaliMethod("/com/eltechs/axs/helpers/ZipInstallerObb.smali", ".method public installImageFromObbIfNeededNew()V"))
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method private findObbFile()Ljava/io/File;")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT, new String[]{"return-object v0"},
                        new String[]{"sget-object v0, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->obbFile:Ljava/io/File;"})
                .close();
        ////UnpackExagearImageObb$1 解压完成时删除临时obb
        new SmaliFile().findSmali("com.eltechs.axs.configuration.startup.actions", "UnpackExagearImageObb$1")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public unpackingCompleted(Ljava/io/File;)V")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                        new String[]{"iget-object p1, p0, Lcom/eltechs/axs/configuration/startup/actions/UnpackExagearImageObb$1;->this$0:Lcom/eltechs/axs/configuration/startup/actions/UnpackExagearImageObb;"},
                        new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->delCopiedObb()V"})
                .close();
        //StartupActivity 不是目标请求就忽略
        new SmaliFile().findSmali("com.eltechs.axs.activities", "StartupActivity")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method protected onActivityResult(IILandroid/content/Intent;)V")
                .patch(SmaliFile.LOCATION_AFTER, SmaliFile.ACTION_INSERT,
                        new String[]{".locals 5"},
                        new String[]{
                                "const/16 v1, 0x2711",
                                "if-eq p1, v1, :cond_3a",
                                "invoke-static {p0, p1, p2, p3}, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->receiveResultManually(Landroid/support/v7/app/AppCompatActivity;IILandroid/content/Intent;)V",
                                "goto :goto_3",
                                ":cond_3a"})
                .close();

        //复制自己的类
        PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                "/com/example/datainsert/exagear/obb",
                "/com/example/datainsert/exagear/RSIDHelper.smali",
                "/com/example/datainsert/exagear/RR.smali"});
        return R.string.actmsg_funcselobb;
    }
}
