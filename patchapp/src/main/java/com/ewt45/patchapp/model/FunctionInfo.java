package com.ewt45.patchapp.model;

import com.ewt45.patchapp.MyApplication;
import com.ewt45.patchapp.R;
import com.ewt45.patchapp.thread.Func;
import com.ewt45.patchapp.thread.FuncCursor;
import com.ewt45.patchapp.thread.FuncFAB;
import com.ewt45.patchapp.thread.FuncMultiWine;
import com.ewt45.patchapp.thread.FuncOtherArgv;
import com.ewt45.patchapp.thread.FuncRenderer;
import com.ewt45.patchapp.thread.FuncResl;
import com.ewt45.patchapp.thread.FuncSInput;
import com.ewt45.patchapp.thread.FuncSelObb;
import com.ewt45.patchapp.thread.FuncShortcut;

/**
 * 包含一个功能的全部相关数据
 * 版本号要不实时获取吧？不记录了
 */
public class FunctionInfo {
    public final static FunctionInfo[] ALL_FUNCTIONS = new FunctionInfo[]{
            new FunctionInfo(new FuncFAB(), R.string.funcname_fab, R.string.fraghelp_body_fab, R.drawable.fab, R.drawable.ic_settings),
            new FunctionInfo(new FuncCursor(), R.string.funcname_cursorimg, R.string.fraghelp_body_cursorimg, R.drawable.showcursor, R.drawable.ic_cursor),
            new FunctionInfo(new FuncResl(), R.string.funcname_prefresl, R.string.fraghelp_body_prefresl, R.drawable.customresl, R.drawable.ic_aspect_ratio),
            new FunctionInfo(new FuncSInput(), R.string.funcname_softinput, R.string.fraghelp_body_softinput, R.drawable.softinput, R.drawable.ic_keyboard),
            new FunctionInfo(new FuncSelObb(), R.string.funcname_selobb, R.string.fraghelp_body_selobb, R.drawable.selectobb, R.drawable.ic_find_file),
            new FunctionInfo(new FuncShortcut(), R.string.funcname_shortcut, R.string.fraghelp_body_shortcut, R.drawable.shortcut, R.drawable.ic_article_shortcut),
            new FunctionInfo(new FuncMultiWine(), R.string.funcname_mw, R.string.fraghelp_body_mw, R.drawable.multiwine, R.drawable.ic_wine),
            new FunctionInfo(new FuncRenderer(), R.string.funcname_renderer, R.string.fraghelp_body_renderer, R.drawable.renderer, R.drawable.ic_tablet),
            new FunctionInfo(new FuncOtherArgv(), R.string.funcname_otherargv, R.string.fraghelp_body_otherargv, R.drawable.otherargv, R.drawable.ic_text),
    };
    public String name;
    public String description;
    public int descpImgId;
    public Func func;
    public int iconId;
    //checked用于step2显示统计数据（1为勾选）。instVer和latestVer在step1解包apk之后统计。
    public int instVer,latestVer, newlyChecked;

    public FunctionInfo(Func func, int name, int descp, int descpImg, int iconId) {
        this.name = MyApplication.i.getString(name);
        this.func = func;
        this.description = MyApplication.i.getString(descp);
        this.descpImgId = descpImg;
        this.iconId = iconId;
    }

    /**
     * step1读取到apk信息成功后，step3回包apk成功后，调用此函数刷新apk的版本号统计信息，以供step2显示使用
     */
    public static void refreshVersionsStatistic() {
        for(FunctionInfo info:FunctionInfo.ALL_FUNCTIONS){
            info.instVer = info.func.getInstalledVersionFormatted();
            info.latestVer = info.func.getLatestVersionFormatted();
            info.newlyChecked = 0;
        }
    }
}
