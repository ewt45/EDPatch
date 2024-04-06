package com.eltechs.axs.configuration.startup.actions;

import static com.eltechs.axs.helpers.AndroidHelpers.openPrivateFileForWriting;

import android.annotation.SuppressLint;
import android.util.Log;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.helpers.SafeFileHelpers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;

/* loaded from: classes.dex */
public class DetectedExecutableFilesCache implements Serializable {
    private static final String CACHE_FILE_NAME_PREFIX = "executable_files_list_cache_";
    private final Set<DirectoryCacheEntry> cachedDirectories = new HashSet<>();
    private final Set<FileCacheEntry> cachedFiles = new HashSet<>();
    private File rootDir;

    private DetectedExecutableFilesCache(File rootDir) {
        this.rootDir = rootDir;
    }

    @SuppressLint("LongLogTag")
    public static DetectedExecutableFilesCache load(File rootDir) {
        try {
            FileInputStream fis = AndroidHelpers.openPrivateFileForReading(CACHE_FILE_NAME_PREFIX + rootDir.getAbsolutePath().replace(IOUtils.DIR_SEPARATOR_UNIX, '_'));
            DetectedExecutableFilesCache cache = (DetectedExecutableFilesCache) new ObjectInputStream(fis).readObject();
            cache.rootDir = rootDir;
            if (!cache.isUpToDate()) {
                cache = null;
            }
            if (fis != null) {
                fis.close();
            }
            return cache;
        } catch (Exception e) {
            Log.w("DetectedExecutableFilesCache", String.format("There was an error reading the cache for '%s'.", rootDir.getAbsolutePath()), e);
            return null;
        }
    }

    public static DetectedExecutableFilesCache createEmpty(File rootDir) {
        DetectedExecutableFilesCache cache = new DetectedExecutableFilesCache(rootDir);
        cache.cachedDirectories.add(new DirectoryCacheEntry(rootDir, rootDir.lastModified()));
        return cache;
    }

    public void persist() {
        try(FileOutputStream fos = openPrivateFileForWriting(CACHE_FILE_NAME_PREFIX + this.rootDir.getAbsolutePath().replace(IOUtils.DIR_SEPARATOR_UNIX, '_'));
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        } catch (Exception ignored) {
        }
    }

    public boolean isUpToDate() throws IOException {
        String absolutePath = this.rootDir.getAbsolutePath();
        for (DirectoryCacheEntry directoryCacheEntry : this.cachedDirectories) {
            if (!directoryCacheEntry.dir.isDirectory()
                    || directoryCacheEntry.dir.lastModified() != directoryCacheEntry.modificationTime
                    || !directoryCacheEntry.dir.getAbsolutePath().startsWith(absolutePath)) {
                return false;
            }
        }
        for (FileCacheEntry fileCacheEntry : this.cachedFiles) {
            File file = new File(fileCacheEntry.parentDir, fileCacheEntry.name);
            if (!file.isFile() || !file.getAbsolutePath().startsWith(absolutePath)) {
                return false;
            }
        }
        return true;
    }

    public void doWithFiles(SafeFileHelpers.FileCallback fileCallback) throws IOException {
        for (FileCacheEntry fileCacheEntry : this.cachedFiles) {
            fileCallback.apply(fileCacheEntry.parentDir, fileCacheEntry.name);
        }
    }

    public void addFile(File parent, String name) throws IOException {
        Assert.isTrue(parent.getAbsolutePath().startsWith(this.rootDir.getAbsolutePath()));
        this.cachedFiles.add(new FileCacheEntry(parent, name));
        while (!parent.equals(this.rootDir)) {
            this.cachedDirectories.add(new DirectoryCacheEntry(parent, parent.lastModified()));
            parent = parent.getParentFile();
        }
    }

    /* loaded from: classes.dex */
    public static class DirectoryCacheEntry implements Serializable {
        public final File dir;
        public final long modificationTime;

        public DirectoryCacheEntry(File dir, long modificationTime) {
            this.dir = dir;
            this.modificationTime = modificationTime;
        }

        public boolean equals(Object obj) {
            if (obj instanceof DirectoryCacheEntry) {
                return this.dir.equals(((DirectoryCacheEntry) obj).dir);
            }
            return false;
        }

        public int hashCode() {
            return this.dir.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public static class FileCacheEntry implements Serializable {
        public final String name;
        public final File parentDir;

        FileCacheEntry(File parentDir, String name) {
            this.parentDir = parentDir;
            this.name = name;
        }

        public boolean equals(Object obj) {
            if (obj instanceof FileCacheEntry) {
                FileCacheEntry fileCacheEntry = (FileCacheEntry) obj;
                return this.parentDir.equals(fileCacheEntry.parentDir) && this.name.equals(fileCacheEntry.name);
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }
}