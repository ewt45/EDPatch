package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.SmaliFile.ACTION_DELETE;
import static com.ewt45.patchapp.patching.SmaliFile.ACTION_INSERT;
import static com.ewt45.patchapp.patching.SmaliFile.LOCATION_AFTER;
import static com.ewt45.patchapp.patching.SmaliFile.LOCATION_BEFORE;

import android.util.Log;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

import java.util.concurrent.Callable;

/**
 * 版本说明
 * 初版，无版本号，只有自定义d盘路径功能。会当做1处理
 * <p>
 * 按位与的方式，用一个int表示自身和子功能的多个版本号
 * 0-4位 自定义d盘，如果此位为0说明整个fab功能都没有
 * 4-8位
 * <p>
 * 类结构说明
 * 自身（fab）和子功能说明。运行时先检测自己自身是否首次添加，再检测每个子功能是否首次添加（通过installedversion判断吧），
 * 是首次添加的就调用一下firstInstall() 然后再调用updateSelfPackage()复制自己的代码
 */
public class FuncFAB implements Func {
    private static final String TAG = "FuncFAB";

    @Override
    public Integer call() throws Exception {
        Sub1DriveD sub1DriveD = new Sub1DriveD();
        Sub2Control sub2Control = new Sub2Control();

        int mergeVersion = getInstalledVersion();

        //首次安装自身
        if (mergeVersion == INVALID_VERSION) {
            Log.d(TAG, "call: 首次安装自身");
            firstInstall();
        }

        //首次安装子功能
        if (((mergeVersion & 0x0000000f)) == INVALID_VERSION) {
            Log.d(TAG, "call: 首次安装子功能-自定义d盘");
            sub1DriveD.firstInstall();
        }
        if (((mergeVersion >> 4) & 0x0000000f) == INVALID_VERSION) {
            Log.d(TAG, "call: 首次安装子功能-自定义按键");
            sub2Control.firstInstall();
        }

        //复制自己的类
        Log.d(TAG, "btnStartPatch: 开始复制自己的smali");
        PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                "/com/example/datainsert/exagear/FAB",
                "/com/example/datainsert/exagear/QH.smali",
                "/com/example/datainsert/exagear/RSIDHelper.smali",
                "/com/example/datainsert/exagear/RR.smali"});

        //复制子功能自己的类
        sub1DriveD.updateSelfPackage();
        sub2Control.updateSelfPackage();
        return R.string.actmsg_funcfab;
    }

    /**
     * 首次安装悬浮按钮
     *
     * @throws Exception
     */
    private void firstInstall() throws Exception {
        Log.d(TAG, "btnStartPatch: 开始修改ex的dex");
        String[] strArr1 = new String[]{
                "new-instance v0, Ljava/io/File;",
                "invoke-static {}, Lcom/eltechs/axs/helpers/AndroidHelpers;->getMainSDCard()Ljava/io/File;",
                "move-result-object v1",
                "const-string v2,",
                "invoke-direct {v0, v1, v2}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V"
        };

        //这里是显示fab按钮本身的，其他的首次安装修改 调用子功能firstInstall（）

        //EDMainActivity
        new SmaliFile()
                .findSmali(null, "EDMainActivity")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method protected onCreate(Landroid/os/Bundle;)V")
                .patch(SmaliFile.LOCATION_BEFORE, ACTION_INSERT,
                        new String[]{"return-void"},
                        new String[]{
                                "new-instance v3, Lcom/example/datainsert/exagear/FAB/FabMenu;",
                                "invoke-direct {v3, p0}, Lcom/example/datainsert/exagear/FAB/FabMenu;-><init>(Landroid/support/v7/app/AppCompatActivity;)V"})
                .close();
    }


    /**
     * //获取的版本是自身版本+全部子功能的版本
     *
     * @return
     */
    @Override
    public int getInstalledVersion() {
        try {
            int a = PatcherFile.getAddedFuncVer(getClass().getSimpleName());
            //如果没有版本信息且存在fabmenu的，就是初版只有一个自定义d盘的，版本号为1
            if (a == INVALID_VERSION && isPatchedOldWay())
                return 1;
            else return a;
        } catch (Exception e) {
            e.printStackTrace();
            return INVALID_VERSION;
        }
    }


    private boolean isPatchedOldWay() {
        boolean patched;
        try {
            SmaliFile edmain = new SmaliFile().findSmali(null, "EDMainActivity");
            patched = edmain.patchedEarlier(".method protected onCreate(Landroid/os/Bundle;)V",
                    SmaliFile.LOCATION_BEFORE, ACTION_INSERT,
                    new String[]{"return-void"},
                    new String[]{
                            "new-instance v3, Lcom/example/datainsert/exagear/FAB/FabMenu;",
                            "invoke-direct {v3, p0}, Lcom/example/datainsert/exagear/FAB/FabMenu;-><init>(Landroid/support/v7/app/AppCompatActivity;)V"});
            edmain.close();

        } catch (Exception e) {
            e.printStackTrace();
            patched = true;
        }
        Log.d(TAG, "funcAdded: 该功能是否已有？" + patched);
        return patched;
    }

    @Override
    public int getLatestVersion() {
        //由多个版本号构成，每个占4位

        /*
        自定义d盘的版本号，如果这个为0说明整个fabmenu没有
        1：初版（旧版没写入版本号）
        2：初次安装后会自动创建Exagear文件夹
         */
        return 0x2
                | 0x1 << 4//自定义按键的版本号
                ;

    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcfab;
    }


    public static class Sub1DriveD {
        public void firstInstall() throws Exception {
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
                    .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method static constructor <clinit>()V")
                    .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, strArr1, strArr1)
                    .patch(SmaliFile.LOCATION_BEFORE, ACTION_INSERT,
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
                    .patch(SmaliFile.LOCATION_BEFORE, ACTION_INSERT,
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
                    .patch(LOCATION_AFTER, ACTION_INSERT, strArr2, new String[]{"invoke-virtual {v3}, Ljava/io/File;->delete()Z"})
                    .close();
        }

        public void updateSelfPackage() {

        }

    }

    public static class Sub2Control {
        public void firstInstall() throws Exception {

        }

        public void updateSelfPackage() throws Exception {
            //自定义操作模式 DefaultControl
            PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                    "/com/eltechs/ed/controls/DefaultControls.smali",
                    //仅供测试用
                    "/com/eltechs/axs/xserver/Pointer.smali",
//                    "/com/eltechs/axs/xserver/ViewFacade.smali",
//                    "/com/eltechs/axs/xserver/PointerEventSender.smali",
//                    "/com/eltechs/axs/xserver/client/XClientWindowListener.smali"
            });

            PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                    "/com/example/datainsert/exagear/controls",});
        }
    }

}
