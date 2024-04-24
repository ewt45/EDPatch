package com.eltechs.axs.configuration.startup.actions;

import android.util.Log;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.AvailableExecutableFilesAware;
import com.eltechs.axs.applicationState.UserApplicationsDirectoryNameAware;
import com.eltechs.axs.configuration.startup.AsyncStartupAction;
import com.eltechs.axs.configuration.startup.AvailableExecutableFiles;
import com.eltechs.axs.configuration.startup.DetectedExecutableFile;
import com.eltechs.axs.configuration.startup.ExecutableFileDetectorsCollection;
import com.eltechs.axs.configuration.startup.PerApplicationSettingsStore;
import com.eltechs.axs.configuration.startup.StartupActionInfo;
import com.eltechs.axs.helpers.FileFinder;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.helpers.UiThread;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/* loaded from: classes.dex */
public class DetectExecutableFiles<StateClass extends AvailableExecutableFilesAware<StateClass> & UserApplicationsDirectoryNameAware> extends AbstractStartupAction<StateClass> implements AsyncStartupAction<StateClass> {
    private static final int DIR_WITH_USER_APPLICATION_SEARCH_DEPTH = 1;
    private static final int EXE_FILES_SEARCH_DEPTH = 3;
    private static final String[] typicalHelperExeNames = {"setup", "install", "unins", "autorun", "msiexec", "update"};
    private final ExecutableFileDetectorsCollection<StateClass> detectors;
    private final boolean useCache;

    /* loaded from: classes.dex */
    public interface ExecutableFileDetector<StateClass> {
        DetectedExecutableFile<StateClass> detect(File file, String str);
    }

    public DetectExecutableFiles(ExecutableFileDetectorsCollection<StateClass> detectors) {
        this(detectors, true);
    }

    public DetectExecutableFiles(ExecutableFileDetectorsCollection<StateClass> detectors, boolean useCache) {
        this.detectors = detectors;
        this.useCache = useCache;
    }

    private final List<File> removeDuplicatedFiles(List<File> list) {
        ArrayList<File> arrayList = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (File file : list) {
            if (!hashSet.contains(file.getAbsolutePath())) {
                arrayList.add(file);
                hashSet.add(file.getAbsolutePath());
            }
        }
        return arrayList;
    }

    @Override // com.eltechs.axs.configuration.startup.actions.AbstractStartupAction, com.eltechs.axs.configuration.startup.StartupAction
    public StartupActionInfo getInfo() {
        return new StartupActionInfo(getAppContext().getString(R.string.sa_searching_for_exe_files));
    }

    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        try {
            final FilesAccumulator filesAccumulator = new FilesAccumulator();
            List<File> directory = FileFinder.findDirectory(new File("/mnt"), DIR_WITH_USER_APPLICATION_SEARCH_DEPTH, getApplicationState().getUserApplicationsDirectoryName().getName());
            File storageFile = new File("/storage");
            if (storageFile.isDirectory()) {
                directory.addAll(FileFinder.findDirectory(storageFile, DIR_WITH_USER_APPLICATION_SEARCH_DEPTH, getApplicationState().getUserApplicationsDirectoryName().getName()));
            }
            for (File file : removeDuplicatedFiles(directory)) {
                if (file.canWrite()) {
                    DetectedExecutableFilesCache load = DetectedExecutableFilesCache.load(file);
                    if (load != null && this.useCache) {
                        load.doWithFiles(filesAccumulator);
                    } else {
                        Log.i("DetectedExecutableFiles", String.format("Cache for '%s' is stale.", file.getAbsolutePath()));
                        final DetectedExecutableFilesCache createEmpty = DetectedExecutableFilesCache.createEmpty(file);
                        SafeFileHelpers.doWithExecutableFiles(file, EXE_FILES_SEARCH_DEPTH, (parent, str) -> {
                            filesAccumulator.apply(parent, str);
                            createEmpty.addFile(parent, str);
                        });
                        createEmpty.persist();
                    }
                }
            }
            applyPerApplicationSettings(filesAccumulator.getDetectedExecutableFiles());
            applyPerApplicationSettings(filesAccumulator.getOtherExecutableFiles());
            UiThread.post(() -> {
                getApplicationState().setAvailableExecutableFiles(new AvailableExecutableFiles<>(
                        filesAccumulator.getDetectedExecutableFiles(),
                        filesAccumulator.getOtherExecutableFiles()));
                sendDone();
            });
        } catch (IOException e) {
            sendError("Failed to enumerate executable files in /mnt/sdcard/ExaGear/.", e);
        }
    }

    public static boolean isInstallerOrUninstallerOrUpdater(String name) {
        String lowerCase = name.toLowerCase();
        for (String typical : typicalHelperExeNames)
            if (lowerCase.contains(typical))
                return true;
        return false;
    }

    private void applyPerApplicationSettings(List<DetectedExecutableFile<StateClass>> list) {
        for (DetectedExecutableFile<StateClass> detectedExecutableFile : list) {
            try {
                PerApplicationSettingsStore.get(detectedExecutableFile).updateDetectedExecutableFileConfiguration();
            } catch (IOException ignored) {
            }
        }
    }

    /* loaded from: classes.dex */
    private class FilesAccumulator implements SafeFileHelpers.FileCallback {
        private final List<DetectedExecutableFile<StateClass>> detectedExecutableFiles;
        private final List<DetectedExecutableFile<StateClass>> otherExecutableFiles;
        private boolean sorted;

        private FilesAccumulator() {
            this.detectedExecutableFiles = new ArrayList<>();
            this.otherExecutableFiles = new ArrayList<>();
            this.sorted = false;
        }

        @Override // com.eltechs.axs.helpers.SafeFileHelpers.FileCallback
        public void apply(File parent, String fileName) throws IOException {
            for (ExecutableFileDetector<StateClass> executableFileDetector : detectors.getDetectors()) {
                DetectedExecutableFile<StateClass> detectedFiles = executableFileDetector.detect(parent, fileName);
                if (detectedFiles != null) {
                    this.detectedExecutableFiles.add(detectedFiles);
                    return;
                }
            }
            DetectedExecutableFile<StateClass> otherFiles = detectors.getDefaultDetector().detect(parent, fileName);
            if (otherFiles != null) {
                this.otherExecutableFiles.add(otherFiles);
            }
        }

        List<DetectedExecutableFile<StateClass>> getDetectedExecutableFiles() {
            sortIfNeed();
            return this.detectedExecutableFiles;
        }

        List<DetectedExecutableFile<StateClass>> getOtherExecutableFiles() {
            sortIfNeed();
            return this.otherExecutableFiles;
        }

        private void sortIfNeed() {
            if (this.sorted) {
                return;
            }
            Comparator<DetectedExecutableFile<StateClass>> comparator = (file1, file2) ->
                    file1.getFileName().compareTo(file2.getFileName());
            Collections.sort(this.detectedExecutableFiles, comparator);
            Collections.sort(this.otherExecutableFiles, comparator);
            this.sorted = true;
        }
    }
}