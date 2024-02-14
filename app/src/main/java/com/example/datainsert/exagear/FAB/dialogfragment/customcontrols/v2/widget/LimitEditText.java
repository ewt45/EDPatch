package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressLint({"AppCompatCustomView"})
public class LimitEditText extends EditText {

    public static final int TYPE_NUMBER_FLOAT = 1;
    public static final int TYPE_NUMBER_INT = 2;
    private float[] mMinMax;

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
                if(mCallback!=null)
                    mCallback.onUpdate(LimitEditText.this);
            }
        });
    }

    /**
     * 设置允许输入的类型
     */
    public LimitEditText setCustomInputType(@InputType int type) {
        if (type == TYPE_NUMBER_FLOAT) {
            setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        }else if(type == TYPE_NUMBER_INT){
            setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
        }
        return this;
    }

    /**
     * 设置允许输入的数字范围(包含最大值和最小值）
     */
    public LimitEditText setRange(float min, float max) {
        if(min>max)
            throw new RuntimeException("min 大于 max");
        mMinMax = new float[]{min, max};
        return this;
    }

    /**
     * 如果输入为数字类型，获取输入的数字大小 （确保在限制范围内）
     */
    public float getFloatValue() {
        String str = getText().toString();
        float value = str.length() == 0 ? 0 : Float.parseFloat(str);
        if(mMinMax!=null){
            if(value<mMinMax[0])
                value = mMinMax[0];
            else if(value>mMinMax[1])
                value = mMinMax[1];
        }
        return value;
    }

    /**
     * 如果输入为数字类型，设置当前数字大小
     */
    public LimitEditText setFloatValue(float value) {
        setText(String.valueOf(value));
        return  this;
    }

    /**
     * 如果输入为数字类型，获取输入的数字大小 （确保在限制范围内）
     */
    public int getIntValue() {
        String str = getText().toString();
        int value = str.length() == 0 ? 0 : Integer.parseInt(str);
        if(mMinMax!=null){
            if(value<mMinMax[0])
                value = (int) mMinMax[0];
            else if(value>mMinMax[1])
                value = (int) mMinMax[1];
        }
        return value;
    }


    /**
     * 如果输入为数字类型，设置当前数字大小
     */
    public LimitEditText setIntValue(int value) {
        setText(String.valueOf(value));
        return this;
    }

    /**
     * 设置一个在输入内容改变之后的回调
     */
    public LimitEditText setUpdateListener(UpdateListener callback){
        mCallback = callback;
        return this;
    }

    public interface UpdateListener{
        void onUpdate(LimitEditText editText);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {TYPE_NUMBER_FLOAT,TYPE_NUMBER_INT})
    public @interface InputType {
    }


}
