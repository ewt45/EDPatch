package com.ewt45.patchapp.thread;

import android.util.Log;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncFAB implements Func {
    String TAG = "FuncFAB";
    @Override
    public Integer call() throws Exception {
        try {
            Log.d(TAG, "btnStartPatch: 开始修改ex的dex");
            String[] strArr1 = new String[]{
                    "new-instance v0, Ljava/io/File;",
                    "invoke-static {}, Lcom/eltechs/axs/helpers/AndroidHelpers;->getMainSDCard()Ljava/io/File;",
                    "move-result-object v1",
                    "const-string v2,",
                    "invoke-direct {v0, v1, v2}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V"
            };


            //EDMainActivity
            new SmaliFile()
                    .findSmali(null, "EDMainActivity")
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method protected onCreate(Landroid/os/Bundle;)V")
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                            new String[]{"return-void"},
                            new String[]{
                                    "new-instance v3, Lcom/example/datainsert/exagear/FAB/FabMenu;",
                                    "invoke-direct {v3, p0}, Lcom/example/datainsert/exagear/FAB/FabMenu;-><init>(Landroid/support/v7/app/AppCompatActivity;)V"})
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method static constructor <clinit>()V")
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, strArr1, strArr1)
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                            new String[]{";->mUserAreaDir:Ljava/io/File;"},
                            new String[]{
                                    "invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;",
                                    "move-result-object v0"})
                    .close();
            //StartGuest
            new SmaliFile()
                    .findSmali(null, "StartGuest")
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method static constructor <clinit>()V")
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, strArr1, strArr1)
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                            new String[]{";->mUserAreaDir:Ljava/io/File;"},
                            new String[]{
                                    "invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;",
                                    "move-result-object v0"})
                    .close();
            //CreateLaunchConfiguration
            String[] strArr2 = new String[]{
                    "const-string v6, \"/dosdevices/d:\"",
                    "invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                    "invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;",
                    "move-result-object v5",
                    "invoke-direct {v3, v4, v5}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V",
            };
            new SmaliFile()
                    .findSmali(null, "CreateLaunchConfiguration")
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public execute()V")
                    .patch(SmaliFile.LOCATION_AFTER, SmaliFile.ACTION_INSERT, strArr2, new String[]{"invoke-virtual {v3}, Ljava/io/File;->delete()Z"})
                    .close();
            //复制自己的类
            Log.d(TAG, "btnStartPatch: 开始复制自己的smali");
            PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                    "/com/example/datainsert/exagear/FAB",
                    "/com/example/datainsert/exagear/RSIDHelper.smali",
                    "/com/example/datainsert/exagear/S.smali"});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.string.actmsg_funcfab;
    }

    /**
     * //先测试一下功能是否已添加过
     * @return
     */
    @Override
    public boolean funcAdded() {
        boolean patched;
        try{
            SmaliFile edmain = new SmaliFile().findSmali(null, "EDMainActivity");
            patched = edmain.patchedEarlier(".method protected onCreate(Landroid/os/Bundle;)V",
                    SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                    new String[]{"return-void"},
                    new String[]{
                            "new-instance v3, Lcom/example/datainsert/exagear/FAB/FabMenu;",
                            "invoke-direct {v3, p0}, Lcom/example/datainsert/exagear/FAB/FabMenu;-><init>(Landroid/support/v7/app/AppCompatActivity;)V"});
            edmain.close();

        }catch (Exception e){
            e.printStackTrace();
            patched = true;
        }
        Log.d(TAG, "funcAdded: 该功能是否已有？"+patched);
        return patched;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcfab;
    }
}
