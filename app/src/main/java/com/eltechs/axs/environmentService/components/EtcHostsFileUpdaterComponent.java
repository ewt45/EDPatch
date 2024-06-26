package com.eltechs.axs.environmentService.components;

import com.eltechs.axs.ExagearImageConfiguration.ExagearImage;
import com.eltechs.axs.Globals;
import com.eltechs.axs.NetworkStateListener;
import com.eltechs.axs.environmentService.EnvironmentComponent;
import com.eltechs.axs.helpers.Assert;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public class EtcHostsFileUpdaterComponent extends EnvironmentComponent {
    private final ExagearImage exagearImage;
    private transient NetworkStateListener listener = null;

    public EtcHostsFileUpdaterComponent(ExagearImage exagearImage) {
        this.exagearImage = exagearImage;
        Assert.isTrue(exagearImage != null);
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void start() {
        this.listener = new NetworkStateListener(Globals.getAppContext(), ipAddr -> {
            File file = new File(EtcHostsFileUpdaterComponent.this.exagearImage.getPath(), "etc/hosts");
            Assert.isTrue(file.exists() && file.isFile());
            Assert.isTrue(file.canRead() && file.canWrite());
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.printf("%s\t%s\n", ipAddr, "localhost");
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Assert.unreachable();
            }
        });
        this.listener.start();
    }

    @Override // com.eltechs.axs.environmentService.EnvironmentComponent
    public void stop() {
        this.listener.stop();
        this.listener = null;
    }
}
