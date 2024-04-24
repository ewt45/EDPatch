package com.eltechs.axs.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FileFinder {
    public static List<File> findDirectory(File root, int depth, String target) throws IOException {
        File[] listFiles;
        Assert.isTrue(root.isDirectory());
        ArrayList<File> arrayList = new ArrayList<>();
        File file2 = new File(root, target);
        if (file2.isDirectory()) {
            arrayList.add(file2.getCanonicalFile());
            return arrayList;
        } else if (depth <= 0 || (listFiles = root.listFiles()) == null) {
            return arrayList;
        } else {
            for (File file3 : listFiles) {
                if (file3.isDirectory()) {
                    List<File> findDirectory = findDirectory(file3, depth - 1, target);
                    if (!findDirectory.isEmpty()) {
                        arrayList.addAll(findDirectory);
                    }
                }
            }
            return arrayList;
        }
    }
}