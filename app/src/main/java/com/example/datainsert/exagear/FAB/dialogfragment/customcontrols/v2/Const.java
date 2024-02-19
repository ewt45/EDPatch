package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.DPAD;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.NORMAL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.BtnType.STICK;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider.currentProfile;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider.profilesDir;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider.workDir;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;

import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.Edit1KeyView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.Edit3ProfilesView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.EditConfigWindow;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneProfile;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.KeyOnBoardView;
import com.example.datainsert.exagear.QH;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class Const {
//    /**
//     * 记录model的全部类型及其对应int
//     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
//     * <br/> 找不到index的时候会返回 负数，不一定是-1？
//     */
//    public static final SparseArray<Class<? extends TouchAreaModel>> modelTypeArray = new SparseArray<>();
    public static float fingerStandingMaxMoveInches = 0.03f;
    public static int fingerTapMaxMs = 300;
    /**
     * 经过测试，12f（安卓像素）比较合适
     */
    public static float fingerTapMaxMoveInches = 0.2f;
//    public static JsonDeserializerTouchAreaModel modelSaver; //用于反序列化时还原抽象类，以及处理文件位置
    public static WeakReference<Edit1KeyView> editKeyViewRef = null;
    public static WeakReference<ControlsFragment> fragmentRef = null;
    public static WeakReference<TouchAreaView> touchAreaViewRef = null;
    public static WeakReference<ViewOfXServer> viewOfXServerRef = null;
    public static WeakReference<Activity> activityRef = null;
    public static WeakReference<GestureContext2> gestureCxtRef = null;
    public static int dp8;
    public static int minTouchSize;
    public static int minBtnAreaSize;
    public static int minStickAreaSize;
    public static int defaultBgColor = 0xffc2e2ff;
    public static int keycodeMaxCount = 256 + 7; //还有 7个鼠标按键
    /**
     * 由于 键盘keycode和鼠标的buttoncode混在一起用了，所以需要用个mask隔开一下，规定大于256的就是鼠标按键，减去256是实际buttoncode
     * <br/> 比如左键就是256 | 1 = 257;
     */
    public static int keycodePointerMask = 256;
    public static String[] keyNames = null;

    public static String defaultProfileName = "default"; //没有任何配置时，默认配置名称

    /**
     * 有些数据需要context才能获取。此函数必须在访问Const成员变量前调用一次。
     */
    public static void init(Activity c, ViewOfXServer viewOfXServer) {

//        Log.d("TAG", "init: gc前static的弱引用会被回收吗 "+testRef.get());
//        Runtime.getRuntime().gc();
//        Log.d("TAG", "init: gc后static的弱引用会被回收吗 "+testRef.get());//有被其他地方引用的话就不会，所以context要等到生命周期结束了的，正常用的时候没问题
        activityRef = new WeakReference<>(c);
        viewOfXServerRef = new WeakReference<>(viewOfXServer);

        dp8 = QH.px(c, 8);
        minTouchSize = QH.px(c, 32);
        minBtnAreaSize = QH.px(c, 48);
        minStickAreaSize = minBtnAreaSize * 2;

        if (keyNames == null)
            keyNames = KeyOnBoardView.initXKeyCodesAndNames(c, keycodeMaxCount);

        ModelProvider.workDir = new File(QH.Files.edPatchDir() + "/customcontrols2");
        ModelProvider.profilesDir = new File(ModelProvider.workDir, "profiles");
        ModelProvider.currentProfile = new File(ModelProvider.workDir, "current");
        //        先检查一下路径是否存在，然后决定是否要初始化；
        //        保证各个文件夹存在，配置至少有一个（算上预设的），且current的符号链接存在
        boolean isFirst = false;
        if (!workDir.exists()) {
            isFirst = true;
            workDir.mkdirs();
        }
        if (!profilesDir.exists()) {
            isFirst = true;
            profilesDir.mkdir();
        }
        if (!currentProfile.exists())
            isFirst = true;

        if (isFirst) {
            OneProfile defaultProfile = new OneProfile(defaultProfileName);
            ModelProvider.saveProfile(defaultProfile);
            ModelProvider.makeCurrent(defaultProfile.name);
        }

    }

    /**
     * detach的时候，调用clear清除内存
     */
    public static void clear() {
        editKeyViewRef = null;
        activityRef = null;
        fragmentRef = null;
        touchAreaViewRef = null;
        viewOfXServerRef = null;
        gestureCxtRef = null;
    }

    public static Context getContext() {
        return activityRef.get();
    }

    public static ViewFacade getViewFacade() {
        ViewOfXServer viewOfXServer = viewOfXServerRef.get();
        return viewOfXServer == null ? null : viewOfXServer.getXServerFacade();
    }

    public static float getDpi() {
        return getContext().getResources().getDisplayMetrics().density;
    }

    public static void setGestureContext(GestureContext2 gestureContext) {
        gestureCxtRef = new WeakReference<>(gestureContext);
    }

    public static GestureContext2 getGestureContext(){
        return gestureCxtRef.get();
    }

    /**
     * 获取EditConfigWindow
     */
    public static EditConfigWindow getEditWindow() {
        return touchAreaViewRef.get().getEditWindow();
    }

    @IntDef({NORMAL, STICK, DPAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnType {
        int NORMAL = 0;
        int STICK = 1;
        int DPAD = 2;
    }

    @IntDef({BtnShape.RECT, BtnShape.OVAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnShape {
        int RECT = 0;
        int OVAL = 1;
    }

    @IntDef({BtnColorStyle.STROKE, BtnColorStyle.FILL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnColorStyle {
        int STROKE = 0;
        int FILL = 1;
    }

    /**
     * 一些外部可控的选项，一般用于开发者（比如不是exa，而是别的应用）
     */
    public static class Config {
        /**
         * 默认false，从RR读取中文英文和俄语字符串。如果为true则从res中读取（尚未实现）
         */
        public static final boolean readStringFromApkRes = false;
        /**
         * 默认false，使用旧support库的依赖库。如果为true则使用jetpack依赖库（尚未实现）
         */
        public static final boolean useLegacySupportLibs = true;
    }


    /**
     * 为防止变量名改动导致json无法反序列化，使用 {@link SerializedName} 注解固定序列化名称
     */
    public static class GsonField {
        public final static String md_ModelType = "modelType";
        public final static String st_StateType = "stateType";
        public final static String st_keycode = "keycode";
        public final static String st_doPress = "doPress";
        public final static String st_doRelease = "doRelease";
        public final static String st_fingerIndex = "fingerIndex";
        public final static String st_fingerXYType = "fingerXYType";
        public final static String st_optionType = "optionType";
        public final static String st_waitUntilFinish = "waitUntilFinish";
        public final static String st_ignorePixels = "ignorePixels";
        public final static String st_zoomFingerIndex1 = "zoomFingerIndex1";
        public final static String st_zoomFingerIndex2 = "zoomFingerIndex2";
        public final static String st_noMoveThreshold = "noMoveThreshold";
        public final static String st_fastMoveThreshold = "fastMoveThreshold" ;
        public final static String st_countDownMs = "countDownMs";
        public final static String md_fsmTable = "fsmTable";
        public final static String st_nearFarThreshold = "nearFarThreshold";
    }
}
