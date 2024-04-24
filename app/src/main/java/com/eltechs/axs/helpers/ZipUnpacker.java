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

    private ZipUnpacker() {
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x00dc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void unpackZip(File assets, File dstDir, final Callbacks callbacks) throws IOException {
        Assert.isTrue(dstDir.isDirectory(), String.format("'%s' must be a directory.", dstDir.getAbsolutePath()));
        long totalSize = assets.length();
        if (callbacks != null)
            UiThread.post(() -> callbacks.reportProgress(-1L));

        try (ZipFile zipFile = new ZipFile(assets)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            long finishedSize = 0;
            long lastProgress = 0;
            while (entries.hasMoreElements()) {
                ZipArchiveEntry nextElement = entries.nextElement();
                if (nextElement.isDirectory()) {
                    File dstFile = new File(dstDir, nextElement.getName());
                    if (dstFile.exists()) {
                        if (!dstFile.isDirectory())
                            throw new IOException(String.format("Attempted to create directory over file with same name '%s'.", dstFile.getAbsolutePath()));
                    } else if (!dstFile.mkdir())
                        throw new IOException(String.format("Failed to create the directory '%s'.", dstFile.getAbsolutePath()));
                } else if (nextElement.isUnixSymlink()) {
                    extractOneSymlink(zipFile, nextElement, new File(dstDir, nextElement.getName()));
                    finishedSize += nextElement.getCompressedSize();
                } else {
                    extractOneFile(zipFile, nextElement, new File(dstDir, nextElement.getName()));
                    finishedSize += nextElement.getCompressedSize();
                }
                final long progress = (100 * finishedSize) / totalSize;
                if (progress >= 5 + lastProgress) {
                    if (callbacks != null)
                        UiThread.post(() -> callbacks.reportProgress(progress));
                    lastProgress = progress;
                }
            }
        }
    }

    private static void extractOneFile(ZipFile zipFile, ZipArchiveEntry zipArchiveEntry, File file) throws IOException {

        Assert.isFalse(zipArchiveEntry.isDirectory(), "extractOneFile() must be applied to file entries.");
        Assert.isFalse(zipArchiveEntry.isUnixSymlink(), "extractOneFile() must be applied to file entries.");
//        if(!file.exists())
//            file.createNewFile();
        try (InputStream inputStream = zipFile.getInputStream(zipArchiveEntry);
             FileOutputStream fileOutputStream2 = new FileOutputStream(file);) {
            IOStreamHelpers.copy(inputStream, fileOutputStream2);
            fileOutputStream2.flush();
            file.setExecutable((zipArchiveEntry.getUnixMode() & 0111) != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractOneSymlink(ZipFile zipFile, ZipArchiveEntry zipArchiveEntry, File file) throws IOException {
        Assert.isFalse(zipArchiveEntry.isDirectory(), "extractOneSymlink() must be applied to symlinks.");
        Assert.isTrue(zipArchiveEntry.isUnixSymlink(), "extractOneSymlink() must be applied to symlinks.");
        SafeFileHelpers.symlink(zipFile.getUnixSymlink(zipArchiveEntry), file.getAbsolutePath());
    }

    /* loaded from: classes.dex */
    public interface Callbacks {
        void reportProgress(long j);
    }
}