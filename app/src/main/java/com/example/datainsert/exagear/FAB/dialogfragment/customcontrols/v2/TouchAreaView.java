package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import static android.support.v4.view.InputDeviceCompat.SOURCE_STYLUS;
import static android.view.InputDevice.SOURCE_MOUSE;
import static android.view.InputDevice.SOURCE_TOUCHSCREEN;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.ClickAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit.EditConfigWindow;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneProfile;

public class TouchAreaView extends FrameLayout {

    final int MAX_FINGERS = 10;
    //TODO 添加新toucharea的时候，应该插入到0的位置。然后遍历的时候先遍历到。手势区域应该放在最后一个。
    private final OneProfile mProfile;
    private final Finger[] userFingers = new Finger[MAX_FINGERS];
    Mouse mMouse = new Mouse();

    public TouchAreaView(@NonNull Context context) {
        super(context);
//        this.xServerFacade = viewOfXServer==null?null:viewOfXServer.getXServerFacade();
//        this.mouse = viewOfXServer==null?null:new Mouse(new PointerEventReporter(viewOfXServer));
//        this.keyboard = new Keyboard(new KeyEventReporter(this.xServerFacade));
//        this.configuration = touchScreenControlsInputConfiguration;

//        setLayerType(LAYER_TYPE_HARDWARE,null);
        setBackgroundResource(R.drawable.someimg);
        setWillNotDraw(false); //设置为false，否则onDraw不会被调用
        setFocusable(true);
        setFocusableInTouchMode(true);
        installKeyListener();

        mProfile = new OneProfile();
        mProfile.addArea(this,new OneGestureArea(), model -> {});

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        for (TouchArea<?> touchArea : mProfile.getTouchAreaList())
            touchArea.onDraw(canvas);
        super.onDraw(canvas);

    }

    private void installKeyListener() {
//        setOnKeyListener((view, keyCode, keyEvent) -> {
//            if (keyEvent.getSource() == SOURCE_MOUSE) {
//                if (keyCode == KEYCODE_BACK) {
//                    if (keyEvent.getAction() == ACTION_DOWN) {
//                        this.xServerFacade.injectPointerButtonPress(3);
//                    } else if (keyEvent.getAction() == ACTION_UP) {
//                        this.xServerFacade.injectPointerButtonRelease(3);
//                    }
//                }
//                return true;
//            } else if (keyCode != KEYCODE_MENU || keyEvent.getAction() != ACTION_UP) {
//                if (keyCode == KEYCODE_BACK && this.configuration.backKeyAction == SHOW_POPUP_MENU) {
//                    if (keyEvent.getAction() == 1) {
//                        getHost().showPopupMenu();
//                    }
//                    return true;
//                } else if (keyCode == KEYCODE_DPAD_CENTER) {
//                    if (keyEvent.getAction() == 0) {
//                        this.xServerFacade.injectPointerButtonPress(3);
//                    } else if (keyEvent.getAction() == 1) {
//                        this.xServerFacade.injectPointerButtonRelease(3);
//                    }
//                    return true;
//                } else if (keyCode == 102 || keyCode == 104) {
//                    if (keyEvent.getAction() == 0) {
//                        TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
//                    } else if (keyEvent.getAction() == 1) {
//                        TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
//                    }
//                    return true;
//                } else if (keyCode == 103 || keyCode == 105) {
//                    if (keyEvent.getAction() == 0) {
//                        TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
//                    } else if (keyEvent.getAction() == 1) {
//                        TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
//                    }
//                    return true;
//                } else if (keyCode != 0) {
//                    if (keyEvent.getAction() == 0) {
//                        return TouchScreenControlsInputWidget.this.keyboard.handleKeyDown(keyCode, keyEvent);
//                    }
//                    if (keyEvent.getAction() == 1) {
//                        return TouchScreenControlsInputWidget.this.keyboard.handleKeyUp(keyCode, keyEvent);
//                    }
//                    return false;
//                } else if (keyEvent.getAction() == 2) {
//                    return TouchScreenControlsInputWidget.this.keyboard.handleUnicodeKeyType(keyEvent);
//                } else {
//                    return false;
//                }
//            } else {
//                TouchScreenControlsInputWidget.this.getHost().showPopupMenu();
//                return true;
//            }
//        });
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

        int i = 0;
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

            //TODO 为什么这俩要遍历所有的finger呢，只处理那一个不行吗
            case MotionEvent.ACTION_MOVE:
                if (this.userFingers[pointerId] != null) {
                    this.userFingers[pointerId].update(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                    handleFingerMove(this.userFingers[pointerId]);
                    break;
                }
//                while (i < MAX_FINGERS) {
//                    if (this.userFingers[i] != null) {
//                        int findPointerIndex = motionEvent.findPointerIndex(i);
//                        if (findPointerIndex >= 0) {
//                            this.userFingers[i].update(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
//                            handleFingerMove(this.userFingers[i]);
//                        } else {
//                            handleFingerUp(this.userFingers[i]);
//                            this.userFingers[i] = null;
//                        }
//                    }
//                    i++;
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (this.userFingers[i] != null) {
                    handleFingerUp(this.userFingers[i]);
                    this.userFingers[i] = null;
                }
//                while (i < MAX_FINGERS) {
//                    if (this.userFingers[i] != null) {
//                        handleFingerUp(this.userFingers[i]);
//                        this.userFingers[i] = null;
//                    }
//                    i++;
//                }
                break;
        }
        // TODO 要不要invalidate传入相应那个area的范围，只修改某一个区域，能减少点消耗
        invalidate();//按下时按钮背景会变化，移动时摇杆要移动，所以需要重绘
        return true;
    }

    /**
     * 发现一个停留在同一个区域，或移出一个区域并进入另一个区域，则停止遍历
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
        addView(new EditConfigWindow(this));
//        for (TouchArea<?> touchArea : mProfile.getTouchAreaList()) {
//            touchArea.mAdapter = new ClickAdapter(0.05f, () -> {
//                Toast.makeText(getContext(), "点击", Toast.LENGTH_LONG).show();
//            });
//        }
    }

    /**
     * 退出编辑模式
     */
    public void exitEdit() {

    }

    public OneProfile getProfile() {
        return mProfile;
    }


}

