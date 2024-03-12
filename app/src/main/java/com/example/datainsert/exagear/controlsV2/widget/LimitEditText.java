package com.example.datainsert.exagear.controlsV2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 尝试拦截返回键，以解决填写文本时此视图抢夺焦点，导致TouchAreaView无法拦截返回事件，fragment直接退出的问题
 * <br/> 全部
 */
@SuppressLint({"AppCompatCustomView"})
public class LimitEditText extends EditText {
    public static final int TYPE_NUMBER_FLOAT = 1; //无符号，带小数点
    public static final int TYPE_NUMBER_INT = 2; //无符号，无小数点
    public static final int TYPE_GIVEN_OPTIONS = 3; //只能选择预先提供好的选项
    public static final int TYPE_TEXT_SINGLE_LINE = 4; //普通的一行文字
    public static final int TYPE_HEX_COLOR_ARGB = 5; //十六进制颜色的argb值，最多8位
    @CustomInputType
    private int mType;
    private float[] mMinMax;
    private int[] mOptions;
    private String[] mOptionTexts;
    private int mSelectedValue;
    private UpdateListener mCallback;

    public LimitEditText(Context context) {
        super(context);
        //TODO 设置默认不获取focus，然后把Nestedscrollview去掉看看（是不是滚动视图获取焦点导致的bug）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setFocusedByDefault(false);
        }
        // 如果自身失去焦点，立马让touchAreaview获取焦点
//        setOnFocusChangeListener((v, hasFocus) -> {
//            if(!hasFocus) {
//                Log.d("TAG", "LimitEditText: 让TouchAreaView获取焦点");
//                Const.getTouchView().requireFocus();
//            }
//        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mType==TYPE_HEX_COLOR_ARGB){
                    for (int i = 0; i < s.length(); i++) {
                        char c1 = s.charAt(i);
                        if (!((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f') || (c1 >= 'A' && c1 <= 'F'))) {
                            s.delete(i, i + 1);
                            return; //只要更改一处，就不往下走了，因为这次更改会再触发一次监听
                        }
                    }
                }
                if (mCallback != null)
                    mCallback.onUpdate(LimitEditText.this);
            }
        });

        //用户正在输入时，代替TouchAreaView拦截返回键（在输入法显示时应该焦点优先在输入法，只有输入法隐藏了这里才会接收到）
        setOnKeyListener((v, keyCode, event) -> {
            if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                Log.d("TAG", "onKey: edittext检测到返回键松开");
                return true;
            }else
                return false;
        });
    }


    /**
     * 设置允许输入的类型
     */
    public LimitEditText setCustomInputType(@CustomInputType int type) {
        mType = type;
        if (type == TYPE_NUMBER_FLOAT) {
            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (type == TYPE_NUMBER_INT) {
            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        } else if (type == TYPE_GIVEN_OPTIONS) {
            setInputType(InputType.TYPE_NULL);
            setFocusable(false); //不允许focus可以解决第一次点击无效的问题
            setSingleLine(false); //选项文本允许多行
//            setMaxLines(100);
            //在setSelectableOptions里创建PopupMenu吧
        }else if(type == TYPE_TEXT_SINGLE_LINE){
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            setSingleLine();
        } else if(type == TYPE_HEX_COLOR_ARGB){
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        }
        return this;
    }

    /**
     * 设置允许输入的数字范围(包含最大值和最小值）
     */
    public LimitEditText setRange(float min, float max) {
        TestHelper.assertTrue(mType == TYPE_NUMBER_FLOAT || mType == TYPE_NUMBER_INT, "输入类型必须为float或int");
        if (min > max)
            throw new RuntimeException("min 大于 max");
        mMinMax = new float[]{min, max};
        return this;
    }


    /**
     * 如果输入为数字类型，获取输入的数字大小 （确保在限制范围内）
     */
    public float getFloatValue() {
        TestHelper.assertTrue(mType == TYPE_NUMBER_FLOAT, "输入类型必须为float");
        String str = getText().toString();
        float value = str.length() == 0 ? 0 : Float.parseFloat(str);
        if (mMinMax != null) {
            if (value < mMinMax[0])
                value = mMinMax[0];
            else if (value > mMinMax[1])
                value = mMinMax[1];
        }
        return value;
    }

    /**
     * 如果输入为数字类型，设置当前数字大小
     */
    public LimitEditText setFloatValue(float value) {
        TestHelper.assertTrue(mType == TYPE_NUMBER_FLOAT, "输入类型必须为float");
        setText(String.valueOf(value));
        return this;
    }

    /**
     * 如果输入为数字类型，获取输入的数字大小 （确保在限制范围内）
     */
    public int getIntValue() {
        TestHelper.assertTrue(mType == TYPE_NUMBER_INT, "输入类型必须为int");
        String str = getText().toString();
        //TODO 应该检查文字是否太大，如果太大的话转换会报错
        int value = str.isEmpty() ? 0 : Integer.parseInt(str);
        if (mMinMax != null) {
            if (value < mMinMax[0])
                value = (int) mMinMax[0];
            else if (value > mMinMax[1])
                value = (int) mMinMax[1];
        }
        return value;
    }


    /**
     * 如果输入为数字类型，设置当前数字大小
     */
    public LimitEditText setIntValue(int value) {
        TestHelper.assertTrue(mType == TYPE_NUMBER_INT, "输入类型必须为int");
        setText(String.valueOf(value));
        return this;
    }

    /**
     * 给定几个选项，用户仅允许选择提供的选项，不能自定义值。
     *
     * @param values 选项对应的值，该value应该属于FSMR.value中的一个数组，否则无法翻译文本
     */
    public LimitEditText setSelectableOptions(int[] values) {
        return setSelectableOptions(values, FSMR.getValuesS(values));
    }

    /**
     * 同 {@link #setSelectableOptions(int[])} 但是传入values可以不为FSMR.value，需要自己提供对应的文本数组
     */
    public LimitEditText setSelectableOptions(int[] values, String[] displayTexts) {
        TestHelper.assertTrue(mType == TYPE_GIVEN_OPTIONS, "输入类型必须为options");
        List<Integer> testList = new ArrayList<>();
        for (int i : values) {
            TestHelper.assertTrue(!testList.contains(i), "可选项的值不能重复");
            testList.add(i);
        }

        mOptions = values;
        mOptionTexts = displayTexts;
        setSelectedValue(values[0]);

        setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), this);
            for (int i = 0; i < values.length; i++) {
                int value = mOptions[i];
                popupMenu.getMenu().add(mOptionTexts[i]).setOnMenuItemClickListener(item -> {
                    setSelectedValue(value);
                    return true;
                });
            }
            popupMenu.show();
        });
        return this;
    }

    /**
     * 如果类型为选项类型，获取当前选择的选项对应的值（必定是之前通过 {@link #setSelectableOptions(int[])} 传入的数组中的中的一项）
     */
    public int getSelectedValue() {
        TestHelper.assertTrue(mType == TYPE_GIVEN_OPTIONS, "输入类型必须为options");
        return mSelectedValue;
    }

    /**
     * 类型是选项类型时，设置当前选择的值
     */
    public LimitEditText setSelectedValue(int value) {
        TestHelper.assertTrue(mType == TYPE_GIVEN_OPTIONS, "输入类型必须为options");
        for (int i = 0; i < mOptions.length; i++)
            if (mOptions[i] == value) break;
            else if (i == mOptions.length - 1)
                throw new RuntimeException("设置的选项值必须包含在可选项内");

        mSelectedValue = value;
        for (int index = 0; index < mOptionTexts.length; index++)
            if (mOptions[index] == value) {
                setText(mOptionTexts[index]);
                break;
            }
        return this;
    }

    public LimitEditText setStringValue(String s) {
        TestHelper.assertTrue(mType == TYPE_TEXT_SINGLE_LINE, "输入类型必须为text");
        setText(s);
        return this;
    }

    public String getStringValue(){
        TestHelper.assertTrue(mType == TYPE_TEXT_SINGLE_LINE, "输入类型必须为text");
        return getText().toString();
    }

    public LimitEditText setHexColorARGBValue(int argb){
        TestHelper.assertTrue(mType == TYPE_HEX_COLOR_ARGB, "输入类型必须为hex color");
        setText(Integer.toHexString(argb));
        return this;
    }

    public int getHexColorARGBValue(){
        TestHelper.assertTrue(mType == TYPE_HEX_COLOR_ARGB, "输入类型必须为hex color");
        StringBuilder builder = new StringBuilder(getText().toString().trim());
        while (builder.length() < 8)
            builder.insert(0, "f");
        //int最大是0x7fffffff 阿这，加了alpha变8位, Integer.parseInt/ valueOf 没法转换了, 要用parseUnsignedInt 或者 Long.parseLong
        return Integer.parseUnsignedInt(builder.toString(), 16);
    }

    /**
     * 设置一个在输入内容改变之后的回调
     */
    public LimitEditText setUpdateListener(UpdateListener callback) {
        mCallback = callback;
        return this;
    }


    public interface UpdateListener {
        void onUpdate(LimitEditText editText);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TYPE_NUMBER_FLOAT, TYPE_NUMBER_INT, TYPE_GIVEN_OPTIONS, TYPE_TEXT_SINGLE_LINE,TYPE_HEX_COLOR_ARGB})
    public @interface CustomInputType {
    }


}
