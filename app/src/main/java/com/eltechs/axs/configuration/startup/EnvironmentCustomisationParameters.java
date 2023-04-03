package com.eltechs.axs.configuration.startup;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.ScreenInfo;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/* loaded from: classes.dex */
public class EnvironmentCustomisationParameters implements Externalizable {
    public static String DEFAULT_CONTROLS_NAME_NONE = "None";
    private static final int STORAGE_FORMAT_VERSION_1 = 1;
    private static final int STORAGE_FORMAT_VERSION_CURR = 2;
    private static final long serialVersionUID = -353894079071511841L;
    private ScreenInfo screenInfo = new ScreenInfo(800, 600, 80, 60, 32);
    private String localeName = "zh_CN.UTF-8";
    private String defaultControlsName = DEFAULT_CONTROLS_NAME_NONE;

    public ScreenInfo getScreenInfo() {
        return this.screenInfo;
    }

    public void setScreenInfo(ScreenInfo screenInfo) {
        this.screenInfo = screenInfo;
    }

    public String getLocaleName() {
        return this.localeName;
    }

    public void setLocaleName(String str) {
        this.localeName = str;
    }

    public String getDefaultControlsName() {
        return this.defaultControlsName;
    }

    public void setDefaultControlsName(String str) {
        this.defaultControlsName = str;
    }

    public void copyFrom(EnvironmentCustomisationParameters environmentCustomisationParameters) {
        this.screenInfo = environmentCustomisationParameters.screenInfo;
        this.localeName = environmentCustomisationParameters.localeName;
        this.defaultControlsName = environmentCustomisationParameters.defaultControlsName;
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        int readInt = objectInput.readInt();
        Assert.isTrue(readInt <= 2, "Attempted to load EnvironmentCustomisationParameters created by a newer version of AXS.");
        if (readInt == 1) {
            this.screenInfo = (ScreenInfo) objectInput.readObject();
            this.localeName = objectInput.readUTF();
            this.defaultControlsName = DEFAULT_CONTROLS_NAME_NONE;
        } else if (readInt == 2) {
            this.screenInfo = (ScreenInfo) objectInput.readObject();
            this.localeName = objectInput.readUTF();
            this.defaultControlsName = objectInput.readUTF();
        }
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(2);
        objectOutput.writeObject(this.screenInfo);
        objectOutput.writeUTF(this.localeName);
        objectOutput.writeUTF(this.defaultControlsName);
    }
}