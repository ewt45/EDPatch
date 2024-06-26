package com.example.datainsert.exagear.controlsV2.widget;

import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.axs.XKeyButton.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.axs.XKeyButton;

import java.util.ArrayList;
import java.util.List;
//TODO Paint.setShadowLayer 可以绘制阴影哎
/**
 * 用于选择按键码时，显示全键盘布局
 * <br/> 不能用SparseArray.indexOfValue获取其对应的索引
 */
public class KeyOnBoardView extends NestedScrollView implements CompoundButton.OnCheckedChangeListener {

    private final List<KeycodeButton> mSelectKeys = new ArrayList<>();
    //x keycode作为索引，Button作为元素
    //不能用SparseArray.indexOfValue获取其对应的索引
    private final SparseArray<KeycodeButton> mKeyBtnArray = new SparseArray<>();

    private boolean mIsMaxOne = false;

    public KeyOnBoardView(@NonNull Context c) {
        super(c);
        setBackgroundColor(0xff5C5F63);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(c);
        addView(horizontalScrollView, new NestedScrollView.LayoutParams(-2, -2));

        final int w = dp8*6;
        LinearLayout linearKeyboardRoot = new LinearLayout(c);
        linearKeyboardRoot.setOrientation(LinearLayout.VERTICAL);
        linearKeyboardRoot.setClipChildren(false);
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
                new XKeyButton.Info[]{key_esc,key_f1,key_f2,key_f3,key_f4,key_f5,key_f6,key_f7,key_f8,key_f9,key_f10,key_f11,key_f12,key_print_screen,key_scroll_lock,key_pause},
                new int[]{w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w},
                new int[]{0,dp8*6,0,0,0,px(28),0,0,0,px(28),0,0,0,dp8,0,0}
        ),QH.LPLinear.one(-2,-2).to());
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
                new XKeyButton.Info[]{key_grave,key_1,key_2,key_3,key_4,key_5,key_6,key_7,key_8,key_9,key_0,key_minus,key_equal,key_backspace,key_insert,key_home,key_page_up,key_number_lock,key_keypad_slash,key_keypad_asterisk,key_keypad_minus,pointer_left,pointer_scroll_up,pointer_right},
                new int[]{w,w,w,w,w,w,w,w,w,w,w,w,w,px(104),w,w,w,w,w,w,w,w,w,w},
                new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,dp8,0,0,dp8,0,0,0,px(56),0,0}
        ),QH.LPLinear.one(-2,-2).top(dp8*3).to());
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
                new XKeyButton.Info[]{key_tab,key_q,key_w,key_e,key_r,key_t,key_y,key_u,key_i,key_o,key_p,key_open_bracket,key_close_bracket,key_backslash,key_delete,key_end,key_page_down,key_keypad_7,key_keypad_8,key_keypad_9,key_keypad_plus,pointer_scroll_down},
                new int[]{px(76),w,w,w,w,w,w,w,w,w,w,w,w,px(76),w,w,w,w,w,w,w,w},
                new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,dp8,0,0,dp8,0,0,0,px(112)}
        ),QH.LPLinear.one(-2,-2).top(-dp8*6).to());
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
                new XKeyButton.Info[]{key_caps_lock,key_a,key_s,key_d,key_f,key_g,key_h,key_j,key_k,key_l,key_semicolon,key_apostrophe,key_enter,key_keypad_4,key_keypad_5,key_keypad_6,pointer_body_stub},
                new int[]{px(92),w,w,w,w,w,w,w,w,w,w,w,px(116),w,w,w,px(160)},
                new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,px(184),0,0,px(112)}
        ),QH.LPLinear.one(-2,-2).top(-dp8*6).to());
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
               new XKeyButton.Info[]{key_left_shift,key_z,key_x,key_c,key_v,key_b,key_n,key_m,key_comma,key_dot,key_slash,key_right_shift,key_up,key_keypad_1,key_keypad_2,key_keypad_3,key_keypad_enter},
               new int[]{px(120),w,w,w,w,w,w,w,w,w,w,px(144),w,w,w,w,w},
               new int[]{0,0,0,0,0,0,0,0,0,0,0,0,dp8*8,dp8*8,0,0,0}
        ),QH.LPLinear.one(-2,-2).top(-dp8*13).to());
        linearKeyboardRoot.addView(getKeyButtonsOneLine(
              new XKeyButton.Info[]{key_left_ctrl,key_left_win,key_left_alt,key_spacebar,key_right_alt,key_right_win,key_menu,key_right_ctrl,key_left,key_down,key_right,key_keypad_0,key_keypad_dot},
              new int[]{px(64),px(64),px(64),px(340),px(60),px(64),px(60),px(60),w,w,w,px(104),w},
              new int[]{0,0,0,0,0,0,0,0,dp8,0,0,dp8,0}
        ),QH.LPLinear.one(-2,-2).top(-dp8*6).to());

        LinearLayout linearMouseRoot = new LinearLayout(c);
        linearMouseRoot.setOrientation(LinearLayout.VERTICAL);
        linearMouseRoot.setClipChildren(false);

        horizontalScrollView.addView(linearKeyboardRoot,QH.LPLinear.one(-2,-2).top().bottom().to());
    }
    private int px(int dp){
        return QH.px(getContext(),dp);
    }

    private LinearLayout getKeyButtonsOneLine(XKeyButton.Info[] keys, int[] widths, int[] extraLefts ){
        assert keys.length == extraLefts.length && widths.length == keys.length;
        Context c = getContext();
        LinearLayout linearLineRoot = new LinearLayout(c);
        linearLineRoot.setOrientation(LinearLayout.HORIZONTAL);
        linearLineRoot.setBaselineAligned(false); //设置成 false，不然按钮文字包含换行时，基线会下移导致按钮位置下移
        linearLineRoot.setClipChildren(false);
        for(int i=0; i<keys.length; i++){
            Info info = keys[i];

            KeycodeButton check = new KeycodeButton(c, widths[i], dp8*6, info, this);
            mKeyBtnArray.put(info.xKeyCode,check);

            //竖向占两/三格
            if(info == key_keypad_plus || info == key_keypad_enter || info == pointer_left || info == pointer_right)
                check.setHeight(dp8*13);
            else if(info == pointer_body_stub)
                check.setHeight(dp8*20);
            //没有对应x keycode，禁用
            if(info == key_left_win || info == key_right_win || info==key_menu || info==pointer_body_stub){
                check.setEnabled(false);
                check.setText("");
            }

            linearLineRoot.addView(check, QH.LPLinear.one(-2,-2).left(dp8+extraLefts[i]).to());
        }
        linearLineRoot.addView(new View(c),QH.LPLinear.one(-2,-2).right().to());
        return linearLineRoot;
    }

    /**
     * 设置初始时选择哪些按钮
     */
    public void setInitSelectedKeys(List<Integer> keyList) {
        for (Integer i : keyList) {
            CompoundButton btn = mKeyBtnArray.get(i);
            if (btn != null && i!=0)
                btn.setChecked(true);
        }
    }

    /**
     * 获取当前选中的按键。
     * <br/> 保证返回的列表至少包含一个按键。若当前没有任何按键选中，则返回的列表只包含一个keycode=0
     */
    public List<Integer> getSelectedKeys() {
        List<Integer> list = new ArrayList<>();
        for (CompoundButton btn : mSelectKeys)
            list.add((Integer) btn.getTag());

        if(list.isEmpty())
            list.add(0);

        return list;
    }


    /**
     * 设置最多只允许选择一个按键。
     */
    public void setOnlyAllowOne(boolean onlyOne) {
        mIsMaxOne = onlyOne;
    }

    /**
     * 点击按钮时的回调
     */
    @Override
    public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
        TestHelper.assertTrue(btn instanceof KeycodeButton, "btn类型必须为KeycodeButton");
        KeycodeButton btn2 = (KeycodeButton) btn;

        //TODO 将旧的选中列表的都清除overlay，处理后再给新的选中列表重新设置一遍，并且记得invalidate
        for(KeycodeButton oldSelectBtn:mSelectKeys)
            oldSelectBtn.clearOrder();

        if (isChecked && !mSelectKeys.contains(btn2)) {
            if (mIsMaxOne){
                for(CompoundButton oldSelectBtn:mSelectKeys)
                    oldSelectBtn.setChecked(false);
                mSelectKeys.clear();
            }
            mSelectKeys.add(btn2);
        } else if (!isChecked) {
            mSelectKeys.remove(btn);
        }

        for (int i=0; i<mSelectKeys.size(); i++)
            mSelectKeys.get(i).setOrder(i+1);

        postInvalidate();
    }

    /**
     * checkbox, 将keycode存在tag中
     */
    @SuppressLint("AppCompatCustomView")
    private static class KeycodeButton extends CheckBox{
        private final DrawableNumber overlayDrawable;

        public KeycodeButton(Context c, int width, int height, Info info, OnCheckedChangeListener listener) {
            super(c);

            setWidth(width);
            setHeight(height);
//            check.setTextSize(TypedValue.COMPLEX_UNIT_SP, keys[i].name.length()>5?12:14);
            setText(info.name);
            setTag(info.xKeyCode);
            setButtonDrawable(null);
            setBackground(createKeyToggleDrawable());
            setTextColor(0xFFF3F3F3);
            setTextAlignment(TEXT_ALIGNMENT_GRAVITY);//不知道为啥，exa的apk里这个设置完raw的是CENTER但get的始终是GRAVITY。只能能用setGravity设置了
            setGravity(Gravity.CENTER);
            setOnCheckedChangeListener(listener);
            setChecked(false);

            overlayDrawable = new DrawableNumber(c, DrawableNumber.GRAVITY_RIGHT_BOTTOM);
            overlayDrawable.setMaxRadius(dp8);
            overlayDrawable.setTint(0xffDAA520);
            getOverlay().add(overlayDrawable);
        }

        private static Drawable createKeyToggleDrawable(){
            //https://blog.csdn.net/Sansecy/article/details/107810160
            //纯色的话直接GradientDrawable设置ColorStateList就能做到了。用不上StateListDrawable。。。
            GradientDrawable gradient = new GradientDrawable();
            gradient.setShape(GradientDrawable.RECTANGLE);
            gradient.setCornerRadius(dp8/2f);
            gradient.setColor(new ColorStateList(
                    new int[][]{{android.R.attr.state_checked},StateSet.WILD_CARD},
                    new int[]{0xff858484,0xff3A3A3A}
            ));
            return gradient;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            overlayDrawable.setBounds(0,0,w,h);
        }

        /**
         * 清除序号标识
         */
        public void clearOrder(){
            overlayDrawable.setLevel(0);
        }

        /**
         * 右下角设置当前序号标识。用于表示当前选中的按键的次序
         */
        public void setOrder(int order) {
            overlayDrawable.setLevel(order);
        }
    }
}
