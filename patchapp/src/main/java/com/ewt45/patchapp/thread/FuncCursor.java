package com.ewt45.patchapp.thread;

import static com.ewt45.patchapp.patching.PatcherFile.TYPE_ASSETS;
import static com.ewt45.patchapp.patching.PatcherFile.TYPE_SMALI;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncCursor implements  Func {
    @Override
    public boolean funcAdded() {
        SmaliFile testFile = new SmaliFile().findSmali("com.eltechs.axs.widgets.viewOfXServer", "AXSRendererGL");
        boolean patched;
        try{
            patched = testFile.patchedEarlier(
                    ".method public constructor <init>",
                    SmaliFile.LOCATION_BEFORE,
                    SmaliFile.ACTION_INSERT,
                    new String[]{
                            "move-result-object p1",
                            "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"},
                    new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/cursor/CursorImage;->createBitmap()Landroid/graphics/Bitmap;"});
        }catch (Exception e){
            e.printStackTrace();
            patched =true;
        }
        return  patched;
    }

    @Override
    public Integer call() throws Exception {
        //AXSRendererGL
        String[] origin1 = new String[]{
                "invoke-direct {p0}, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->createXCursorBitmap()Landroid/graphics/Bitmap;",
                "move-result-object p1",
                "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"};
        String[] origin2 = new String[]{
                "move-result-object p1",
                "iput-object p1, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;"};

        String[] dstDelete = new String[]{"invoke-direct {p0}, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->createXCursorBitmap()Landroid/graphics/Bitmap;"};
        String[] dstAdd = new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/cursor/CursorImage;->createBitmap()Landroid/graphics/Bitmap;"};
        new SmaliFile().findSmali("com.eltechs.axs.widgets.viewOfXServer", "AXSRendererGL")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public constructor <init>")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, origin1, dstDelete)
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT, origin2, dstAdd)
                .close();
        //复制自己的类和光标
        PatcherFile.copy(TYPE_SMALI, new String[]{"/com/example/datainsert/exagear/cursor"});
        PatcherFile.copy(TYPE_ASSETS,new String[]{"/mouse.png"});
        return R.string.actmsg_funccursor;

    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_funccursor;
    }
}