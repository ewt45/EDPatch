package org.ewt45.customcontrols;

import android.content.Context;

/**
 * @deprecated 为了通用起见 最好别用这个。函数现传context吧
 */
@Deprecated
public class Globals {
    static Context c;
    public static Context getAppContext(){
        return c;
    }
}
