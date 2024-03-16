package com.example.datainsert.exagear.controlsV2;

import static android.support.v4.view.InputDeviceCompat.SOURCE_STYLUS;
import static android.view.InputDevice.SOURCE_MOUSE;
import static android.view.InputDevice.SOURCE_TOUCHSCREEN;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_MULTIPLE;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_BUTTON_L1;
import static android.view.KeyEvent.KEYCODE_BUTTON_L2;
import static android.view.KeyEvent.KEYCODE_BUTTON_R1;
import static android.view.KeyEvent.KEYCODE_BUTTON_R2;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_UNKNOWN;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.axs.AndroidKeyReporter;
import com.example.datainsert.exagear.controlsV2.axs.XKeyButton;
import com.example.datainsert.exagear.controlsV2.edit.EditConfigWindow;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneProfile;
import com.example.datainsert.exagear.controlsV2.options.OptionsProvider;
import com.example.datainsert.exagear.controlsV2.widget.TransitionHistoryView;

public class TouchAreaView extends FrameLayout implements View.OnKeyListener {

    private static final String TAG = "TouchAreaView";
    final int MAX_FINGERS = 10;
    private final Finger[] userFingers = new Finger[MAX_FINGERS];
    private final Paint mFramePaint = new Paint();
//    private final Keyboard mKeyboard;
    Mouse mMouse = new Mouse();
    //TODO 添加新toucharea的时候，应该插入到0的位置。然后遍历的时候先遍历到。手势区域应该放在最后一个。
    private OneProfile mProfile;
    private EditConfigWindow mEditWindow; //编辑模式下的编辑视图根窗口
    private TransitionHistoryView mTvGestureHistory; //显示

    public TouchAreaView(@NonNull Context context) {
        super(context);
//        this.xServerFacade = viewOfXServer==null?null:viewOfXServer.getXServerFacade();
//        this.mouse = viewOfXServer==null?null:new Mouse(new PointerEventReporter(viewOfXServer));
//        this.mKeyboard = new Keyboard(new KeyEventReporter(viewFacade));
//        this.configuration = touchScreenControlsInputConfiguration;

//        setLayerType(LAYER_TYPE_HARDWARE,null);

        setId(View.generateViewId());
        setWillNotDraw(false); //设置为false，否则onDraw不会被调用
        setOnKeyListener(this);

        requireFocus();

        mFramePaint.setStrokeWidth(4);
        mFramePaint.setColor(Color.RED);
    }



    /**
     * 获取焦点。用于拦截按键
     */
    public void requireFocus() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setFocusedByDefault(true);
        requestFocus();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (Const.detailDebug) {
            canvas.drawLine(0, 0, getWidth(), 0, mFramePaint);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mFramePaint);
            canvas.drawLine(getWidth(), getHeight(), 0, getHeight(), mFramePaint);
            canvas.drawLine(0, getHeight(), 0, 0, mFramePaint);

        }
        for (TouchArea<?> touchArea : mProfile.getTouchAreaList())
            touchArea.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        int action = keyEvent.getAction();
        if (keyEvent.getSource() == SOURCE_MOUSE) {
            if (keyCode == KEYCODE_BACK) {
                if (action == ACTION_DOWN) {
                    Const.getXServerHolder().injectPointerButtonPress(3);
                } else if (action == ACTION_UP) {
                    Const.getXServerHolder().injectPointerButtonRelease(3);
                }
            }
            return true;
        } else if (keyCode == KEYCODE_MENU && keyEvent.getAction() == ACTION_UP) {
            handleShowAllOptions();
            return true;
        } else {
            if (keyCode == KEYCODE_BACK) {// && this.configuration.backKeyAction == SHOW_POPUP_MENU
                if (action == ACTION_UP)
                    handleShowAllOptions();
                return true;
            } else if (keyCode == KEYCODE_DPAD_CENTER) {
                if (action == ACTION_DOWN) {
                    Const.getXServerHolder().injectPointerButtonPress(3);
                } else if (action == ACTION_UP) {
                    Const.getXServerHolder().injectPointerButtonRelease(3);
                }
                return true;
            } else if (keyCode == KEYCODE_BUTTON_L1 || keyCode == KEYCODE_BUTTON_L2) {
                if(action == ACTION_DOWN)
                    Const.getXServerHolder().injectKeyPress(XKeyButton.key_left_shift.xKeyCode);
                else if(action == ACTION_UP)
                    Const.getXServerHolder().injectKeyRelease(XKeyButton.key_left_shift.xKeyCode);
                return true;
            } else if (keyCode == KEYCODE_BUTTON_R1 || keyCode == KEYCODE_BUTTON_R2) {
                if(action == ACTION_DOWN)
                    Const.getXServerHolder().injectKeyPress(XKeyButton.key_left_ctrl.xKeyCode);
                else if(action == ACTION_UP)
                    Const.getXServerHolder().injectKeyRelease(XKeyButton.key_left_ctrl.xKeyCode);
                return true;
            }
            else if(keyCode != KEYCODE_UNKNOWN || action == ACTION_MULTIPLE){
                return AndroidKeyReporter.handleAKeyEvent(
                        action,keyCode,keyEvent.getUnicodeChar(),keyEvent.getCharacters());
            }
            else {
                return false;
            }
        }
    }

    /**
     * 当按下返回键/菜单键时，应该显示全部选项。如果处于编辑模式下，则改为显示“是否退出编辑”的对话框
     */
    private void handleShowAllOptions() {
        if (!mProfile.isEditing())
            OptionsProvider.getOption(OptionsProvider.OPTION_SHOW_ALL_OPTIONS).run();
        else
            TestHelper.showConfirmDialog(getContext(), RR.getS(RR.ctr2_editExitConfirm), (dialog, which) -> exitEdit());

    }


    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        int source = motionEvent.getSource() & SOURCE_TOUCHSCREEN;
        boolean isStylus = (motionEvent.getSource() & SOURCE_STYLUS) == SOURCE_STYLUS;
        boolean isMouse = (motionEvent.getSource() & SOURCE_MOUSE) == SOURCE_MOUSE;
        return isStylus || isMouse
                ? this.mMouse.handleMouseEvent(motionEvent)
                : super.onGenericMotionEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean isTouchScreen = (motionEvent.getSource() & SOURCE_TOUCHSCREEN) == SOURCE_TOUCHSCREEN;
        boolean isStylus = (motionEvent.getSource() & SOURCE_STYLUS) == SOURCE_STYLUS;
        boolean isMouse = (motionEvent.getSource() & SOURCE_MOUSE) == SOURCE_MOUSE;
        if (isTouchScreen || isStylus)
            return handleTouchEvent(motionEvent);
        if (isMouse)
            return this.mMouse.handleMouseEvent(motionEvent);
        else
            return super.onTouchEvent(motionEvent);
    }

    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);
        int actionMasked = motionEvent.getActionMasked();
        if (pointerId >= MAX_FINGERS) {
            return true;
        }

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                this.userFingers[pointerId] = new Finger(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                handleFingerDown(this.userFingers[pointerId]);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (this.userFingers[pointerId] != null) {
                    this.userFingers[pointerId].release(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                    handleFingerUp(this.userFingers[pointerId]);
                    this.userFingers[pointerId] = null;
                    break;
                }
                break;

            //TODO 为什么这俩要遍历所有的finger呢，只处理那一个不行吗 （啊貌似pointerId永远对应最后按下的那一根手指，其他手指接收不到事件了）（同时间多个手指的变化只会发送一次事件，所以不能一次只处理一根手指）
            case MotionEvent.ACTION_MOVE:
//                if (this.userFingers[pointerId] != null) {
//                    this.userFingers[pointerId].update(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
//                    handleFingerMove(this.userFingers[pointerId]);
//                    break;
//                }
                for (int i = 0; i < MAX_FINGERS; i++) {
                    if (this.userFingers[i] != null) {
                        int findPointerIndex = motionEvent.findPointerIndex(i);
                        if (findPointerIndex >= 0) {
                            this.userFingers[i].update(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                            handleFingerMove(this.userFingers[i]);
                        } else {
                            handleFingerUp(this.userFingers[i]);
                            this.userFingers[i] = null;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                //同时间多个手指的变化只会发送一次事件，所以不能一次只处理一根手指
//                if (this.userFingers[i] != null) {
//                    handleFingerUp(this.userFingers[i]);
//                    this.userFingers[i] = null;
//                }
                for (int i = 0; i < MAX_FINGERS; i++) {
                    if (this.userFingers[i] != null) {
                        handleFingerUp(this.userFingers[i]);
                        this.userFingers[i] = null;
                    }
                }
                break;
        }
        //要不要invalidate传入相应那个area的范围，只修改某一个区域，能减少点消耗（阿这 传入边界的犯法已经被废弃了）
        invalidate();//按下时按钮背景会变化，移动时摇杆要移动，所以需要重绘
        return true;
    }

    //TODO TouchScreenControlsWidget在这里重新构建了手势区域（手势context需要用到area的宽高吧）
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 发现一个停留在同一个区域，或移出一个区域并进入另一个区域，则停止遍历
     * <br/> 要求一个手指最多只能由一个触摸区域处理
     */
    private void handleFingerMove(Finger userFinger) {
        int tmp = 0;
        for (TouchArea<?> touchArea : mProfile.getTouchAreaList()) {
            int handled = touchArea.handleFingerMove(userFinger);
            if (handled == TouchArea.HANDLED_KEEP)
                return;
            else if ((tmp | handled) == (TouchArea.HANDLED_ADD | TouchArea.HANDLED_REMOVE))
                return;
        }
    }

    /**
     * 发现移出一个区域，则停止遍历
     */
    private void handleFingerUp(Finger userFinger) {
        for (TouchArea<?> touchArea : mProfile.getTouchAreaList()) {
            if (TouchArea.HANDLED_REMOVE == touchArea.handleFingerUp(userFinger))
                return;
        }
    }

    /**
     * 只要有一个区域接收了，就不再管其他区域
     */
    private void handleFingerDown(Finger userFinger) {
        for (TouchArea<?> touchArea : mProfile.getTouchAreaList()) {
            if (TouchArea.HANDLED_ADD == touchArea.handleFingerDown(userFinger))
                return;
        }
    }

    /**
     * 进入编辑模式
     */
    public void startEdit() {
        if (mEditWindow != null)
            return;
        Context c = getContext();
        mProfile.syncAreaList(true);

        //手势历史的textview
        mTvGestureHistory = new TransitionHistoryView(c);
        mTvGestureHistory.setVisibility(GONE);
        FrameLayout.LayoutParams param = new LayoutParams(-1, -1);
//        param.topMargin = 40;
        addView(mTvGestureHistory, param);

        mEditWindow = new EditConfigWindow(c);
        addView(mEditWindow);
    }

    public EditConfigWindow getEditWindow() {
        return mEditWindow;
    }

    public TransitionHistoryView getGestureHistoryTextView() {
        return mTvGestureHistory;
    }

    ;

    /**
     * 退出编辑模式，顺便保存配置
     */
    public void exitEdit() {
        if (mEditWindow == null)
            return;
        removeView(mEditWindow);
        removeView(mTvGestureHistory);
        mEditWindow = null;
        mTvGestureHistory = null;
        //退出编辑模式的时候顺便保存一下配置吧
        ModelProvider.saveProfile(mProfile);
        mProfile.syncAreaList(false);

        //重新获取一下焦点试试？为啥退出之后再返回是fragment会退出呢
        requireFocus();
    }

    public OneProfile getProfile() {
        return mProfile;
    }

    /**
     * 设置为当前的配置OneProfile，并同步touchArea
     */
    public void setProfile(OneProfile profile) {
        boolean isEditing = mProfile != null && mProfile.isEditing();
        mProfile = profile;
        mProfile.syncAreaList(isEditing);
        //TODO 还应该同步profile里存储的全局属性，比如屏幕按键显隐，鼠标移动速度等
    }


}

