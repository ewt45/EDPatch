package com.eltechs.axs.configuration.startup.actions;

import android.content.Context;
import android.util.AtomicFile;
//import com.eltechs.axs.R;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.helpers.ZipInstallerObb;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.RSIDHelper;
import com.example.datainsert.exagear.obb.SelectObbFragment;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/* loaded from: classes.dex */
public class UnpackExagearImageObb<StateClass extends ExagearImageAware> extends AbstractStartupAction<StateClass> {
    private final String[] keepOldFiles;
    private final boolean mayTakeFromSdcard;
    private final String progressFileName;

    public UnpackExagearImageObb(boolean z, String[] strArr, String str) {
        this.mayTakeFromSdcard = z;
        this.keepOldFiles = strArr;
        this.progressFileName = str;
    }

    public UnpackExagearImageObb(boolean z, String[] strArr) {
        this.mayTakeFromSdcard = z;
        this.keepOldFiles = strArr;
        this.progressFileName = null;
    }

    public UnpackExagearImageObb(boolean z) {
        this.mayTakeFromSdcard = z;
        this.keepOldFiles = new String[0];
        this.progressFileName = null;
    }

    @Override
    // com.eltechs.axs.configuration.startup.actions.AbstractStartupAction, com.eltechs.axs.configuration.startup.StartupAction
    public StartupActionInfo getInfo() {
        return new StartupActionInfo("", this.progressFileName);
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        ExagearImageAware exagearImageAware = (ExagearImageAware) getApplicationState();
        final Context appContext = getAppContext();
        final File file = this.progressFileName != null ? new File(this.progressFileName) : null;
        if (file != null) {
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            new ZipInstallerObb(appContext, true, this.mayTakeFromSdcard, exagearImageAware.getExagearImage(), new ZipInstallerObb.Callbacks() { // from class: com.eltechs.axs.configuration.startup.actions.UnpackExagearImageObb.1
                @Override // com.eltechs.axs.helpers.ZipInstallerObb.Callbacks
                public void unpackingInProgress() {
                }

                @Override // com.eltechs.axs.helpers.ZipInstallerObb.Callbacks
                public void noObbFound() {
                    UnpackExagearImageObb.this.sendError(appContext.getResources().getString(
                            RSIDHelper.rslvID(R.string.no_obb_file_found, 0x7f0d0071)));
                }

                @Override // com.eltechs.axs.helpers.ZipInstallerObb.Callbacks
                public void unpackingCompleted(File file2) {
                    SelectObbFragment.delCopiedObb();
                    UnpackExagearImageObb.this.sendDone();
                }

                @Override // com.eltechs.axs.helpers.ZipInstallerObb.Callbacks
                public void error(String str) {
                    UnpackExagearImageObb.this.sendError(str);
                }

                @Override // com.eltechs.axs.helpers.ZipUnpacker.Callbacks
                public void reportProgress(long j) {
                    if (file != null) {
                        try {
                            AtomicFile atomicFile = new AtomicFile(file);
                            FileOutputStream startWrite = atomicFile.startWrite();
                            startWrite.write((j + IOUtils.LINE_SEPARATOR_UNIX + "解压中，请等待...").getBytes());
                            atomicFile.finishWrite(startWrite);
                        } catch (IOException ignored) {
                        }
                    }

                }
            }, this.keepOldFiles).installImageFromObbIfNeededNew();
        } catch (IOException e) {
            sendError("Failed to unpack the exagear system image.", e);
        }
    }
}