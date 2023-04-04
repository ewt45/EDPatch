package com.example.datainsert.exagear.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static String TAG= "MySurfaceView";
    private DrawThread mDrawThread;

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawThread = new DrawThread(holder.getSurface());
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawThread.stopDraw();
        try {
            mDrawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DrawThread extends Thread {
        private Surface mSurface;
        private boolean mRunning = true;
        private Paint mPaint = new Paint();

        DrawThread(Surface surface) {
            mSurface = surface;
            mPaint.setColor(Color.RED);
        }

        void stopDraw() {
            mRunning = false;
        }

        @Override
        public void run() {
            super.run();

            int left = 0;
            int top = 0;
            int width = 100;
            int height = 100;
            int verticalFlag = 5;
            int horizontalFlag = 5;
            while (mRunning) {
                Canvas canvas = mSurface.lockCanvas(null);
                canvas.drawColor(Color.WHITE);
                canvas.drawRect(left, top, left + width, top + height, mPaint);
                mSurface.unlockCanvasAndPost(canvas);
//                Log.d(TAG, String.format("run: canvas.width=%d, height=%d",canvas.getWidth(),canvas.getHeight()));


                if(((top + height) >= 400) || top<0)
                    verticalFlag = - verticalFlag;

                if(((left + width) >= 800) || left<0)
                    horizontalFlag = - horizontalFlag;

                left += horizontalFlag;
                top += verticalFlag;
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
