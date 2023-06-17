package com.ewt45.patchapp.thread;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.patching.PatcherFile;
import com.ewt45.patchapp.patching.SmaliFile;

public class FuncShortcut implements Func {
    @Override
    public int getStartMessage() {
        return R.string.actmsg_funcshortcut;
    }

    @Override
    public int getInstalledVersion() {
        return SmaliFile.findVersionInClass("com.example.datainsert.exagear.shortcut.MoreShortCut");
    }

    @Override
    public int getLatestVersion() {
        return 1;
    }

    @Override
    public Integer call() throws Exception {
        if (getInstalledVersion() == INVALID_VERSION)
            firstInstall();


        PatcherFile.copy(PatcherFile.TYPE_SMALI, new String[]{
                "/com/example/datainsert/exagear/shortcut",
                "/com/example/datainsert/exagear/RR.smali",
                "/com/example/datainsert/exagear/QH.smali",
        });
        return R.string.actmsg_funcshortcut;

    }

    private void firstInstall() throws Exception {
        //ChooseXDGLinkFragment
        new SmaliFile()
                .findSmali("com.eltechs.ed.fragments.ChooseXDGLinkFragment$XDGNodeAdapter$2")
                .limit(SmaliFile.LIMIT_TYPE_METHOD, ".method public onClick(Landroid/view/View;)V")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                        new String[]{"invoke-virtual {v1}, Landroid/widget/PopupMenu;->show()V"},
                        new String[]{
                                "iget-object p1, p0, Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment$XDGNodeAdapter$2;->this$1:Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment$XDGNodeAdapter;",
                                "iget-object p1, p1, Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment$XDGNodeAdapter;->this$0:Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment;",
                                "invoke-static {p1}, Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment;->access$800(Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment;)Z",
                                "move-result v2",
                                "iget-object p1, v0, Lcom/eltechs/ed/fragments/ChooseXDGLinkFragment$XDGNode;->mLink:Lcom/eltechs/ed/XDGLink;",
                                "invoke-static {v2, v1, p1}, Lcom/example/datainsert/exagear/shortcut/MoreShortcut;->addOptionsToMenu(ZLandroid/widget/PopupMenu;Lcom/eltechs/ed/XDGLink;)V"
                        })
                .close();

        //EDStartupActivity
        String[] deleted = new String[]{
                "new-instance v0, Lcom/eltechs/ed/startupActions/WDesktop",
                "invoke-direct {v0}, Lcom/eltechs/ed/startupActions/WDesktop;-><init>()V",
                "invoke-virtual {v2, v0}, Lcom/eltechs/axs/configuration/startup/StartupActionsCollection;->addAction(Lcom/eltechs/axs/configuration/startup/StartupAction;)V"};
        new SmaliFile()
                .findSmali("com.eltechs.ed.activities.EDStartupActivity").
                limit(SmaliFile.LIMIT_TYPE_METHOD, ".method protected initialiseStartupActions()V")
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_DELETE, deleted, deleted)
                .patch(SmaliFile.LOCATION_BEFORE, SmaliFile.ACTION_INSERT,
                        new String[]{"return-void"},
                        new String[]{"invoke-static {p0}, Lcom/example/datainsert/exagear/shortcut/MoreShortcut;->launchFromShortCutOrNormally(Landroid/support/v7/app/AppCompatActivity;)V"})
                .close();
    }
}
