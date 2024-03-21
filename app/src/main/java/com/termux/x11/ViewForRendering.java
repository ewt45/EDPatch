package com.termux.x11;

import static com.termux.x11.input.InputStub.BUTTON_SCROLL;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.eltechs.axs.Globals;
import com.eltechs.axs.KeyCodesX;
import com.eltechs.axs.applicationState.EnvironmentAware;
import com.eltechs.axs.environmentService.AXSEnvironment;
import com.eltechs.axs.environmentService.components.XServerComponent;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.proto.output.replies.Str;
import com.eltechs.axs.xserver.Pointer;
import com.eltechs.axs.xserver.ScreenInfo;
import com.eltechs.axs.xserver.ViewFacade;
import com.termux.x11.input.InputStub;

import java.nio.charset.Charset;

public class ViewForRendering extends LorieView {
    /**
     * `1234567890-=	qwertyuiop[]asdfghjkl;'\zxcvbnm,./空格回车 (注意回车虽然是0xd，但安卓里是0xa)
     * int[] plainChars={0x60, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x2d, 0x3d, 0x9, 0x71, 0x77, 0x65, 0x72, 0x74, 0x79, 0x75, 0x69, 0x6f, 0x70, 0x5b, 0x5d, 0x61, 0x73, 0x64, 0x66, 0x67, 0x68, 0x6a, 0x6b, 0x6c, 0x3b, 0x27, 0x5c, 0x7a, 0x78, 0x63, 0x76, 0x62, 0x6e, 0x6d, 0x2c, 0x2e, 0x2f, 0x20, 0xd,0xa};
     * 按键输入如果是unicode貌似 游戏内不识别。所以unicode可以转keycode的尽量转keycode
     */
    static final int[] keycodeSafeList = {
            9, 10, 16, 32, 39, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 59, 61, 91, 92, 93, 96, 97, 98, 99, 100, 101, 102,
            103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122,};
    static final boolean[] keycodeSafeTestList = new boolean[128];
    private static final String TAG = "ViewForRendering";
    private static final int XI_TouchBegin = 18; //MotionEvent.ACTION_DOWN MotionEvent.ACTION_POINTER_DOWN
    private static final int XI_TouchUpdate = 19; //MotionEvent.ACTION_MOVE
    private static final int XI_TouchEnd = 20; //MotionEvent.ACTION_UP MotionEvent.ACTION_POINTER_UP MotionEvent.ACTION_CANCEL
    private static ViewForRendering mInstance;

    static {
        for (int i : keycodeSafeList)
            keycodeSafeTestList[i] = true;
    }


    public ViewForRendering(Context context) {
        super(context);
        mInstance = this;
        AXSEnvironment environment = ((EnvironmentAware) Globals.getApplicationState()).getEnvironment();
        XServerComponent xServerComponent = environment.getComponent(XServerComponent.class);
        assert xServerComponent!=null;
        p  = new Point(xServerComponent.getScreenInfo().widthInPixels,xServerComponent.getScreenInfo().heightInPixels);
    }
   final Point p;

    public static void keyEvent(int keycode, int keySym, boolean down) {
        if (mInstance == null)
            return;
        //终于知道问题所在了。exa里android keycode在java里转为x keycode（用KeyCodeX），之后传到这里的keycode直接是x keycode + 8。
        // 而tx11往native传的参数keycode仍然是android keycode。然后native中转为x keycode. 传的参数scancode才是x keycode

        //keyEvent只能处理keycode。所以keSym优先，避免需要shift的字符等无法输入（但是这样就不支持长按了？）
        if (keySym != 0 &&
                //貌似游戏还不识别unicode输入。所以能转keycode还是转keycode
                !(keySym < 128 && keycodeSafeTestList[keySym]
                        && keycode != 0 && keycode != 8)) {
            textEvent(keySym, down);
            return;
        }

        //应该让它用unicode。也就是scancode不为0. 也就是把scancode设为unicode，也就是把scancode设为keysym
        if (keycode == 0 || keycode == 8)
            Log.e(TAG, "keyEvent: keycode为0 不应出现这种情况");

        int scancode = keycode - 8; //exa不仅转换了android keycode，而且提前加好8了
        mInstance.sendKeyEvent(scancode, keycode, down);
        //native里送的是scancode+8.。。
    }

    public static void textEvent(int keySym, boolean down) {
        if (mInstance == null)
            return;

        //textevent不支持长按，所以需要跳过一个事件
        if (!down)
            return;

//        //不知道为什么括号没法输入。只能shift + 9/0了 (最新tx11好像没这问题了）
//        if (keySym == 0x28 || keySym == 0x29) {
//            KeyCodesX parenKeyCode = keySym == 0x28 ? KeyCodesX.KEY_9 : KeyCodesX.KEY_0;
//            keyEvent(KeyCodesX.KEY_SHIFT_LEFT.getValue(), 0, true);
//            keyEvent(parenKeyCode.getValue(), 0, true);
//            keyEvent(parenKeyCode.getValue(), 0, false);
//            keyEvent(KeyCodesX.KEY_SHIFT_LEFT.getValue(), 0, false);
//            return;
//        }

        //回车 应该是return 0xff0d .但是从keyevent获取到的unicode是0x0a linefeed，手动改一下
        if (keySym == 0x0a)
            keySym = 0x0d;

        //虽然现在tx11都不用这个改用sendTextEvent，但是传字节数组的话需要转为字节数组，精度不够（但是精度不够好像也不会引起什么问题？）
        mInstance.sendUnicodeEvent(keySym);
    }

    public static void mouseEvent(int xPos, int yPos, int keycode, boolean isPress, boolean relative) {
        if (mInstance == null)
            return;
        //？？？滚轮上下都是BUTTON_SCROLL==4，然后负数是上正数是下
        if (keycode == Pointer.BUTTON_SCROLL_DOWN || keycode == Pointer.BUTTON_SCROLL_UP) {
            xPos = 0; //横向滚动 exa好像不支持
            yPos = keycode == Pointer.BUTTON_SCROLL_UP ? -60 : 60; //60正好
            keycode = BUTTON_SCROLL;
        }

        mInstance.sendMouseEvent(xPos, yPos, keycode, isPress, relative);
    }

    public static void touchEvent(int x, int y) {
        if (mInstance == null)
            return;
        //先一律设置成XI_TouchUpdate吧。id是手指编号先设置成0
        mInstance.sendTouchEvent(XI_TouchUpdate, 0, x, y);
    }

    /**
     * 退出容器时，自身的静态实例清空
     */
    public static void clearStaticInstance() {
        mInstance = null;
    }
}