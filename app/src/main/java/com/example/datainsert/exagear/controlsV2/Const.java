package com.example.datainsert.exagear.controlsV2;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ExagearImageAware;
import com.example.datainsert.exagear.controlsV2.axs.XKeyButton;
import com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView;
import com.example.datainsert.exagear.controlsV2.edit.EditConfigWindow;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.QH;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Const {
    private static boolean initiated=false;
//    /**
//     * 记录model的全部类型及其对应int
//     * <br/>注意TouchAreaModel可能有继承关系，所以不能用instanceOf，应该用getClass().equals
//     * <br/> 找不到index的时候会返回 负数，不一定是-1？
//     */
//    public static final SparseArray<Class<? extends TouchAreaModel>> modelTypeArray = new SparseArray<>();
    @Deprecated
    public static float fingerStandingMaxMoveInches = 0.03f;
    public static int fingerTapMaxMs = 300;
    /**经过测试，18f（安卓像素）比较合适*/
    public static float fingerTapMaxMovePixels = 18f;
    /** 手指移动多远距离，鼠标滚动应该滚动一次. 好像60 滚动刚好和手指移动同步 */
    public static float fingerMoveDistToOneScrollUnit = 60f;
    /** 摇杆移动鼠标: 将横向和竖向分为多少个片段（一次最大发送一个片段的距离） */
    public static final int stickMouse_howManyFragment = 100;
    /** 摇杆移动鼠标: 如果每次移动一个片段的距离，经过多少秒正好能从最左侧移动到最右侧 */
    public static final float stickMouse_howMuchTimeToMoveAcross = 1.25f;
    /* 摇杆移动鼠标: 每隔多少毫秒发送一个片段的距离 */
    public static final int stickMouse_interval = (int) (1000 * stickMouse_howMuchTimeToMoveAcross / stickMouse_howManyFragment);
    /** 摇杆移动鼠标: 一个片段移动多远距离 */
    public static float[] stickMouse_distXYPerMove;
    /** toucharea 圆角矩形的圆角大小 */
    public static final int TOUCH_AREA_ROUND_CORNER_RADIUS = 10;
    /** touchArea 边框描边大小 */
    public static final int TOUCH_AREA_STROKE_WIDTH = 4;
    /** touchArea 绘制文字的最小大小（高度） 12sp对应的px值 */
    public static int TOUCH_AREA_MIN_TEXT_SIZE;
    /** touchArea 绘制文字的最大大小（高度） 20sp对应的px值 （32也行不过还是22吧） */
    public static int TOUCH_AREA_MAX_TEXT_SIZE;
    public static Edit1KeyView editKeyView = null;
    /** 用于计算 {@link #maxPointerDeltaDistInOneEvent} 应为几分之一*/
    public final static int maxDivisor = 20;
    /**一次最远只能移动屏幕宽/高的20分之一 */
    public static float maxPointerDeltaDistInOneEvent;
    private static TouchAreaView touchAreaView = null;
    public static XServerViewHolder xServerViewHolder = null;
    public static FragmentActivity activityRef = null;
    public static GestureContext2 gestureContextRef = null;
    public static int dp8;
    public static int minTouchSize;
    public static int minBtnAreaSize;
    public static int minStickAreaSize;
    public static float stickInnerOuterRatio = 2/3f; //摇杆的内圆与外圆半径之比
    public static float stickInnerMaxOffOuterRadiusRatio = 1; //摇杆内圆允许移动的距离（到内圆圆心）与外圆半径之比
    public static double stickMoveThreshold = 20; //摇杆按下并移动时，若手指距离中心小于此距离，则算作不移动

    public static int defaultTouchAreaBgColor = 0xffe8f6ff;//0xffc2e2ff;
    public static String profilePreferDefaultName = "default"; //设置新容器默认配置时，优先寻找叫这个名称的配置
    public static String bundledProfilesPath = "controls/profiles"; //内置配置在assets中的位置
    public static List<String> profileBundledNames = new ArrayList<>(); //放在apk/assets内的配置名（注意不是文件名）。
    public static final String fragmentTag = "ControlsFragment"; // 添加fragment时应该用这个tag，后续通过Const.get获取fragment时会用这个tag去寻找
    /** 启动任务管理器选项，执行初始脚本的环境变量名（将命令中中该字符串替换为脚本位置） */
    public static final String OPTION_TASKMGR_START_SH_ENV = "$ANOTHER_SH";
    public static boolean detailDebug = false; //用于调试的便捷开关
    /** {@link TestHelper#getWindowDisplaySize(Context)} 的值，init时赋值一次。*/
    public static Point windowDisplaySize = null;

    //TODO 如果要在没有全部完成之前发布的话，在“其他”页面添加说明这个是alpha版，不推荐使用，可能含有bug，升级到正式版时可能有冲突需要清除数据重装。
    /**
     * 有些数据需要context才能获取。此函数必须在访问Const成员变量前调用一次。
     */
    public static void init(FragmentActivity c, @NonNull XServerViewHolder holder) {
//        Log.d("TAG", "init: gc前static的弱引用会被回收吗 "+testRef.get());
//        Runtime.getRuntime().gc();
//        Log.d("TAG", "init: gc后static的弱引用会被回收吗 "+testRef.get());//有被其他地方引用的话就不会，所以context要等到生命周期结束了的，正常用的时候没问题
        activityRef = c;
        xServerViewHolder = holder;

        dp8 = QH.px(c, 8);
        minTouchSize = QH.px(c, 32);
        minBtnAreaSize = QH.px(c, 32);
        minStickAreaSize = minBtnAreaSize;
        int[] xWH = holder.getXScreenPixels();
        maxPointerDeltaDistInOneEvent =  1.0f * Math.min(xWH[0], xWH[1]) / Const.maxDivisor;
        stickMouse_distXYPerMove = new float[]{1.0f * xWH[0] / stickMouse_howManyFragment, 1.0f * xWH[1] / stickMouse_howManyFragment};
        TOUCH_AREA_MIN_TEXT_SIZE = QH.sp2px(c, 12);
        TOUCH_AREA_MAX_TEXT_SIZE = QH.sp2px(c, 20);
        windowDisplaySize = TestHelper.getWindowDisplaySize(c);


        Files.rootfsDir = ((ExagearImageAware) Globals.getApplicationState()).getExagearImage().getPath();
        Files.workDir = new File(QH.Files.edPatchDir() + "/customcontrols2");
        Files.profilesDir = new File(Files.workDir, "profiles");
        Files.currentProfile = new File(Files.workDir, "current");
        Files.currentContProfile = new File(Files.rootfsDir,"home/xdroid/currentProfile");
        Files.defaultProfileForNewContainer = new File(Files.workDir, "default");

        //        先检查一下路径是否存在，然后决定是否要初始化；
        //        保证各个文件夹存在，配置至少有一个（算上预设的），且current的符号链接存在
        boolean isFirst = false;
        if (!Files.workDir.exists()) {
            isFirst = true;
            Files.workDir.mkdirs();
        }
        if (!Files.profilesDir.exists()) {
            isFirst = true;
            Files.profilesDir.mkdir();
        }
        if (!Files.currentProfile.exists() || Objects.requireNonNull(Files.profilesDir.list()).length==0)
            isFirst = true;

        //（若未解压过）解压预设的几个配置以及设置新容器默认配置
        profileBundledNames = ModelProvider.readBundledProfilesFromAssets(c,isFirst);

        // 如果默认配置不存在则设置一个（优先找名称为default的），assets里不存在任何配置的情况不考虑了吧
        if(!Files.defaultProfileForNewContainer.exists()){
            String defaultName = profileBundledNames.contains(profilePreferDefaultName)
                    ? profilePreferDefaultName : profileBundledNames.get(0);
            ModelProvider.makeDefaultForNewContainer(defaultName);
        }

        //调整启动容器时应选择的配置（新容器：用户/系统设定的默认配置，开启容器单独配置选项：该容器最近一次所选的配置）
        ModelProvider.prepareCurrentProfileWhenContainerStart();

        initiated=true;
    }



    /**
     * 一些exagear的实现
     */
    public static void initExagearExtension(){
        Extension.addImpl(Extension.MOUSE_MOVE_CAMERA_RELATIVE,ConstExagearExtension.MouseMoveCameraAdapter.class);
    }

    /**
     * detach的时候，调用clear清除内存
     */
    public static void clear() {
        activityRef = null;
        xServerViewHolder = null;
        //TODO touchAreaView不清空了，声明周期让fragment维护（其附属的的view和profile也要考虑下怎么办）
//        editKeyViewRef = null;
//        touchAreaViewRef = null;
//        gestureContextRef = null;

        initiated=false;
    }

    /**
     * 用于判断是否已经调用过init。
     */
    public static boolean isInitiated() {
        return initiated;
    }

    /**
     * 调用clear之前，调用此方法删除fragment
     */
    public static void clearFragment() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(Const.fragmentTag);
        if(fragment instanceof ControlsFragment){
            manager.beginTransaction().remove(fragment).commit();
        }

    }

    public static Context getContext() {
        return getActivity();
    }
    public static FragmentActivity getActivity(){return activityRef;}

    public static ControlsFragment getControlFragment(){
        return (ControlsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

    public static float getDpi() {
        return getContext().getResources().getDisplayMetrics().density;
    }

    public static void setGestureContext(GestureContext2 ctx) {
        gestureContextRef = ctx;
    }

    public static GestureContext2 getGestureContext(){
        return gestureContextRef;
    }

    /**
     * 获取EditConfigWindow
     */
    public static EditConfigWindow getEditWindow() {
        return getTouchView().getEditWindow();
    }

    public static void setTouchView(TouchAreaView view){
        touchAreaView = view;
    }

    /**
     * 获取TouchAreaView
     */
    public static TouchAreaView getTouchView() {
        return touchAreaView;
    }

    /**
     * 从touchareaview中获取当前profile
     */
    public static OneProfile getActiveProfile(){
        return touchAreaView.getProfile();
    }

    public static boolean isEditing(){
        return getActiveProfile().isEditing();
    }

    public static XServerViewHolder getXServerHolder(){
        return xServerViewHolder;
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
     * 一些可选功能，只能提供接口，然后具体应用具体实现。如果实现了可以放到这里,要求子类实现必须无参构造
     * <br/> 显示选项时，如果getImpl返回不为null，则可以显示该选项
     */
    public static class Extension{
        /**
         * 用于游戏中，鼠标移动对应第一人称视角移动，此时鼠标不应因为触碰到屏幕边界而停下。
         */
        public static final int MOUSE_MOVE_CAMERA_RELATIVE = 1;
        private static final SparseArray<Class<?>> implList = new SparseArray<>();

        /**
         * 新建一个该类的实例并返回
         */
        public static <T> T getImpl(int code){
            Class<?> tClz = implList.get(code);
            T i = null;
            if(tClz!=null){
                try {
                    i= (T) tClz.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
            return i;
        }

        public static void addImpl(int code,Class<?> impl){
            implList.put(code,impl);
        }
    }

    /**
     * 获取x keycode按键码对应名称。若keycode大于 {@link XKeyButton#POINTER_MASK} 则认为是鼠标按键
     */
    public static String getKeyOrPointerButtonName(int keycode){
        String name = XKeyButton.xKeyNameArr[keycode];
//        if((keycode & XKeyButton.POINTER_MASK) == XKeyButton.POINTER_MASK)
//            name = XPointerButton.xKeyNamesArr[keycode - XKeyButton.POINTER_MASK];
//        else
//            name = XKeyButton.xKeyNameArr[keycode];
        return name == null? "None" : name;
    }


    /**
     * 为防止变量名改动导致json无法反序列化，使用 {@link SerializedName} 注解固定序列化名称
     */
    public static class GsonField {
        public final static String md_ModelType = "modelType";
        public final static String md_fsmTable = "fsmTable";
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
        public final static String st_nearFarThreshold = "nearFarThreshold";
        public final static String st_pointMoveType = "pointMoveType";
    }

    /**
     * 记录一些应用级别的偏好，这些没法记录在一个profile里，而应该是对全体profile生效
     */
    public static class Pref{
        /** 允许不同容器使用不同配置. 默认为false */
        private static final String PREF_KEY_ENABLE_PROFILE_PER_CONTAINER = "ENABLE_PROFILE_PER_CONTAINER";
        /** 启动任务管理器选项的替换命令。默认为空字符串。不为空时执行指定命令，每段参数用换行分割。 */
        private static final String PREF_KEY_RUN_TASKMGR_ALT = "RUN_TASKMGR_ALT";
        public static void setProfilePerContainer(boolean enable){
            QH.getPreference().edit().putBoolean(PREF_KEY_ENABLE_PROFILE_PER_CONTAINER,enable).apply();
        }

        public static boolean isProfilePerContainer(){
            return QH.getPreference().getBoolean(PREF_KEY_ENABLE_PROFILE_PER_CONTAINER,false);
        }

        public static void setRunTaskmgrAlt(String cmd){
            QH.getPreference().edit().putString(PREF_KEY_RUN_TASKMGR_ALT,cmd).apply();
        }

        public static String getRunTaskmgrAlt(){
            return QH.getPreference().getString(PREF_KEY_RUN_TASKMGR_ALT,"");
        }
    }

    /**
     * 文件相关的路径
     */
    public static class Files {
        public static File rootfsDir; //rootfs路径

        public static File workDir; //自定义操作模式 相关文件的根目录
        public static File profilesDir; //存储全部配置的目录
        public static File currentProfile; //当前全局配置的软链接
        public static File currentContProfile; //当前容器默认配置的软链接
        public static File defaultProfileForNewContainer; //新建容器时 使用的默认配置

    }
}
