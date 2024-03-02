package com.example.datainsert.exagear.controlsV2.widget.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.datainsert.exagear.controlsV2.TestHelper;

class Main extends View {
    private static final String TAG = "Main";
    private final Rect mDrawRect = new Rect(0,0,0,0);
    private final Paint mPaint ;

    public Main(Context context) {
        super(context);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffebb8b8);




        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                invalidate();
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawRect.right = w;
        mDrawRect.bottom = h;
        resizePainter(w);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] newSpec = TestHelper.makeSquareOnMeasure(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(newSpec[0], newSpec[1]);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        canvas.save();
        canvas.drawRect(mDrawRect,mPaint);
        canvas.restore();;

    }


    private void resizePainter(int size) {
        int colorCount = 12;
        int step = 360 / colorCount;
        int[] colors = new int[colorCount + 1];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 -  i * step;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];
        LinearGradient colorGradient = new LinearGradient(0,0,0,size, colors,null, Shader.TileMode.CLAMP);
        LinearGradient maskGradient = new LinearGradient(0,0,size,0, 0x00FFFFFF, 0xffFFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(colorGradient, maskGradient, PorterDuff.Mode.SRC_OVER);
        mPaint.setShader(composeShader);



    }
}
