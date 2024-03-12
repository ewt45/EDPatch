package com.example.datainsert.exagear.controlsV2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.RR;

/**
 * 每次切屏/旋转 都会彻底删除fragment并重新添加
 */
public class CustomInterfaceOverlay implements XServerDisplayActivityInterfaceOverlay {
    public static final String TAG = "CustomInterfaceOverlay";
    public static final float buttonSizeInches = 0.4f;
    private static final float buttonSzNormalDisplayInches = 0.4f;
    private static final float buttonSzSmallDisplayInches = 0.3f;
    private static final float displaySizeThresholdInches = 5.0f;
    private static int containerViewId = View.NO_ID;
    private final int buttonWidthPixelsFixup = 30;
    private boolean isToolbarsVisible = true;
    private boolean hasAttached = false;

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity a, ViewOfXServer viewOfXServer) {
        Log.d(TAG, "attach: ");
        //加一个framelayout视图，设置id（attach可能重复调用所以首先检查是否已经存在）（但是添加fragment之后framelayout还在嘛 应该还在）
        //先设置extension，再调用init，传入activity和xserverviewholder，此时会创建fragment
        // detach要删除fragment吗？
        //fragmentmanager findFragmentById 可以传入framelayout的id来寻找fragment，或者byTag寻找

        //要不activity和holder传入fragment的构造函数吧，然后用户自己attach fragment


        //先检查container视图是否存在，若不存在则添加到视图树中

        //必要的全局初始化
        Const.init(a, new XServerViewHolderImpl(viewOfXServer));

        FrameLayout containerView = new FrameLayout(a);
        containerViewId = View.generateViewId();
        containerView.setId(containerViewId);
        ((ViewGroup) a.findViewById(RR.id.mainView())).addView(containerView, new ViewGroup.LayoutParams(-1, -1));

        Fragment oldFragment = a.getSupportFragmentManager().findFragmentByTag(Const.fragmentTag);
        Fragment fragment = ControlsFragment.newInstance(oldFragment);

        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();

        if (oldFragment != null)
            transaction.remove(oldFragment);

        transaction.add(containerViewId, fragment, Const.fragmentTag)
                .addToBackStack(null)//还是加一个吧，否则失去焦点的时候直接退出acitivity了
                .commit();



        //可行的方法

//        boolean shouldAddFragment = false;
//        Fragment fragment = a.getSupportFragmentManager().findFragmentByTag(Const.fragmentTag);
//
//        if(containerViewId == View.NO_ID || a.findViewById(containerViewId)==null){
//            shouldAddFragment = true;
//            FrameLayout containerView = new FrameLayout(a);
//            containerViewId = View.generateViewId();
//            containerView.setId(containerViewId);
//            ((ViewGroup)a.findViewById(RR.id.mainView())).addView(containerView,new ViewGroup.LayoutParams(-1,-1));
//
//            //由于容器视图不存在，原先如果有附加的fragment也要删除,保证下面会新建fragment
//            if(fragment!=null)
//                a.getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
//        }
//
//        //必要的全局初始化
//        Const.init(a,new XServerViewHolderImpl(viewOfXServer));
//
//        //如果fragment不存在，则挂载到刚才新建的视图的id上，否则不添加
//        if(shouldAddFragment || a.getSupportFragmentManager().findFragmentByTag(Const.fragmentTag)==null){
//            if(fragment==null)
//                fragment = new ControlsFragment()   ;
//            Const.initShowFragment(containerViewId, (ControlsFragment) fragment);
//        }

        Const.getXServerHolder().setShowCursor(true);

        View nullView = new View(a);
        nullView.setFocusable(false);
        nullView.setFocusableInTouchMode(false);
        nullView.setVisibility(View.GONE);
        nullView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        return nullView;
//        SharedPreferences sp = a.getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
//        this.tscWidget = new TouchScreenControlsWidget(a, viewOfXServer, this.controlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
//        this.tscWidget.setZOrderMediaOverlay(true);

//        //自定义弹窗菜单
//        TextView textView = new TextView(a);
//        textView.setBackgroundColor(a.getResources().getColor(android.R.color.transparent));
//        returnLayout.addView(textView, new FrameLayout.LayoutParams(0, 0, 5));
//        controlsFactory.setPopupMenu(new SpecialPopupMenu(a,textView));
//        //用factory填充布局和toucharea
//        controlsFactory.reinflateControlLayout(tscWidget.getContext(),viewOfXServer);

//        return returnLayout;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public void detach() {
        //这里不清除fragment了，让系统自动处理（而且如果要导入导出配置的话，这里清除就没法回调了）
//        Const.clearFragment();
        Const.clear();
    }

//    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
//    public boolean isSidePanelsVisible() {
//        return isToolbarsVisible;
//    }
//
//    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
//    public void toggleSidePanelsVisibility() {
//        isToolbarsVisible = !isToolbarsVisible;
//        leftToolbar.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
//        rightToolbar.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
//        btnContainer.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
//        if(isToolbarsVisible){
//            refreshControlUI();//刷新布局
//        }else{
//            controlsFactory.hideControlPanelsTouchArea();//将toucharea范围改为0
//        }
//    }

//    /**
//     * 进入按键编辑。从四指触屏的菜单中启动
//     */
//    public void startEditing(){
//        if (btnContainer!=null){
//            btnContainer.setElevation(200);
//            //进入编辑模式，先显示一次dialog
//            new CustomControls().show(((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getSupportFragmentManager(),CustomControls.TAG);
//
//        }
//
//    }
//
//    /**
//     * (仅刷新，不退出编辑）（隐藏按键的情况下会退出编辑）
//     * 更新按钮布局和toucharea(先序列化，待会factory会从本地反序列化读取最新的）
//     */
//    public void refreshControlUI(){
//        Log.d(TAG, "refreshControlUI: ");
//        //用lockmanager锁一下试试看，会不会解决构建过程就触摸按钮导致卡死的问题
//        controlsFactory.saveToFileKeyCodes2and3(Globals.getAppContext());
//        controlsFactory.reinflateControlLayout(tscWidget.getContext(),viewOfXServer);
//        if(tscWidget!=null && tscWidget.getChildCount()>0 && tscWidget.getVisibility()==VISIBLE) {
//            tscWidget.requestLayout();
//        }
//
//        //如果隐藏按键的话直接退出编辑
//        if(!isToolbarsVisible)
//            btnContainer.setElevation(0);
//    }
//
//    /**
//     * 退出编辑状态（先刷新布局，再退出编辑，会调用 refreshControlUI）
//     */
//    public void endEditing(){
//        refreshControlUI();
//        btnContainer.setElevation(0);
//    }
//
////    public static FalloutInterfaceOverlay2 getInstance(){
////        return null;
////    }
//
//    public FalloutTouchScreenControlsFactory2 getControlsFactory() {
//        return controlsFactory;
//    }


}
