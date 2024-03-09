package com.example.datainsert.exagear;

import static com.example.datainsert.exagear.QH.rslvID;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.eltechs.axs.Globals;
import com.eltechs.ed.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//函数名，函数参数，变量名也不能随意改。这样用户添加的旧功能，尝试读取旧变量名，添加另外一个功能时RR被复制成新的，旧变量名就没了

//使用sparseArray，可以不连续的数值，可以不按顺序的数值。在版本更迭的时候，数值可以更改，不影响未更新的功能，但是成员变量名不能更改。
public class RR {
    /**
     * 存储按键按钮的framelayout布局.在dialogfragment里用这个来寻找当前是否存在该布局
     */
    public static final int BTNCONTAINER_RESOURCE_ID = 0x7f095123;
    public static final int VIEWPAGER_RESOURCE_ID = 0x7f095124; //滑动视图的id
    private static final Map<String, SparseArray<String>> stringMap = new HashMap<>();
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
    public static int cmCtrl_s1_msMoveViewport = 136;
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
    public static int shortcut_menuItem_addAppSc = 103;     //app快捷方式
    public static int shortcut_DontShowUp = 104;
    public static int shortcut_TipAfterAdd = 105;
    public static int mw_newContProgress = 106;   //多wine v2 下载/删除wine
    public static int mw_fragTitle = 107;
    public static int mw_tabTitles = 108;
    //    public static int mw_tabDlable = 109;
    public static int mw_refreshBtn = 109;
    public static int mw_dlSourceBtn = 110;
    public static int mw_dataSizeMB = 111;
    public static int mw_dialog_download = 112;
    public static int mw_dialog_extract = 113;
    public static int mw_dialog_checksum = 114;
    public static int mw_localMenuItem = 115;
    public static int mw_localState = 116;
    public static int mw_tips = 117;
    public static int mw_contNoWineTips = 118;
    public static int render_title = 119;
    public static int fab_hide = 120;
    public static int pa_title = 130;
    public static int pa_explain = 131;
    public static int pa_checkRun = 132;
    public static int pa_checkLog = 133;
    public static int pa_btnParam = 134;
    public static int pa_troubleShooting = 135;
    public static int xegw_title = 137;
    public static int xegw_info = 138;
    public static int xegw_legacyDraw_tip = 139;
    public static int xegw_notification = 140;
    public static int xegw_btyOpt = 141;
    public static int DriveD2_info = 142;
    public static int DriveD2_newDrive = 143;
    public static int DriveD2_delDrive = 144;
    public static int DriveD2_devType = 145;
    public static int DriveD2_parType = 146;
    public static int DriveD2_title = 147;
    public static int DriveD2_errors = 148;
    public static int global_edit = 149;
    public static int global_del = 150;
    public static int global_add = 151;
    public static int othArg_taskset_useCustom = 153;
    public static int othArg_ib_autorun = 154;
    public static int othArg_serviceExeDisable = 155;
    public static int conSet_otherArgv_title = 156;
    public static int conSet_otherArgv_prefValue = 157;
    public static int othArg_preview = 162;
    public static int othArg_info = 163;
    public static int othArg_edit_delConfirm = 164;
    public static int othArg_edit_attrTitles = 165;
    public static int othArg_edit_typeChoices = 166;
    public static int othArg_edit_typeInfo = 167;
    public static int othArg_preview_titles = 168;
    public static int othArg_info_more = 169;
    public static int fabVO_checkTitles = 170;
    public static int fabVO_radioTitles = 171;
    public static int fabVO_btnTitles = 172;
    public static int fabVO_startResults = 173;

    public static int  fsm_event_complete = 174;
    public static int fsm_event_release  = 175;
    public static int fsm_event_new_touch = 176;
    public static int fsm_event_near = 177;
    public static int fsm_event_far = 178;
    public static int fsm_event_noMove = 179;
    public static int fsm_event_slowMove = 180;
    public static int fsm_event_fastMove = 181;
    public static int fsm_event_noMoveThenRelease = 182;
    public static int fsm_event_moveThenRelease = 183;
    public static int fsm_state_testSpd = 184;
    public static int fsm_state_init=185;
    public static int fsm_state_fallback=186;
    public static int fsm_state_1FMouseMove=187;
    public static int fsm_state_action_click =188;
    public static int fsm_state_action_msMove =189;
    public static int fsm_state_msScroll=190;
    public static int fsm_state_distFingerMouse=191;
    public static int fsm_state_fingerNum=192;
    public static int fsm_state_2FZoom=193;
    public static int fsm_state_action_option =194;
    public static int fsm_event_finger_num_same = 195;
    public static int ctr2_editTab_titles = 200;
    public static int global_type = 204;
    public static int global_color = 205;
    public static int global_alias = 207;
    public static int global_size = 208;
    public static int global_keycode = 209;
    public static int global_shape = 210;
    public static int ctr2_prop_direction_title = 212;
    public static int ctr2_prop_colorStyle_names = 213;
    public static int ctr2_prop_type_names = 214;
    public static int ctr2_prop_shape_names = 215;
    public static int ctr2_prop_direction_names = 216;
    public static int ctr2_ges_state = 217;
    public static int ctr2_ges_action = 218;
    public static int ctr2_ges_transition = 219;
    public static int ctr2_ges_preState = 220;
    public static int ctr2_ges_postState = 221;
    public static int ctr2_ges_subTitles= 222;
    public static int ctr2_ges_debug_realtimeInfo =223;
    public static int ctr2_ges_debug_graph = 224;
    public static int global_done = 225;
    public static int ctr2_ges_fillInAlias = 226;
    public static int ctr2_ges_state_edit_tabTitles = 227;
    public static int ctr2_ges_stateDelWarns = 228;
    public static int ctr2_ges_actionDelWarns = 229;
    public static int global_instructions = 230;
    public static int ctr2_ges_transInfo = 231;
    public static int ctr2_ges_state_edit_tran_tableHeaders = 232;
    public static int ctr2_stateProp_PrsRlsCheck = 233;
    public static int ctr2_stateProp_fingerIndex = 234;
    public static int ctr2_stateProp_fingerXYType = 235;
    public static int ctr2_stateProp_option = 236;
    public static int ctr2_stateProp_noMoveThreshold = 237;
    public static int ctr2_stateProp_zoom2Finger = 238;
    public static int ctr2_stateProp_farDistThres = 239;
    public static int ctr2_stateProp_fastMoveThres = 240;
    public static int ctr2_stateProp_countDown = 241;
    public static int global_empty = 242;
    public static int ctr2_FSMR_value_fingerXYType = 243;
    public static int ctr2_FSMR_value_fingerIndex = 244;
    public static int ctr2_FSMR_value_pointerMoveType = 245;
    public static int ctr2_customControls2 = 246;
    public static int ctr2_option_showHideTouchArea = 248;
    public static int global_quit = 249;
    public static int ctr2_option_showAll = 250;
    public static int ctr2_option_softInput = 251;
    public static int ctr2_profile_importMsgs=252;
    public static int ctr2_profile_exportMsgs=253;
    public static int ctr2_profile_addOptions=254;
    public static int ctr2_profile_oneOptions=255;
    public static int ctr2_profile_editName=256;
    public static int ctr2_profile_delConfirm=257;
    public static int global_save=258;
    public static int ctr2_other_saveExit=259;
    public static int ctr2_other_mouseSpeed=260;
    public static int ctr2_other_showTouchArea = 261;




    public static String locale = refreshLocale();

    static {
        SparseArray<String> zhArray = new SparseArray<>();
        zhArray.put(Dialog_PosBtn, "确定");
        zhArray.put(Dialog_NegBtn, "取消");
        zhArray.put(DriveD_Title, "修改D盘路径");
        zhArray.put(DriveD_ParDirKey_1, "手机存储(根目录)");
        zhArray.put(DriveD_ParDirKey_2, "手机存储(应用专属目录)");
        zhArray.put(DriveD_ParDirKey_3, "外置SD卡(应用专属目录)");
        zhArray.put(DriveD_Explain, "请指定一个安卓文件夹作为D盘");
        zhArray.put(DriveD_DescTitle, "说明");
        zhArray.put(DriveD_DescCont, "android11及以上，在非应用专属目录下的游戏加载/读档速度可能变慢。解决方法是将d盘修改到应用专属目录，或将游戏复制到c/z盘（c/z盘固定在应用专属目录）。 ");
        zhArray.put(DriveD_SncBrBtn, "重启");
        zhArray.put(DriveD_SncBrTxt, "设置已更新，手动重启应用后生效");
        zhArray.put(DriveD_ToastExitFail, "设置未更新");
        zhArray.put(DriveD_EditDstTitle, "文件夹名称");
        zhArray.put(DriveD_EditParTitle, "文件夹位置");
        zhArray.put(DriveD_getPathFail, "无法获取路径");
        zhArray.put(DriveD_check_1, "文件夹父目录存在");
        zhArray.put(DriveD_check_2, "文件夹存在");
        zhArray.put(DriveD_check_3, "是文件夹类型");
        zhArray.put(DriveD_check_4, "具有该文件夹的读取权限");
        zhArray.put(DriveD_check_5, "具有该文件夹的写入权限");
        zhArray.put(DriveD_NoStrgPmsn, "应用的文件存储权限被禁止");
        zhArray.put(SelObb_info, "无法找到obb数据包。请检查数据包名称和位置，或手动选择obb文件。");
        zhArray.put(SelObb_btn, "手动选择");
        zhArray.put(SelObb_selResult, "所选文件不是obb数据包$选中obb。正在解压，请耐心等待……");
        zhArray.put(CstRsl_swtTxt, "使用自定义分辨率");
        zhArray.put(CstRsl_editW, "输入自定义宽度");
        zhArray.put(CstRsl_editH, "输入自定义高度");
        zhArray.put(cmCtrl_title, "自定义操作模式");
        zhArray.put(cmCtrl_lgPressHint, "大部分选项可以通过长按查看说明");
        zhArray.put(cmCtrl_tabMouse, "鼠标");
        zhArray.put(cmCtrl_tabKeys, "按键");
        zhArray.put(cmCtrl_tabStyle, "样式");
        zhArray.put(cmCtrl_s1_showCursor, "显示鼠标光标");
        zhArray.put(cmCtrl_s1_showCursorTip, "设置进入容器后鼠标光标显示或隐藏。\n若因为添加“强制显示光标”功能导致同时显示默认鼠标光标和游戏自带光标，使用该选项可隐藏默认鼠标光标。");
        zhArray.put(cmCtrl_s1_relMove, "鼠标移动使用相对定位（手势控制 2）");
        zhArray.put(cmCtrl_s1_relMoveTip, "勾选后采用第二种手势控制。\n\n**手势控制 1**：\n- 单指点击 = 鼠标在手指位置左键点击\n- 单指按下后立刻移动 = 鼠标滚轮\n- 单指长按后移动 = 鼠标在手指位置左键按下（拖拽）\n- 单指长按后松开 = 鼠标在手指位置右键点击\n- 双指点击 = 显示/隐藏安卓输入法\n- 双指按下后移动 = 缩放窗口（松开一根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单\n\n**手势控制 2**：\n- 单指点击 = 鼠标原处左键点击\n- 单指移动 = 鼠标位置移动\n- 二指点击 = 鼠标原处右键点击\n- 二指按下后立刻移动 = 鼠标滚轮\n- 二指长按后移动 = 鼠标在当前位置左键按下（拖拽）\n- 三指按下并移动 = 缩放窗口（松开1根手指变为移动窗口）\n- 三指点击 = 弹出操作选项菜单");
        zhArray.put(cmCtrl_s1_msMoveViewport, "限制移动距离$更新位置时间间隔$每次移动距离");
        zhArray.put(cmCtrl_s2_layoutMode, "布局方式");
        zhArray.put(cmCtrl_s2_layoutModeTip, "左右侧栏：经典布局，按键位于主画面的左右两侧，每一侧可以有多列，每一列可以有多个按键。\n\n自由位置：按钮可以自由摆放（需要启动容器后，三指触屏调出菜单项，进入编辑模式后才能编辑位置）。可以添加摇杆按钮。\n\n两种布局数据分开存储，位于手机存储目录/Android/data/包名/files/custom_control2(3).txt，可以手动备份，但不保证日后的更新能兼容。");
        zhArray.put(cmCtrl_s2_LSideTitle, "左侧按键栏");
        zhArray.put(cmCtrl_s2_RSideTitle, "右侧按键栏");
        zhArray.put(cmCtrl_s2_FreePosTitle, "选取按键");
        zhArray.put(cmCtrl_s2_popEdit, "编辑");
        zhArray.put(cmCtrl_s2_popDel, "删除");
        zhArray.put(cmCtrl_s2_ColEditTip, "长按按钮拖拽排序。点击按钮进行编辑。");
        zhArray.put(cmCtrl_s2_modeSide, "左右侧栏");
        zhArray.put(cmCtrl_s2_modeFree, "自由位置");
        zhArray.put(cmCtrl_s3_sampleBtn, "样例");
        zhArray.put(cmCtrl_s3_btnColor, "按钮颜色&透明度");
        zhArray.put(cmCtrl_s3_btnColorTip, "按钮颜色为6位的十六进制数字，如：2121FA，随便找一个在线颜色选取网站即可获取。\n透明度为0-255，当透明度设置到0时，按钮背景完全透明，文字保留1/3的透明度。");
        zhArray.put(cmCtrl_s3_btnSize, "按钮大小");
        zhArray.put(cmCtrl_s3_btnSizeTip, "设置按钮的宽高。范围为10~200dp。小于10为自适应宽/高。");
        zhArray.put(cmCtrl_s3_sideColor, "左右侧栏背景颜色");
        zhArray.put(cmCtrl_s3_sideColorTip, "该选项对应当“按键 - 布局方式”选择为左右侧栏时，左右侧栏的底色。\n输入格式为6位的十六进制颜色，如000000，透明度固定为255完全不透明。");
        zhArray.put(cmCtrl_actionEdit, "编辑按键");
        zhArray.put(cmCtrl_actionCtrlShow, "显示按键");
        zhArray.put(cmCtrl_actionCtrlHide, "隐藏按键");
        zhArray.put(cmCtrl_mouseLeftName, "鼠标左键");
        zhArray.put(cmCtrl_mouseRightName, "鼠标右键");
        zhArray.put(cmCtrl_mouseMiddleName, "鼠标中键");
        zhArray.put(cmCtrl_allKeysJoyTitle, "摇杆");
        zhArray.put(cmCtrl_allKeysMouseTitle, "鼠标");
        zhArray.put(cmCtrl_editMenu1Dialog, "显示详细设置");
        zhArray.put(cmCtrl_editMenu2Exit, "退出编辑");
        zhArray.put(cmCtrl_BtnEditReName, "重命名");
        zhArray.put(cmCtrl_JoyEdit4Ways, "仅使用4个方向而不是8个方向");
        zhArray.put(cmCtrl_JoyEdit4WaysTip, "勾选此选项使用4个方向，则同一时刻只会按下一个按键。若不勾选，当移动到斜方向时会触发两个按键，即有8个方向。\n允许斜向会导致判定方向变化的角度从45°变为22.5°，所以在游戏不支持斜向的情况下建议开启此选项。");
        zhArray.put(cmCtrl_JoyEditKeys, "设置摇杆按键");
        zhArray.put(cmCtrl_JoyEditKeyCstm, "自定义");
        zhArray.put(cmCtrl_gs_lClick, "左键点击$单指点击屏幕$单指点击屏幕");//获取的时候按split("\\$")分割成三段，第一个是标题，第二个是操作1的介绍，第二个是操作2的介绍
        zhArray.put(cmCtrl_gs_rClick, "右键点击$单指长按后松开$二指点击屏幕");
        zhArray.put(cmCtrl_gs_vScroll, "鼠标滚轮$单指上下滑动$二指上下滑动");
        zhArray.put(cmCtrl_gs_dndLeft, "左键拖拽$单指长按后移动$二指长按后移动");
        zhArray.put(cmCtrl_gs_zoom, "缩放窗口$二指按下并移动，松开1指变为移动窗口$三指按下并移动，松开1指变为移动窗口");
        zhArray.put(cmCtrl_gs_menu, "弹窗菜单$三指点击屏幕$三指点击屏幕");
        zhArray.put(cmCtrl_gs1Abs_title, "手势控制 1$原始的默认模式");
        zhArray.put(cmCtrl_gs2Rel_title, "手势控制 2$触摸板模式");
        zhArray.put(cmCtrl_gs_moveCursor, "移动鼠标$ $单指按下并移动");
        zhArray.put(cmCtrl_gs_keyboard, "安卓输入法$二指点击屏幕$ ");
        zhArray.put(cmCtrl_actionCtrlTip, "手势说明");
        zhArray.put(cmCtrl_actionSubCtrl, "自定义操作模式");
        zhArray.put(cmCtrl_s1_msSpd, "鼠标灵敏度");
        zhArray.put(cmCtrl_reset, "重置");
        zhArray.put(cmCtrl_s1_msSpdTip, "调整鼠标移动速度，仅在使用“手势控制 2”时生效。");
        zhArray.put(cmCtrl_s1_msOffScr, "视角转动速度（鼠标移到屏幕边界之后）");
        zhArray.put(cmCtrl_s1_msOffScrTip, "数值为0~70。一般设置为0，即不允许鼠标移到桌面外部。\n某些游戏中，若鼠标移动到边界时视角无法继续转动，可以尝试调大该数值，允许鼠标移到边界外以继续转动视角。\n\n注意若想此功能生效，需要设置游戏全屏，并且在环境设置中修改分辨率与游戏全屏分辨率完全相同，即画面右下方不能有多余的黑边。无需设置注册表MouseWarpOverride=force。");
        zhArray.put(cmCtrl_s2_sideTitleTip, "点击“+”新建一列按键。长按可进行排序。");
        zhArray.put(cmCtrl_s2_selectBtn, "选择按键……");
        zhArray.put(cmCtrl_BtnEditTrigger, "松手时保持按下状态");
        zhArray.put(cmCtrl_BtnEditComb, "组合键");
        zhArray.put(cmCtrl_tabOther, "其他");
        zhArray.put(cmCtrl_s4_tips, "<ul>\n" +
                "  <li>&ensp; 在环境设置中将操作模式调为“默认(default)”即可启用此自定义模式。</li>\n" +
                "  <li>&ensp; 启动环境后，可以通过三指触屏调出此界面进行实时修改。</li>\n" +
                "  <li>&ensp; 本功能仅提供基础设置，若有更复杂的需求请使用Input Bridge.</li>\n" +
                "</ul>");
        zhArray.put(cmCtrl_s4_export, "导出");
        zhArray.put(cmCtrl_s4_import, "导入");
        zhArray.put(cmCtrl_s4_trsportTitle, "数据转移");
        zhArray.put(cmCtrl_s4_trsportTip, "用户可以将自定义按键功能相关数据导入或导出，但不保证此功能升级后兼容旧版数据。\n点击导出，将数据以文本格式复制到剪切板；点击导入，将尝试从剪切板读取文本转为数据。\n进入环境后无法进行导入。");
        zhArray.put(cmCtrl_s4_exportResult, "已复制到剪切板");
        zhArray.put(cmCtrl_s4_importResult, "导入成功$导入失败");
        zhArray.put(cmCtrl_s3_txtSize, "文字大小");
        zhArray.put(cmCtrl_s3_btnRoundShape, "圆形按钮");
        zhArray.put(cmCtrl_actionRotate, "旋转屏幕");
        zhArray.put(abtFab_title, "关于");
        zhArray.put(abtFab_info, "<ul>\n" +
                "  <li>&ensp; Exagear模拟器官方（Eltechs）已停止开发。本菜单为第三方补丁，旨在添加一些便捷功能。</li>\n" +
                "  <li>&ensp; 您可以通过“ED自助补丁”将第三方功能加入原版apk中：https://github.com/ewt45/EDPatch/releases。请勿用于商业用途。</li>\n" +
                "</ul>");
        zhArray.put(firstLaunch_snack, "额外功能可以在右下操作按钮中找到。");
        zhArray.put(shortcut_menuItem_addAppSc, "添加为app快捷方式");
        zhArray.put(shortcut_DontShowUp, "不再显示此提示");
        zhArray.put(shortcut_TipAfterAdd, "为该.desktop文件创建app快捷方式，之后可以通过长按app图标 -> 点击快捷方式快速启动程序。快捷方式最多可以添加四个，启动快捷方式前确保app后台已被清除。\n\n将该exe快捷方式删除后，app快捷方式在下一次启动时会被自动删除。\n\n该功能在安卓7以下无法使用。");
        zhArray.put(mw_newContProgress, "创建容器...");
        zhArray.put(mw_fragTitle, "添加/删除wine版本");
        zhArray.put(mw_tabTitles, "本地$可下载$说明");
        zhArray.put(mw_refreshBtn, "↻ 刷新列表");
        zhArray.put(mw_dlSourceBtn, "下载源");
        zhArray.put(mw_dataSizeMB, " MB");
        zhArray.put(mw_dialog_download, " 下载成功$ 下载失败$本地文件已存在，跳过下载$有下载正在进行中，无法新建下载");
        zhArray.put(mw_dialog_extract, "解压中...$解压成功$解压失败");
        zhArray.put(mw_localMenuItem, "安装$校验$卸载$删除压缩包");
        zhArray.put(mw_dialog_checksum, "压缩包校验结束，没有发现问题$校验码文本不存在，无法校验$压缩包损坏，请尝试删除并重新下载");
        zhArray.put(mw_localState, "已启用$未启用");
        zhArray.put(mw_tips, "<ul>\n" +
                "<li>&ensp; “本地”页面：<br/>对已下载或预置的wine进行管理。点击安装（解压）后，会显示“已启用”，已启用的wine会显示在新建容器时的选项中。可通过卸载（删除已解压文件夹）来减少本地占用。本地存放位置为：z:/opt/WineCollection。</li>\n" +
                "<li>&ensp; “可下载”页面：<br/>从网络下载更多版本的wine。下载源可选择WineHQ（官方构建）或Kron4ek（第三方，体积小），其中WineHQ仅提供 ubuntu 18 的对应列表，Kron4ek不提供 staging 版本。下载成功后会显示在“本地”页面。若由于网络原因下载失败，可以尝试切换下载线路。</li>\n" +
                "</ul>\n");
        zhArray.put(mw_contNoWineTips, "没有检测到已启用的wine，本次创建的容器可能无法启动。建议删除该容器，点击下载按钮下载并安装wine后，重新创建容器。");
        zhArray.put(render_title, "图形渲染设置");
        zhArray.put(fab_hide, "隐藏");
        zhArray.put(pa_title, "PulseAudio (XSDL)");
        zhArray.put(pa_explain, "PulseAudio用于播放音频，可以缓解一部分声音问题。本功能用到的PulseAudio服务端提取自Xserver XSDL，需要手机支持64位。");
        zhArray.put(pa_checkRun, "开启pulseaudio服务$在启动容器时一并启动PulseAudio服务");
        zhArray.put(pa_checkLog, "输出日志$将日志保存到 z:/opt/edpatch/pulseaudio-xsdl/logs/palog.txt");
        zhArray.put(pa_btnParam, "修改启动参数$错误的参数会导致无法启动，请谨慎修改。不支持引号排除空格。");
        zhArray.put(pa_troubleShooting, "故障排查 ⓘ$" +
                "检查PulseAudio服务是否正常工作：启动容器后左下角起点 - 运行 - 输入winecfg打开 - 音效，“选中的驱动”显示 winepulse.drv，点击“测试音频”按钮能听到声音。" +
                "\n\n若显示结果不正确，请检查以下几点：" +
                "$1. pulseaudio elf文件被正确执行：勾选输出日志，启动容器并查看日志是否有报错。若以默认参数启动，应有“Daemon startup successful.”字样。" +
                "\n2. pulse的tcp模块被正确加载：若以默认参数启动，./pulseaudio.conf中会自动加载tcp模块，无需额外设置：load-module module-native-protocol-tcp auth-anonymous=true port=4713 " +
                "\n3. 启动容器时导出环境变量：PULSE_SERVER=tcp:127.0.0.1:4713。本功能在启动容器时会自动添加环境变量，无需额外设置。可通过过Exaterm或Putty确认环境变量。" +
                "\n4. wine注册表声音驱动项设置正确：某些数据包会在左下角起点提供切换pulse声音的便捷注册表项。也可以手动修改：左下角起点 - 运行 - 输入regedit打开，找到以下路径：[HKEY_CURENT_USER\\Software\\Wine\\Drivers] 若右侧包含“Audio”项，则该项的值应包含字符串“pulse”。" +
                "\n \n ");
        zhArray.put(xegw_title, "Xegw (termux:x11)");
        zhArray.put(xegw_info, "x11服务端用于显示图形画面，由于exa实现的x11服务端比较简陋，无法支持dxvk等，所以将termux:x11实现的x11服务端移植到exa内，以期实现更好的渲染效果。感谢termux:x11开发者Twaik的帮助。");
        zhArray.put(xegw_legacyDraw_tip, "如果启动容器后只显示黑屏和一个箭头鼠标，勾选此选项可以解决问题，但是渲染效率会变低。");
        zhArray.put(xegw_notification, "Xegw x服务端对应的进程，保留此通知以防应用切后台时进程被杀");
        zhArray.put(xegw_btyOpt, "关闭电池优化$运行容器时，xegw在手机通知栏中示一条固定通知（加上exa原有的一共两个），这样应用切换后台时不会黑屏。\n\n若依然出现黑屏，请在手机设置中关闭本应用的电池优化（可以通过此按钮跳转），若关闭电池优化依然出现黑屏，请在手机设置中允许应用自启动（设置界面因系统而异，请自行寻找）。");
        zhArray.put(DriveD2_title, "修改磁盘路径");
        zhArray.put(DriveD2_info, "请为该磁盘设置对应的安卓文件夹路径");
        zhArray.put(DriveD2_newDrive, "新建盘符");
        zhArray.put(DriveD2_delDrive, "删除当前盘符");
        zhArray.put(DriveD2_devType, "手机存储$其他外部存储设备$其他外部存储设备(无)");
        zhArray.put(DriveD2_parType, "根目录$应用专属目录");
        zhArray.put(DriveD2_errors, "请先授予app存储权限 $ 该文件夹不存在 $ 该路径指向的是文件而不是文件夹 $ 没有对该文件夹的读取权限 $ 没有对该文件夹的写入权限 $ 读取该路径时出现错误: ");
        zhArray.put(conSet_otherArgv_title, "额外启动参数");
        zhArray.put(conSet_otherArgv_prefValue, "额外命令/环境变量");
        zhArray.put(othArg_info, "本页面为全部可用的参数，被勾选的参数会在当前容器中启用。$了解更多...");
        zhArray.put(othArg_info_more, "启动容器时，勾选的额外参数会被插入到运行wine的执行命令中，插入后的完整命令可在/sdcard/x86-stderr.txt中查看。\n\n若插入参数前，原命令已包含这个参数，则可能插入失败。实际结果可以点击\"预览\"按钮输入测试命令或启动容器后在txt中查看。\n\n全部可用参数存在z:/opt/edpatch/contArgs.txt中，每个容器启用的参数存在z:/home/xdroid_n/contArgs.txt中。但由于对存储格式有严格要求，不建议直接修改txt。");
        zhArray.put(othArg_taskset_useCustom, "手动指定CPU核心");
        zhArray.put(othArg_ib_autorun, "自动运行ib.exe(使用InputBridge时省去手动运行ib.exe)");
        zhArray.put(othArg_serviceExeDisable, "禁用services.exe(某些游戏需要禁用它才能正常运行)");
        zhArray.put(othArg_preview, "预览");
        zhArray.put(global_add, "添加");
        zhArray.put(global_edit, "编辑");
        zhArray.put(global_del, "删除");
        zhArray.put(othArg_edit_delConfirm, "确定要删除该参数吗？\n此操作会对全部容器生效。若您想仅修改当前容器，点击勾选框取消勾选即可。\n\n");
        zhArray.put(othArg_edit_attrTitles, "参数别名$参数内容$参数类型$新建的容器默认勾选该参数");
        zhArray.put(othArg_edit_typeChoices, "环境变量$命令 - 原命令开头$命令 - 原命令执行前$命令 - 原命令执行后");
        zhArray.put(othArg_edit_typeInfo, "参数类型分为两种：环境变量和命令。\n\n环境变量: 作为本次执行命令的环境变量，原命令开头若包含相同名称的环境变量，则会覆盖该参数的值。\n\n命令：若选择 在原命令执行前/后，则此参数的命令与原命令间用一个 '&' 连接。");
        zhArray.put(othArg_preview_titles, "原命令示例$插入参数后");
        zhArray.put(fabVO_checkTitles,"使用vtest协议2（需要Mesa 19.1.0 v3及以上）$使用GL ES 3.x而不是OpenGL$使用多线程egl访问$DXTn (S3TC) 解压（一些游戏需要）$自动重启服务");
        zhArray.put(fabVO_radioTitles,"覆盖位置:$左上方$居中$隐藏*$* 与VTEST_WIN=1一起使用以在X11窗口中绘制，而不是悬浮窗覆盖");
        zhArray.put(fabVO_btnTitles,"清除服务$开始服务$开启悬浮窗权限");
        zhArray.put(fabVO_startResults,"服务启动成功，现在你可以启动容器并尝试运行游戏了。$服务启动失败。");


        zhArray.put(fsm_event_complete,"完成");
        zhArray.put(fsm_event_release,"某手指松开");
        zhArray.put(fsm_event_new_touch,"新手指按下");
        zhArray.put(fsm_event_near,"手指距离指针_近");
        zhArray.put(fsm_event_far,"手指距离指针_远");
        zhArray.put(fsm_event_noMove,"手指_未移动");
        zhArray.put(fsm_event_slowMove,"手指_移动_慢速");
        zhArray.put(fsm_event_fastMove,"手指_移动_快速");
        zhArray.put(fsm_event_noMoveThenRelease,"某手指_未移动并松开");
        zhArray.put(fsm_event_moveThenRelease,"某手指_移动并松开");
        zhArray.put(fsm_event_finger_num_same,"手指数量不变");
        zhArray.put(fsm_state_testSpd,"限时测量手指移速");
        zhArray.put(fsm_state_init,"初始状态");
        zhArray.put(fsm_state_fallback,"默认（返回初始状态）");
        zhArray.put(fsm_state_1FMouseMove,"手指移动带动鼠标移动");
        zhArray.put(fsm_state_action_click,"键盘/鼠标按钮");
        zhArray.put(fsm_state_action_msMove,"鼠标位置移动");
        zhArray.put(fsm_state_msScroll,"鼠标滚轮滚动");
        zhArray.put(fsm_state_distFingerMouse,"判断手指与鼠标位置距离");
        zhArray.put(fsm_state_fingerNum,"检测手指数量变化");
        zhArray.put(fsm_state_2FZoom,"两根手指缩放");
        zhArray.put(fsm_state_action_option,"执行选项");
        zhArray.put(ctr2_editTab_titles,"按键$手势$多配置$其他");
        zhArray.put(global_type,"类型");
        zhArray.put(global_color,"颜色");
        zhArray.put(global_alias,"别名");
        zhArray.put(global_size,"尺寸");
        zhArray.put(global_keycode,"按键码");
        zhArray.put(global_shape,"形状");
        zhArray.put(ctr2_prop_direction_title,"方向");
        zhArray.put(ctr2_prop_colorStyle_names,"描边$填充");
        zhArray.put(ctr2_prop_type_names,"按钮$摇杆$十字键");
        zhArray.put(ctr2_prop_shape_names,"矩形$圆形");
        zhArray.put(ctr2_prop_direction_names,"4方向$8方向");
        zhArray.put(ctr2_ges_state,"状态");
        zhArray.put(ctr2_ges_action,"附加操作");
        zhArray.put(ctr2_ges_transition,"状态转移");
        zhArray.put(ctr2_ges_preState,"转移前状态");
        zhArray.put(ctr2_ges_postState,"转移后状态");
        zhArray.put(ctr2_ges_subTitles,"状态$附加操作$调试");
        zhArray.put(ctr2_ges_debug_realtimeInfo,"实时显示状态转移");
        zhArray.put(ctr2_ges_debug_graph,"查看当前的状态转移图");
        zhArray.put(global_done,"完成");
        zhArray.put(ctr2_ges_fillInAlias,"请填写别名。");
        zhArray.put(ctr2_ges_state_edit_tabTitles,"状态转移$属性");
        zhArray.put(ctr2_ges_stateDelWarns,"确定要删除吗？与此状态相关的状态转移也会被一并删除。" +
                "$以下转移将会被删除:");
        zhArray.put(ctr2_ges_actionDelWarns,"确定要删除吗？" +
                "$以下状态转移将无法再使用此附加操作:");
        zhArray.put(global_instructions,"说明");
        zhArray.put(ctr2_ges_transInfo,"状态转移的基本概念：[当前状态]运行 -> 满足某个条件 -> 发送对应[事件] -> 执行对应[附加操作] -> 停止当前状态，运行[下一个状态]。\n为每个事件设置不同的下一状态，即可走向不同分支。" +
                "\n\n可发送事件: 该状态可能发送的全部事件" +
                "\n\n下一个状态: 用户可自定义此状态。只能从当前已创建的状态列表中选择。" +
                "\n\n 附加操作: 用户可自定义此操作。只能从当前已创建的操作列表中选择。注意执行多个操作时有先后顺序。");
        zhArray.put(ctr2_ges_state_edit_tran_tableHeaders,"可发送事件$下一个状态$附加操作");
        zhArray.put(ctr2_stateProp_PrsRlsCheck,"按下$松开");
        zhArray.put(ctr2_stateProp_fingerIndex,"第几根手指");
        zhArray.put(ctr2_stateProp_fingerXYType,"手指坐标类型");
        zhArray.put(ctr2_stateProp_option,"操作");
        zhArray.put(ctr2_stateProp_noMoveThreshold,"手指移动小于此距离，则算作不移动");
        zhArray.put(ctr2_stateProp_zoom2Finger,"第几根手指 (其一)$第几根手指 (其二)");
        zhArray.put(ctr2_stateProp_farDistThres,"超过此大小则属于远距离，否则属于近距离");
        zhArray.put(ctr2_stateProp_fastMoveThres,"手指移动大于此距离，则算作快速移动；小于此距离则算作慢速移动");
        zhArray.put(ctr2_stateProp_countDown,"倒计时限时 (毫秒)");
        zhArray.put(global_empty,"空");
        zhArray.put(ctr2_FSMR_value_fingerXYType,"任意一根手指最后一次位置改变时$手指初始按下时$手指当前位置");
        zhArray.put(ctr2_FSMR_value_fingerIndex,"全部手指$1$2$3$4$5$6$7$8$9$10");
        zhArray.put(ctr2_FSMR_value_pointerMoveType,"正常$视角转动");
        zhArray.put(ctr2_customControls2,"自定义操作模式");
        zhArray.put(ctr2_option_showHideTouchArea,"显示/隐藏屏幕按键");
        zhArray.put(global_quit,"退出");
        zhArray.put(ctr2_option_showAll,"显示全部选项");
        zhArray.put(ctr2_option_softInput,"显示/隐藏安卓输入法");
        zhArray.put(ctr2_profile_importMsgs,"导入成功: $导入失败: ");
        zhArray.put(ctr2_profile_exportMsgs,"导出成功: $导出失败: ");
        zhArray.put(ctr2_profile_addOptions,"空白配置$复制现有配置$从本地文件导入");
        zhArray.put(ctr2_profile_oneOptions,"导出为文件$重命名$删除");
        zhArray.put(ctr2_profile_editName,"配置名称");
        zhArray.put(ctr2_profile_delConfirm,"确定要删除吗？");
        zhArray.put(global_save,"保存");
        zhArray.put(ctr2_other_saveExit,"保存并退出编辑");
        zhArray.put(ctr2_other_mouseSpeed,"鼠标移动速度倍率");
        zhArray.put(ctr2_other_showTouchArea,"显示屏幕按键 (退出编辑时生效)");


        /*







         */
        SparseArray<String> enArray = new SparseArray<>();
        enArray.put(Dialog_PosBtn, "confirm");
        enArray.put(Dialog_NegBtn, "cancel");
        enArray.put(DriveD_Title, "Change the Location of Drive D");
        enArray.put(DriveD_ParDirKey_1, "External Storage");
        enArray.put(DriveD_ParDirKey_2, "External Storage(App-specific storage)");
        enArray.put(DriveD_ParDirKey_3, "SD Card(App-specific storage)");
        enArray.put(DriveD_Explain, "set an android directory as drive d");
        enArray.put(DriveD_DescTitle, "tips");
        enArray.put(DriveD_DescCont, "on android11+, read/write speed could be extremely slow. To solve this problem, set drive d to app-specific storage, or copy game to drive c/z ");
        enArray.put(DriveD_SncBrBtn, "restart");
        enArray.put(DriveD_SncBrTxt, "preference is changed, restart app to apply it");
        enArray.put(DriveD_ToastExitFail, "preference is not changed");
        enArray.put(DriveD_EditDstTitle, "Directory Name");
        enArray.put(DriveD_EditParTitle, "Directory Location");
        enArray.put(DriveD_getPathFail, "unable to retrieve the path");
        enArray.put(DriveD_check_1, "Directory parent folder exists");
        enArray.put(DriveD_check_2, "Directory exists");
        enArray.put(DriveD_check_3, "is directory type");
        enArray.put(DriveD_check_4, "App is allowed to read the directory");
        enArray.put(DriveD_check_5, "App is allowed to write to the directory");
        enArray.put(DriveD_NoStrgPmsn, "App's storage permission is not granted");
        enArray.put(SelObb_info, "No obb detected, please try selecting it manually");
        enArray.put(SelObb_btn, "select manually");
        enArray.put(SelObb_selResult, "Selected file isn't an obb file.$Obb found. Extracting...");
        enArray.put(CstRsl_swtTxt, "using custom resolution");
        enArray.put(CstRsl_editW, "input width");
        enArray.put(CstRsl_editH, "input height");
        enArray.put(cmCtrl_title, "Custom Controls");
        enArray.put(cmCtrl_lgPressHint, "Long click options to check its description.");
        enArray.put(cmCtrl_tabMouse, "Mouse");
        enArray.put(cmCtrl_tabKeys, "Keys");
        enArray.put(cmCtrl_tabStyle, "Style");
        enArray.put(cmCtrl_s1_showCursor, "Display mouse Cursor");
        enArray.put(cmCtrl_s1_showCursorTip, "Display or hide the mouse cursor in containers.\nIf there are two cursors in game checking this option may help to hide one.");
        enArray.put(cmCtrl_s1_relMove, "Move mouse relatively (Gesture Mode 2)");
        enArray.put(cmCtrl_s1_relMoveTip, "If checked, the second Gesture mode will be used.\n\n**Gesture Mode 1**:\n- One finger click = Mouse left click at finger's position\n- One finger press and move = Mouse wheel scroll\n- One finger long press and move = Mouse left button press (drag)\n- One finger long click = Mouse right click\n- Two fingers click = toggle android keyboard\n- Two fingers press and move = resize the window (release one finger to move the window)\n- Three fingers click = Show popup menu\n\n**Gesture Mode 2**:\n- One finger click = Mouse left click at its own position\n- One finger move = Mouse move with finger's movement\n- Two fingers click = Mouse right click\n- Two fingers press and move = Mouse wheel scroll\n- Tow fingers long press and move = Mouse left button press (drag)\n- Three fingers press and move 1 or 2 of them = Resize the window (release 1 or 2 of them to move the window)\n- Three fingers click = Show popup menu");
        enArray.put(cmCtrl_s1_msMoveViewport, "Restrict mouse movement$Position-update interval$Limited distance");
        enArray.put(cmCtrl_s2_layoutMode, "Layout Mode");
        enArray.put(cmCtrl_s2_layoutModeTip, "Left&Right Sidebar: Classic layout. Key buttons are put in the sidebar and the main frame won't be overlaid.\n\nFree Position: Buttons can be placed anywhere (you need to enter a container, three-fingers click to edit a button's position). The joystick-style button is available.\n\nCustom data is stored on device at Android/data/PACKAGE_NAME/files/custom_control2(3).txt. They can be backed up manually, but may not be compatible with future updates (if there is any).");
        enArray.put(cmCtrl_s2_LSideTitle, "Left Sidebar");
        enArray.put(cmCtrl_s2_RSideTitle, "Right Sidebar");
        enArray.put(cmCtrl_s2_FreePosTitle, "Select buttons");
        enArray.put(cmCtrl_s2_popEdit, "Modify");
        enArray.put(cmCtrl_s2_popDel, "Delete");
        enArray.put(cmCtrl_s2_ColEditTip, "Long press buttons to rearrange them. Click buttons to edit them.");
        enArray.put(cmCtrl_s2_modeSide, "Left&Right Sidebar");
        enArray.put(cmCtrl_s2_modeFree, "Free Position");
        enArray.put(cmCtrl_s3_sampleBtn, "Sample");
        enArray.put(cmCtrl_s3_btnColor, "Color & Alpha of buttons");
        enArray.put(cmCtrl_s3_btnColorTip, "Color is a RGB hex value, e.g. 2121FA. \nAlpha varies from 0 to 255. If alpha is 0, button's background is transparent, button's text keeps 1/3 of the visibility.");
        enArray.put(cmCtrl_s3_btnSize, "Size of buttons");
        enArray.put(cmCtrl_s3_btnSizeTip, "Set the width and height of a button, varying from 10 to 200dp. Value lower than 10 will set the width and height adaptively.");
        enArray.put(cmCtrl_s3_sideColor, "Color of Sidebars");
        enArray.put(cmCtrl_s3_sideColorTip, "When the option \"Keys - Layout Mode \" is Left&Right Sidebar, this color will be used as the background color of sidebars. Color is a RGB hex value, e.g. 000000, and alpha is always 255.");
        enArray.put(cmCtrl_actionEdit, "Edit Controls");
        enArray.put(cmCtrl_actionCtrlShow, "Show Controls");
        enArray.put(cmCtrl_actionCtrlHide, "Hide Controls");
        enArray.put(cmCtrl_mouseLeftName, "Mouse Left");
        enArray.put(cmCtrl_mouseRightName, "Mouse Right");
        enArray.put(cmCtrl_mouseMiddleName, "Mouse Middle");
        enArray.put(cmCtrl_allKeysJoyTitle, "Joystick");
        enArray.put(cmCtrl_allKeysMouseTitle, "Mouse");
        enArray.put(cmCtrl_editMenu1Dialog, "Show Editing Dialog");
        enArray.put(cmCtrl_editMenu2Exit, "Exit");
        enArray.put(cmCtrl_BtnEditReName, "Rename");
        enArray.put(cmCtrl_JoyEdit4Ways, "Use 4 directions instead of 8 directions");
        enArray.put(cmCtrl_JoyEdit4WaysTip, "If checked, only one key will be pressed at a time. If unchecked, two buttons will be triggered when moving to the diagonal direction, that is, there are 8 directions.\nAllowing the diagonal direction will cause the angle of the determination direction to change from 45° to 22.5°, so it is recommended to enable this option if the game does not support diagonal.");
        enArray.put(cmCtrl_JoyEditKeys, "Button keys");
        enArray.put(cmCtrl_JoyEditKeyCstm, "Custom");
        enArray.put(cmCtrl_gs_lClick, "Left Click$One finger click$One finger click");
        enArray.put(cmCtrl_gs_rClick, "Right Click$One finger long press & release$Two fingers click");
        enArray.put(cmCtrl_gs_vScroll, "Scroll (Wheel)$One finger move$Two fingers move");
        enArray.put(cmCtrl_gs_dndLeft, "Left Press (Drag)$One finger long press and move$Two fingers long press and move");
        enArray.put(cmCtrl_gs_zoom, "Zoom$Two fingers press and move$Three fingers press and move 1 or 2 of them");
        enArray.put(cmCtrl_gs_menu, "Popup Menu$Three fingers click$Three fingers click");
        enArray.put(cmCtrl_gs1Abs_title, "Gesture Mode 1$Original Default Mode");
        enArray.put(cmCtrl_gs2Rel_title, "Gesture Mode 2$TouchPad Mode");
        enArray.put(cmCtrl_gs_moveCursor, "Cursor Move$ $One finger move");
        enArray.put(cmCtrl_gs_keyboard, "Toggle Keyboard$Two fingers click$ ");
        enArray.put(cmCtrl_actionCtrlTip, "Gesture Tips");
        enArray.put(cmCtrl_actionSubCtrl, "Custom Control");
        enArray.put(cmCtrl_s1_msSpd, "Mouse sensitivity");
        enArray.put(cmCtrl_reset, "Reset");
        enArray.put(cmCtrl_s1_msSpdTip, "Adjust the pointer speed, effective only in Gesture Mode 2");
        enArray.put(cmCtrl_s1_msOffScr, "Camera Speed(When mouse hits the screen boundary)");
        enArray.put(cmCtrl_s1_msOffScrTip, "The value is 0~70. Default is 0, that is, the mouse is not allowed to move outside the screen. \nIn some games, if the camera cannot continue to move when the mouse moves to the boundary, you can try to increase the value to allow camera continue to move. \n\nNote that if for function to take effect, you need to full screen the game, and adjust the resolution in the container's properties to be exactly the same as the full screen resolution of the game, that is, there should be no extra black frame at the bottom right of the screen. There is no need to set the registry MouseWarpOverride=force.");
        enArray.put(cmCtrl_s2_sideTitleTip, "Click \"+\" to add one column of buttons. Long press to rearrange them.");
        enArray.put(cmCtrl_s2_selectBtn, "Select keys……");
        enArray.put(cmCtrl_BtnEditTrigger, "Keep pressed after releasing the finger");
        enArray.put(cmCtrl_BtnEditComb, "Combination");
        enArray.put(cmCtrl_tabOther, "Others");
        enArray.put(cmCtrl_s4_tips, "    <ul>\n" +
                "        <li>&ensp; To enable this control, set Control Mode \"default\" in container properties</li>\n" +
                "        <li>&ensp; This dialog can also show up by three-finger click after launching the container.</li>\n" +
                "        <li>&ensp; This function provides only basic settings. For better customization Input Bridge is preferred.</li>\n" +
                "    </ul>");
        enArray.put(cmCtrl_s4_export, "Export");
        enArray.put(cmCtrl_s4_import, "Import");
        enArray.put(cmCtrl_s4_trsportTitle, "Data Transfer");
        enArray.put(cmCtrl_s4_trsportTip, "Users can import or export data of Custom Controls, but it is not guaranteed that it will be compatible in the future.\nClick Export to copy the data to the clipboard in text format. Click Import to try to read text from the clipboard into data.");
        enArray.put(cmCtrl_s4_exportResult, "Copied to clipboard");
        enArray.put(cmCtrl_s4_importResult, "Import succeeded$Import failed");
        enArray.put(cmCtrl_s3_txtSize, "Text Size");
        enArray.put(cmCtrl_s3_btnRoundShape, "Round Button");
        enArray.put(cmCtrl_actionRotate, "Rotate Screen");
        enArray.put(abtFab_title, "About");
        enArray.put(abtFab_info, "<ul>\n" +
                "  <li>&ensp; Exagear Android (Eltechs) project has been closed. This menu is a third-party patch designed to add some handy features.</li>\n" +
                "  <li>&ensp; You can add this and more features into Exagear by EDPatch: https://github.com/ewt45/EDPatch/releases</li>\n" +
                "</ul>");
        enArray.put(firstLaunch_snack, "Extra features can be found in the bottom left button.");
        enArray.put(shortcut_menuItem_addAppSc, "Add as app shortcut");
        enArray.put(shortcut_DontShowUp, "Don't show up again");
        enArray.put(shortcut_TipAfterAdd, "Creating an app shortcut for this .desktop file. Later you can launch it by long pressing APP icon and clicking the app shortcut. A maximum of four shortcuts can be added. Before launching from shortcut, ensure that the app is not running at background. \n\nAfter deleting the exe shortcut ( .desktop file), the app shortcut will be automatically deleted the next time launching the app . \n\nThis feature requires Android 7 and above.");
        enArray.put(mw_newContProgress, "Creating container...");
        enArray.put(mw_fragTitle, "Add/Remove Wines");
        enArray.put(mw_tabTitles, "Local$Downloadable$Tips");
        enArray.put(mw_refreshBtn, "↻ Refresh");
        enArray.put(mw_dlSourceBtn, "Sources");
        enArray.put(mw_dataSizeMB, " MB");
        enArray.put(mw_dialog_download, " download completed.$ download failed.$Local file exists, download skipped.$Other download process is running, operation cancelled.");
        enArray.put(mw_dialog_extract, "Extracting...$Extraction completed.$Extraction failed.");
        enArray.put(mw_localMenuItem, "Install$Checksum$Uninstall$Delete archive");
        enArray.put(mw_dialog_checksum, "Archive contains no errors.$Unable to check without sha256sums.txt.$Archive is corrupted. Please delete and download it again.");
        enArray.put(mw_localState, "active$inactive");
        enArray.put(mw_tips, "<ul>\n" +
                "<li>&ensp; Local:<br/>Edit the downloaded or bundled Wines. After Clicking Install (extract) option, it will be displayed as 'active', which can be selected when creating a new container. Use Uninstall (delete extracted folder) option to reduce local storage. Wines are stored at z:/opt/WineCollection.</li>\n" +
                "<li>&ensp; Downloadable:<br/>Download all kinds of Wines from the Internet. Available sources are WineHQ(Official build, only ubuntu18-builds are listed) and Kron4ek(shrinked size, staging versions are not included). Downloaded Wines appear at 'Local' page. </li>\n" +
                "</ul>\n");
        enArray.put(mw_contNoWineTips, "No active Wine detected. This container probably can not launch. Please delete it, click the download button to install Wines and try again.");
        enArray.put(render_title, "Renderer");
        enArray.put(fab_hide, "Hide");
        enArray.put(pa_title, "PulseAudio (XSDL)");
        enArray.put(pa_explain, "PulseAudio is used to play audio, reducing sound problems. This function uses PulseAudio server extracted from Xserver XSDL. It requires 64-bit support on your device.");
        enArray.put(pa_checkRun, "Enable PulseAudio service$Start PulseAudio server when launching a container");
        enArray.put(pa_checkLog, "Output logs$Save runtime logs to Android/data/%s/files/logs/palog.txt");
        enArray.put(pa_btnParam, "Edit launching params$Wrong params will result in errors, please modify with caution");
        enArray.put(pa_troubleShooting, "Troubleshooting ⓘ$" +
                "Check if PulseAudio service is working properly: After Launching the container, click start menu - Run - type winecfg to open it - Audio: \"Selected drivers\" should be winepulse.drv, click \"Test Sound\" button to hear the test sound." +
                "\n\nIf the result is incorrect, please check the following conditions:" +
                "$1. Pulseaudio elf file is executed correctly: Check \"Output logs\" option, launch the container and look into the log for errors. If started with default parameters, there should be \"Daemon startup successful.\"" +
                "\n2. Pulse tcp module is loaded correctly: If started with default parameters, . /pulseaudio.conf will automatically load the tcp module without additional settings: load-module module-native-protocol-tcp auth-anonymous=true port=4713." +
                "\n3. Export environment variables: PULSE_SERVER=tcp:127.0.0.1:4713. This function will automatically add environment variables when starting the container, no additional settings are needed. Environment variables can be confirmed with Exaterm or Putty." +
                "\n4. Wine sound driver in registry: Some caches provides a convenient registry entry for switching into pulse sound at the start menu. Or you can change it manually: Click start menu - Run - Type regedit to open it and find the following path: [HKEY_CURENT_USER\\Software\\Wine\\Drivers]. If \"Audio\" appears at the right side, it's value should contain the string \"pulse\"." +
                "\n \n ");
        enArray.put(xegw_title, "Xegw (termux:x11)");
        enArray.put(xegw_info, "The x11 server is used for displaying graphics. Since the original x11 server implemented in exagear is rather simple and can't support dxvk, etc., the x11 server implemented in termux:x11 is ported to exagear so as to achieving better rendering. Thanks for the help of Twaik, developer of termux:x11.");
        enArray.put(xegw_legacyDraw_tip, "If black screen with an arrow cursor are displayed after starting the container, checking this option will solve the problem, but rendering may be slower.");
        enArray.put(xegw_notification, "Xegw xserver. Keep the notification to prevent disorder after switching app to background.");
        enArray.put(xegw_btyOpt, "Disable battery optimization $After container starts, it shows a status bar notification for Xegw, so that Xegw process will not be killed when switching to the background and only a black screen is left when switching back.\n\nIf the problem is not solved, try to turn off the battery optimization of this app in Settings (through this button). If problem is still not solved, try to enable autostart of the app in Settings (this interface varies from ROM to ROM, so you need to find it yourself).");
        enArray.put(DriveD2_title, "Change Locations of Drives");
        enArray.put(DriveD2_info, "Set an android directory as this drive.");
        enArray.put(DriveD2_newDrive, "New Drive");
        enArray.put(DriveD2_delDrive, "Delete this Drive");
        enArray.put(DriveD2_devType, "Phone's Storage$Other Storage Device$Other Storage Device (None)");
        enArray.put(DriveD2_parType, "Root Dir$App-specific Files Dir");
        enArray.put(DriveD2_errors, "Please allow the app's storage permission. $ The directory doesn't exist. $ The path denotes a file rather than a directory. $ App has no permission of reading contents of this directory. $ App has no permission of writing contents into this directory. $ Error: ");


        enArray.put(conSet_otherArgv_title, "Extra Launching Arguments");
        enArray.put(conSet_otherArgv_prefValue, "Extra cmd/env");
        enArray.put(othArg_info, "All available params are listed here. Selected items will be applied to the current container.$Learn more...");
        enArray.put(othArg_info_more, "These params will be inserted into the original command when launching the container. Full command can be found at /sdcard/x86-stderr.txt.\n\nIf the parameter is already included in the original command, the insertion may fail. The actual result can be viewed by clicking \"Preview\" button to enter the test command or viewed in the txt after starting the container. \n\nAll available params are stored at z:/opt/edpatch/contArgs.txt, and the parameters enabled for each container are stored at z:/home/xdroid_n/contArgs.txt. However, it is not recommended to modify the txt directly due to strict requirements on the storage format.");
        enArray.put(othArg_taskset_useCustom, "Set_CPU_cores");
        enArray.put(othArg_ib_autorun, "Autorun_ib.exe");
        enArray.put(othArg_serviceExeDisable, "Kill_services.exe");
        enArray.put(othArg_preview, "Preview");
        enArray.put(global_add, "Add");
        enArray.put(global_edit, "Edit");
        enArray.put(global_del, "Delete");
        enArray.put(othArg_edit_delConfirm, "Are you sure to delete this param?\nThis action will take effect on all containers. If you want to disable it only in the current container, please unselect it from the checkbox.\n\n");
        enArray.put(othArg_edit_attrTitles, "Alias$Arg$Type$Enable by default for new containers");
        enArray.put(othArg_edit_typeChoices, "Environment Variable$Cmd - in the front of original Cmd$Cmd - before original Cmd executed$Cmd - after original Cmd executed");
        enArray.put(othArg_edit_typeInfo, "Argument type is either ENV or CMD.\n\nEnvironment Variable: Used for the command to be executed. If the original cmd contains a env with the same name, this params will be overridden.\n\nCmd: If it's before/after the original cmd, this cmd and the original cmd are connected with one '&'.");
        enArray.put(othArg_preview_titles, "Original CMD$CMD with params");
        enArray.put(fabVO_checkTitles,"Use vtest protocol 2 (new Mesa libGL needed)$Use GL ES 3.x instead of OpenGL$Use multi-thread egl access$DXTn (S3TC) decompress (needed for some games)$Auto restart services");
        enArray.put(fabVO_radioTitles,"Overlay position:$Top-left$Centered$invisible *$* Used with VTEST_WIN=1 to draw in X11 window instead of overlay");
        enArray.put(fabVO_btnTitles,"Clean Services$Start Services$Allow Drawing Overlay");
        enArray.put(fabVO_startResults,"Services start successfully. Now you can try to start the container and run games.$Services start failed.");



        enArray.put(fsm_event_complete,"complete");
        enArray.put(fsm_event_release,"a finger released");
        enArray.put(fsm_event_new_touch,"new finger pressed");
        enArray.put(fsm_event_near,"near");
        enArray.put(fsm_event_far,"far");
        enArray.put(fsm_event_noMove,"finger not move");
        enArray.put(fsm_event_slowMove,"finger slow move");
        enArray.put(fsm_event_fastMove,"finger fast move");
        enArray.put(fsm_event_noMoveThenRelease,"a finger not moved then released");
        enArray.put(fsm_event_moveThenRelease,"a finger moved then released");
        enArray.put(fsm_event_finger_num_same,"finger numbers not change");
        enArray.put(fsm_state_testSpd,"Measure finger speed with countdown");
        enArray.put(fsm_state_init,"Init state");
        enArray.put(fsm_state_fallback,"Default state (return to init state)");
        enArray.put(fsm_state_1FMouseMove,"Finger move to pointer move");
        enArray.put(fsm_state_action_click,"Keyboard/Mouse button");
        enArray.put(fsm_state_action_msMove,"Move pointer position");
        enArray.put(fsm_state_msScroll,"Finger move to mouse scroll");
        enArray.put(fsm_state_distFingerMouse,"Check distance between finger and pointer");
        enArray.put(fsm_state_fingerNum,"Detect finger number change");
        enArray.put(fsm_state_2FZoom,"Two fingers zoom");
        enArray.put(fsm_state_action_option,"Run options");
        enArray.put(ctr2_editTab_titles,"Keys$Gestures$Profiles$Others");
        enArray.put(global_type,"Type");
        enArray.put(global_color,"Color");
        enArray.put(global_alias,"Alias");
        enArray.put(global_size,"Size");
        enArray.put(global_keycode,"Keycodes");
        enArray.put(global_shape,"Shape");
        enArray.put(ctr2_prop_direction_title,"Direction");
        enArray.put(ctr2_prop_colorStyle_names,"stroke$fill");
        enArray.put(ctr2_prop_type_names,"Button$Stick$D-pad");
        enArray.put(ctr2_prop_shape_names,"Rectangle$Circle");
        enArray.put(ctr2_prop_direction_names,"4 directions$8 directions");
        enArray.put(ctr2_ges_state,"State");
        enArray.put(ctr2_ges_action,"Action");
        enArray.put(ctr2_ges_transition,"Transition");
        enArray.put(ctr2_ges_preState,"pre-state");
        enArray.put(ctr2_ges_postState,"post-state");
        enArray.put(ctr2_ges_subTitles,"States$Actions$Debug");
        enArray.put(ctr2_ges_debug_realtimeInfo,"realtime transition info");
        enArray.put(ctr2_ges_debug_graph,"show all transitions in a graph");
        enArray.put(global_done,"Done");
        enArray.put(ctr2_ges_fillInAlias,"Please fill in the Alias.");
        enArray.put(ctr2_ges_state_edit_tabTitles,"Transitions$Properties");
        enArray.put(ctr2_ges_stateDelWarns,"Are you sure? All transitions that contains this state will be deleted too." +
                "$The following transitions will be deleted:");
        enArray.put(ctr2_ges_actionDelWarns,"Are you sure?" +
                "$The following transitions will be unable to use this action:");
        enArray.put(global_instructions,"Instructions");
        enArray.put(ctr2_ges_transInfo,
                "Basic concept of transition: [current(pre) state] running -> match a condition -> send corresponding [event] -> perform corresponding [actions] -> stop pre state, run [post state].\n" +
                        "Set different post states for different events, so that it works the way you want." +
                "\n\nEvents: all possible events that this pre state is able to send." +
                "\n\nPost States: Users can customize the states. Only states that have been previously created at Gesture tab can be selected." +
                "\n\nActions: Users can customize the actions. They also need to be created first. Note that multiple actions run in the order you set.");
        enArray.put(ctr2_ges_state_edit_tran_tableHeaders,"Events$Post States$Actions");
        enArray.put(ctr2_stateProp_PrsRlsCheck,"Press$Release");
        enArray.put(ctr2_stateProp_fingerIndex,"Which finger");
        enArray.put(ctr2_stateProp_fingerXYType,"Finger location type");
        enArray.put(ctr2_stateProp_option,"Option");
        enArray.put(ctr2_stateProp_noMoveThreshold,"Finger movement less than this distance is counted as no movement");
        enArray.put(ctr2_stateProp_zoom2Finger,"Which finger (one)$Which finger (the other)");
        enArray.put(ctr2_stateProp_farDistThres,"Distance above this is counted as far distance, otherwise near");
        enArray.put(ctr2_stateProp_fastMoveThres,"Finger movement greater than this distance is a fast move, otherwise is a slow move");
        enArray.put(ctr2_stateProp_countDown,"Countdown (millisecond)");
        enArray.put(global_empty,"Empty");
        enArray.put(ctr2_FSMR_value_fingerXYType, "Latest location of any finger" +
                        "$Location when this finger touch the screen" +
                        "$Current location of this finger");
        enArray.put(ctr2_FSMR_value_fingerIndex,"All fingers$1$2$3$4$5$6$7$8$9$10");
        enArray.put(ctr2_FSMR_value_pointerMoveType,"Normal$Game camera moving");
        enArray.put(ctr2_customControls2,"Custom Controls 2");
        enArray.put(ctr2_option_showHideTouchArea,"Show/Hide buttons");
        enArray.put(global_quit,"Quit");
        enArray.put(ctr2_option_showAll,"Show all options");
        enArray.put(ctr2_option_softInput,"Show/Hide Android input method");
        enArray.put(ctr2_profile_importMsgs,"Import succeeded: $Import failed: ");
        enArray.put(ctr2_profile_exportMsgs,"Export succeeded: $Export failed: ");
        enArray.put(ctr2_profile_addOptions,"Empty profile$Copy existing profile$Import from local file");
        enArray.put(ctr2_profile_oneOptions,"Export as file$Rename$Delete");
        enArray.put(ctr2_profile_editName,"Profile name");
        enArray.put(ctr2_profile_delConfirm,"Delete this profile?");
        enArray.put(global_save,"Save");
        enArray.put(ctr2_other_saveExit,"Save and exit");
        enArray.put(ctr2_other_mouseSpeed,"Mouse movement speed multiplier");
        enArray.put(ctr2_other_showTouchArea,"Show buttons on screen \n(take effect after exit edit mode)");
        /*





         */

        SparseArray<String> ruArray = new SparseArray<>();
        ruArray.put(Dialog_PosBtn, "подтвердить");
        ruArray.put(Dialog_NegBtn, "отмена");
        ruArray.put(DriveD_Title, "Изменить расположение диска D");
        ruArray.put(DriveD_ParDirKey_1, "External Storage(Папки каталога Android)");
        ruArray.put(DriveD_ParDirKey_2, "External Storage(Папка каталога Android/data)");
        ruArray.put(DriveD_ParDirKey_3, "SD Card(Папка каталога SD/Android/data)");
        ruArray.put(DriveD_Explain, "Выберите каталог Android для расположения диска D");
        ruArray.put(DriveD_DescTitle, "Рекомендация");
        ruArray.put(DriveD_DescCont, "На Android11+ скорость чтения/записи в каталоге Android может быть очень низкой. Чтобы решить эту проблему, установите диск D в каталог Android/data или скопируйте игру на диск C или Z.");
        ruArray.put(DriveD_SncBrBtn, "перезапустить");
        ruArray.put(DriveD_SncBrTxt, "Расположение диска D изменено");
        ruArray.put(DriveD_ToastExitFail, "Настройки не изменились");
        ruArray.put(DriveD_EditDstTitle, "Имя папки");
        ruArray.put(DriveD_EditParTitle, "Местоположение каталога");
        ruArray.put(DriveD_getPathFail, "Невозможно найти путь");
        ruArray.put(DriveD_check_1, "Корневой каталог существует");
        ruArray.put(DriveD_check_2, "Конечная папка существует");
        ruArray.put(DriveD_check_3, "Выбранный файл является папкой");
        ruArray.put(DriveD_check_4, "Приложению разрешено читать каталог");
        ruArray.put(DriveD_check_5, "Приложению разрешено записывать в каталог");
        ruArray.put(DriveD_NoStrgPmsn, "У приложения нет разрешения на доступ к Хранилищу");
        ruArray.put(SelObb_info, "Выберите файл obb вручную");
        ruArray.put(SelObb_btn, "выбрать вручную");
        ruArray.put(SelObb_selResult, "Выбранный файл не является файлом obb.$Obb найден. Распаковка...");
        ruArray.put(CstRsl_swtTxt, "Использовать кастомное разрешение");
        ruArray.put(CstRsl_editW, "ширина");
        ruArray.put(CstRsl_editH, "высота");
        ruArray.put(cmCtrl_title, "Кастомное Управление");
        ruArray.put(cmCtrl_lgPressHint, "Удерживайте долгий тап для получения описания функций.");
        ruArray.put(cmCtrl_tabMouse, "Мышь");
        ruArray.put(cmCtrl_tabKeys, "Ключи");
        ruArray.put(cmCtrl_tabStyle, "Стиль");
        ruArray.put(cmCtrl_s1_showCursor, "Отображать курсор мыши");
        ruArray.put(cmCtrl_s1_showCursorTip, "Отображать или скрывать курсор мыши в контейнерах.\nЕсли в игре два курсора, включение этой опции может помочь скрыть один.");
        ruArray.put(cmCtrl_s1_relMove, "Отображение курсора как тачпада (Режим жестов 2)");
        ruArray.put(cmCtrl_s1_relMoveTip, "Если флаг установлен, будет использоваться Режим жестов 2.\n\n**Режим жестов 1**:\n- Тап одним пальцем = Клик левой кнопкой мыши на позиции пальца\n- Тап и перемещение одним пальцем = Прокрутка мышью\n- Долгий тап одним пальцем и перемещение = Нажатие левой кнопки мыши (Перетаскивание)\n- Тап одним пальцем с удержанием = Клик правой кнопкой мыши\n- Тап двумя пальцами = Вызов клавиатуры Android\n- Тап двумя пальцами с удержанием и движение в разные стороны = Изменить размер окна (отпустите один палец, чтобы переместить окно)\n- Тап тремя пальцами = Показать меню\n\n**Режим жестов 2**:\n- Тап одним пальцем = Клик левой кнопкой мыши в позиции положения курсора\n- Движение одним пальцем = Движение курсора мыши\n- Тап двумя пальцами = Клик правой кнопкой мыши\n- Тап двумя пальцами и движение = Прокрутка колесика мыши\n- Тап двумя пальцами с удержанием и перемещение = Нажатие левой кнопки мыши (перетаскивание)\n- Тап тремя пальцами и перемещение 1 или 2 из них = Изменение размера окна (отпустите 1 или 2 пальца, чтобы переместить окно)\n- Тап тремя пальцами = Показать меню");
        ruArray.put(cmCtrl_s1_msMoveViewport, "Ограничить движение мыши$Интервал обновления позиции$Ограничить расстояние");
        ruArray.put(cmCtrl_s2_layoutMode, "Режим макета");
        ruArray.put(cmCtrl_s2_layoutModeTip, "Левая и правая боковые панели это классический макет. Кнопки ключей размещаются на боковых панелях, и основной экран не перекрывается.\n\nСвободная позиция: кнопки можно размещать где угодно (тап тремя пальцами для вызова меню, выбрать редактировать и изменить положение кнопки).  Доступна кнопка в виде джойстика.\n\nПользовательские данные хранятся на устройстве в Android/data/ИМЯ_ПАКЕТА/files/custom_control2(3).txt. Их можно создать как резервную копию вручную. Они могут быть несовместимы с будущими обновлениями(если они будут).");
        ruArray.put(cmCtrl_s2_LSideTitle, "Левая боковая панель");
        ruArray.put(cmCtrl_s2_RSideTitle, "Правая боковая панель");
        ruArray.put(cmCtrl_s2_FreePosTitle, "Выбор кнопок");
        ruArray.put(cmCtrl_s2_popEdit, "Изменить");
        ruArray.put(cmCtrl_s2_popDel, "Удалить");
        ruArray.put(cmCtrl_s2_ColEditTip, "Нажмите и удерживайте на кнопки, чтобы переставить их. Однократное нажатие, чтобы редактировать.");
        ruArray.put(cmCtrl_s2_modeSide, "Левая и Правая боковая панель");
        ruArray.put(cmCtrl_s2_modeFree, "Свободная позиция");
        ruArray.put(cmCtrl_s3_sampleBtn, "Текст");
        ruArray.put(cmCtrl_s3_btnColor, "Цвет и Прозрачность кнопок");
        ruArray.put(cmCtrl_s3_btnColorTip, "Цвет - это шестнадцатеричное значение RGB, например 2121FA. \nПрозрачность варьируется от 0 до 255. Если Прозрачность равна 0 то фон кнопки прозрачен, а текст кнопки сохраняет 1/3 видимости.");
        ruArray.put(cmCtrl_s3_btnSize, "Размер кнопок");
        ruArray.put(cmCtrl_s3_btnSizeTip, "Установите ширину и высоту кнопки в диапазоне от 10 до 200 dp. Значение меньше 10 будет адаптивно по ширине и высоте.");
        ruArray.put(cmCtrl_s3_sideColor, "Цвет боковых панелей");
        ruArray.put(cmCtrl_s3_sideColorTip, "Когда для параметра \"Кнопки - Режим макета\" выбрано значение \"Левая и Правая боковая панель\", этот цвет будет использоваться в качестве цвета фона боковых панелей. Цвет - это шестнадцатеричное значение RGB, например 000000, а Прозрачность всегда 255.");
        ruArray.put(cmCtrl_actionEdit, "Редактировать Управление");
        ruArray.put(cmCtrl_actionCtrlShow, "Показать Управление");
        ruArray.put(cmCtrl_actionCtrlHide, "Скрыть Управление");
        ruArray.put(cmCtrl_mouseLeftName, "Левая кнопка мыши");
        ruArray.put(cmCtrl_mouseRightName, "Правая кнопка мыши");
        ruArray.put(cmCtrl_mouseMiddleName, "Средняя кнопка мыши");
        ruArray.put(cmCtrl_allKeysJoyTitle, "Джойстик");
        ruArray.put(cmCtrl_allKeysMouseTitle, "Мышь");
        ruArray.put(cmCtrl_editMenu1Dialog, "Показать диалог редактирования");
        ruArray.put(cmCtrl_editMenu2Exit, "Выход");
        ruArray.put(cmCtrl_BtnEditReName, "Переименовать");
        ruArray.put(cmCtrl_JoyEdit4Ways, "Использовать 4 направления вместо 8-ми направлений");
        ruArray.put(cmCtrl_JoyEdit4WaysTip, "Если флаг установлен, за один раз будет нажата только одна клавиша. Если флаг не установлен, при перемещении в диагональном направлении будут срабатывать две кнопки, т. е. существует 8 направлений.\nРазрешение диагонального направления приведет к изменению угла направления определения с 45° на 22,5°, поэтому рекомендуется включить эту опцию, если игра не поддерживает диагональ.");
        ruArray.put(cmCtrl_JoyEditKeys, "Ключи кнопок");
        ruArray.put(cmCtrl_JoyEditKeyCstm, "Кастомные");
        ruArray.put(cmCtrl_gs_lClick, "Левый клик мыши$Тап одним пальцем$Тап одним пальцем");
        ruArray.put(cmCtrl_gs_rClick, "Правый клик мыши$Долгий тап и отпустить одним пальцем$Тап двумя пальцами");
        ruArray.put(cmCtrl_gs_vScroll, "Колёсико (прокрутка мыши)$Движение одним пальцем$Движение двумя пальцами");
        ruArray.put(cmCtrl_gs_dndLeft, "Левый клик мыши (Перетаскивание)$Долгий тап одним пальцем и перемещение$Долгий тап двумя пальцами и перемещение");
        ruArray.put(cmCtrl_gs_zoom, "Приблизить$Тап двумя пальцами и перемещение$Тап тремя пальцами и перемещение 1 или 2 из них");
        ruArray.put(cmCtrl_gs_menu, "Меню$Тап тремя пальцами$Тап тремя пальцами");
        ruArray.put(cmCtrl_gs1Abs_title, "Режим жестов 1$Оригинальный режим Default");
        ruArray.put(cmCtrl_gs2Rel_title, "Режим жестов 2$Режим TouchPad");
        ruArray.put(cmCtrl_gs_moveCursor, "Переместить курсор$ $Движение одним пальцем");
        ruArray.put(cmCtrl_gs_keyboard, "Клавиатура$Тап двумя пальцами$ ");
        ruArray.put(cmCtrl_actionCtrlTip, "Рекомендации жестов");
        ruArray.put(cmCtrl_actionSubCtrl, "Кастомное Управление");
        ruArray.put(cmCtrl_s1_msSpd, "Чувствительность мыши");
        ruArray.put(cmCtrl_reset, "Сброс");
        ruArray.put(cmCtrl_s1_msSpdTip, "Отрегулируйте скорость указателя мыши, работает только в Режиме жестов 2");
        ruArray.put(cmCtrl_s1_msOffScr, "Скорость Камеры(Когда мышь достигает границы экрана)");
        ruArray.put(cmCtrl_s1_msOffScrTip, "Этот параметр имеет значения в диапазоне 0~70. По умолчанию 0, т. е. мышь не может перемещаться за пределы экрана. \nВ некоторых играх, если камера не может продолжать двигаться, когда мышь приближается к границе, вы можете попытаться увеличить это значение, чтобы камера продолжала двигаться. \n\nОбратите внимание, для того, чтобы функция заработала, вам необходимо развернуть игру на весь экран, а разрешение в свойствах контейнера настроить так, чтобы оно было точно таким же, как полноэкранное разрешение игры, то есть не должно быть лишних черных рамок в правом и нижнем углу экрана. При этом устанавливать в реестре \"MouseWarpOverride=force\" не нужно");
        ruArray.put(cmCtrl_s2_sideTitleTip, "Нажмите \"+\" чтобы добавить один столбец кнопок. Нажмите и удерживайте, чтобы изменить их порядок.");
        ruArray.put(cmCtrl_s2_selectBtn, "Выбрать ключи……");
        ruArray.put(cmCtrl_BtnEditTrigger, "Продолжать нажатие после того как отпустили палец(Удержание)");
        ruArray.put(cmCtrl_BtnEditComb, "Комбинация");
        ruArray.put(cmCtrl_tabOther, "Прочее");
        ruArray.put(cmCtrl_s4_tips, "    <ul>\n" +
                "        <li>&ensp; Чтобы включить Кастомное Управление - выберите режим управления \"Default\" в настройках контейнера.</li>\n" +
                "        <li>&ensp; Это окно также можно открыть, тапнув тремя пальцами в запущенном контейнере.</li>\n" +
                "        <li>&ensp; Эта функция обеспечивает только основные настройки. Для лучшей и более точной настройки рекомендуется использовать Input Bridge.</li>\n" +
                "    </ul>");
        ruArray.put(cmCtrl_s4_export, "Экспорт");
        ruArray.put(cmCtrl_s4_import, "Импорт");
        ruArray.put(cmCtrl_s4_trsportTitle, "Обмен данными");
        ruArray.put(cmCtrl_s4_trsportTip, "Пользователи могут импортировать или экспортировать конфиги элементов управления, но их совместимость в будущем не гарантируется.\nНажмите \"Экспорт\", чтобы скопировать данные в буфер обмена в текстовом формате. Нажмите \"Импорт\", чтобы попытаться загрузить текст из буфера обмена в данные.");
        ruArray.put(cmCtrl_s4_exportResult, "Скопировано в буфер обмена");
        ruArray.put(cmCtrl_s4_importResult, "Импорт выполнен успешно$Не удалось импортировать");
        ruArray.put(cmCtrl_s3_txtSize, "Размер текста");
        ruArray.put(cmCtrl_s3_btnRoundShape, "Круглая кнопка");
        ruArray.put(cmCtrl_actionRotate, "Поворот экрана");
        ruArray.put(abtFab_title, "О приложении");
        ruArray.put(abtFab_info, "<ul>\n" +
                "  <li>&ensp; Exagear Android (Eltechs) проект закрыт. Это меню представляет собой сторонний патч, предназначенный для добавления некоторых удобных функций.</li>\n" +
                "  <li>&ensp; Вы можете добавить эту и другие функции в Exagear с помощью EDPatch: https://github.com/ewt45/EDPatch/releases</li>\n" +
                "</ul>");
        ruArray.put(firstLaunch_snack, "Для изменения расположения диска D нажать ⚙️");
        ruArray.put(shortcut_menuItem_addAppSc, "📌 Добавить как внешний ярлык");
        ruArray.put(shortcut_DontShowUp, "Не показывать снова");
        ruArray.put(shortcut_TipAfterAdd, "Создание ярлыка программы для файла .desktop. После вы можете запустить его, нажав и удерживая значок приложения и кликнуть на ярлык программы. Можно добавить максимум четыре ярлыка. Перед запуском с ярлыка убедитесь, что приложение не работает в фоновом режиме. \n\nПосле удаления ярлыка exe (файл .desktop) внешний ярлык будет автоматически удален при следующем запуске приложения. \n\nДля этой функции требуется Android 7 и выше.");
        ruArray.put(mw_newContProgress, "Создание контейнера...");
        ruArray.put(mw_fragTitle, "Добавить/Удалить Wine");
        ruArray.put(mw_tabTitles, "Установка$Загрузка$Советы");
        ruArray.put(mw_refreshBtn, "↻ Обновить");
        ruArray.put(mw_dlSourceBtn, "Источники");
        ruArray.put(mw_dataSizeMB, " MB");
        ruArray.put(mw_dialog_download, "Загрузка завершена.$Загрузка не удалась.$Локальный файл существует, загрузка пропущена.$Выполняется другой процесс загрузки, операция отменена.");
        ruArray.put(mw_dialog_extract, "Извлечение...$Извлечение завершено.$Извлечение не удалось.");
        ruArray.put(mw_localMenuItem, "Установить$Контрольная сумма$Удалить$Удалить архив");
        ruArray.put(mw_dialog_checksum, "Архив не содержит ошибок.$Невозможно проверить без sha256sums.txt.$Архив поврежден. Пожалуйста, удалите и загрузите его снова.");
        ruArray.put(mw_localState, "Активный$Неактивный");
        ruArray.put(mw_tips, "<ul>\n" +
                "<li>&ensp; Установка wine:<br/>Вы можете редактировать загруженные или предустановленные версии wine. После выбора опции Установить (Извлечь) выбранная версия wine будет отображаться как 'Активная', теперь её можно выбрать при создании нового контейнера. Используйте опцию Удалить (удалить папку с wine), чтобы уменьшить объем затятой внутренней памяти вашего девайса. Файлы wine хранятся в папке Z:/opt/WineCollection.</li>\n" +
                "<li>&ensp; Загрузка файлов wine:<br/>загрузка всех видов wine из интернета. Доступные источники: WineHQ (официальная сборка, перечислены только сборки ubuntu18) и Kron4ek (уменьшенный размер, промежуточные версии wine не включены). Загруженные версии wine появляются на странице 'Установленные'.</li>\n" +
                "</ul>\n");
        ruArray.put(mw_contNoWineTips, "Активный Wine не найден. Этот контейнер вероятно, не может запуститься. Удалите его, затем нажмите кнопку загрузки, чтобы установить необходимый Wine и повторите попытку.");
        ruArray.put(fab_hide, "Скрыть");
        ruArray.put(render_title, "Выбор рендера");
        ruArray.put(pa_title, "PulseAudio (XSDL)");
        ruArray.put(pa_explain, "PulseAudio используется для воспроизведения звука, уменьшения проблемы связанных со звуком. Эта функция использует сервер PulseAudio, извлеченный из apk Xserver XSDL. Что требуется поддержка 64-битной версии Андроид на вашем девайсе.");
        ruArray.put(pa_checkRun, "Включить службу PulseAudio$Запускать сервер PulseAudio при запуске контейнера");
        ruArray.put(pa_checkLog, "Вывод лога$Сохранить лог в Android/data/%s/files/logs/palog.txt");
        ruArray.put(pa_btnParam, "Изменить параметры запуска$Неверные параметры приведут к ошибкам изменяйте их только с пониманием того что делаете.");
        ruArray.put(pa_troubleShooting, "Устранение неполадок ⓘ$" +
                "Проверьте, правильно ли работает служба PulseAudio: для этого после запуска контейнера щелкните меню Пуск - Выполнить - введите winecfg, чтобы открыть его, на вкладке Аудио: \"В выбранные драйверы\" должны быть winepulse.drv, нажмите кнопку \"Проверить звук\", чтобы услышать тестовый звук." +
                "\n\nЕсли нет результата, проверьте следующие условия:" +
                "$1. Файл Pulseaudio для работы выполняется правильно: установите флаг \"Вывод лога\", запустите контейнер и просмотрите лог на наличие ошибок. При запуске с параметрами по умолчанию должно быть сообщение \"Успешный запуск образа\"" +
                "\n2. Модуль Pulse tcp загружается правильно: если он запущен с параметрами по умолчанию, .  /pulseaudio.conf автоматически загрузит модуль tcp без дополнительных настроек: load-module module-native-protocol-tcp auth-anonymous=true port=4713." +
                "\n3. Экспорт переменных среды: PULSE_SERVER=tcp:127.0.0.1:4713. Эта функция автоматически добавит переменные окружения при запуске контейнера, никаких дополнительных настроек не требуется. Переменные среды можно проверить с помощью Exaterm или Putty." +
                "\n4. Драйвер звука Wine в реестре: некоторые кеши предоставляют удобную запись в реестре для переключения на Pulseaudio в меню Пуск. Или вы можете отредактировать его вручную: Нажмите меню Пуск - Выполнить - введите regedit, чтобы открыть редактор реестра, и найдите следующий путь: [HKEY_CURENT_USER\\Software\\Wine\\Drivers]. создайте строковый параметр \"Audio\" и укажите его значением \"pulse\"." +
                "\n \n ");
        ruArray.put(xegw_title, "Xegw (termux:x11)");
        ruArray.put(xegw_info, "Xserver x11 используется для отображения графики. Поскольку исходный Xserver x11, реализованный в ExaGear, довольно прост он не может поддерживать dxvk и т. д., Xserver x11, реализованный в Termux-x11 перенесен в exagear для достижения лучшего рендеринга. Спасибо за помощь Twaik, разработчику Termux-x11.");
        ruArray.put(xegw_legacyDraw_tip, "Если после запуска контейнера отображается только чёрный экран со стрелкой-курсором, включение этого параметра решит проблему, но рендеринг может замедлиться.");
        ruArray.put(xegw_notification, "Xegw xserver.  Не отключаете данное уведомление, чтобы предотвратить сбои после переключения ExaGear в фоновый режим.");
        ruArray.put(xegw_btyOpt, "Отключить оптимизацию батареи$После запуска контейнера в строке состояния отображается уведомление для Xegw, поэтому процесс Xegw не будет завершаться при переключении в фоновый режим, при отключении будет отображаться только чёрный экран.\n\nЕсли это не решает проблему чёрного экрана при переходе в фоновый режим попробуйте отключить оптимизацию батареи для ExaGear в Настройках (с помощью этой кнопки). Если проблема все еще не решена, попробуйте включить автозапуск приложения ExaGear в Настройках, этот интерфейс расположатся в разных местах в зависимости от прошивки поэтому вам нужно найти это самим.");
        ruArray.put(DriveD2_title, "Изменить расположение дисков");
        ruArray.put(DriveD2_info, "Установите каталог Android в качестве выбранного диска.");
        ruArray.put(DriveD2_newDrive, "Создать новый Диск");
        ruArray.put(DriveD2_delDrive, "Удалить этот Диск");
        ruArray.put(DriveD2_devType, "Память телефона$Другое устройство памяти$Другое устройство памяти (Не обнаружено)");
        ruArray.put(DriveD2_parType, "Корневой каталог$Каталог файлов приложения");
        ruArray.put(DriveD2_errors, " Необходимо дать приложению разрешение на хранение. $ Каталог не существует. $ Этот путь обозначает файл, а не каталог. $ Приложение не имеет разрешения на чтение содержимого этого каталога. $ Приложение не имеет разрешения на запись в этот каталог. $ Ошибка: ");

        ruArray.put(conSet_otherArgv_title, "Дополнительные Аргументы Запуска");
        ruArray.put(conSet_otherArgv_prefValue, "Дополнительные cmd/env");
        ruArray.put(othArg_info, "Здесь перечислены все доступные параметры. Выбранные аргументы будут применены к текущему контейнеру.$Подробнее...");
        ruArray.put(othArg_info_more, "Эти аргументы будут добавлены в основную команду запуска контейнера.  Полную команду можно найти в файле по пути /sdcard/x86-stderr.txt.\n\nЕсли аргумент уже включен в исходную команду, добавление аргумента может быть неудачным.  Фактический результат можно просмотреть, нажав кнопку \"Предварительный просмотр\", используйте её чтобы ввести тестовую команду аргумента, или для просмотра аргумента в текстовом формате. \n\nВсе объявленные аргументы хранятся в Z:/opt/edpatch/contArgs.txt, а параметры, включенные для каждого контейнера, хранятся в Z:/home/xdroid_n/contArgs.txt.  Однако напрямую изменять txt не рекомендуется из-за строгих требований к формату хранения.");
        ruArray.put(othArg_taskset_useCustom, "Выбор_ядер_CPU");
        ruArray.put(othArg_ib_autorun, "Автозапуск_ib.exe");
        ruArray.put(othArg_serviceExeDisable, "Отключить_services.exe");
        ruArray.put(othArg_preview, "Предварительный просмотр");
        ruArray.put(global_add, "Добавить");
        ruArray.put(global_edit, "Редактировать");
        ruArray.put(global_del, "Удалить");
        ruArray.put(othArg_edit_delConfirm, "Вы уверены, что хотите удалить этот аргумент?\nЭто действие повлияет на все контейнеры. Если вы хотите отключить его только в текущем контейнере, просто снимите флажок.\n\n");
        ruArray.put(othArg_edit_attrTitles, "Имя Аргумента$Аргумент$Тип$Включить по умолчанию для новых контейнеров");
        ruArray.put(othArg_edit_typeChoices, "Переменная среды$Cmd - перед исходным Cmd$Cmd - до выполнения исходного Cmd$Cmd - после выполнения исходного Cmd");
        ruArray.put(othArg_edit_typeInfo, "Тип аргумента - ENV или CMD.\n\nПеременная среды: используется для выполняемой команды. Если исходный cmd содержит окружение с тем же именем, эти параметры будут переопределены.\n\nCmd: Если он находится до или после исходного cmd, этот cmd и исходный cmd соединяются одним знаком '&'.");
        ruArray.put(othArg_preview_titles, "Оригинальный CMD$CMD с параметрами CMD");
        ruArray.put(fabVO_checkTitles,"Использовать протокол vtest 2 (требуется новая библиотека Mesa libGL)$Использовать GL ES 3.x вместо OpenGL$Использовать многопоточный доступ egl$Распаковка DXTn (S3TC) (необходима для некоторых игр)$Автоматический перезапуск сервисов");
        ruArray.put(fabVO_radioTitles,"Позиция Оверлэя: $Левый  верхний  угол$Центр$Невидимая *$* Используется с VTEST_WIN=1 для рисования в окне X11 вместо наложения.");
        ruArray.put(fabVO_btnTitles,"Очистить Сервисы$Запустить Сервисы$Разрешить наложение Оверлэя");
        ruArray.put(fabVO_startResults,"Сервисы запускаются успешно. Теперь можно попробовать запустить контейнер, и запустить игры.$Не удалось запустить Сервисы.");        /*




         */
        stringMap.put("zh", zhArray);
        stringMap.put("en", enArray);
        stringMap.put("ru", ruArray);
    }

    public static String refreshLocale() {
        if (Globals.getApplicationState() == null)
            return null;
        return Globals.getAppContext().getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 根据id，返回对应语言的字符串
     */
    public static String getS(int id) {
        if (locale == null)
            locale = Globals.getAppContext().getResources().getConfiguration().locale.getLanguage();
//        Log.d("S", "get: 获取字符串，当前系统语言为" + locale + ", 要获取的字符串为" + id);
        String returnStr = null;
        if (stringMap.containsKey(locale))
            returnStr = Objects.requireNonNull(stringMap.get(locale)).get(id);
        if (returnStr == null) //即使有对应语言的数组，也可能忘了加对应翻译
            returnStr = Objects.requireNonNull(stringMap.get("en")).get(id, "");
        return returnStr;
    }

    /**
     * 获取id对应的字符串（getS），然后用'\\$' 分割成字符串数组并返回
     */
    public static String[] getSArr(int id) {
        return getS(id).split("\\$");
    }

    public static class attr {
        /**
         * 为对话框设置自定义视图的时候，手动设置边距
         */
        @Deprecated
        public static int dialogPaddingDp = 24;
//TODO 'TypedArray' used without 'try'-with-resources statement
        public static int textColorPrimary(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
            int color = array.getColor(0, Color.BLACK);
            array.recycle();
            return color;
        }
        public static int textColorPrimaryInverse(Context c){
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.textColorPrimaryInverse});
            int color = array.getColor(0, Color.WHITE);
            array.recycle();
            return color;
        }

        public static int colorPrimary(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
            int color = array.getColor(0, 0xff2196F3);
            array.recycle();
            return color;
        }

        public static int colorControlNormal(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.colorControlNormal});
            int color = array.getColor(0, 0xff2196F3);
            array.recycle();
            return color;
        }

        public static int colorBackground(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.colorBackground});
            int color = array.getColor(0, 0x00ffffff);
            array.recycle();
            return color;
        }
        public static Drawable selectableItemBackground(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
            Drawable d = array.getDrawable(0);
            array.recycle();
            return d;
        }

        public static Drawable listDivider(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            Drawable d = array.getDrawable(0);
            array.recycle();
            return d;
        }
    }

    public static class dimen {

        public static int margin8Dp() {
            return QH.px(Globals.getAppContext(), 8);
        }

        /**
         * 48dp对应的px值
         */
        public static int minCheckSize() {
            return QH.px(Globals.getAppContext(), 48);
        }

        /**
         * 24dp对应的px值
         */
        public static int dialogPadding() {
            return QH.px(Globals.getAppContext(), 24);
        }

    }

    public static class integer {

    }

    public static class drawable {
        public static int ic_add_24dp() {
            return rslvID(R.drawable.ic_add_24dp, 0x7f08009b);
        }

        public static int ic_more_vert_24dp() {
            return rslvID(R.drawable.ic_more_vert_24dp, 0x7f0800a9);
        }
    }

    public static class id{
        public static int ed_main_content_frame(){
            return rslvID(R.id.ed_main_content_frame,0x7f09006e);
        }
        public static int ed_main_fragment_container(){
            return rslvID(R.id.ed_main_fragment_container,0x7f090070);
        }
        public static int mainView(){
            return rslvID(R.id.mainView,0x7f0900a0);
        }
    }


}
