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
    public static int SelObb_selResult = 23;
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
    public static int cmCtrl_actionCtrlShow = 54;
    public static int cmCtrl_actionCtrlHide = 55;
    public static int cmCtrl_mouseLeftName = 56;
    public static int cmCtrl_mouseRightName = 57;
    public static int cmCtrl_mouseMiddleName = 58;
    public static int cmCtrl_allKeysJoyTitle = 59;
    public static int cmCtrl_allKeysMouseTitle = 60;
    public static int cmCtrl_editMenu1Dialog = 61;
    public static int cmCtrl_editMenu2Exit = 62;
    public static int cmCtrl_BtnEditReName = 63;
    public static int cmCtrl_JoyEdit4Ways = 64;
    public static int cmCtrl_JoyEdit4WaysTip = 65;
    public static int cmCtrl_JoyEditKeys = 66;
    public static int cmCtrl_JoyEditKeyCstm = 67;
    public static int cmCtrl_gs_lClick = 68;
    public static int cmCtrl_gs_rClick = 69;
    public static int cmCtrl_gs_vScroll = 70;
    public static int cmCtrl_gs_dndLeft = 71;
    public static int cmCtrl_gs_zoom = 72;
    public static int cmCtrl_gs_menu = 73;
    public static int cmCtrl_gs1Abs_title = 74;
    public static int cmCtrl_gs2Rel_title = 75;
    public static int cmCtrl_gs_moveCursor = 76;
    public static int cmCtrl_gs_keyboard = 77;
    public static int cmCtrl_actionCtrlTip = 78;
    public static int cmCtrl_actionSubCtrl = 79;
    public static int cmCtrl_s1_msSpd = 80;
    public static int cmCtrl_reset = 81;
    public static int cmCtrl_s1_msSpdTip = 82;
    public static int cmCtrl_s1_msOffScr = 83;
    public static int cmCtrl_s1_msOffScrTip = 84;
    public static int cmCtrl_s2_sideTitleTip = 85;
    public static int cmCtrl_s2_selectBtn = 86;
    public static int cmCtrl_BtnEditTrigger = 87;
    public static int cmCtrl_BtnEditComb = 88;
    public static int cmCtrl_tabOther = 89;
    public static int cmCtrl_s4_tips = 90;
    public static int cmCtrl_s4_export = 91;
    public static int cmCtrl_s4_import = 92;
    public static int cmCtrl_s4_trsportTitle = 93;
    public static int cmCtrl_s4_trsportTip = 94;
    public static int cmCtrl_s4_exportResult = 95;
    public static int cmCtrl_s4_importResult = 96;
    public static int cmCtrl_s3_txtSize = 97;
    public static int cmCtrl_s3_btnRoundShape = 98;
    public static int cmCtrl_actionRotate = 99;
    public static int abtFab_title = 100;
    public static int abtFab_info = 101;
    public static int firstLaunch_snack = 102;
    //app快捷方式
    public static int shortcut_menuItem_addAppSc = 103;
    public static int shortcut_DontShowUp = 104;
    public static int shortcut_TipAfterAdd = 105;


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
                "设置已更新，手动重启应用后生效", //index 10
                "设置未更新",
                "文件夹名称",
                "文件夹位置",
                "无法获取路径",
                "文件夹父目录存在",
                "文件夹存在",
                "是文件夹类型",
                "具有该文件夹的读取权限",
                "具有该文件夹的写入权限",
                "应用文件存储权限被禁止",      //20
                "无法找到obb数据包。请检查数据包名称和位置，或手动选择obb文件。",
                "手动选择",
                "所选文件不是obb数据包$选中obb。正在解压，请耐心等待……",
                "使用自定义分辨率",
                "输入自定义宽度",
                "输入自定义高度",

                "自定义操作模式",
                "大部分选项可以通过长按查看说明",
                "鼠标",
                "按键",  //30
                "样式",
                "显示鼠标光标",
                "设置进入容器后鼠标光标显示或隐藏。\n若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。",
                "鼠标移动使用相对定位（手势控制 2）",
                "勾选后采用第二种手势控制。\n\n**手势控制 1**：\n- 单指点击 = 鼠标在手指位置左键点击\n- 单指按下后立刻移动 = 鼠标滚轮\n- 单指长按后移动 = 鼠标在手指位置左键按下（拖拽）\n- 单指长按后松开 = 鼠标在手指位置右键点击\n- 双指点击 = 显示/隐藏安卓输入法\n- 双指按下后移动 = 缩放窗口（松开一根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单\n\n**手势控制 2**：\n- 单指点击 = 鼠标原处左键点击\n- 单指移动 = 鼠标位置移动\n- 二指点击 = 鼠标原处右键点击\n- 二指按下后立刻移动 = 鼠标滚轮\n- 二指长按后移动 = 鼠标在当前位置左键按下（拖拽）\n- 三指按下并移动 = 缩放窗口（松开1根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单",
                "布局方式",
                "左右侧栏：经典布局，按键位于主画面的左右两侧，每一侧可以有多列，每一列可以有多个按键。\n\n自由位置：按钮可以自由摆放（需要启动容器后，三指触屏调出菜单项，进入编辑模式后才能编辑位置）。可以添加摇杆按钮。\n\n两种布局数据分开存储，位于手机存储目录/Android/data/包名/files/custom_control2(3).txt，可以手动备份，但不保证日后的更新能兼容。",
                "左侧按键栏",
                "右侧按键栏",
                "选取按键",     //40
                "编辑",
                "删除",
                "长按按钮拖拽排序。点击按钮进行编辑。",
                "左右侧栏",
                "自由位置",
                "样例",
                "按钮颜色&透明度",
                "按钮颜色为6位的十六进制数字，如：2121FA，随便找一个在线颜色选取网站即可获取。\n透明度为0-255，当透明度设置到0时，按钮背景完全透明，文字保留1/3的透明度。",
                "按钮大小",
                "设置按钮的宽高。范围为10~200dp。小于10为自适应宽/高。",     //50
                "左右侧栏背景颜色",
                "该选项对应当“按键 - 布局方式”选择为左右侧栏时，左右侧栏的底色。\n输入格式为6位的十六进制颜色，如000000，透明度固定为255完全不透明。",
                "编辑按键",
                "显示按键",
                "隐藏按键",
                "鼠标左键",
                "鼠标右键",
                "鼠标中键",
                "摇杆",
                "鼠标",               //60
                "显示详细设置",
                "退出编辑",
                "重命名",
                "仅使用4个方向而不是8个方向",
                "勾选此选项使用4个方向，则同一时刻只会按下一个按键。若不勾选，当移动到斜方向时会触发两个按键，即有8个方向。\n允许斜向会导致判定方向变化的角度从45°变为22.5°，所以在游戏不支持斜向的情况下建议开启此选项。",
                "设置摇杆按键",
                "自定义",
                "左键点击$单指点击屏幕$单指点击屏幕", //获取的时候按split("\\$")分割成三段，第一个是标题，第二个是操作1的介绍，第二个是操作2的接受啊
                "右键点击$单指长按后松开$二指点击屏幕",
                "鼠标滚轮$单指上下滑动$二指上下滑动",               //70
                "左键拖拽$单指长按后移动$二指长按后移动",
                "缩放窗口$二指按下并移动，松开1指变为移动窗口$三指按下并移动，松开1指变为移动窗口",
                "弹窗菜单$三指点击屏幕$三指点击屏幕",
                "手势控制 1$原始的默认模式",
                "手势控制 2$触摸板模式",
                "移动鼠标$ $单指按下并移动",
                "安卓输入法$二指点击屏幕$ ",
                "手势说明",
                "自定义操作模式",
                "鼠标灵敏度",                //80
                "重置",
                "调整鼠标移动速度，仅在使用“手势控制 2”时生效。",
                "视角转动速度（鼠标移到屏幕边界之后）",
                "数值为0~70。一般设置为0，即不允许鼠标移到桌面外部。\n某些游戏中，若鼠标移动到边界时视角无法继续转动，可以尝试调大该数值，允许鼠标移到边界外以继续转动视角。\n\n注意若想此功能生效，需要设置游戏全屏，并且在环境设置中修改分辨率与游戏全屏分辨率完全相同，即画面右下方不能有多余的黑边。无需设置注册表MouseWarpOverride=force。",
                "点击“+”新建一列按键。长按可进行排序。",
                "选择按键……",
                "松手时保持按下状态",
                "组合键",
                "其他",
                "<ul>\n" +
                        "  <li>在环境设置中将操作模式调为“默认(default)”即可启用此自定义模式。</li>\n" +
                        "  <li>启动环境后，可以通过三指触屏调出此界面进行实时修改。</li>\n" +
                        "  <li>本功能仅提供基础设置，若有更复杂的需求请使用Input Bridge.</li>\n" +
                        "</ul>",
                "导出",
                "导入",
                "数据转移",
                "用户可以将自定义按键功能相关数据导入或导出，但不保证此功能升级后兼容旧版数据。\n点击导出，将数据以文本格式复制到剪切板；点击导入，将尝试从剪切板读取文本转为数据。\n进入环境后无法进行导入。",
                "已复制到剪切板",
                "导入成功$导入失败",
                "文字大小",
                "圆形按钮",
                "旋转屏幕",
                "关于",
                "<ul>\n" +
                        "  <li>Exagear模拟器官方（Eltechs）已停止开发。本菜单为第三方补丁，旨在添加一些便捷功能。</li>\n" +
                        "  <li>您可以通过“ED自助补丁”将第三方功能加入原版apk中：https://github.com/ewt45/EDPatch/releases。请勿用于商业用途。</li>\n" +
                        "</ul>",
                "额外功能可以在右下操作按钮中找到。",

                "添加为app快捷方式",
                "不再显示此提示",
                "为该.desktop文件创建app快捷方式，之后可以通过长按app图标 -> 点击快捷方式快速启动程序。快捷方式最多可以添加四个，启动快捷方式前确保app后台已被清除。\n\n将该exe快捷方式删除后，app快捷方式在下一次启动时会被自动删除。\n\n该功能在安卓7以下无法使用。",

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
                "No obb detected, please try selecting it manually",//                "can't find exagear obb file, please check its name and location or select it manually.\n on Android11+, Android/obb and Android/data directory can't be seen in system file picker.",

                "select manually",
                "Selected file isn't an obb file.$Obb found. Extracting...",
                "using custom resolution",
                "input width",
                "input height",

                "Custom Controls",
                "Long click options to check its description.",
                "Mouse",
                "Keys",
                "Style",
                "Display mouse Cursor",
                "Display or hide the mouse cursor in containers.\nIf there are two cursors in game checking this option may help to hide one.",
                "Move mouse relatively (Gesture Mode 2)",
                "If checked, the second Gesture mode will be used.\n\n**Gesture Mode 1**:\n- One finger click = Mouse left click at finger's position\n- One finger press and move = Mouse wheel scroll\n- One finger long press and move = Mouse left button press (drag)\n- One finger long click = Mouse right click\n- Two fingers click = toggle android keyboard\n- Two fingers press and move = resize the window (release one finger to move the window)\n- Three fingers click = Show popup menu\n\n**Gesture Mode 2**:\n- One finger click = Mouse left click at its own position\n- One finger move = Mouse move with finger's movement\n- Two fingers click = Mouse right click\n- Two fingers press and move = Mouse wheel scroll\n- Tow fingers long press and move = Mouse left button press (drag)\n- Three fingers press and move 1 or 2 of them = Resize the window (release 1 or 2 of them to move the window)\n- Three fingers click = Show popup menu",
                "Layout Mode",
                "Left&Right Sidebar: Classic layout. Key buttons are put in the sidebar and the main frame won't be overlaid.\n\nFree Position: Buttons can be placed anywhere (you need to enter a container, three-fingers click to edit a button's position). The joystick-style button is available.\n\nCustom data is stored on device at Android/data/PACKAGE_NAME/files/custom_control2(3).txt. They can be backed up manually, but may not be compatible with future updates (if there is any).",
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
                "Mouse Left",
                "Mouse Right",
                "Mouse Middle",
                "Joystick",
                "Mouse",
                "Show Editing Dialog",
                "Exit",
                "Rename",
                "Use 4 directions instead of 8 directions",
                "If checked, only one key will be pressed at a time. If unchecked, two buttons will be triggered when moving to the diagonal direction, that is, there are 8 directions.\nAllowing the diagonal direction will cause the angle of the determination direction to change from 45° to 22.5°, so it is recommended to enable this option if the game does not support diagonal.",
                "Button keys",
                "Custom",
                "Left Click$One finger click$One finger click", //获取的时候按split("\\$")分割成三段，第一个是标题，第二个是操作1的介绍，第二个是操作2的接受啊
                "Right Click$One finger long press & release$Two fingers click",
                "Scroll (Wheel)$One finger move$Two fingers move",
                "Left Press (Drag)$One finger long press and move$Two fingers long press and move",
                "Zoom$Two fingers press and move$Three fingers press and move 1 or 2 of them",
                "Popup Menu$Three fingers click$Three fingers click",
                "Gesture Mode 1$Original Default Mode",
                "Gesture Mode 2$TouchPad Mode",
                "Cursor Move$ $One finger move",
                "Toggle Keyboard$Two fingers click$ ",
                "Gesture Tips",
                "Custom Control",
                "Mouse sensitivity",
                "Reset",
                "Adjust the pointer speed, effective only in Gesture Mode 2",
                "Camera Speed(When mouse hits the screen boundary)",
                "The value is 0~70. Default is 0, that is, the mouse is not allowed to move outside the screen. \nIn some games, if the camera cannot continue to move when the mouse moves to the boundary, you can try to increase the value to allow camera continue to move. \n\nNote that if for function to take effect, you need to full screen the game, and adjust the resolution in the container's properties to be exactly the same as the full screen resolution of the game, that is, there should be no extra black frame at the bottom right of the screen. There is no need to set the registry MouseWarpOverride=force.",
                "Click \"+\" to add one column of buttons. Long press to rearrange them.",
                "Select keys……",
                "Keep pressed after releasing the finger",
                "Combination",
                "Others",
                "    <ul>\n" +
                        "        <li>To enable this control, set Control Mode \"default\" in container properties</li>\n" +
                        "        <li>This dialog can also show up by three-finger click after launching the container.</li>\n" +
                        "        <li>This function provides only basic settings. For better customization Input Bridge is preferred.</li>\n" +
                        "    </ul>",
                "Export",
                "Import",
                "Data Transfer",
                "Users can import or export data of Custom Controls, but it is not guaranteed that it will be compatible in the future.\nClick Export to copy the data to the clipboard in text format. Click Import to try to read text from the clipboard into data.",
                "Copied to clipboard",
                "Import succeeded$Import failed",
                "Text Size",
                "Round Button",
                "Rotate Screen",
                "About",
                "<ul>\n" +
                        "  <li>Exagear Android (Eltechs) project has been closed. This menu is a third-party patch designed to add some handy features.</li>\n" +
                        "  <li>You can add this and more features into Exagear by EDPatch: https://github.com/ewt45/EDPatch/releases</li>\n" +
                        "</ul>",
                "Extra features can be found in the bottom left button.",

                "Add as app shortcut",
                "Don't show up again",
                "Creating an app shortcut for this .desktop file. Later you can launch it by long pressing APP icon and clicking the app shortcut. A maximum of four shortcuts can be added. Before launching from shortcut, ensure that the app is not running at background. \n\nAfter deleting the exe shortcut ( .desktop file), the app shortcut will be automatically deleted the next time launching the app . \n\nThis feature requires Android 7 and above.",

        };

        String[] ruStrings = new String[]{
                "подтвердить",
                "отмена",
                "Изменить расположение диска D",
                "External Storage(Папки каталога Android)",
                "External Storage(Папка каталога Android/data)",
                "SD Card(Папка каталога SD/Android/data)",
                "Выберите каталог Android для расположения диска D",
                "Рекомендация",
                "На Android11+ скорость чтения/записи в каталоге Android может быть очень низкой. Чтобы решить эту проблему, установите диск D в каталог Android/data или скопируйте игру на диск C или Z.",
                "перезапустить",
                "Расположение диска D изменено",
                "Настройки не изменились",
                "Имя папки",
                "Местоположение каталога",
                "Невозможно найти путь",
                "Корневой каталог существует",
                "Конечная папка существует",
                "Выбранный файл является папкой",
                "Приложению разрешено читать каталог",
                "Приложению разрешено записывать в каталог",
                "У приложения нет разрешения на доступ к Хранилищу",
                "Выберите файл obb вручную",//                "не удается найти файл obb, проверьте его имя и местоположение или выберите его вручную.\n на Android11+ каталоги Android/obb и Android/data не видны в средстве выбора системных файлов.",

                "выбрать вручную",
                "Выбранный файл не является файлом obb.$Obb найден. Распаковка...",
                "Использовать кастомное разрешение",
                "ширина",
                "высота",

                "Кастомное Управление",
                "Удерживайте долгий тап для получения описания функций.",
                "Мышь",
                "Ключи",
                "Стиль",
                "Отображать курсор мыши",
                "Отображать или скрывать курсор мыши в контейнерах.\nЕсли в игре два курсора, включение этой опции может помочь скрыть один.",
                "Отображение курсора как тачпада (Режим жестов 2)",
                "Если флаг установлен, будет использоваться Режим жестов 2.\n\n**Режим жестов 1**:\n- Тап одним пальцем = Клик левой кнопкой мыши на позиции пальца\n- Тап и перемещение одним пальцем = Прокрутка мышью\n- Долгий тап одним пальцем и перемещение = Нажатие левой кнопки мыши (Перетаскивание)\n- Тап одним пальцем с удержанием = Клик правой кнопкой мыши\n- Тап двумя пальцами = Вызов клавиатуры Android\n- Тап двумя пальцами с удержанием и движение в разные стороны = Изменить размер окна (отпустите один палец, чтобы переместить окно)\n- Тап тремя пальцами = Показать меню\n\n**Режим жестов 2**:\n- Тап одним пальцем = Клик левой кнопкой мыши в позиции положения курсора\n- Движение одним пальцем = Движение курсора мыши\n- Тап двумя пальцами = Клик правой кнопкой мыши\n- Тап двумя пальцами и движение = Прокрутка колесика мыши\n- Тап двумя пальцами с удержанием и перемещение = Нажатие левой кнопки мыши (перетаскивание)\n- Тап тремя пальцами и перемещение 1 или 2 из них = Изменение размера окна (отпустите 1 или 2 пальца, чтобы переместить окно)\n- Тап тремя пальцами = Показать меню",
                "Режим макета",
                "Левая и правая боковые панели это классический макет. Кнопки ключей размещаются на боковых панелях, и основной экран не перекрывается.\n\nСвободная позиция: кнопки можно размещать где угодно (тап тремя пальцами для вызова меню, выбрать редактировать и изменить положение кнопки).  Доступна кнопка в виде джойстика.\n\nПользовательские данные хранятся на устройстве в Android/data/ИМЯ_ПАКЕТА/files/custom_control2(3).txt. Их можно создать как резервную копию вручную. Они могут быть несовместимы с будущими обновлениями(если они будут).",
                "Левая боковая панель",
                "Правая боковая панель",
                "Выбор кнопок",
                "Изменить",
                "Удалить",
                "Нажмите и удерживайте на кнопки, чтобы переставить их. Однократное нажатие, чтобы редактировать.",
                "Левая и Правая боковая панель",
                "Свободная позиция",
                "Текст",
                "Цвет и Прозрачность кнопок",
                "Цвет - это шестнадцатеричное значение RGB, например 2121FA. \nПрозрачность варьируется от 0 до 255. Если Прозрачность равна 0 то фон кнопки прозрачен, а текст кнопки сохраняет 1/3 видимости.",
                "Размер кнопок",
                "Установите ширину и высоту кнопки в диапазоне от 10 до 200 dp. Значение меньше 10 будет адаптивно по ширине и высоте.",
                "Цвет боковых панелей",
                "Когда для параметра \"Кнопки - Режим макета\" выбрано значение \"Левая и Правая боковая панель\", этот цвет будет использоваться в качестве цвета фона боковых панелей. Цвет - это шестнадцатеричное значение RGB, например 000000, а Прозрачность всегда 255.",
                "Редактировать Управление",
                "Показать Управление",
                "Скрыть Управление",
                "Левая кнопка мыши",
                "Правая кнопка мыши",
                "Средняя кнопка мыши",
                "Джойстик",
                "Мышь",
                "Показать диалог редактирования",
                "Выход",
                "Переименовать",
                "Использовать 4 направления вместо 8-ми направлений",
                "Если флаг установлен, за один раз будет нажата только одна клавиша. Если флаг не установлен, при перемещении в диагональном направлении будут срабатывать две кнопки, т. е. существует 8 направлений.\nРазрешение диагонального направления приведет к изменению угла направления определения с 45° на 22,5°, поэтому рекомендуется включить эту опцию, если игра не поддерживает диагональ.",
                "Ключи кнопок",
                "Кастомные",
                "Левый клик мыши$Тап одним пальцем$Тап одним пальцем", //комментарий: после получения это разделит его на три части, первая - заголовок, вторая - описание режима 1, третья - описание режима 2.
                "Правый клик мыши$Долгий тап и отпустить одним пальцем$Тап двумя пальцами",
                "Колёсико (прокрутка мыши)$Движение одним пальцем$Движение двумя пальцами",
                "Левый клик мыши (Перетаскивание)$Долгий тап одним пальцем и перемещение$Долгий тап двумя пальцами и перемещение",
                "Приблизить$Тап двумя пальцами и перемещение$Тап тремя пальцами и перемещение 1 или 2 из них",
                "Меню$Тап тремя пальцами$Тап тремя пальцами",
                "Режим жестов 1$Оригинальный режим Default",
                "Режим жестов 2$Режим TouchPad",
                "Переместить курсор$ $Движение одним пальцем",
                "Клавиатура$Тап двумя пальцами$ ",
                "Рекомендации жестов",
                "Кастомное Управление",
                "Чувствительность мыши",
                "Сброс",
                "Отрегулируйте скорость указателя мыши, работает только в Режиме жестов 2",
                "Скорость Камеры(Когда мышь достигает границы экрана)",
                "Этот параметр имеет значения в диапазоне 0~70. По умолчанию 0, т. е. мышь не может перемещаться за пределы экрана. \nВ некоторых играх, если камера не может продолжать двигаться, когда мышь приближается к границе, вы можете попытаться увеличить это значение, чтобы камера продолжала двигаться. \n\nОбратите внимание, для того, чтобы функция заработала, вам необходимо развернуть игру на весь экран, а разрешение в свойствах контейнера настроить так, чтобы оно было точно таким же, как полноэкранное разрешение игры, то есть не должно быть лишних черных рамок в правом и нижнем углу экрана. При этом устанавливать в реестре \"MouseWarpOverride=force\" не нужно",
                "Нажмите \"+\" чтобы добавить один столбец кнопок. Нажмите и удерживайте, чтобы изменить их порядок.",
                "Выбрать ключи……",
                "Продолжать нажатие после того как отпустили палец(Удержание)",
                "Комбинация",
                "Прочее",
                "    <ul>\n" +
                        "        <li>Чтобы включить Катомное Управление - выберите режим управления\"Default\" в настройках контейнера.</li>\n" +
                        "        <li>Это окно также можно открыть, тапнув тремя пальцами в запущенном контейнере.</li>\n" +
                        "        <li>Эта функция обеспечивает только основные настройки. Для лучшей и более точной настройки рекомендуется использовать Input Bridge.</li>\n" +
                        "    </ul>",
                "Экспорт",
                "Импорт",
                "Обмен данными",
                "Пользователи могут импортировать или экспортировать конфиги элементов управления, но их совместимость в будущем не гарантируется.\nНажмите \"Экспорт\", чтобы скопировать данные в буфер обмена в текстовом формате. Нажмите \"Импорт\", чтобы попытаться загрузить текст из буфера обмена в данные.",
                "Скопировано в буфер обмена",
                "Импорт выполнен успешно$Не удалось импортировать",
                "Размер текста",
                "Круглая кнопка",
                "Поворот экрана",
                "О приложении",
                "<ul>\n" +
                        "  <li>Exagear Android (Eltechs) проект закрыт. Это меню представляет собой сторонний патч, предназначенный для добавления некоторых удобных функций.</li>\n" +
                        "  <li>Вы можете добавить эту и другие функции в Exagear с помощью EDPatch: https://github.com/ewt45/EDPatch/releases</li>\n" +
                        "</ul>",
                "Для изменения расположения диска D нажать ⚙️",

                "📌 Добавить как внешний ярлык",
                "Не показывать снова",
                "Создание ярлыка программы для файла .desktop. После вы можете запустить его, нажав и удерживая значок приложения и кликнуть на ярлык программы. Можно добавить максимум четыре ярлыка. Перед запуском с ярлыка убедитесь, что приложение не работает в фоновом режиме. \n\nПосле удаления ярлыка exe (файл .desktop) внешний ярлык будет автоматически удален при следующем запуске приложения. \n\nДля этой функции требуется Android 7 и выше.",

        };

        stringMap.put("zh", zhStrings);
        stringMap.put("en", enStrings);
        stringMap.put("ru", ruStrings);
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
