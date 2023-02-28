package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import static com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment.getOneLineWithTitle;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_FILE_NAME_SETTING;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ALPHA;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_TXT_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.eltechs.axs.Finger;
import com.eltechs.axs.Globals;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.activities.XServerDisplayActivity;
import com.eltechs.axs.activities.XServerDisplayActivityInterfaceOverlay;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;
import com.example.datainsert.exagear.FAB.dialogfragment.BaseFragment;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controls.interfaceOverlay.FalloutInterfaceOverlay2;
import com.example.datainsert.exagear.controls.model.OneKey;

import java.util.Locale;

public class RegularKeyBtn extends BaseMoveBtn {
    private static final String TAG = "RegularKeyBtn";
    private final OneKey mOneKey;
    //    private InjectKeyListener injectKeyListener; //非编辑状态下，点击按钮触发的按键操作
//    private final GestureDetectorCompat myGestureListener;
    private final ViewFacade mViewFacade;

    private int touchAreaLength = 60;


    private boolean isModifierPress = false; //修饰键，用来判断应该按下还是松开
    //用于修饰键切换状态时，反转颜色
    private int mBgColor = Color.WHITE;
    private int mTxColor = Color.BLACK;

    public RegularKeyBtn(Context context, OneKey oneKey, ViewOfXServer viewOfXServer) {
        super(context);

//        setLayoutParams(new ViewGroup.LayoutParams(-2,-2));
        mOneKey = oneKey;
        this.mViewFacade = viewOfXServer==null?null:viewOfXServer.getXServerFacade();
//        myGestureListener = new GestureDetectorCompat(getContext(), new MyGestureListener());

        //先不在内部初始化样式了。之后写个public函数，传入是否是自定义位置的。然后从外部调用进行初始化？
        setupStyle();
    }

    /**
     * 设置样式（颜色，文字，位置等）。应该在添加到父布局前调用一次
     */
    private void setupStyle() {
        SharedPreferences sp = getContext().getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        mBgColor = sp.getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE);
        mTxColor = sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK);
        this.setText(mOneKey.getName());
        this.setTag(mOneKey);
        //设置按钮背景样式
        this.setBackgroundTintList(ColorStateList.valueOf(mBgColor));
        this.setTextColor(mTxColor);
        RippleDrawable spdrawable = (RippleDrawable) this.getBackground();
        spdrawable.setColor(ColorStateList.valueOf(sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK) & 0x50ffffff));
        spdrawable.setAlpha(sp.getInt(PREF_KEY_BTN_ALPHA, 255));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sp.getInt(PREF_KEY_BTN_WIDTH, -2), sp.getInt(PREF_KEY_BTN_HEIGHT, -2));
        params.setMargins(mOneKey.getMarginLeft(), mOneKey.getMarginTop(), 0, 0);
        this.setLayoutParams(params);
    }

    public static void setupStyleOnly(Button btn){
        SharedPreferences sp = btn.getContext().getSharedPreferences(PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        int mBgColor = sp.getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE);
        int mTxColor = sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK);
        //设置按钮背景样式
        btn.setBackgroundTintList(ColorStateList.valueOf(mBgColor));
        btn.setTextColor(mTxColor);
        RippleDrawable spdrawable = (RippleDrawable) btn.getBackground();
        spdrawable.setColor(ColorStateList.valueOf(sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK) & 0x50ffffff));
        spdrawable.setAlpha(sp.getInt(PREF_KEY_BTN_ALPHA, 255));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sp.getInt(PREF_KEY_BTN_WIDTH, -2), sp.getInt(PREF_KEY_BTN_HEIGHT, -2));
        btn.setLayoutParams(params);
    }

//    public void setInjectKeyListener(InjectKeyListener injectKeyListener) {
//        this.injectKeyListener = injectKeyListener;
//    }

    @Override
    protected void updateModelMargins(int marginLeft, int marginTop) {
        mOneKey.setMarginLeft(marginLeft); //getleft()+mLastX
        mOneKey.setMarginTop(marginTop);
    }

    @Override
    public void injectPress(Finger finger) {
        Log.d(TAG, "injectPress: 按下键"+mOneKey.getName());
         /*
        按键输入，如果是shift这种，按下就触发，交给ontouch处理，否则交给onclick处理
        不如先都交给onclick处理吧。修饰键点击时只按下或抬起，普通键点击时一次完整的按下抬起。
         */
        if(mViewFacade==null)
            return;
        if (isModifierKey()) {
            if (!isModifierPress)
                mViewFacade.injectKeyPress((byte) (mOneKey.getCode() + 8));
            else
                mViewFacade.injectKeyRelease((byte) (mOneKey.getCode() + 8));
            //修饰键，显示颜色反转
            setBackgroundTintList(ColorStateList.valueOf(isModifierPress ? mBgColor : mTxColor));
            setTextColor(isModifierPress ? mTxColor : mBgColor);
        } else {
            mViewFacade.injectKeyPress((byte) (mOneKey.getCode() + 8));
        }
        isModifierPress = !isModifierPress;
    }

    @Override
    public void injectMove(Finger finger) {

    }

    @Override
    public void injectRelease(Finger finger) {
        //仅供调试
        if(mOneKey.getName().equalsIgnoreCase("ESC") && Globals.getAppContext().getPackageName().equals("com.ewt45.exagearsupportv7")){
            XServerDisplayActivity.myUIOverlayForTest.startEditing();
        }
        if(mViewFacade==null)
            return;
        if(!isModifierKey()){
            mViewFacade.injectKeyRelease((byte) (mOneKey.getCode() + 8));
        }

    }

    //编辑时点击
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine();
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setText(mOneKey.getName());
        editText.setLayoutParams(new ViewGroup.LayoutParams(QH.px(getContext(),100),-2));
        LinearLayout rootView = getOneLineWithTitle(getContext(),"重命名",editText,false);
        int padding = QH.px(getContext(), RR.attr.dialogPaddingDp);
        rootView.setPadding(padding,0,padding,0);
        new AlertDialog.Builder(getContext())
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    mOneKey.setName(editText.getText().toString());
                    setText(editText.getText().toString());
                })
                .setTitle("按钮属性")
                .setNegativeButton(android.R.string.cancel, null)
                .setView(rootView).create().show();
    }


    public OneKey getmOneKey() {
        return mOneKey;
    }

    /**
     * 判断这个按钮对应的按键是否为修饰键。如果是的话按键输入用ontouch处理（按下时就注入press），否则用click处理
     * 先都由onclick处理吧
     */
    public boolean isModifierKey() {
        int key = mOneKey.getCode() + 8;
        return key == KeyCodesX.KEY_SHIFT_LEFT.getValue() || key == KeyCodesX.KEY_SHIFT_RIGHT.getValue()
                || key == KeyCodesX.KEY_CONTROL_LEFT.getValue() || key == KeyCodesX.KEY_CONTROL_RIGHT.getValue()
                || key == KeyCodesX.KEY_ALT_LEFT.getValue() || key == KeyCodesX.KEY_ALT_RIGHT.getValue();
    }


    //鸿蒙的点击监听还是有问题。直接用gesture判断吧。给每个
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
//            Log.d(DEBUG_TAG,"onDown: " + event.toString());

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return RegularKeyBtn.this.performClick();
        }

        //现在貌似双击只会触发一次点击。。。是这个的缘故吗（还真是。。。）
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return RegularKeyBtn.this.performClick();
        }

    }


}
