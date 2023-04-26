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
    public static void unpackZip(File file, File file2, final Callbacks callbacks) throws IOException {
        IOException iOException;
        ZipFile zipFile;
        Assert.isTrue(file2.isDirectory(), String.format("'%s' must be a directory.", file2.getAbsolutePath()));
        long length = file.length();
        if (callbacks != null) {
            UiThread.post(new Runnable() { // from class: com.eltechs.axs.helpers.ZipUnpacker.1
                @Override // java.lang.Runnable
                public void run() {
                    callbacks.reportProgress(-1L);
                }
            });
        }
        zipFile = new ZipFile(file);
        try {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            long j = 0;
            long j2 = 0;
            while (entries.hasMoreElements()) {
                ZipArchiveEntry nextElement = entries.nextElement();
                if (nextElement.isDirectory()) {
                    File file3 = new File(file2, nextElement.getName());
                    if (file3.exists()) {
                        if (!file3.isDirectory()) {
                            throw new IOException(String.format("Attempted to create directory over file with same name '%s'.", file3.getAbsolutePath()));
                        }
                    } else if (!file3.mkdir()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", file3.getAbsolutePath()));
                    }
                } else if (nextElement.isUnixSymlink()) {
                    extractOneSymlink(zipFile, nextElement, new File(file2, nextElement.getName()));
                    j += nextElement.getCompressedSize();
                } else {
                    extractOneFile(zipFile, nextElement, new File(file2, nextElement.getName()));
                    j += nextElement.getCompressedSize();
                }
                final long j3 = (100 * j) / length;
                if (j3 >= 5 + j2) {
                    if (callbacks != null) {
                        UiThread.post(new Runnable() { // from class: com.eltechs.axs.helpers.ZipUnpacker.2
                            @Override // java.lang.Runnable
                            public void run() {
                                callbacks.reportProgress(j3);
                            }
                        });
                    }
                    j2 = j3;
                }
            }
            zipFile.close();
        } catch (IOException e2) {
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