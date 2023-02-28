package com.example.datainsert.exagear;

import com.eltechs.axs.Globals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RR {
    /**
     * 存储按键按钮的framelayout布局.在dialogfragment里用这个来寻找当前是否存在该布局
     */
    public static final int BTNCONTAINER_RESOURCE_ID = 0x7f095123;
    public static final int VIEWPAGER_RESOURCE_ID = 0x7f095124; //滑动视图的id
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
    public static int SelObb_btn = 22;
    public static int SelObb_wrongFile = 23;
    public static int CstRsl_swtTxt = 24;
    public static int CstRsl_editW = 25;
    public static int CstRsl_editH = 26;
    public static int cmCtrl_title = 27;
    public static int cmCtrl_lgPressHint = 28;
    public static int cmCtrl_tabMouse = 29;
    public static int cmCtrl_tabKeys = 30;
    public static int cmCtrl_tabStyle = 31;
    public static int cmCtrl_s1_showCursor = 32;
    public static int cmCtrl_s1_showCursorTip = 33;
    public static int cmCtrl_s1_relMove = 34;
    public static int cmCtrl_s1_relMoveTip = 35;
    public static int cmCtrl_s2_layoutMode = 36;
    public static int cmCtrl_s2_layoutModeTip = 37;
    public static int cmCtrl_s2_LSideTitle = 38;
    public static int cmCtrl_s2_RSideTitle = 39;
    public static int cmCtrl_s2_FreePosTitle = 40;
    public static int cmCtrl_s2_popEdit = 41;
    public static int cmCtrl_s2_popDel = 42;
    public static int cmCtrl_s2_ColEditTip = 43;
    public static int cmCtrl_s2_modeSide = 44;
    public static int cmCtrl_s2_modeFree = 45;
    public static int cmCtrl_s3_sampleBtn = 46;
    public static int cmCtrl_s3_btnColor = 47;
    public static int cmCtrl_s3_btnColorTip = 48;
    public static int cmCtrl_s3_btnSize = 49;
    public static int cmCtrl_s3_btnSizeTip = 50;
    public static int cmCtrl_s3_sideColor = 51;
    public static int cmCtrl_s3_sideColorTip = 52;
    public static int cmCtrl_actionEdit = 53;
    public static int cmCtrl_actionCtrlShow=54;
    public static int cmCtrl_actionCtrlHide=55;
    private static String locale;

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
                "输入自定义高度",

                "按键设置（自定义操作模式）",
                "部分选项可以通过长按查看说明",
                "鼠标",
                "按键",
                "样式",
                "显示鼠标光标",
                "设置进入容器后鼠标光标显示或隐藏。\n若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。",
                "鼠标移动使用相对定位",
                "勾选后采用第二种手势操作。\n\n**手势操作 1**：\n- 单指点击 = 鼠标在手指位置左键点击\n- 单指按下后立刻移动 = 鼠标滚轮\n- 单指长按后移动 = 鼠标在手指位置左键按下（拖拽）\n- 单指长按后松开 = 鼠标在手指位置右键点击\n- 双指点击 = 显示/隐藏安卓输入法\n- 双指按下后移动 = 缩放窗口（松开一根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单\n\n**手势操作 2**：\n- 单指点击 = 鼠标原处左键点击\n- 单指移动 = 鼠标位置移动\n- 二指点击 = 鼠标原处右键点击\n- 二指按下后立刻移动 = 鼠标滚轮\n- 二指长按后移动 = 鼠标在当前位置左键按下（拖拽）\n- 三指按下并移动1或2根手指 = 缩放窗口（松开1或2根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单",
                "布局方式",
                "左右侧栏：经典布局，按键位于主画面的左右两侧，每一侧可以有多列，每一列可以有多个按键。\n\n自由位置：按钮可以自由摆放（需要启动容器后，三指触屏调出菜单项，进入编辑模式后才能编辑位置）。可以添加摇杆按钮。",
                "左侧按键栏",
                "右侧按键栏",
                "选取按键",
                "编辑",
                "删除",
                "长按按钮拖拽排序。点击按钮进行编辑。",
                "左右侧栏",
                "自由位置",
                "样例",
                "按钮颜色&透明度",
                "按钮颜色为6位的十六进制颜色，如：2121FA。\n透明度为0-255，当透明度设置到0时，按钮背景完全透明，文字保留1/3的透明度。",
                "按钮大小",
                "设置按钮的宽高。范围为10~200dp。小于10为自适应宽/高。",
                "左右侧栏背景颜色",
                "该选项对应当“按键 - 布局方式”选择为左右侧栏时，左右侧栏的底色。\n输入格式为6位的十六进制颜色，如000000，透明度固定为255完全不透明。",
                "编辑按键",
                "显示按键",
                "隐藏按键",
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
                "input height",

                "Controls Settings",
                "Long click options to check its description",
                "Mouse",
                "Keys",
                "Style",
                "Display mouse Cursor",
                "Display or hide the mouse cursor in containers.\nIf there are two cursors in game checking this option may help to hide one.",
                "Move mouse relatively",
                "If checked, the second gesture mode will be used.\n\n**Gesture Mode 1**:\n- One finger click = Mouse left click at finger's position\n- One finger press and move = Mouse scroll\n- One finger long press and move = Mouse left button press (drag)\n- One finger long click = Mouse right click\n- Two fingers click = toggle android keyboard\n- Two fingers press and move = resize the window (release one finger to move the window)\n- Three fingers click = Show popup menu\n\n**Gesture Mode 2**:\n- One finger click = Mouse left click at its own position\n- One finger move = Mouse move with finger's movement\n- Two fingers click = Mouse right click\n- Two fingers press and move = Mouse scroll\n- Tow fingers long press and move = Mouse left button press (drag)\n- Three fingers press and move 1 or 2 of them = Resize the window (release 1 or 2 of them to move the window)\n- Three fingers click = Show popup menu",
                "Layout Mode",
                "Left&Right Sidebar: Classic layout. Key buttons are put in the sidebar and the main frame won't be overlaid.\n\nFree Position: Buttons can be placed anywhere (you need to launch a container, three-fingers click to show popup menu, enter the editing mode to change a button's position). The joystick-style button is available.",
                "Left Sidebar",
                "Right Sidebar",
                "Select buttons",
                "Modify",
                "Delete",
                "Long press buttons to rearrange them. Click buttons to edit them.",
                "Left&Right Sidebar",
                "Free Position",
                "Sample",
                "Color & Alpha of buttons",
                "Color is a RGB hex value, e.g. 2121FA. \nAlpha varies from 0 to 255. If alpha is 0, button's background is transparent, button's text keeps 1/3 of the visibility.",
                "Size of buttons",
                "Set the width and height of a button, varying from 10 to 200dp. Value lower than 10 will set the width and height adaptively.",
                "Color of Sidebars",
                "When the option \"Keys - Layout Mode \" is Left&Right Sidebar, this color will be used as the background color of sidebars. Color is a RGB hex value, e.g. 000000, and alpha is always 255.",
                "Edit Controls",
                "Show Controls",
                "Hide Controls",
        };

        stringMap.put("zh", zhStrings);
        stringMap.put("en", enStrings);
    }

    public static String getS(int id) {
        if (locale == null)
            locale = Globals.getAppContext().getResources().getConfiguration().locale.getLanguage();
//        Log.d("S", "get: 获取字符串，当前系统语言为" + locale + ", 要获取的字符串为" + id);
        if (stringMap.containsKey(locale)) {
            return Objects.requireNonNull(stringMap.get(locale))[id];
        } else {
            return Objects.requireNonNull(stringMap.get("en"))[id];
        }

    }

    public static class attr {
        /**
         * 为对话框设置自定义视图的时候，手动设置边距
         */
        public static int dialogPaddingDp = 24;
    }

    public static class integer {
        public static int viewpadding = 8;
    }


}
