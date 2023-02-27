package com.example.datainsert.exagear.controls.interfaceOverlay.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.eltechs.axs.xserver.Drawable;

/**
 * 用于绘制摇杆按钮的样式
 */
public class JoyStickPainter {
    private Paint mOutLine;//外轮廓
    Drawable mBtnDrawable; //按钮样式
    public JoyStickPainter(Context c){
        mOutLine=new Paint();
        SharedPreferences sp;

        mOutLine.setColor(Color.WHITE);
    }

    public void draw(Canvas canvas, int x, int y){
        //绘制边框

//        canvas.drawCircle(centerWheelX, centerWheelY, whiteWheelRadius, whiteWheelPaint);
    }
}
