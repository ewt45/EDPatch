package com.eltechs.axs.ExagearImageConfiguration;

import java.io.Serializable;

/* loaded from: classes.dex */
public class WineDiskInfo implements Serializable {
    public final String diskLetter;
    public final String diskTargetPath;

    public WineDiskInfo(String diskLetter, String diskTargetPath) {
        this.diskLetter = diskLetter;
        this.diskTargetPath = diskTargetPath;
    }
}
