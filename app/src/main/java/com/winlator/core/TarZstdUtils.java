package com.winlator.core;

import android.content.Context;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TarZstdUtils {
    public static boolean extract(Context c, File destination) {
        File extractedObb = new File(c.getFilesDir(), "obb");
        try (InputStream is = c.getAssets().open("obb");
             FileOutputStream fos = new FileOutputStream(extractedObb)) {
            if(!StreamUtils.copy(is, fos))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return extract(extractedObb,destination);

//        try (InputStream inStream = new ZstdCompressorInputStream(new BufferedInputStream(c.getAssets().open("obb"), 8192));
//             ArchiveInputStream tar = new TarArchiveInputStream(inStream);) {
//
//            while (true) {
//                try {
//                    TarArchiveEntry entry = (TarArchiveEntry) tar.getNextEntry();
//                    if (entry == null)
//                        return true;
//
//                    if (!tar.canReadEntryData(entry))
//                        continue;
//
//                    File file = new File(destination, entry.getName());
//                    if (entry.isDirectory()) {
//                        if (!file.isDirectory()) {
//                            file.mkdirs();
//                        }
//                    } else if (entry.isSymbolicLink()) {
//                        FileUtils.symlink(entry.getLinkName(), file.getAbsolutePath());
//                    } else {
//                        try (BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file), 8192)) {
//                            if (!StreamUtils.copy(tar, outStream))
//                                return false;
//                        }
//
//                    }
//                    FileUtils.chmod(file, 505);
//                } catch (Throwable ignored) {
//                }
//            }
//        } catch (IOException e) {
//            return false;
//        }

    }

    private static void addFile(ArchiveOutputStream tar, File file, String entryName) {
        try {
            tar.putArchiveEntry(tar.createArchiveEntry(file, entryName));
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file), 8192);
            StreamUtils.copy(inStream, tar);
            inStream.close();
            tar.closeArchiveEntry();
        } catch (Exception e) {
        }
    }

    private static void addDirectory(ArchiveOutputStream tar, File folder, String basePath) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (!FileUtils.isSymlink(file)) {
                if (file.isDirectory()) {
                    String entryName = basePath + file.getName() + "/";
                    tar.putArchiveEntry(tar.createArchiveEntry(folder, entryName));
                    tar.closeArchiveEntry();
                    addDirectory(tar, file, entryName);
                } else {
                    addFile(tar, file, basePath + file.getName());
                }
            }
        }
    }

    public static void compress(File file, File zipFile) {
        compress(file, zipFile, 3);
    }

    public static void compress(File file, File zipFile, int level) {
        compress(new File[]{file}, zipFile, level);
    }

    public static void compress(File[] files, File destination, int level) {
        try {
            OutputStream outStream = new ZstdCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(destination), 8192), level);
            TarArchiveOutputStream tar = new TarArchiveOutputStream(outStream);
            try {
                tar.setLongFileMode(2);
                for (File file : files) {
                    if (!FileUtils.isSymlink(file)) {
                        if (file.isDirectory()) {
                            String basePath = file.getName() + "/";
                            tar.putArchiveEntry(tar.createArchiveEntry(file, basePath));
                            tar.closeArchiveEntry();
                            addDirectory(tar, file, basePath);
                        } else {
                            addFile(tar, file, file.getName());
                        }
                    }
                }
                tar.finish();
                tar.close();
                outStream.close();
            } catch (Throwable th) {
                try {
                    tar.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
        }
    }

    public static boolean extract(File source, File destination) {
        if (source == null || !source.isFile()) {
            return false;
        }
        try {
            InputStream inStream = new ZstdCompressorInputStream(new BufferedInputStream(new FileInputStream(source), 8192));
            ArchiveInputStream tar = new TarArchiveInputStream(inStream);
            while (true) {
                try {
                    TarArchiveEntry entry = (TarArchiveEntry) tar.getNextEntry();
                    if (entry != null) {
                        if (tar.canReadEntryData(entry)) {
                            File file = new File(destination, entry.getName());
                            if (entry.isDirectory()) {
                                if (!file.isDirectory()) {
                                    file.mkdirs();
                                }
                            } else if (entry.isSymbolicLink()) {
                                FileUtils.symlink(entry.getLinkName(), file.getAbsolutePath());
                            } else {
                                BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file), 8192);
                                if (!StreamUtils.copy(tar, outStream)) {
                                    outStream.close();
                                    tar.close();
                                    inStream.close();
                                    return false;
                                }
                                outStream.close();
                            }
                            FileUtils.chmod(file, 505);
                        }
                    } else {
                        tar.close();
                        inStream.close();
                        return true;
                    }
                } catch (Throwable th) {
                    try {
                        tar.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
