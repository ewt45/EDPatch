package com.eltechs.axs.configuration.startup;

import com.eltechs.axs.helpers.AndroidHelpers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.io.IOUtils;

/* loaded from: classes.dex */
public class PerApplicationSettingsStore {
    private final String ecpFile;
    private final DetectedExecutableFile<?> settingsFor;

    private PerApplicationSettingsStore(DetectedExecutableFile<?> detectedExecutableFile) {
        this.settingsFor = detectedExecutableFile;
        String replace = new File(detectedExecutableFile.getParentDir(), detectedExecutableFile.getFileName()).getAbsolutePath().replace(IOUtils.DIR_SEPARATOR_UNIX, '_');
        this.ecpFile = "ecp_" + replace;
    }

    public static PerApplicationSettingsStore get(DetectedExecutableFile<?> detectedExecutableFile) {
        return new PerApplicationSettingsStore(detectedExecutableFile);
    }

    public void updateDetectedExecutableFileConfiguration() throws IOException {
        try {
            this.settingsFor.getEnvironmentCustomisationParameters().copyFrom(loadEnvironmentCustomisationParameters());
        } catch (FileNotFoundException ignored) {
        }
    }

    public void storeDetectedExecutableFileConfiguration() throws IOException {
        storeEnvironmentCustomisationParameters(this.settingsFor.getEnvironmentCustomisationParameters());
    }

    private EnvironmentCustomisationParameters loadEnvironmentCustomisationParameters() throws IOException {
        try {
            FileInputStream openPrivateFileForReading = AndroidHelpers.openPrivateFileForReading(this.ecpFile);
            EnvironmentCustomisationParameters environmentCustomisationParameters = (EnvironmentCustomisationParameters) new ObjectInputStream(openPrivateFileForReading).readObject();
            if (openPrivateFileForReading != null) {
                openPrivateFileForReading.close();
            }
            return environmentCustomisationParameters;
        } catch (ClassNotFoundException e) {
            throw new IOException("Deserialisation of EnvironmentCustomisationParameters has failed.", e);
        }
    }

    private void storeEnvironmentCustomisationParameters(EnvironmentCustomisationParameters environmentCustomisationParameters) throws IOException {
        FileOutputStream openPrivateFileForWriting = AndroidHelpers.openPrivateFileForWriting(this.ecpFile);
        try(ObjectOutputStream oos = new ObjectOutputStream(openPrivateFileForWriting)) {
            oos.writeObject(environmentCustomisationParameters);
        }
    }
}