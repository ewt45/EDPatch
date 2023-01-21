//package com.example.datainsert.exagear.FAB;
//
//import android.util.Log;
//
//import com.eltechs.axs.Globals;
//
//import java.util.HashMap;
//import java.util.Map;
//
////应在fabmenu获取local后再被调用
//public class SO {
//    private static final Map<String,String> map= new HashMap<>();
//    private static String locale;
//    private interface ValueSender{
//        String getStringValue();
//    }
//
//
//    public static String f(E e){
//        //系统中文，exagear里文件夹位置的popupMenu的菜单项title居然是英文的。可能是初始化字符串的时候还没设置FabMenu的local？那算了在自己类里设置吧，反正有全局context
//        if(locale==null)
//             locale = Globals.getAppContext().getResources().getConfiguration().locale.getLanguage();
//        //尝试手机语言，英语，中文
//        Log.d("S", "f: 获取字符串，当前系统语言为"+locale+", 要获取的字符串为"+e);
//        String str = map.get(e.toString()+locale);
//        if(str==null)
//            str = map.get(e.toString() +"en");
//        if(str==null)
//            str = map.get(e.toString() +"zh");
//        return str;
//    }
//
//    //只有这个向外暴露，然后外面调用S.get()获取string，接收到E之后只取e的toString就好了
//    public enum E implements ValueSender{
//        Dialog_PosBtn("确定"),
//        Dialog_NegBtn("取消"),
//        DriveD_Title("修改D盘路径"),
//        DriveD_ParDirKey_1("手机存储(根目录)"),
//        DriveD_ParDirKey_2("手机存储(应用专属目录)"),
//        DriveD_ParDirKey_3("外置SD卡(应用专属目录)"),
//        DriveD_Explain("请指定一个安卓文件夹作为D盘"),
//        DriveD_DescTitle("说明"),
//        DriveD_DescCont("android11及以上，在非应用专属目录下的游戏加载/读档速度可能变慢。解决方法是将d盘修改到应用专属目录，或将游戏复制到c/z盘（c/z盘默认在应用专属目录）。 "),
//        DriveD_SncBrBtn("重启"),
//        DriveD_SncBrTxt("设置已更新，手动重启应用后生效"),
//        DriveD_ToastExitFail("设置未更新"),
//        DriveD_EditDstTitle("文件夹名称"),
//        DriveD_EditParTitle("文件夹位置"),
//        DriveD_getPathFail("无法获取路径"),
//        DriveD_check_1("文件夹父目录存在"),
//        DriveD_check_2("文件夹存在"),
//        DriveD_check_3("是文件夹类型"),
//        DriveD_check_4("具有该文件夹的读取权限"),
//        DriveD_check_5("具有该文件夹的写入权限"),
//        DriveD_NoStrgPmsn("应用文件存储权限被禁止"),
//        ;
//        E(String s){
//            stringValue = s;
//        }
//        final String stringValue;
//        @Override
//        public String getStringValue() {
//            return stringValue;
//        }
//    }
//    private enum E_en implements ValueSender{
//        Dialog_PosBtn("confirm"),
//        Dialog_NegBtn("cancel"),
//        DriveD_Title("Change the Location of Drive D"),
//        DriveD_ParDirKey_1("External Storage"),
//        DriveD_ParDirKey_2("External Storage(App-specific storage)"),
//        DriveD_ParDirKey_3("SD Card(App-specific storage)"),
//        DriveD_Explain("set an android directory as drive d"),
//        DriveD_DescTitle("tips"),
//        DriveD_DescCont("on android11+, read/write speed could be extremely slow. To solve this problem, set drive d to app-specific storage, or copy game to drive c/z "),
//        DriveD_SncBrBtn("restart"),
//        DriveD_SncBrTxt("preference is changed, restart app to apply it"),
//        DriveD_ToastExitFail("preference is not changed"),
//        DriveD_EditDstTitle("Directory Name"),
//        DriveD_EditParTitle("Directory Location"),
//        DriveD_getPathFail("unable to retrieve the path"),
//        DriveD_check_1("Directory parent folder exists"),
//        DriveD_check_2("Directory exists"),
//        DriveD_check_3("is directory type"),
//        DriveD_check_4("App is allowed to read the directory"),
//        DriveD_check_5("App is allowed to write to the directory"),
//        DriveD_NoStrgPmsn("App's storage permission is not granted"),
//        ;
//        E_en(String s){
//            stringValue = s;
//        }
//        final String stringValue;
//        @Override
//        public String getStringValue() {
//            return stringValue;
//        }
//    }
//    static{
//        for(E e:E.values()){
//            map.put(e.toString()+"zh",e.getStringValue());
//        }
//        for(E_en e: E_en.values()){
//            map.put(e.toString()+"en",e.getStringValue());
//        }
//
//    }
//}
