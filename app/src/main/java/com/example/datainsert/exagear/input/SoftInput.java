package com.example.datainsert.exagear.input;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eltechs.axs.Globals;
import com.eltechs.axs.applicationState.ApplicationStateBase;

public class SoftInput {
    private static final String TAG = "SoftInput";
    private static boolean showing = false;
    private static final int VERSION_FOR_EDPATCH = 2;

    public static void toggle() {
        InputMethodManager imm = (InputMethodManager) Globals.getAppContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (Globals.getApplicationState() == null)
            return;
        View decorView = ((ApplicationStateBase) Globals.getApplicationState()).getCurrentActivity().getWindow().getDecorView();
        Log.d(TAG, "onClick: 当前顶层布局为" + decorView);
        if (Build.VERSION.SDK_INT > 29) {
            //如果应该显示键盘，等一秒，顶层view调起键盘（如果应该取消键盘那么什么都不做，点完菜单项的时候键盘应该会自动消失）
            decorView.postDelayed(() -> {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    //这个逻辑错了，原本的toggle是如果当前显示就隐藏，隐藏就显示，而不是固定第一次显示第二次隐藏。（行吧实际测试toggle安卓12并没法从显示到隐藏，隐藏弹窗关闭那一瞬间输入法闪退了，200毫秒之后会变成隐藏到显示）
//                View view = decorView.findFocus();//decorView.findFocus()
//                if (view == null)
//                    view = decorView;
//                Log.d(TAG, "run: 当前顶层布局为" + decorView + ", focus视图为" + view);
//                if (showing)
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//这个token不管是TouchScreenControlsInputWidget还是decorView都是一样的
//                else
//                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//原来安卓12只要延迟1秒，用toggle也没问题（阿这ex用show反而没法显示？）(原来ex有个focus的view TouchScreenControlsInputWidget，附着到focus的view就行了）
//                showing = !showing;
            }, 200);
        }
//        安卓10及以下保留原来的方式(或者安卓12要隐藏键盘的时候，这个应该没问题吧）
        else {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void toggleTest(AppCompatActivity a) {
        InputMethodManager imm = (InputMethodManager) Globals.getAppContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View decorView = a.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT > 29) {
            //如果应该显示键盘，等一秒，顶层view调起键盘（如果应该取消键盘那么什么都不做，点完菜单项的时候键盘应该会自动消失）
            decorView.postDelayed(() -> {
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                View view = decorView.findFocus();//decorView.findFocus()
                if (view == null)
                    view = decorView;
                Log.d(TAG, "run: 当前顶层布局为" + decorView + ", focus视图为" + view);
                if (showing) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//这个token不管是TouchScreenControlsInputWidget还是decorView都是一样的
                } else {
                    imm.showSoftInput(view, 0);//原来安卓12只要延迟1秒，用toggle也没问题（阿这ex用show反而没法显示？）(原来ex有个focus的view TouchScreenControlsInputWidget，附着到focus的view就行了）
                }
                showing = !showing;
            }, 1000);
        }
//        安卓10及以下保留原来的方式(或者安卓12要隐藏键盘的时候，这个应该没问题吧）
        else {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

}
