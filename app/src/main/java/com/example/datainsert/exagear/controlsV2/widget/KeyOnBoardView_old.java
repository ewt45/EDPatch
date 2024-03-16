package com.example.datainsert.exagear.controlsV2.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ToggleButton;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controlsV2.TestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//TODO Paint.setShadowLayer 可以绘制阴影哎
/**
 * 用于选择按键码时，显示全键盘布局
 */
public class KeyOnBoardView_old extends NestedScrollView implements CompoundButton.OnCheckedChangeListener {

    private final List<CompoundButton> mSelectKeys = new ArrayList<>();
    private final Map<Integer, CompoundButton> mKeyBtnMap = new HashMap<>();
    private final ViewGroup mKeyboardView;
    private boolean mIsMaxOne = false;

    public KeyOnBoardView_old(@NonNull Context context) {
        super(context);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        addView(horizontalScrollView, new NestedScrollView.LayoutParams(-2, -2));

        mKeyboardView = (ViewGroup) TestHelper.getAssetsView(context,
                QH.isTesting()
                        ?"controls/available_key_view_buggy.xml":"controls/available_key_view.xml");
//        mKeyboardView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.aaa_available_key_view, horizontalScrollView, false);

//        TODO app:layout_constraint_xxxx 这种属性放在exa的app里会变成别的，暂时不知道怎么解决，先用最普通的视图吧
        setupUI(mKeyboardView);
        horizontalScrollView.addView(mKeyboardView);

//        if(!Const.isInitiated()){
//            setupUI(mKeyboardView);
//            horizontalScrollView.addView(mKeyboardView);
//        }else{
//            LinearLayout simpleView = new LinearLayout(context);
//            simpleView.setOrientation(LinearLayout.VERTICAL);
//            for(int i=0; i<keyNames.length; i++){
//                String keyName = keyNames[i];
//                if(keyName==null || keyName.isEmpty())
//                    continue;
//                CheckBox checkBox = new CheckBox(context);
//                checkBox.setText(keyName);
//                checkBox.setTag(Integer.toString(i));
//                checkBox.setOnCheckedChangeListener(this);
//                mKeyBtnMap.put(i,checkBox);
//                simpleView.addView(checkBox);
//            }
//            horizontalScrollView.addView(simpleView);
//        }
    }

    private static int getKeyByTag(View btn) {
        String keycodeStr = (String) btn.getTag();
        return Integer.parseInt(keycodeStr);
    }

    /**
     * 子布局全部为CompoundButton
     * 设置textOn和textOff全部为getText
     * 设置Drawable
     */
    private void setupUI(ViewGroup keyboardView) {
        for (int i = 0; i < keyboardView.getChildCount(); i++) {
            CompoundButton btn = (CompoundButton) keyboardView.getChildAt(i);
            btn.setButtonDrawable(null);
            btn.setBackground(TestHelper.getAssetsDrawable(getContext(), "controls/keyboard_key_toggle.xml"));
            btn.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            btn.setTextColor(0xFFF3F3F3);
            if (btn instanceof ToggleButton) {
                ((ToggleButton) btn).setTextOn(((ToggleButton) btn).getTextOff());
            }

            if (!btn.isEnabled()) {
//                btn.setVisibility(INVISIBLE);
                continue;
            }

            if (!(btn.getTag() instanceof String)) {
                Log.e("TAG", "该按钮没设置keycode：" + btn.getText());
                continue;
            }

            btn.setOnCheckedChangeListener(this);
            btn.setChecked(false);
            mKeyBtnMap.put(getKeyByTag(btn), btn);
        }
    }

    /**
     * 设置初始时选择哪些按钮
     *
     * @param keyList
     */
    public void setInitSelectedKeys(List<Integer> keyList) {
        for (Integer i : keyList) {
            CompoundButton btn = mKeyBtnMap.get(i);
            if (btn != null)
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
            list.add(getKeyByTag(btn));

        if(list.size()==0)
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
        //tag是字符串形式的
        if (!(btn.getTag() instanceof String)) {
            return;
        }

//        String keycodeStr = (String) btn.getTag();
//        int keycode = Integer.parseInt(keycodeStr);
        if (isChecked && !mSelectKeys.contains(btn)) {
            if (mIsMaxOne){
                for(CompoundButton oldSelectBtn:mSelectKeys)
                    oldSelectBtn.setChecked(false);
                mSelectKeys.clear();
            }
            mSelectKeys.add(btn);
        } else if (!isChecked) {
            mSelectKeys.remove(btn);
        }
    }
}
