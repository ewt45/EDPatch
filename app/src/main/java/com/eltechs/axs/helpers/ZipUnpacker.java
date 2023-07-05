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
        IOException iOException;
        ZipFile zipFile;
        Assert.isTrue(dstDir.isDirectory(), String.format("'%s' must be a directory.", dstDir.getAbsolutePath()));
        long length = assets.length();
        if (callbacks != null) {
            UiThread.post(new Runnable() { // from class: com.eltechs.axs.helpers.ZipUnpacker.1
                @Override // java.lang.Runnable
                public void run() {
                    callbacks.reportProgress(-1L);
                }
            });
        }
        zipFile = new ZipFile(assets);
        try {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            long j = 0;
            long j2 = 0;
            while (entries.hasMoreElements()) {
                ZipArchiveEntry nextElement = entries.nextElement();
                if (nextElement.isDirectory()) {
                    File file3 = new File(dstDir, nextElement.getName());
                    if (file3.exists()) {
                        if (!file3.isDirectory()) {
                            throw new IOException(String.format("Attempted to create directory over file with same name '%s'.", file3.getAbsolutePath()));
                        }
                    } else if (!file3.mkdir()) {
                        throw new IOException(String.format("Failed to create the directory '%s'.", file3.getAbsolutePath()));
                    }
                } else if (nextElement.isUnixSymlink()) {
                    extractOneSymlink(zipFile, nextElement, new File(dstDir, nextElement.getName()));
                    j += nextElement.getCompressedSize();
                } else {
                    extractOneFile(zipFile, nextElement, new File(dstDir, nextElement.getName()));
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

        Assert.isFalse(zipArchiveEntry.isDirectory(), "extractOneFile() must be applied to file entries.");
        Assert.isFalse(zipArchiveEntry.isUnixSymlink(), "extractOneFile() must be applied to file entries.");
//        if(!file.exists())
//            file.createNewFile();
        try (InputStream inputStream = zipFile.getInputStream(zipArchiveEntry);
             FileOutputStream fileOutputStream2 = new FileOutputStream(file);) {
            IOStreamHelpers.copy(inputStream, fileOutputStream2);
            fileOutputStream2.flush();
            file.setExecutable((zipArchiveEntry.getUnixMode() & 73) != 0);
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