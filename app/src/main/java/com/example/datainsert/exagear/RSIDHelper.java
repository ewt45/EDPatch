package com.example.datainsert.exagear;

import com.eltechs.axs.Globals;

public class RSIDHelper {
    /**
     * 用于处理使用的资源id。用自己的工程测试的时候，返回gradle自动分配的id，添加到待修改的apk后使用apk原有id。
     * 省的每次编译成smali都要手动替换，麻烦死了
     * @param my 我自己的apk的资源id
     * @param ori 别人apk的资源id
     * @return 应该使用的资源id
     */
    public static int rslvID(int my, int ori){
        return Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7")? my : ori;
    }
}
