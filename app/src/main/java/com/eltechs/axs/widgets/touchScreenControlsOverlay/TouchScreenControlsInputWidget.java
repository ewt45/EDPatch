package com.eltechs.axs.widgets.touchScreenControlsOverlay;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;

import android.content.Context;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.eltechs.axs.Finger;
import com.eltechs.axs.KeyEventReporter;
import com.eltechs.axs.Keyboard;
import com.eltechs.axs.Mouse;
import com.eltechs.axs.PointerEventReporter;
import com.eltechs.axs.TouchScreenControls;
import com.eltechs.axs.configuration.TouchScreenControlsInputConfiguration;
import com.eltechs.axs.widgets.viewOfXServer.ViewOfXServer;
import com.eltechs.axs.xserver.ViewFacade;

public class TouchScreenControlsInputWidget extends View {
    private final String TAG = "TouchScr16Widget";

    private final int MAX_FINGERS;
    private final TouchScreenControlsInputConfiguration configuration;
    private final Keyboard keyboard;//处理键盘操作
    private final Mouse mouse;
    private TouchScreenControls touchScreenControls;
    private final Finger[] userFingers;
    private final ViewFacade xServerFacade;


    /**
     * 在xml中声明需要用到attrs所以再加一个构造方法
     */
//    public TouchScreenControlsInputWidget(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        Log.d(TAG, "TouchScreenControlsInputWidget: 开始初始化view");
//        //初始化键盘
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        this.xServerFacade = viewOfXServer.getXServerFacade();
//        this.keyboard = new Keyboard(new KeyEventReporter(this.xServerFacade));
//
//        installKeyListener();//进行监听键盘和点击操作
//
//    }

    /**
     * ex的dex中原本的构造方法
     */
    public TouchScreenControlsInputWidget(Context context, ViewOfXServer viewOfXServer, TouchScreenControlsInputConfiguration touchScreenControlsInputConfiguration) {
        super(context);
        this.MAX_FINGERS = 10;
        this.userFingers = new Finger[10];
//        this.xServerFacade = viewOfXServer.getXServerFacade();
        this.xServerFacade =null;
        this.mouse = new Mouse(new PointerEventReporter(viewOfXServer));
        this.keyboard = new Keyboard(new KeyEventReporter(this.xServerFacade));
        this.configuration = touchScreenControlsInputConfiguration;
        setFocusable(true);
        setFocusableInTouchMode(true);
        installKeyListener();
    }

    public TouchScreenControlsInputWidget(Context context) {
        this(context,null,null);
    }


    /**
     * 监听按键操作，说是软件输入法不会进到这里？
     * 知道了。如果要让view获取到软键盘输入，在调出键盘后调用requestFocus就行了。
     */
    private void installKeyListener() {
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {

                if (keyEvent.getSource() == InputDevice.SOURCE_MOUSE) {
                    if (i == KEYCODE_BACK) {
                        if (keyEvent.getAction() == ACTION_DOWN) {
//                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonPress(3);
                        } else if (keyEvent.getAction() == ACTION_UP) {
//                            TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonRelease(3);
                        }
                    }
                    return true;
                } else if (i != KeyEvent.KEYCODE_MENU || keyEvent.getAction() != ACTION_UP) {
//                        if (i == KEYCODE_BACK && TouchScreenControlsInputWidget.this.configuration.backKeyAction == TouchScreenControlsInputConfiguration.BackKeyAction.SHOW_POPUP_MENU) {
//                            if (keyEvent.getAction() == ACTION_UP) {
//                                TouchScreenControlsInputWidget.this.getHost().showPopupMenu();
//                            }
//                            return true;
//                        }else
                    if (i == KEYCODE_DPAD_CENTER) {
//                            if (keyEvent.getAction() == ACTION_DOWN) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonPress(3);
//                            } else if (keyEvent.getAction() == ACTION_UP) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectPointerButtonRelease(3);
//                            }
                        return true;
                    } else if (i == KeyEvent.KEYCODE_BUTTON_L1 || i == KeyEvent.KEYCODE_BUTTON_L2) {
//                            if (keyEvent.getAction() == ACTION_DOWN) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
//                            } else if (keyEvent.getAction() == ACTION_UP) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_SHIFT_LEFT.getValue());
//                            }
                        return true;
                    } else if (i == KeyEvent.KEYCODE_BUTTON_R1 || i == KeyEvent.KEYCODE_BUTTON_R2) {
//                            if (keyEvent.getAction() == ACTION_DOWN) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectKeyPress((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
//                            } else if (keyEvent.getAction() == ACTION_UP) {
//                                TouchScreenControlsInputWidget.this.xServerFacade.injectKeyRelease((byte) KeyCodesX.KEY_CONTROL_LEFT.getValue());
//                            }
                        return true;
                    } else if (i != KeyEvent.KEYCODE_UNKNOWN) {
                        Log.d(TAG, "onKey: 输入了认识的keycode：" + i);
                        if (keyEvent.getAction() == ACTION_DOWN) {
                            return TouchScreenControlsInputWidget.this.keyboard.handleKeyDown(i, keyEvent);
                        }
                        if (keyEvent.getAction() != ACTION_UP) {
                            return false;
                        }
                        return TouchScreenControlsInputWidget.this.keyboard.handleKeyUp(i, keyEvent);
                    } else if (keyEvent.getAction() != 2) {
                        return false;
                    }
                    //不认识的keycode，最后会当做unicode处理
                    else {
                        Log.d(TAG, "onKey: 输入不认识的keycode"+i+"，作为unicode处理");
                        return TouchScreenControlsInputWidget.this.keyboard.handleUnicodeKeyType(keyEvent);
                    }

                } else {
                    Log.d(TAG, "onKey: 最外层监听到key：" + i + " ,对应字符：" + keyEvent.getCharacters());
//                    Log.d(TAG, "onKey: 此时应该调出弹窗菜单");
//                        TouchScreenControlsInputWidget.this.getHost().showPopupMenu();
                    return true;
                }

            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = (motionEvent.getSource() & InputDeviceCompat.SOURCE_TOUCHSCREEN) == InputDeviceCompat.SOURCE_TOUCHSCREEN;
        boolean z2 = (motionEvent.getSource() & InputDeviceCompat.SOURCE_STYLUS) == InputDeviceCompat.SOURCE_STYLUS;
        boolean z3 = (motionEvent.getSource() & InputDeviceCompat.SOURCE_MOUSE) == InputDeviceCompat.SOURCE_MOUSE;
        if (z || z2) {
            return handleTouchEvent(motionEvent);
        }
        if (z3) {
            return this.mouse.handleMouseEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);
        int actionMasked = motionEvent.getActionMasked();
        if (pointerId >= 10) {
            return true;
        }
        int i = 0;
        switch (actionMasked) {
            case ACTION_DOWN:
            case ACTION_POINTER_DOWN:
                this.userFingers[pointerId] = new Finger(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                this.touchScreenControls.handleFingerDown(this.userFingers[pointerId]);
                break;
            case ACTION_UP:
            case ACTION_POINTER_UP:
                if (this.userFingers[pointerId] != null) {
                    this.userFingers[pointerId].release(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
                    this.touchScreenControls.handleFingerUp(this.userFingers[pointerId]);
                    this.userFingers[pointerId] = null;
                    break;
                }
                break;
            case ACTION_MOVE:
                while (i < 10) {
                    if (this.userFingers[i] != null) {
                        int findPointerIndex = motionEvent.findPointerIndex(i);
                        if (findPointerIndex >= 0) {
                            this.userFingers[i].update(motionEvent.getX(findPointerIndex)*1.4f, motionEvent.getY(findPointerIndex)*1.4f);
                            this.touchScreenControls.handleFingerMove(this.userFingers[i]);
                        } else {
                            this.touchScreenControls.handleFingerUp(this.userFingers[i]);
                            this.userFingers[i] = null;
                        }
                    }
                    i++;
                }
                break;
            case ACTION_CANCEL:
                while (i < 10) {
                    if (this.userFingers[i] != null) {
                        this.touchScreenControls.handleFingerUp(this.userFingers[i]);
                        this.userFingers[i] = null;
                    }
                    i++;
                }
                break;
        }
        return true;
    }



}
