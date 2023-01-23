package com.example.datainsert.exagear;

import android.util.Log;

import com.eltechs.axs.Globals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class S {
    private static String locale;
    private static final Map<String, String[]> stringMap = new HashMap<>();

    public static int Dialog_PosBtn = 0;
    public static int Dialog_NegBtn = 1;
    public static int DriveD_Title = 2;
    public static int DriveD_ParDirKey_1 = 3;
    public static int DriveD_ParDirKey_2 = 4;
    public static int DriveD_ParDirKey_3 = 5;
    public static int DriveD_Explain = 6;
    public static int DriveD_DescTitle = 7;
    public static int DriveD_DescCont = 8;
    public static int DriveD_SncBrBtn = 9;
    public static int DriveD_SncBrTxt = 10;
    public static int DriveD_ToastExitFail = 11;
    public static int DriveD_EditDstTitle = 12;
    public static int DriveD_EditParTitle = 13;
    public static int DriveD_getPathFail = 14;
    public static int DriveD_check_1 = 15;
    public static int DriveD_check_2 = 16;
    public static int DriveD_check_3 = 17;
    public static int DriveD_check_4 = 18;
    public static int DriveD_check_5 = 19;
    public static int DriveD_NoStrgPmsn = 20;
    public static int SelObb_info = 21;
    public static int SelObb_btn=22;
    public static int SelObb_wrongFile=23;
    public static int CstRsl_swtTxt=24;
    public static int CstRsl_editW=25;
    public static int CstRsl_editH=26;

    static {
        //需要确保翻译完全，因为是通过索引来取字符串的
        String[] zhStrings = new String[]{
                "确定",
                "取消",
                "修改D盘路径",
                "手机存储(根目录)",
                "手机存储(应用专属目录)",
                "外置SD卡(应用专属目录)",
                "请指定一个安卓文件夹作为D盘",
                "说明",
                "android11及以上，在非应用专属目录下的游戏加载/读档速度可能变慢。解决方法是将d盘修改到应用专属目录，或将游戏复制到c/z盘（c/z盘默认在应用专属目录）。 ",
                "重启",
                "设置已更新，手动重启应用后生效",
                "设置未更新",
                "文件夹名称",
                "文件夹位置",
                "无法获取路径",
                "文件夹父目录存在",
                "文件夹存在",
                "是文件夹类型",
                "具有该文件夹的读取权限",
                "具有该文件夹的写入权限",
                "应用文件存储权限被禁止",
                "无法找到obb数据包。请检查数据包名称和位置，或手动选择obb文件。",//*安卓11及以上，无法选择Android/obb及Android/data目录内的文件
                "手动选择",
                "所选文件不是obb数据包",
                "使用自定义分辨率",
                "输入自定义宽度",
                "输入自定义高度"
        };
        String[] enStrings = new String[]{
                "confirm",
                "cancel",
                "Change the Location of Drive D",
                "External Storage",
                "External Storage(App-specific storage)",
                "SD Card(App-specific storage)",
                "set an android directory as drive d",
                "tips",
                "on android11+, read/write speed could be extremely slow. To solve this problem, set drive d to app-specific storage, or copy game to drive c/z ",
                "restart",
                "preference is changed, restart app to apply it",
                "preference is not changed",
                "Directory Name",
                "Directory Location",
                "unable to retrieve the path",
                "Directory parent folder exists",
                "Directory exists",
                "is directory type",
                "App is allowed to read the directory",
                "App is allowed to write to the directory",
                "App's storage permission is not granted",
                "No obb detected, please try Selecting it manually",//                "can't find exagear obb file, please check its name and location or select it manually.\n on Android11+, Android/obb and Android/data directory can't be seen in system file picker.",

                "select manually",
                "selected file is not an obb file",
                "using custom resolution",
                "input width",
                "input height"

        };

        stringMap.put("zh", zhStrings);
        stringMap.put("en", enStrings);
    }

    public static String get(int id) {
        if (locale == null)
            locale = Globals.getAppContext().getResources().getConfiguration().locale.getLanguage();
        Log.d("S", "f: 获取字符串，当前系统语言为" + locale + ", 要获取的字符串为" + id);
        if (stringMap.containsKey(locale)) {
            return Objects.requireNonNull(stringMap.get(locale))[id];
        } else {
            return Objects.requireNonNull(stringMap.get("zh"))[id];
        }
    }


}
