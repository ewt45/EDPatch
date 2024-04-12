package com.eltechs.axs.helpers;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class FileHelpers {
    private static final String UBT_FAKE_SYMLINK_SUFFIX = "_symlink";

    public static boolean checkCaseInsensitivityInDirectory(File file) throws IOException {
        return false;
    }

    private FileHelpers() {
    }

    public static void copyFilesInDirectoryNoReplace(File file, File file2) throws IOException {
        String[] list;
        if (!file.isDirectory()) {
            throw new IOException("Copy source is not a directory.");
        }
        if (!file2.exists() && !file2.mkdir()) {
            throw new IOException("Failed to create destination directory.");
        }
        list = file.list();
        for (String str : list) {
            File file3 = new File(file, str);
            File file4 = new File(file2, str);
            if (!file4.exists()) {
                copyFile(file3, file4);
            }
        }
    }

    public static void copyFile(File file, File file2) throws IOException {
        FileInputStream fileInputStream;
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Copy source is not an existing regular file.");
        }
        if (file2.exists()) {
            if (!file2.isFile() || !file2.canWrite()) {
                throw new IOException("Destination is not a file or is a read-only file..");
            }
        } else if (!file2.createNewFile()) {
            throw new IOException("Can't create destination file.");
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                try {
                    IOStreamHelpers.copy(fileInputStream, fileOutputStream2);
                    fileInputStream.close();
                    fileOutputStream2.close();
                } catch (Throwable th) {
                    fileOutputStream = fileOutputStream2;
                    fileInputStream.close();
                    fileOutputStream.close();
                    throw th;
                }
            } catch (Throwable ignored) {
            }
        } catch (Throwable th3) {
        }
    }


    public static void copyDirectory(File file, File file2) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException("Source '" + file + "' does not exist or is not a directory");
        } else if (file.getCanonicalPath().equals(file2.getCanonicalPath())) {
            throw new IOException("Source '" + file + "' and destination '" + file2 + "' are the same");
        } else if (file2.getCanonicalPath().startsWith(file.getCanonicalPath())) {
            throw new IOException("Destination '" + file2 + "' is a subfolder of source '" + file + "'");
        } else {
            doCopyDirectory(file, file2);
        }
    }

    private static void doCopyDirectory(File file, File file2) throws IOException {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            throw new IOException("Failed to list contents of " + file);
        }
        if (file2.exists()) {
            if (!file2.isDirectory()) {
                throw new IOException("Destination '" + file2 + "' exists but is not a directory");
            }
        } else if (!file2.mkdirs() && !file2.isDirectory()) {
            throw new IOException("Destination '" + file2 + "' directory cannot be created");
        }
        if (!file2.canWrite()) {
            throw new IOException("Destination '" + file2 + "' cannot be written to");
        }
        for (File file3 : listFiles) {
            File file4 = new File(file2, file3.getName());
            if (file3.isDirectory()) {
                doCopyDirectory(file3, file4);
            } else {
                copyFile(file3, file4);
            }
        }
    }

    public static boolean doesFileExist(File file, String str) {
        return new File(file, str).exists();
    }

    public static boolean doesDirectoryExist(String str) {
        return new File(str).isDirectory();
    }

    public static void createFakeSymlink(String str, String str2, String str3) throws IOException {
        File file = new File(String.format("%s/%s%s", StringHelpers.removeTrailingSlashes(str), str2, UBT_FAKE_SYMLINK_SUFFIX));
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(str3.getBytes());
        fileOutputStream.write(10);
        fileOutputStream.close();
    }

    public static String fixPathForVFAT(String str) {
        return str.replace(':', '_');
    }

    public static void moveDirectory(File file, File file2) throws IOException {
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("Copy source is not an existing directory.");
        }
        if (file2.exists()) {
            if (!file2.isDirectory()) {
                throw new IOException("Destination is an existing file.");
            }
            if (file2.list().length != 0) {
                throw new IOException("Destination directory is not empty.");
            }
            if (!file2.delete()) {
                throw new IOException("Failed to delete existing destination directory.");
            }
        }
        if (!file.renameTo(file2)) {
            throw new IOException("Failed to rename source directory.");
        }
    }

    public static void createDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException(String.format("Path '%s' already exists and it is not a directory.", file.getAbsolutePath()));
            }
        } else if (!file.mkdirs()) {
            throw new IOException("Can't create directory.");
        }
    }

    public static File createDirectory(File parent, String dirName) {
        File target = new File(parent, dirName);
        target.mkdirs();
        return target;
    }

    public static File touch(String str) throws IOException {
        File file = new File(str);
        file.createNewFile();
        return file;
    }

    public static File touch(File file, String str) throws IOException {
        File file2 = new File(file, str);
        file2.createNewFile();
        return file2;
    }

    public static File getSuperParent(File file) {
        File parentFile = file.getParentFile();
        return parentFile.getPath().equals("/") ? file : getSuperParent(parentFile);
    }

    public static String cutExagearComponentFromPath(File file) {
        String[] split = file.getAbsolutePath().split("ExaGear", 2);
        Assert.isTrue(split.length >= 1, "cutExagearComponentFromPath : Wrong Exagear path");
        if (split.length < 2) {
            return "";
        }
        Assert.isTrue(split.length == 2, "cutExagearComponentFromPath: Something goes wrong");
        return split[1];
    }

    public static String getExagearRootFromPath(File file) {
        String[] split = file.getAbsolutePath().split("ExaGear");
        Assert.isTrue(split.length >= 1, "getExagearRootFromPath : Path without Exagear component");
        return split[0] + "ExaGear";
    }

    public static String cutRootPrefixFromPath(File file, File file2) {
        String absolutePath = file.getAbsolutePath();
        String absolutePath2 = file2.getAbsolutePath();
        String substring = absolutePath.substring(absolutePath2.length());
        boolean startsWith = absolutePath.startsWith(absolutePath2);
        Assert.state(startsWith, absolutePath2 + " isn't a prefix of " + absolutePath);
        Assert.state(substring.charAt(0) == '/');
        return substring;
    }

    @SuppressLint("NewApi")
    public static List<String> readAsLines(File file) throws IOException {
        LinkedList<String> linkedList = new LinkedList<>();
        return Files.readAllLines(file.toPath());

//        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//        while (true) {
//            try {
//                //为什么这个会出错啊
//
//                String readLine = bufferedReader.readLine();
//                if (readLine == null) {
//                    return linkedList;
//                }
//                linkedList.add(readLine);
//            } finally {
//                bufferedReader.close();
//            }
//        }
    }

    public static boolean replaceStringInFile(File file, String str, String str2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[(int) file.length()];
        fileInputStream.read(bArr);
        fileInputStream.close();
        String str3 = new String(bArr);
        if (str3.contains(str)) {
            String replace = str3.replace(str, str2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(replace.getBytes());
            fileOutputStream.close();
            return true;
        }
        return false;
    }
}