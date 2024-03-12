package com.example.datainsert.exagear.controlsV2;

import static com.example.datainsert.exagear.controlsV2.Const.BtnType.DPAD;
import static com.example.datainsert.exagear.controlsV2.Const.BtnType.NORMAL;
import static com.example.datainsert.exagear.controlsV2.Const.BtnType.STICK;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import com.example.datainsert.exagear.controlsV2.edit.Edit1KeyView;
import com.example.datainsert.exagear.controlsV2.edit.EditConfigWindow;
import com.example.datainsert.exagear.controlsV2.gestureMachine.GestureContext2;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.controlsV2.widget.KeyOnBoardView;
import com.example.datainsert.exagear.QH;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class Const {
    public static boolean initiated=false;
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
    public static Edit1KeyView editKeyView = null;
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

    public static int defaultBgColor = 0xffc2e2ff;
    public static int keycodeMaxCount = 256 + 7; //还有 7个鼠标按键
    /**
     * 由于 键盘keycode和鼠标的buttoncode混在一起用了，所以需要用个mask隔开一下，规定大于256的就是鼠标按键，减去256是实际buttoncode
     * <br/> 比如左键就是256 | 1 = 257;
     */
    public static int keycodePointerMask = 256;
    public static String[] keyNames = null;

    public static String defaultProfileName = "default"; //没有任何配置时，默认配置名称
    public static final String fragmentTag = "ControlsFragment"; // 添加fragment时应该用这个tag，后续通过Const.get获取fragment时会用这个tag去寻找

    public static boolean detailDebug = false; //用于调试的便捷开关
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
        minBtnAreaSize = QH.px(c, 48);
        minStickAreaSize = minBtnAreaSize;

        if (keyNames == null)
            keyNames = KeyOnBoardView.initXKeyCodesAndNames(c, keycodeMaxCount);

        ModelProvider.workDir = new File(QH.Files.edPatchDir() + "/customcontrols2");
        ModelProvider.profilesDir = new File(ModelProvider.workDir, "profiles");
        ModelProvider.currentProfile = new File(ModelProvider.workDir, "current");
        //        先检查一下路径是否存在，然后决定是否要初始化；
        //        保证各个文件夹存在，配置至少有一个（算上预设的），且current的符号链接存在
        boolean isFirst = false;
        if (!ModelProvider.workDir.exists()) {
            isFirst = true;
            ModelProvider.workDir.mkdirs();
        }
        if (!ModelProvider.profilesDir.exists()) {
            isFirst = true;
            ModelProvider.profilesDir.mkdir();
        }
        if (!ModelProvider.currentProfile.exists())
            isFirst = true;

        //TODO 添加预设的几个配置（不过不应该在这添加，因为不应该允许用户删除）
        if (isFirst) {
            OneProfile defaultProfile = new OneProfile(defaultProfileName);
            ModelProvider.saveProfile(defaultProfile);
            ModelProvider.makeCurrent(defaultProfile.name);
        }

        initiated=true;
    }

    /**
     * 一些exagear的实现
     */
    public static void initExagearExtension(){
        Extension.addImpl(Extension.MOUSE_MOVE_CAMERA_RELATIVE,ConstExagearExtension.MouseMoveCameraAdapter.class);
    }

    /**
     * 调用 {@link #init(FragmentActivity, XServerViewHolder)} 之后调用此函数添加fragment。传入用于替换显示fragment的视图id
     */
    public static void initShowFragment(int frameId, ControlsFragment fragment){
        //添加fragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(frameId, fragment, Const.fragmentTag)
                .addToBackStack(null) //如果不用退出fragment的话不驾到backstack也无所谓吧
                .commit();

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
        public final static String st_pointMoveType = "pointMoveType";
    }
}
