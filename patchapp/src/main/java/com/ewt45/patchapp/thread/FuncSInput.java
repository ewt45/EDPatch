package com.ewt45.patchapp.thread;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncSInput implements Func{
    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcsinput;
    }

    @Override
    public boolean funcAdded() {
        SmaliFile test = new SmaliFile().findSmali("com.eltechs.axs.helpers","AndroidHelpers");
        boolean b = test.patchedEarlier(".method public static toggleSoftInput()V",
                SmaliFile.LOCATION_BEFORE,
                SmaliFile.ACTION_INSERT,
                new String[]{"return-void"},
                new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/input/SoftInput;->toggle()V"});
        test.close();
        return b;
    }

    @Override
    public Integer call() throws Exception {
        String[] strings = new String[]{"Landroid/view/inputmethod/InputMethodManager;->toggleSoftInput(II)V"};
        new SmaliFile().findSmali("com.eltechs.axs.helpers","AndroidHelpers")
                .limit(SmaliFile.LIMIT_TYPE_METHOD,".method public static toggleSoftInput()V")
                .patch(SmaliFile.LOCATION_BEFORE,SmaliFile.ACTION_DELETE, strings,strings   )
                .patch(SmaliFile.LOCATION_BEFORE,SmaliFile.ACTION_INSERT,new String[]{"return-void"},
                        new String[]{"invoke-static {}, Lcom/example/datainsert/exagear/input/SoftInput;->toggle()V"})
                .close();
        PatcherFile.copy(PatcherFile.TYPE_SMALI,new String[]{"/com/example/datainsert/exagear/input"});
        return R.string.actmsg_funcsinput;
    }
}
