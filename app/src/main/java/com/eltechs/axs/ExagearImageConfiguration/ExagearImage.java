package com.eltechs.axs.ExagearImageConfiguration;

import android.content.Context;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.FileHelpers;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class ExagearImage {
    private final File path;

    private ExagearImage(File path) {
        this.path = path;
    }

    /**
     * 获取镜像目录
     * @param dirName 镜像所在文件夹名（例如 /data/data/包名/files/image，这里就传image
     * @param internalDir true使用AndroidHelpers.getInternalFilesDirectory，false使用getExternalFilesDirectory
     */
    public static ExagearImage find(Context context, String dirName, boolean internalDir) {
        if (internalDir) {
            return new ExagearImage(AndroidHelpers.getInternalFilesDirectory(context, dirName));
        }
        return new ExagearImage(AndroidHelpers.getExternalFilesDirectory(context, dirName));
    }

    public File getPath() {
        return this.path;
    }

    public boolean isValid() {
        return this.path.isDirectory() && FileHelpers.doesFileExist(this.path, ExagearImagePaths.IMG_VERSION);
    }

    public int getImageVersion() {
        try {
            return Integer.parseInt(FileHelpers.readAsLines(new File(this.path, ExagearImagePaths.IMG_VERSION)).get(0));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public File getConfigurationDir() {
        return new File(this.path, ExagearImagePaths.DOT_EXAGEAR);
    }

    public File getVpathsList() {
        return new File(this.path, ExagearImagePaths.EXAGEAR_VPATHS_LIST);
    }
}