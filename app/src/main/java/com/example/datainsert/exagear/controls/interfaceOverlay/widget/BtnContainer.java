package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.CustomControls;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.model.KeyCodes3;

import java.io.File;
import java.io.IOException;

/**
 * 用于自定义按键位置，编辑时的外层布局,覆盖到framelayout最上层.
 * 如果要进入/退出编辑状态，请调用setEditing或switchEditing
 *
 * 如果在容器内，编辑时显示详细编辑的dialog，可以
 */
public class BtnContainer extends FrameLayout {
    //   自身视图id BTNCONTAINER_RESOURCE_ID = 0x7f095123;//可以通过这个id寻找此视图
    public static final String TAG="BtnContainer";



    public BtnContainer(@NonNull Context context) {
        super(context);
//        mViewOfXServer = viewOfXServer;
//        this.mViewFacade = viewOfXServer!=null?viewOfXServer.getXServerFacade():null;
//        serFile = new File(getContext().getFilesDir(), KeyCodes3.KeyStoreFileName);
//        //先尝试反序列读取之前的设置，如果没有再新建
//        try {
//            mKeyCodes3 = KeyCodes3.deserialize(serFile);
//        } catch (IOException | ClassNotFoundException e) {
//            mKeyCodes3=new KeyCodes3();
//            Log.d(TAG, "buildUI: 反序列化文件失败");
//        }

        setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
//        setTag(mKeyCodes3);

        //反序列化，初始化添加按钮
//        reAddBtnsFromKeys();

        setId(RR.BTNCONTAINER_RESOURCE_ID);
        //设置点击事件 View::showContextMenu
        setOnClickListener(View::showContextMenu);
        setOnCreateContextMenuListener(this::buildMenu);
    }


//    /**
//     * 设置是否进入编辑状态。编辑状态下点击空白处弹出菜单。退出编辑时会序列化当前设置
//     * 退出编辑时应该调用.(修改按键个数和初始化时会调用addBtnForKeys，这个函数调用setEditing就行了）
//     * 同时会设置按钮的状态，设置对应的点击事件
//     */
//    public void setEditing(boolean editing) {
//        isEditing = editing;
//        //自身点击菜单
//
//        setOnClickListener(isEditing?View::showContextMenu:null);
//        setOnCreateContextMenuListener(isEditing?this::buildMenu:null);
//        setClickable(isEditing);
//        setLongClickable(isEditing);
//        //设置按钮是否移动。以及点击事件
//        for(int i=0; i<getChildCount(); i++){
//            if(getChildAt(i) instanceof RegularKeyBtn)
//                ((RegularKeyBtn) getChildAt(i)).setEditing(isEditing);
//            else if(getChildAt(i) instanceof JoyStickBtn)
//                ((JoyStickBtn) getChildAt(i)).setEditing(isEditing);
//
//            assert getChildAt(i) instanceof RegularKeyBtn ||getChildAt(i) instanceof JoyStickBtn;
//        }
//        //如果变为非编辑状态，序列化，
//        if(!isEditing){
//            try {
//                KeyCodes3.serialize(mKeyCodes3, serFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 点击空白处时，显示菜单：添加/删除按钮、 退出编辑
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    private void buildMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "buildMenu: 获取到的id为"+getId());
        menu.add(getS(RR.cmCtrl_editMenu1Dialog)).setOnMenuItemClickListener(item -> {
//            createSelectKeysDialog();
            CustomControls fragment = new CustomControls();
            fragment.show(((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getSupportFragmentManager(),CustomControls.TAG);
            return true;
        });
        //退出编辑的时候序列化keycode3
        menu.add(getS(RR.cmCtrl_editMenu2Exit)).setOnMenuItemClickListener(item -> {
            //将自己从父布局中移除？
            FalloutInterfaceOverlay2 ui = (FalloutInterfaceOverlay2) ((XServerDisplayActivityConfigurationAware) Globals.getApplicationState()).getXServerDisplayActivityInterfaceOverlay();
            ui.endEditing();
            return true;
        });
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//
//        Log.d(TAG, String.format("onLayout: 布局宽高变化 %d,%d,%d,%d",left,top,right,bottom));
//
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.d(TAG, String.format("onMeasure: 布局测量宽高 %d,%d",widthMeasureSpec,heightMeasureSpec));
//
//    }
    //    /**
//     * 创建一个包含AvailableKeysView的dialog，关闭时会更新自身的keycodes3
//     * 可以从外部调用
//     */
//    public void createSelectKeysDialog(){
//        AvailableKeysView allKeysView = new AvailableKeysView(getContext(), getSelectedCondition(),mKeyCodes3.getJoyList().size());
//        allKeysView.showWithinDialog((dialog, which) -> {
//            //修改按键个数。判断当前按键是否已存在，不存在的话，按keycode大小插入进去吧
////                AvailableKeysView.updateKeyCodes3(mKeyCodes3, allKeysView);
//            for (int i = 0; i < mKeyCodes3.getKeyList().size(); i++) {
//                OneKey oneKey = mKeyCodes3.getKeyList().get(i);
//                //如果由隐藏变为显示，那么属性初始化
//                if(!oneKey.isShow() && allKeysView.keySelect[i]){
//                    oneKey.setMarginTop(0);
//                    oneKey.setMarginLeft(0);
//                }
//                oneKey.setShow(allKeysView.keySelect[i]);
//            }
//            //摇杆按键个数同步
//            while(allKeysView.joyStickNum<mKeyCodes3.getJoyList().size()){
//                mKeyCodes3.getJoyList().remove(mKeyCodes3.getJoyList().size()-1);
//            }
//            while(allKeysView.joyStickNum>mKeyCodes3.getJoyList().size()){
//                mKeyCodes3.getJoyList().add(new JoyStickBtn.Params());
//            }
//
//            //删除现有按钮。再重新添加
//            reAddBtnsFromKeys();
//        });
//    }

//    /**
//     * //删除全部子布局（按键），然后根据现有数据，添加按键
//     */
//    public void reAddBtnsFromKeys(){
//        removeAllViews();
//        for (OneKey oneKey : mKeyCodes3.getKeyList()) {
//            if (!oneKey.isShow())
//                continue;
//            //只有勾选的按钮才添加到布局中
//            RegularKeyBtn btn = new RegularKeyBtn(getContext(), oneKey, mViewOfXServer);
//            btn.setupStyle();
//            addView(btn);
//        }
//        //添加摇杆
//        for(JoyStickBtn.Params joyParams:mKeyCodes3.getJoyList()){
//            JoyStickBtn joyStickBtn = new JoyStickBtn(getContext(),joyParams);
//            joyStickBtn.setViewFacade(mViewOfXServer);
//            addView(joyStickBtn);
//            Log.d(TAG, "reAddBtnsFromKeys: 摇杆的margin 左="+((LayoutParams)joyStickBtn.getLayoutParams()).leftMargin+" 上="+((LayoutParams)joyStickBtn.getLayoutParams()).topMargin);
//        }
////        setEditing(isEditing);
//    }
//
//    private boolean[] getSelectedCondition() {
//        boolean[] condition = new boolean[mKeyCodes3.getKeyList().size()];
//
//        for (int i = 0; i < mKeyCodes3.getKeyList().size(); i++) {
//            condition[i] = mKeyCodes3.getKeyList().get(i).isShow();
//        }
//        return condition;
//    }

}
