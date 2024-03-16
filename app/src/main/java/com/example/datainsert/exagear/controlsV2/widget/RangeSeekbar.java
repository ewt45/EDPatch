package com.example.datainsert.exagear.controlsV2.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

/**
 * rawValue:原始pogress
 * <br/> value:通过变换后得到的实际有意义的数值
 */
public abstract class RangeSeekbar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {
    private int mRawValue;
    private final int mRawOffset;

    OnValueChangeListener mListener;
    /**
     * @param minValue 最小值，转换后的数值
     * @param maxValue 最大值，转换后的数值
     */
    public RangeSeekbar(Context context, int minValue, int maxValue) {
        super(context);

        //
        // y = 2x+10 y:[20,30]
        // 那么x[5,10] x为rawValue，y为finalValue
        //这里应该传入y的范围，然后setmin max是x的范围

        int rawMin = finalToRaw(minValue);
        int rawMax = finalToRaw(maxValue);
        mRawOffset = rawMin;
        setMax(rawMax+ mRawOffset);

        setOnSeekBarChangeListener(this);
    }

    /**
     * rawValue转为value
     */
    abstract protected int rawToFinal(int rawValue);

    /**
     * value转为rawValue
     */
    abstract protected int finalToRaw(int finalValue);

//    @Override
//    public synchronized void setProgress(int progress) {
//        throw new RuntimeException("请不要使用这个方法。");
//    }
//
//    @Override
//    public synchronized int getProgress() {
//        throw new RuntimeException("请不要使用这个方法。");
//    }
//
//    @Override
//    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
//        throw new RuntimeException("请不要使用这个方法。");
//    }



    /**
     * 设置进度，传入转换后数值。请使用此方法以代替setProgress
     */
    public void setValue(int value){
        super.setProgress(finalToRaw(value)-mRawOffset);
    }

    /**
     * 获取当前转换后数值。请使用此方法以代替getProgress
     */
    public int getValue(){
        return rawToFinal(getProgress()+mRawOffset);

    }
    /**
     * 获取当前转换后数值
     */
    public float getFloatValue(){
        return 0;
    }

    /**
     * 获取当前转换前数值
     */
    public int getRawValue(){
        return getProgress();
    }

    /**
     * 数值变化时的回调，请使用此方法以代替setOnSeekbarChangeListener
     */
    public void setOnValueChangeListener(OnValueChangeListener l) {
        mListener = l;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mListener!=null)
            mListener.onValueChanged(this, rawToFinal(progress+mRawOffset),fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnValueChangeListener{
        void onValueChanged(RangeSeekbar seekbar, int value, boolean fromUser);
    }
}
