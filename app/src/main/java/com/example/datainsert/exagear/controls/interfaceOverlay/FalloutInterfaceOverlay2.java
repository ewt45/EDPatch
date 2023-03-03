package com.example.datainsert.exagear.controls.interfaceOverlay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.LinearLayout.HORIZONTAL;

import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.CommonApplicationConfigurationAccessor;
import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels;
import com.eltechs.axs.activities.menus.Quit;
import com.eltechs.axs.activities.menus.ShowKeyboard;
import com.eltechs.axs.activities.menus.ShowUsage;
import com.eltechs.axs.activities.menus.ToggleHorizontalStretch;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.widgets.actions.AbstractAction;
import com.eltechs.axs.widgets.popupMenu.AXSPopupMenu;
import com.eltechs.axs.widgets.touchScreenControlsOverlay.TouchScreenControlsWidget;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.CustomControls;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.BtnContainer;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.SpecialPopupMenu;
import com.example.datainsert.exagear.controls.menus.ControlEdit;
import com.example.datainsert.exagear.controls.menus.ControlToggleVisibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * btncontainer和tscwidget为兄弟布局且btncontainer
 *
 * 编辑按钮布局调用startEditing
 * 退出编辑（更新布局）调用refreshControlUI
 */
public class FalloutInterfaceOverlay2 implements XServerDisplayActivityInterfaceOverlay, XServerDisplayActivityUiOverlaySidePanels {
    public static final String TAG = "FalloutIOverlay2";
    public static final float buttonSizeInches = 0.4f;
    private static final float buttonSzNormalDisplayInches = 0.4f;
    private static final float buttonSzSmallDisplayInches = 0.3f;
    private static final float displaySizeThresholdInches = 5.0f;
    private LinearLayout leftToolbar;
    private LinearLayout rightToolbar;
    private BtnContainer btnContainer; //按键编辑的外部容器
    private TouchScreenControlsWidget tscWidget;
    private ViewOfXServer viewOfXServer;
    private final int buttonWidthPixelsFixup = 30;
    private boolean isToolbarsVisible = true;
    private final FalloutTouchScreenControlsFactory2 controlsFactory = new FalloutTouchScreenControlsFactory2() ;

    public static final String TSCWIDGET_TAG="TouchScreenControlsWidget_tag";


    private static boolean isDisplaySmall(DisplayMetrics displayMetrics) {
        return ((float) displayMetrics.widthPixels) / ((float) displayMetrics.densityDpi) < displaySizeThresholdInches;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public View attach(XServerDisplayActivity a, ViewOfXServer viewOfXServer) {
        SharedPreferences sp = a.getSharedPreferences(PREF_FILE_NAME_SETTING,Context.MODE_PRIVATE);
        this.viewOfXServer = viewOfXServer;

        FrameLayout returnLayout = new FrameLayout(a);
        returnLayout.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));

        //先初始化自由位置或两侧栏的按钮容器布局，然后交给factory
        btnContainer = new BtnContainer(a, viewOfXServer);

        leftToolbar = new LinearLayout(a);
        leftToolbar.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        leftToolbar.setOrientation(HORIZONTAL);

        rightToolbar = new LinearLayout(a);
        rightToolbar.setLayoutParams(new LinearLayout.LayoutParams(-2, -1, 0.0f));
        rightToolbar.setOrientation(HORIZONTAL);

        this.tscWidget = new TouchScreenControlsWidget(a, viewOfXServer, this.controlsFactory, TouchScreenControlsInputConfiguration.DEFAULT);
        this.tscWidget.setZOrderMediaOverlay(true);

        //把btnContainer传给factory。要在tscWidget添加到布局之前
        controlsFactory.setControlContainers(btnContainer,leftToolbar,rightToolbar);
        //自定义弹窗菜单
        TextView textView = new TextView(a);
        textView.setBackgroundColor(a.getResources().getColor(android.R.color.transparent));
        returnLayout.addView(textView, new FrameLayout.LayoutParams(0, 0, 5));
        controlsFactory.setPopupMenu(new SpecialPopupMenu(a,textView));
        //用factory填充布局和toucharea
        controlsFactory.reinflateControlLayout(tscWidget.getContext(),viewOfXServer);


        //底层先放btnContainer
        returnLayout.addView(btnContainer);
        //然后再放一层，左右侧栏，中间是主画面
        LinearLayout linearTscAndSidebar = new LinearLayout(a);
        linearTscAndSidebar.setOrientation(HORIZONTAL);
        linearTscAndSidebar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearTscAndSidebar.addView(leftToolbar);
        linearTscAndSidebar.addView(this.tscWidget, new LinearLayout.LayoutParams(0, -1, 1.0f));
        linearTscAndSidebar.addView(rightToolbar);
        FrameLayout.LayoutParams centerParams = new FrameLayout.LayoutParams(-1,-1);
        centerParams.gravity= Gravity.CENTER;
        returnLayout.addView(linearTscAndSidebar,centerParams);

                //a.getSharedPreferences(PREF_FILE_NAME_SETTING,Context.MODE_PRIVATE).getBoolean(PREF_KEY_BTN_ON_WIDGET,false);
        //用于判断重写的方法是返回侧栏相关还是按键编辑相关

        if(viewOfXServer!=null){
            viewOfXServer.setHorizontalStretchEnabled(new CommonApplicationConfigurationAccessor().isHorizontalStretchEnabled());
            //设置鼠标显隐
            viewOfXServer.getConfiguration().setShowCursor( sp.getBoolean(PREF_KEY_SHOW_CURSOR,true));
            //设置编辑按键的菜单项
//            List<AbstractAction> popupLists = new ArrayList<>(Arrays.asList(new ControlEdit(), new ShowKeyboard(), new ToggleHorizontalStretch(), new ControlToggleVisibility(), new ShowUsage(), new Quit()));
//            a.addDefaultPopupMenu(popupLists);
        }


        //之前如果设置过隐藏按键，那就隐藏
        leftToolbar.setVisibility(isToolbarsVisible?VISIBLE:GONE);
        rightToolbar.setVisibility(isToolbarsVisible?VISIBLE:GONE);
        btnContainer.setVisibility(isToolbarsVisible?VISIBLE:GONE);

        return returnLayout;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay
    public void detach() {
        if(tscWidget!=null){
            this.tscWidget.detach();
        }
        this.tscWidget = null;
        this.leftToolbar = null;
        this.rightToolbar = null;
        this.btnContainer=null;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public boolean isSidePanelsVisible() {
        return isToolbarsVisible;
    }

    @Override // com.eltechs.axs.activities.XServerDisplayActivityUiOverlaySidePanels
    public void toggleSidePanelsVisibility() {
        isToolbarsVisible = !isToolbarsVisible;
        leftToolbar.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
        rightToolbar.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
        btnContainer.setVisibility(isToolbarsVisible ? VISIBLE : GONE);
        if(isToolbarsVisible){
            refreshControlUI();//刷新布局
        }else{
            controlsFactory.hideControlPanelsTouchArea();//将toucharea范围改为0
        }
    }

    /**
     * 进入按键编辑。从四指触屏的菜单中启动
     */
    public void startEditing(){
        if (btnContainer!=null){
            btnContainer.setElevation(200);
            //进入编辑模式，先显示一次dialog
            new CustomControls().show(((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getSupportFragmentManager(),CustomControls.TAG);

        }

    }

    /**
     * (仅刷新，不退出编辑）
     * 更新按钮布局和toucharea(先序列化，待会factory会从本地反序列化读取最新的）
     */
    public void refreshControlUI(){
        Log.d(TAG, "refreshControlUI: ");
        controlsFactory.serializeKeyCodes2and3(Globals.getAppContext());
        controlsFactory.reinflateControlLayout(tscWidget.getContext(),viewOfXServer);
        if(tscWidget!=null && tscWidget.getChildCount()>0 && tscWidget.getVisibility()==VISIBLE) {
            tscWidget.requestLayout();
        }
    }

    /**
     * 退出编辑状态（先刷新布局，再退出编辑，会调用 refreshControlUI）
     */
    public void endEditing(){
        refreshControlUI();
        btnContainer.setElevation(0);
    }


    public static FalloutInterfaceOverlay2 getInstance(){
        return null;
    }

    public FalloutTouchScreenControlsFactory2 getControlsFactory() {
        return controlsFactory;
    }




}