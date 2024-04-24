package com.eltechs.axs.configuration.startup;

import com.eltechs.axs.configuration.startup.actions.DetectExecutableFiles;
import java.io.File;

/* loaded from: classes.dex */
public abstract class SimpleExecutableFileDetector<StateClass> implements DetectExecutableFiles.ExecutableFileDetector<StateClass> {
    private final String fileName;

    protected abstract DetectedExecutableFile<StateClass> createDescriptor(File file, String str);

    public SimpleExecutableFileDetector(String fileName) {
        this.fileName = fileName;
    }

    @Override // com.eltechs.axs.configuration.startup.actions.DetectExecutableFiles.ExecutableFileDetector
    public final DetectedExecutableFile<StateClass> detect(File file, String name) {
        return name.equalsIgnoreCase(this.fileName) ? createDescriptor(file, name) : null;
    }
}