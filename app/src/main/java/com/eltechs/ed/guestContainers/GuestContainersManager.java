package com.eltechs.ed.guestContainers;

import android.content.Context;
import android.util.LongSparseArray;

import com.eltechs.axs.AppConfig;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.helpers.FileHelpers;
import com.eltechs.axs.helpers.SafeFileHelpers;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.ed.WineRegistryEditor;
import com.eltechs.ed.XDGLink;
import com.example.datainsert.exagear.mutiWine.MutiWine;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/* loaded from: classes.dex */
public class GuestContainersManager {
    public static final String LOCAL_RUN_SCRIPT = "run.sh";
    public static final String RECIPES_GUEST_DIR = "/opt/recipe/";
    public static final String TAG = "GuestContainersManager";
    private static final String CONTAINER_DESKTOP_DIR = ".wine/drive_c/users/xdroid/Desktop/";
    private static final String CONTAINER_DIR_PREFIX = "xdroid_";
    private static final String CONTAINER_ICONS_32x32_DIR = ".local/share/icons/hicolor/32x32/apps/";
    private static final String CONTAINER_PATTERN_GUEST_DIR = "/opt/guestcont-pattern/";
    private static final String CONTAINER_START_MENU_DIR = ".local/share/applications/wine/Programs/";
    private static final String NOTEPAD_GUEST_PATH = "/opt/AkelPad.exe";
    private static volatile GuestContainersManager mInstance;
    private LongSparseArray<GuestContainer> mContainers;
    private Context mContext;
    private File mHomeDir;
    private File mImageDir;
    private Long mMaxContainerId;

    public GuestContainersManager(Context context) {
        this.mContext = context;
        this.mImageDir = new File(context.getFilesDir(), "image");
        this.mHomeDir = new File(this.mImageDir, "home");
        makeContainersList();
        convertFromOldVersion();
    }

    public static synchronized GuestContainersManager getInstance(Context context) {
        GuestContainersManager guestContainersManager;
        synchronized (GuestContainersManager.class) {
            if (mInstance == null) {
                mInstance = new GuestContainersManager(context);
            }
            guestContainersManager = mInstance;
        }
        return guestContainersManager;
    }

    private static ScreenInfo loadOldWinePrefixScreenInfo(String str) {
        try {
            FileInputStream openPrivateFileForReading = AndroidHelpers.openPrivateFileForReading("screenInfo" + str.replace(IOUtils.DIR_SEPARATOR_UNIX, '_'));
            ScreenInfo screenInfo = (ScreenInfo) new ObjectInputStream(openPrivateFileForReading).readObject();
            if (openPrivateFileForReading != null) {
                openPrivateFileForReading.close();
            }
            return screenInfo;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<GuestContainer> getContainersList() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mContainers.size(); i++) {
            arrayList.add(this.mContainers.valueAt(i));
        }
        return arrayList;
    }

    public GuestContainer getContainerById(Long l) {
        return this.mContainers.get(l);
    }

    public String getHostPath(String str) {
        return new File(this.mImageDir, str).getAbsolutePath();
    }

    public String getGuestPath(String str) {
        return FileHelpers.cutRootPrefixFromPath(new File(str), this.mImageDir);
    }

    public String getGuestImagePath() {
        return this.mImageDir.getAbsolutePath();
    }

    public String getGuestWinePrefixPath() {
        return new File(this.mHomeDir, "xdroid/.wine").getAbsolutePath();
    }

    private void fillContainerInfo(GuestContainer guestContainer) {
        guestContainer.mWinePrefixPath = guestContainer.mPath + "/.wine";
        guestContainer.mDesktopPath = new File(guestContainer.mPath, CONTAINER_DESKTOP_DIR).getAbsolutePath();
        guestContainer.mStartMenuPath = new File(guestContainer.mPath, CONTAINER_START_MENU_DIR).getAbsolutePath();
        guestContainer.mIconsPath = new File(guestContainer.mPath, CONTAINER_ICONS_32x32_DIR).getAbsolutePath();
        guestContainer.mConfig = new GuestContainerConfig(this.mContext, guestContainer);
    }

    private void makeContainersList() {
        File[] listFiles;
        this.mContainers = new LongSparseArray<>();
        this.mMaxContainerId = 0L;
        if (!mHomeDir.exists())
            return;
        ;
        for (File file : this.mHomeDir.listFiles()) {
            if (file.isDirectory() && file.getName().startsWith(CONTAINER_DIR_PREFIX)) {
                GuestContainer guestContainer = new GuestContainer();
                long valueOf = Long.parseLong(file.getName().replace(CONTAINER_DIR_PREFIX, ""));
                guestContainer.mId = valueOf;
                guestContainer.mPath = file.getAbsolutePath();
                fillContainerInfo(guestContainer);
                this.mContainers.append(valueOf, guestContainer);
                if (valueOf > this.mMaxContainerId) {
                    this.mMaxContainerId = valueOf;
                }
            }
        }
    }

    private boolean initNewContainer(GuestContainer newContainer, GuestContainer refContainer) {
        File contPattern = refContainer != null
                ? new File(refContainer.mPath)
                : new File(this.mImageDir, MutiWine.getCustomPatternPath());//"/opt/guestcont-pattern/"

        fillContainerInfo(newContainer);
        File file2 = new File(newContainer.mPath);
        try {
            try {
                FileUtils.copyDirectory(contPattern, file2, file3 -> !SafeFileHelpers.isSymlink(file3.getAbsolutePath()), true);
                File runsh = new File(newContainer.mWinePrefixPath, LOCAL_RUN_SCRIPT);
                if (!runsh.exists()) {
                    FileUtils.copyFile(new File(getHostPath(RECIPES_GUEST_DIR), "run/simple.sh"), runsh);
                }
                if (refContainer == null) {
                    newContainer.mConfig.loadDefaults();
                } else {
                    GuestContainerConfig.cloneContainerConfig(refContainer, newContainer);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                FileUtils.deleteDirectory(file2);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public GuestContainer createContainer() {
        return createContainer(null);
    }

    public GuestContainer createContainer(GuestContainer refContainer) {
        long newContainerId = this.mMaxContainerId + 1;
        File file = this.mHomeDir;
        File file2 = new File(file, CONTAINER_DIR_PREFIX + newContainerId);
        if (file2.mkdirs()) {
            GuestContainer newContainer = new GuestContainer();
            newContainer.mId = newContainerId;
            newContainer.mPath = file2.getAbsolutePath();
            if (initNewContainer(newContainer, refContainer)) {
                this.mMaxContainerId = this.mMaxContainerId + 1;
                this.mContainers.append(newContainerId, newContainer);
                return newContainer;
            }
            return null;
        }
        return null;
    }

    public void deleteContainer(GuestContainer guestContainer) {
        if (getCurrentContainer() == guestContainer) {
            makeContainerCurrent(null);
        }
        guestContainer.mConfig.deleteConfig();
        this.mContainers.delete(guestContainer.mId);
        File file = new File(guestContainer.mPath);
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace(); //为什么删文件夹会失败？
            String parent = file.getParent();
            file.renameTo(new File(parent, "corrupted_" + file.getName()));
        }
    }

    public void cloneContainer(GuestContainer guestContainer) {
        createContainer(guestContainer);
    }

    public GuestContainer getCurrentContainer() {
        Long currentGuestContId = AppConfig.getInstance(this.mContext).getCurrentGuestContId();
        if (currentGuestContId != 0) {
            return this.mContainers.get(currentGuestContId);
        }
        return null;
    }

    public void makeContainerCurrent(GuestContainer guestContainer) {
        AppConfig.getInstance(this.mContext).setCurrentGuestContId(guestContainer != null ? guestContainer.mId : 0L);
    }

    public void makeContainerActive(GuestContainer guestContainer) {
        File file = new File(this.mHomeDir, "xdroid");
        file.delete();
        SafeFileHelpers.symlink("./xdroid_" + guestContainer.mId, file.getAbsolutePath());
    }

    public String getIconPath(XDGLink xDGLink) {
        File file = new File(xDGLink.guestCont.mIconsPath);
        File file2 = new File(file, xDGLink.icon + ".png");
        if (file2.exists()) {
            return file2.getAbsolutePath();
        }
        return null;
    }

    public void copyXDGLinkToDesktop(XDGLink xDGLink) {
        File file = xDGLink.linkFile;
        try {
            FileUtils.copyFile(file, new File(xDGLink.guestCont.mDesktopPath, file.getName()));
        } catch (IOException ignored) {
        }
    }

    private void fixWinePrefixForXDGLinks(File file, String str, String str2) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                fixWinePrefixForXDGLinks(file2, str, str2);
                if (file2.listFiles().length == 0) {
                    file2.delete();
                }
            } else if (file2.getName().toLowerCase().endsWith(".desktop")) {
                try {
                    if (!FileHelpers.replaceStringInFile(file2, str, str2)) {
                        file2.delete();
                    }
                } catch (IOException unused) {
                }
            }
        }
    }

    private void convertXDGLinks(File file, GuestContainer guestContainer) {
        String guestPath = getGuestPath(getGuestWinePrefixPath());
        fixWinePrefixForXDGLinks(new File(guestContainer.mPath, CONTAINER_DESKTOP_DIR), getGuestPath(file.getAbsolutePath()), guestPath);
        fixWinePrefixForXDGLinks(new File(guestContainer.mPath, CONTAINER_START_MENU_DIR), getGuestPath(file.getAbsolutePath()), guestPath);
    }

    private void processAndUpdateRunScript(GuestContainer guestContainer) throws IOException {
        File file = new File(guestContainer.mWinePrefixPath, LOCAL_RUN_SCRIPT);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[(int) file.length()];
        fileInputStream.read(bArr);
        fileInputStream.close();
        if (new String(bArr).contains(" -w")) {
            guestContainer.mConfig.setRunArguments("-w");
        }
        FileUtils.copyFile(new File(getHostPath(RECIPES_GUEST_DIR), "run/simple.sh"), file);
    }

    public void convertFromOldVersion() {
        File[] listFiles;
        File file = new File(this.mImageDir, "/home/xdroid/wp/");
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    GuestContainer createContainer = createContainer();
                    createContainer.mConfig.setScreenInfo(loadOldWinePrefixScreenInfo(getGuestPath(file2.getAbsolutePath())));
                    try {
                        FileUtils.copyDirectory(new File(this.mImageDir, "/home/xdroid/.local/share/icons"), new File(createContainer.mPath, ".local/share/icons"));
                        FileUtils.copyDirectory(new File(this.mImageDir, "/home/xdroid/.local/share/applications/wine/Programs/"), new File(createContainer.mPath, CONTAINER_START_MENU_DIR));
                    } catch (IOException unused) {
                    }
                    File file3 = new File(createContainer.mPath, ".wine");
                    try {
                        FileUtils.deleteDirectory(file3);
                    } catch (IOException unused2) {
                    }
                    file2.renameTo(file3);
                    convertXDGLinks(file2, createContainer);
                    WineRegistryEditor wineRegistryEditor = new WineRegistryEditor(new File(file3, "user.reg"));
                    try {
                        wineRegistryEditor.read();
                        wineRegistryEditor.setStringParam("Software\\Wine\\DirectInput", "MouseWarpOverride", "disable");
                        try {
                            FileUtils.copyFile(new File(this.mImageDir, NOTEPAD_GUEST_PATH), new File(file3, "drive_c/windows/notepad.exe"));
                            FileUtils.copyFile(new File(this.mImageDir, NOTEPAD_GUEST_PATH), new File(file3, "drive_c/windows/system32/notepad.exe"));
                            wineRegistryEditor.setStringParam("Software\\Wine\\DllOverrides", "notepad.exe", "native,builtin");
                        } catch (IOException unused3) {
                        }
                        wineRegistryEditor.write();
                    } catch (IOException unused4) {
                    }
                    try {
                        processAndUpdateRunScript(createContainer);
                    } catch (IOException unused5) {
                    }
                    try {
                        FileHelpers.replaceStringInFile(new File(file3, "user.reg"), getGuestPath(file2.getAbsolutePath()), getGuestPath(getGuestWinePrefixPath()));
                    } catch (IOException unused6) {
                    }
                }
            }
            File file4 = new File(this.mImageDir, "/home/xdroid/");
            file4.renameTo(new File(this.mImageDir, "/home/old_xdroid/"));
            file4.setReadable(true, false);
        }
    }
}