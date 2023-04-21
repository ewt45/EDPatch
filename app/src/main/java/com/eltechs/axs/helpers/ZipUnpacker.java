package com.eltechs.axs.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* loaded from: classes.dex */
public abstract class ZipUnpacker {

    /* loaded from: classes.dex */
    public interface Callbacks {
        void reportProgress(long j);
    }

    private ZipUnpacker() {
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00dc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void unpackZip(File targetFile, File dstDir, final Callbacks callbacks) throws IOException {
        IOException iOException;
        ZipFile zipFile;
        Assert.isTrue(dstDir.isDirectory(), String.format("'%s' must be a directory.", dstDir.getAbsolutePath()));
        long length = targetFile.length();
        if (callbacks != null) {
            UiThread.post(() -> callbacks.reportProgress(-1L));
        }
        zipFile = new ZipFile(targetFile);
        try {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            long extractedSize = 0;
            long lastProgress = 0;
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                if (zipArchiveEntry.isDirectory()) {
                    File newDir = new File(dstDir, zipArchiveEntry.getName());
                    if (newDir.exists()) {
                        if (!newDir.isDirectory()) {
                            throw new IOException(String.format("Attempted to create directory over file with same name '%s'.", newDir.getAbsolutePath()));
                        }
                    } else if (!newDir.mkdir()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", newDir.getAbsolutePath()));
                    }
                } else if (zipArchiveEntry.isUnixSymlink()) {
                    extractOneSymlink(zipFile, zipArchiveEntry, new File(dstDir, zipArchiveEntry.getName()));
                    extractedSize += zipArchiveEntry.getCompressedSize();
                } else {
                    extractOneFile(zipFile, zipArchiveEntry, new File(dstDir, zipArchiveEntry.getName()));
                    extractedSize += zipArchiveEntry.getCompressedSize();
                }
                final long currentProgress = (100 * extractedSize) / length;
                if (currentProgress >= 5 + lastProgress) {
                    if (callbacks != null) {
                        UiThread.post(() -> callbacks.reportProgress(currentProgress));
                    }
                    lastProgress = currentProgress;
                }
            }
            zipFile.close();
        } catch (IOException e2) {
            e2.printStackTrace();
            iOException = e2;
            try {
                zipFile.close();
            } catch (IOException ignored) {
            }
            throw iOException;
        }
    }

    private static void extractOneFile(ZipFile zipFile, ZipArchiveEntry zipArchiveEntry, File file) throws IOException {
        IOException e;
        InputStream inputStream;
        Assert.isFalse(zipArchiveEntry.isDirectory(), "extractOneFile() must be applied to file entries.");
        Assert.isFalse(zipArchiveEntry.isUnixSymlink(), "extractOneFile() must be applied to file entries.");
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = zipFile.getInputStream(zipArchiveEntry);
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    IOStreamHelpers.copy(inputStream, fileOutputStream2);
                    fileOutputStream2.flush();
                    fileOutputStream2.close();
                    inputStream.close();
                    file.setExecutable((zipArchiveEntry.getUnixMode() & 73) != 0);
                } catch (IOException e2) {
                    e = e2;
                    fileOutputStream = fileOutputStream2;
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {
                    }
                    try {
                        fileOutputStream.close();
                    } catch (IOException ignored) {
                    }
                    throw e;
                }
            } catch (IOException e3) {
                e = e3;
            }
        } catch (IOException e4) {
            e = e4;
            inputStream = null;
        }
    }

    private static void extractOneSymlink(ZipFile zipFile, ZipArchiveEntry zipArchiveEntry, File file) throws IOException {
        Assert.isFalse(zipArchiveEntry.isDirectory(), "extractOneSymlink() must be applied to symlinks.");
        Assert.isTrue(zipArchiveEntry.isUnixSymlink(), "extractOneSymlink() must be applied to symlinks.");
        SafeFileHelpers.symlink(zipFile.getUnixSymlink(zipArchiveEntry), file.getAbsolutePath());
    }
}