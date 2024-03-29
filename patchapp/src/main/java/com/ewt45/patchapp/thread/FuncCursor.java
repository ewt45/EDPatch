package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_ASSETS;
import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import android.app.Application;

import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncCursor implements Func {
    private static final String TAG = "FuncCursor";

    @Override
    public int getInstalledVersion() {
        int version = SmaliFile.findVersionInClass("com.example.datainsert.exagear.cursor.CursorImage");
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

    @Override
    public int getLatestVersion() {
        return 2;
    }

    private boolean isPatchedOldWay() {
        SmaliFile testFile = new SmaliFile().findSmali("com.eltechs.axs.widgets.viewOfXServer.AXSRendererGL");
        boolean patched;
        try {
            patched = testFile.patchedEarlier(
                    ".method public constructor <init>",
                    SmaliFile.LOCATION_BEFORE,
                    SmaliFile.ACTION_INSERT,
                    new String[]{
                            "move-result-object p1",
                            "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"},
                    new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/cursor/CursorImage;->createBitmap()Landroid/graphics/Bitmap;"})
                    //或者可能直接把函数加到那个类里了
                    | testFile.containsLine("Resources;->getAssets()");


        } catch (Exception e) {
            e.printStackTrace();
            patched = true;
        }
        testFile.close();
        return patched;
    }


    @Override
    public Integer call() throws Exception {
        //如果首次安装，修改ex的dex (version=1用的是png图片方法，也需要首次安装）
        int installedVersion = getInstalledVersion();
        if (installedVersion == INVALID_VERSION)
            firstPatch();

        //复制自己的类和光标
        PatcherFile.copy(TYPE_SMALI, new String[]{"/com/example/datainsert/exagear/cursor"});
        PatcherFile.copy(TYPE_ASSETS, new String[]{"/mouse.png"});


        return R.string.funcname_cursorimg;

    }

    private void firstPatch() throws Exception {
        String[] origin1 = new String[]{
                "invoke-direct {p0}, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->createXCursorBitmap()Landroid/graphics/Bitmap;",
                "move-result-object p1",
                "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"};
        String[] origin2 = new String[]{
                "move-result-object p1",
                "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"};

        String[] dstDelete = new String[]{"invoke-direct {p0}, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->createXCursorBitmap()Landroid/graphics/Bitmap;"};
        String[] dstAdd = new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/cursor/CursorImage;->createBitmap()Landroid/graphics/Bitmap;"};
        new SmaliFile().findSmali("com.eltechs.axs.widgets.viewOfXServer.AXSRendererGL")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public constructor <init>")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, origin1, dstDelete)
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT, origin2, dstAdd)
                .close();

    }

    @Override
    public int getStartMessage() {
        return R.string.funcname_cursorimg;
    }
}
