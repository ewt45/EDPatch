package com.example.datainsert.exagear.controlsV2;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.datainsert.exagear.controlsV2.model.OneProfile;

public class TouchAreaViewDrawable extends Drawable {
    OneProfile mProfile;

    public TouchAreaViewDrawable(OneProfile profile) {
        mProfile=profile;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Log.d("TouchAreaViewDrawable", "draw: 被调用。目前有area多少个："+mProfile.getTouchAreaList().size());
        for (TouchArea<?> touchArea : mProfile.getTouchAreaList())
            touchArea.onDraw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
