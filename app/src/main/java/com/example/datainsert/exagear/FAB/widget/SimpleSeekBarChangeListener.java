package com.example.datainsert.exagear.FAB.widget;

import android.widget.SeekBar;

/**
 * seekbar，只需要重写一个方法的listener
 */
public class SimpleSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    Callback mCallback;
    public interface Callback{
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    }

    public SimpleSeekBarChangeListener(Callback callback){
        mCallback = callback;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mCallback.onProgressChanged(seekBar,progress,fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
