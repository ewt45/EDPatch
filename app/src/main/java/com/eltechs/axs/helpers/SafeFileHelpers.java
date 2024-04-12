package com.eltechs.axs.helpers;

import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class SafeFileHelpers {
    private static final FileFilter ACCEPT_ANY_FILE;
    private static final FileFilter ACCEPT_EXECUTABLE_FILES;

    /* loaded from: classes.dex */
    public interface FileCallback {
        void apply(File parent, String fileName) throws IOException;
    }

    /* loaded from: classes.dex */
    public interface FileFilter {
        boolean matches(File file, String str) throws IOException;
    }

    public static native boolean exists(String str);

    private static native boolean initialiseNativeParts();

    public static native boolean isDirectory(String str);

    public static native boolean isSymlink(String str);

    private static native String[] listWellNamedFilesImpl(String str);

    private static native int removeDirectoryImpl(String str, boolean z);

    /**
     * 创建符号链接
     * @param source 真实文件的路径
     * @param target 要创建的链接的路径
     */
    public static native int symlink(String source, String target);

    static {
        System.loadLibrary("fs-helpers");
        Assert.state(initialiseNativeParts(), "Managed and native parts of SafeFileHelpers do not match one another.");
        ACCEPT_ANY_FILE = new FileFilter() { // from class: com.eltechs.axs.helpers.SafeFileHelpers.1
            @Override // com.eltechs.axs.helpers.SafeFileHelpers.FileFilter
            public boolean matches(File file, String str) throws IOException {
                return true;
            }
        };
        ACCEPT_EXECUTABLE_FILES = new FileFilter() { // from class: com.eltechs.axs.helpers.SafeFileHelpers.2
            @Override // com.eltechs.axs.helpers.SafeFileHelpers.FileFilter
            public boolean matches(File file, String str) throws IOException {
                return str.toLowerCase().endsWith(".exe");
            }
        };
    }

    private SafeFileHelpers() {
    }

    public static void cleanupDirectory(File file) throws IOException {
        if (file.exists()) {
            Assert.isTrue(file.isDirectory(), String.format("cleanupDirectory(): '%s' is not a directory.", file.getAbsolutePath()));
            int removeDirectoryImpl = removeDirectoryImpl(file.getAbsolutePath(), false);
            if (removeDirectoryImpl != 0) {
                throw new IOException(String.format("Failed to remove directory '%s'; errno = %d", file.getAbsolutePath(), Integer.valueOf(-removeDirectoryImpl)));
            }
        }
    }

    public static void removeDirectory(File file) throws IOException {
        if (file.exists()) {
            Assert.isTrue(file.isDirectory(), String.format("removeDirectory(): '%s' is not a directory.", file.getAbsolutePath()));
            int removeDirectoryImpl = removeDirectoryImpl(file.getAbsolutePath(), true);
            if (removeDirectoryImpl != 0) {
                throw new IOException(String.format("Failed to remove directory '%s'; errno = %d", file.getAbsolutePath(), Integer.valueOf(-removeDirectoryImpl)));
            }
        }
    }

    public static void doWithFiles(File file, FileCallback fileCallback) throws IOException {
        doWithFiles(file, Integer.MAX_VALUE, ACCEPT_ANY_FILE, fileCallback);
    }

    public static void doWithExecutableFiles(File file, FileCallback fileCallback) throws IOException {
        doWithExecutableFiles(file, Integer.MAX_VALUE, fileCallback);
    }

    public static void doWithExecutableFiles(File file, int i, FileCallback fileCallback) throws IOException {
        doWithFiles(file, i, ACCEPT_EXECUTABLE_FILES, fileCallback);
    }

    public static void doWithFiles(File root, int depth, FileFilter fileFilter, FileCallback fileCallback) throws IOException {
        if (depth < 0) {
            return;
        }
        Assert.isTrue(root.isDirectory());
        File rootCanonicalFile = root.getCanonicalFile();
        String[] children = listWellNamedFiles(rootCanonicalFile);
        for (String childName : children) {
            File child = new File(rootCanonicalFile, childName);
            if (child.isFile()) {
                if (fileFilter.matches(rootCanonicalFile, childName)) {
                    fileCallback.apply(rootCanonicalFile, childName);
                }
            } else if (child.isDirectory()) {
                doWithFiles(child, children.length == 1 ? depth : depth - 1, fileFilter, fileCallback);
            }
        }
    }

    private static String[] listWellNamedFiles(File file) throws IOException {
        String[] listWellNamedFilesImpl = listWellNamedFilesImpl(file.getAbsolutePath());
        if (listWellNamedFilesImpl == null) {
            throw new IOException(String.format("Failed to list files with well-formed names in '%s'.", file.getAbsolutePath()));
        }
        return listWellNamedFilesImpl;
    }

    public static FileFilter byNameFileFilter(final String str) {
        return new FileFilter() { // from class: com.eltechs.axs.helpers.SafeFileHelpers.3
            @Override // com.eltechs.axs.helpers.SafeFileHelpers.FileFilter
            public boolean matches(File file, String str2) throws IOException {
                return str2.equalsIgnoreCase(str);
            }
        };
    }
}