package com.eltechs.axs.ExagearImageConfiguration;

import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.helpers.SafeFileHelpers;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public class TempDirMaintenanceComponent extends EnvironmentComponent {
    private final File pathToImage;

    public TempDirMaintenanceComponent(ExagearImage exagearImage) {
        this.pathToImage = exagearImage.getPath();
    }

    public File getTempDir() {
        return new File(this.pathToImage, ExagearImagePaths.EXAGEAR_TMP_DIR);
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() throws IOException {
        FileHelpers.createDirectory(getTempDir());
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        cleanTempDir();
    }

    private void cleanTempDir() {
        File tempDir = getTempDir();
        if (tempDir.isDirectory()) {
            try {
                SafeFileHelpers.cleanupDirectory(tempDir);
            } catch (IOException unused) {
            }
        }
    }
}
