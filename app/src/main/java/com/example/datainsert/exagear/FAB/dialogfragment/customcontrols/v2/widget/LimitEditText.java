package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"AppCompatCustomView"})
public class LimitEditText extends EditText {
    public static final int TYPE_NUMBER_FLOAT = 1; //无符号，带小数点
    public static final int TYPE_NUMBER_INT = 2; //无符号，无小数点
    public static final int TYPE_GIVEN_OPTIONS = 3; //只能选择预先提供好的选项
    public static final int TYPE_TEXT_SINGLE_LINE = 4; //普通的一行文字
    @InputType
    private int mType;
    private float[] mMinMax;
    private int[] mOptions;
    private String[] mOptionTexts;
    private int mSelectedValue;
    private UpdateListener mCallback;

    public LimitEditText(Context context) {
        super(context);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCallback != null)
                    mCallback.onUpdate(LimitEditText.this);
            }
        });
    }

    /**
     * 设置允许输入的类型
     */
    public LimitEditText setCustomInputType(@InputType int type) {
        mType = type;
        if (type == TYPE_NUMBER_FLOAT) {
            setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (type == TYPE_NUMBER_INT) {
            setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
        } else if (type == TYPE_GIVEN_OPTIONS) {
            setInputType(android.text.InputType.TYPE_NULL);
            setFocusable(false); //不允许focus可以解决第一次点击无效的问题
            setSingleLine(false); //选项文本允许多行
//            setMaxLines(100);
            //在setSelectableOptions里创建PopupMenu吧
        }else if(type == TYPE_TEXT_SINGLE_LINE){
            setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
            setSingleLine();
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
        int value = str.length() == 0 ? 0 : Integer.parseInt(str);
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
        return getText().toString();
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
    @IntDef(value = {TYPE_NUMBER_FLOAT, TYPE_NUMBER_INT, TYPE_GIVEN_OPTIONS, TYPE_TEXT_SINGLE_LINE})
    public @interface InputType {
    }


}
