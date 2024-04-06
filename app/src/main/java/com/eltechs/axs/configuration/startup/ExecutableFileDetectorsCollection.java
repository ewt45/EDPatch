package com.eltechs.axs.configuration.startup;

import com.eltechs.axs.configuration.startup.actions.DetectExecutableFiles;
import java.util.Collection;

/* loaded from: classes.dex */
public class ExecutableFileDetectorsCollection<StateClass> {
    private final DetectExecutableFiles.ExecutableFileDetector<StateClass> defaultDetector;
    private final Collection<? extends DetectExecutableFiles.ExecutableFileDetector<StateClass>> detectors;

    public ExecutableFileDetectorsCollection(Collection<? extends DetectExecutableFiles.ExecutableFileDetector<StateClass>> collection, DetectExecutableFiles.ExecutableFileDetector<StateClass> executableFileDetector) {
        this.detectors = collection;
        this.defaultDetector = executableFileDetector;
    }

    public Collection<? extends DetectExecutableFiles.ExecutableFileDetector<StateClass>> getDetectors() {
        return this.detectors;
    }

    public DetectExecutableFiles.ExecutableFileDetector<StateClass> getDefaultDetector() {
        return this.defaultDetector;
    }
}