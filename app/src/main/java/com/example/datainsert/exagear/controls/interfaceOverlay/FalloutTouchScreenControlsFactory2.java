package com.example.datainsert.exagear.controls.interfaceOverlay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.LinearLayout.VERTICAL;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.eltechs.axs.GestureStateMachine.GestureContext;
import com.eltechs.axs.Globals;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchEventMultiplexor;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.TouchScreenControlsFactory;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.graphicsScene.GraphicsSceneConfigurer;
import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.BtnContainer;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.JoyStickBtn;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.RegularKeyBtn;
import com.example.datainsert.exagear.controls.interfaceOverlay.widget.UnmovableBtn;
import com.example.datainsert.exagear.controls.model.KeyCodes2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;
import com.example.datainsert.exagear.controls.model.OneCol;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 如果要设置按钮自定义位置，请在实例化之后调用setBtnContainer
 * 在factory这里维护keycode实例吧
 * 在新建一个touchscreencontrol的时候直接从本地反序列化读取keycodes3，这样不用类之间传来传去（
 *
 * 在需要刷新视图的时候调用serializeKeyCodes2and3序列化保存一下，然后调用reinflate填充视图和更新toucharea，toucharea在tscWidget的onLayout的时候调用create的时候会被添加（会不会在更新之前就调用create了？）
 */
public class FalloutTouchScreenControlsFactory2 implements TouchScreenControlsFactory {
    private static final String TAG = "FalloutTSCFactory2";
    KeyCodes2 mKeyCodes2;
    KeyCodes3 mKeyCodes3;
    private GestureContext gestureContext;
    private BtnContainer mBtnContainer;
    private LinearLayout mLeftBar;
    private LinearLayout mRightBar;

    /**
     * 用于存储toucharea的列表，从uiOverlay那边更新。
     */
    private final List<BtnTouchArea> mBtnAreaList = new ArrayList<>();

    @Override // com.eltechs.axs.TouchScreenControlsFactory
    public boolean hasVisibleControls() {
        return false;
    }

    /**
     * 按钮自由位置的时候，设置用于显示按钮的布局容器
     * 以及左右侧栏的布局容器
     * 填充内容按钮都在factory里完成
     */
    public void setControlContainers(
            @NonNull BtnContainer emptyBtnContainer, @NonNull LinearLayout leftToolbar, @NonNull LinearLayout rightToolbar) {
        mBtnContainer = emptyBtnContainer;
        mLeftBar = leftToolbar;
        mRightBar = rightToolbar;
        //如果已经获取实例，就不重新获取了，保证其他人要用都从自己这里拿实例，这样确保自己这是最新的。
        Context c = emptyBtnContainer.getContext();
        try {
            if (mKeyCodes2 == null)
                mKeyCodes2 = KeyCodes2.deserialize(new File(c.getFilesDir(), KeyCodes2.KeyStoreFileName));
            else
                KeyCodes2.serialize(mKeyCodes2, new File(c.getFilesDir(), KeyCodes2.KeyStoreFileName));
        } catch (IOException | ClassNotFoundException e) {
            if (mKeyCodes2 == null) mKeyCodes2 = new KeyCodes2();
            Log.d(TAG, "create: KeyCodes2(反)序列化文件失败");
        }
        try {
            if (mKeyCodes3 == null)
                mKeyCodes3 = KeyCodes3.deserialize(new File(c.getFilesDir(), KeyCodes3.KeyStoreFileName));
            else
                KeyCodes3.serialize(mKeyCodes3, new File(c.getFilesDir(), KeyCodes3.KeyStoreFileName));
        } catch (IOException | ClassNotFoundException e) {
            if (mKeyCodes3 == null) mKeyCodes3 = new KeyCodes3();
            Log.d(TAG, "create: KeyCodes3(反)序列化文件失败");
        }
    }

    @Override // com.eltechs.axs.TouchScreenControlsFactory
    public TouchScreenControls create(View view, ViewOfXServer viewOfXServer) {
        Log.d(TAG, "create: factory新创建了TouchScreenControls");
        GraphicsSceneConfigurer graphicsSceneConfigurer = new GraphicsSceneConfigurer();
        graphicsSceneConfigurer.setSceneViewport(0.0f, 0.0f, view.getWidth(), view.getHeight());
        TouchScreenControls touchScreenControls = new TouchScreenControls(graphicsSceneConfigurer);
        fillTouchScreenControls(touchScreenControls, view, viewOfXServer);
        return touchScreenControls;
    }

    private void fillTouchScreenControls(TouchScreenControls touchScreenControls, View view, ViewOfXServer viewOfXServer) {
        DisplayMetrics displayMetrics = AndroidHelpers.getDisplayMetrics();
//        if (view.getWidth() <= displayMetrics.widthPixels / 2) {
//            return;
//        }
        TouchEventMultiplexor touchEventMultiplexor = new TouchEventMultiplexor();
        TouchArea touchArea = new TouchArea(0.0f, 0.0f, view.getWidth(), view.getHeight(), touchEventMultiplexor);
        //手势操作
        if (viewOfXServer != null) {
            this.gestureContext = GestureMachineMix.create(viewOfXServer, touchArea, touchEventMultiplexor, displayMetrics.densityDpi);
            //GestureMachineConfigurerFallout2.createGestureContext(viewOfXServer, touchArea, touchEventMultiplexor, displayMetrics.densityDpi, () -> ((XServerDisplayActivity) ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity()).showPopupMenu());
        }
        //按钮布局和范围相关在reinflate函数里设置好，在这里直接添加上
        touchScreenControls.add(new BtnAndTouchScreenControl(mBtnAreaList, touchArea));
    }

    public KeyCodes3 getKeyCodes3() {
        return mKeyCodes3;
    }

    public KeyCodes2 getKeyCodes2() {
        return mKeyCodes2;
    }


    public void serializeKeyCodes2and3(Context c) {
        try {
            KeyCodes2.serialize(mKeyCodes2, new File(c.getFilesDir(), KeyCodes2.KeyStoreFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            KeyCodes3.serialize(mKeyCodes3, new File(c.getFilesDir(), KeyCodes3.KeyStoreFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改按键后，重新填充按钮
     * （先序列化存储当前的model。再清空充填布局容器）
     *
     * @param c
     * @param viewOfXServer
     */
    public void reinflateControlLayout(Context c, ViewOfXServer viewOfXServer) {
        //先序列化存储当前的model。再清空充填布局容器
        serializeKeyCodes2and3(c);
        //先清空全部按键
        mBtnContainer.removeAllViews();
        mLeftBar.removeAllViews();
        mRightBar.removeAllViews();
        mBtnAreaList.clear();
        //填充布局期间先设置成隐藏吧，不然会触发tscWidget的onLayout那就无限循环创建了
//        mBtnContainer.setVisibility(GONE);
//        mLeftBar.setVisibility(GONE);
//        mRightBar.setVisibility(GONE);
        //如果之前设置了隐藏状态，然后退出到后台，再次切换到前台的时候重新构建布局，这时候应该不显示按键
        XServerDisplayActivityInterfaceOverlay ui = ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
        if(ui instanceof FalloutInterfaceOverlay2 && !((FalloutInterfaceOverlay2) ui).isSidePanelsVisible()){
            return;
        }

        boolean isBtnFreePos = BaseFragment.getPreference().getBoolean(PREF_KEY_CUSTOM_BTN_POS, false);
        //填充自由位置按键或两侧按键
        if (isBtnFreePos) {
            inflateBtnContainer(mBtnAreaList, c, viewOfXServer);
//            //填充完再显示上
//            mBtnContainer.setVisibility(VISIBLE);

        } else {
            inflateSidebar(mBtnAreaList, c, viewOfXServer);
//            mLeftBar.setVisibility(VISIBLE);
//            mRightBar.setVisibility(VISIBLE);
        }

    }

    private void inflateSidebar(List<BtnTouchArea> btnAreaList, Context c, ViewOfXServer viewOfXServer) {

        SharedPreferences sp = c.getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        for (int i = 0; i < 2; i++) {
            LinearLayout linearLayout = i == 0 ? mLeftBar : mRightBar;
            linearLayout.setBackgroundColor(sp.getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK));

//            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //如果有自定义按键，用自定义的
            for (OneCol oneCol : i == 0 ? mKeyCodes2.getLeftSide() : mKeyCodes2.getRightSide()) {
                ScrollView oneColScroll = new ScrollView(c);
                oneColScroll.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
                LinearLayout oneColLinear = new LinearLayout(c);
                oneColLinear.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
                oneColLinear.setOrientation(VERTICAL);
                for (OneKey oneKey : oneCol.getmAllKeys())
                    oneColLinear.addView(new UnmovableBtn(c, oneKey, viewOfXServer));
                oneColScroll.addView(oneColLinear);
                linearLayout.addView(oneColScroll);
            }
//            if (!this.isToolbarsVisible) {
//                linearLayout.setVisibility(GONE);
//            }
        }

    }

    private void inflateBtnContainer(List<BtnTouchArea> btnAreaList, Context c, ViewOfXServer viewOfXServer) {
        SharedPreferences sp = c.getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        int width = sp.getInt(PREF_KEY_BTN_WIDTH, -2);
        int height = sp.getInt(PREF_KEY_BTN_HEIGHT, -2);


        for (OneKey oneKey : mKeyCodes3.getKeyList()) {
            //只有勾选的按钮才添加到布局中
            if (!oneKey.isShow())
                continue;
            RegularKeyBtn btn = new RegularKeyBtn(c, oneKey, viewOfXServer);
            //添加视图
            mBtnContainer.addView(btn);
            //添加对应的toucharea
            BtnTouchArea btnTouchArea = new BtnTouchArea(oneKey.getMarginLeft(), oneKey.getMarginTop(), width, height, new BtnKeyPressAdapter(btn));
            btnAreaList.add(btnTouchArea);
            //自适应宽高是-2，那就只能在显示之后获取区域再更新了
            if (width == -2 || height == -2) {
                btn.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> btnTouchArea.updateArea(left, top, right, bottom));
            }
        }
        //添加摇杆
        for (JoyStickBtn.Params joyParams : mKeyCodes3.getJoyList()) {
            JoyStickBtn joyStickBtn = new JoyStickBtn(c, joyParams);
            joyStickBtn.setViewFacade(viewOfXServer);
            //添加视图
            mBtnContainer.addView(joyStickBtn);
            //添加对应toucharea
            btnAreaList.add(new BtnTouchArea(joyParams.getMarginLeft(), joyParams.getMarginTop(), joyStickBtn.getLayoutParams().width / 2f, new BtnKeyPressAdapter(joyStickBtn)));
//                Log.d(TAG, "reAddBtnsFromKeys: 摇杆的margin 左="+((FrameLayout.LayoutParams)joyStickBtn.getLayoutParams()).leftMargin+" 上="+((FrameLayout.LayoutParams)joyStickBtn.getLayoutParams()).topMargin);
        }
    }

    /**
     * 隐藏按键布局的时候，toucharea也要删去
     */
    public void hideControlPanelsTouchArea(){
        for(BtnTouchArea area:mBtnAreaList){
            area.updateArea(0,0,0,0);
        }
    }


}
