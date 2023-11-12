package com.ewt45.patchapp.thread;

public interface Func extends Action {
    /**
     * 此版本号代表该功能未添加到apk中
     */
    public static final int INVALID_VERSION = 0;

    /**
     * 已经安装的版本，用于判断该功能是否已添加，以及是否可以升级
     * @return
     */
     public int getInstalledVersion();

    /**
     * 获取当前该版本最新的版本号，用于判断是否可以升级或者添加
     * @return
     */
     public int getLatestVersion();

    /**
     * 主要用于FuncFab返回显示版本号，因为有多个所以需要格式化一下
     */
     default int getLatestVersionFormatted(){
         return getLatestVersion();
     }
    /**
     * 主要用于FuncFab返回显示版本号，因为有多个所以需要格式化一下
     */
     default int getInstalledVersionFormatted(){
         return getInstalledVersion();
     }
}
