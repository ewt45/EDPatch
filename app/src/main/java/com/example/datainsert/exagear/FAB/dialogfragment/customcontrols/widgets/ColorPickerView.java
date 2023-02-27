//package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.ComposeShader;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PointF;
//import android.graphics.PorterDuff;
//import android.graphics.RadialGradient;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Shader;
//import android.graphics.SweepGradient;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
////import com.t.don.tiny.colorwheelpicker.R;
//
//
//public class ColorPickerView extends View {
//    private static final float RADIUS_WIDTH_RATIO = 0.2583f;
//    private static final float RADIUS_WIDTH_WHITE_RATIO = 0.272f;
//    private static final float RADIUS_WIDTH_COLOR_RING_RATIO = 0.3666f;
//    private Context mContext;
//    private Paint mPaint;
//    private Paint colorWheelPaint;    // 绘制色盘
//    private Paint markerPaint;//绘制点标记颜色
//    private Paint whiteWheelPaint;//白色圆圈画笔
//    private Paint colorRingPaint;//彩色圆环画笔
//
//
//    private Bitmap colorWheelBitmap;// 彩灯色盘位图
//    private Bitmap colorRingBitmap;// 彩灯彩色圆环位图
////    private Bitmap markerBitmap;//颜色点标记图片
////    private Bitmap colorRingBtnBitmap;//彩色外圈按钮图片
//    private PointF currentPoint = new PointF();//当前点的位置
//    private PointF markerPoint = new PointF();//点标记指向的位置
//    private PointF colorRingBtnPoint = new PointF();//彩色外圈按钮所在位置
//
//    private int centerX, centerY;
//    private int colorWheelRadius;//色盘半径
//    private int whiteWheelRadius;//白色圆圈半径
//    private int colorRingRadius;//彩色圆环半径
//
//    private int ringWidth = 10;
//    private Rect mColorWheelRect;//色盘绘制区域
//    private Rect mColorRingRect;//色环绘制区域
//
//    private int currentColor;//当前选取的颜色
//    private int centerWheelX, centerWheelY;//主体圆圈中心点
//    private Matrix colorRingMatrix;//彩色圆环按钮Matrix
//
//    public ColorPickerView(Context context) {
//        this(context, null);
//    }
//
//    public ColorPickerView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//
//    }
//
//    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.mContext = context;
//        mPaint = new Paint();
//        mPaint.setAlpha(100);
//        colorWheelPaint = new Paint();
//        markerPaint = new Paint();
//        markerPaint.setAntiAlias(true);//消除锯齿
//        markerPaint.setDither(true);//防抖动
//        whiteWheelPaint = new Paint();
//        colorRingPaint = new Paint();
//        colorWheelPaint.setAntiAlias(true);//消除锯齿
//        colorWheelPaint.setDither(true);//防抖动
//        whiteWheelPaint.setAntiAlias(true);
//        whiteWheelPaint.setDither(true);
//        colorRingPaint.setAntiAlias(true);
//        colorRingPaint.setDither(true);
//        colorRingPaint.setStyle(Paint.Style.STROKE);//设置画笔为描边
//        colorRingMatrix = new Matrix();
////        markerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.combined_shape);
////        colorRingBtnBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_slider);
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        //绘制白色圆圈
//        drawWhiteWheel(canvas);
//        //绘制色盘
//        canvas.drawBitmap(colorWheelBitmap, mColorWheelRect.left, mColorWheelRect.top, null);
//        //绘制外圈彩色圆环
//        canvas.drawBitmap(colorRingBitmap, mColorRingRect.left, mColorRingRect.top, null);
//        //绘制外圈彩色圆环上的按钮
//        drawColorRingBtn(canvas);
//        //绘制点标记
//        drawMarker(canvas);
//    }
//
//    private void drawColorRingBtn(Canvas canvas) {
////        int colorRingBtnWidth = colorRingBtnBitmap.getWidth();
////        int colorRingBtnHeight = colorRingBtnBitmap.getHeight();
////        int left = centerWheelX - colorRingBtnWidth;
////        int top = centerWheelY - colorRingRadius - colorRingBtnHeight;
////        // colorRingBtnRect = new RectF(left, top, left + colorRingBtnWidth, top + colorRingBtnHeight);
////        colorRingBtnPoint.x = left + colorRingBtnWidth / 2;
////        colorRingBtnPoint.y = top + colorRingBtnHeight / 2;
////        colorRingMatrix.preTranslate(colorRingBtnPoint.x, colorRingBtnPoint.y);
////        // canvas.drawBitmap(colorRingBtnBitmap, null, colorRingBtnRect, null);
////        canvas.drawBitmap(colorRingBtnBitmap, colorRingMatrix, null);
////        colorRingMatrix.reset();
//    }
//
//
//    private void drawWhiteWheel(Canvas canvas) {
//        //绘制白色圆圈
//        whiteWheelPaint.setColor(Color.WHITE);
//        canvas.drawCircle(centerWheelX, centerWheelY, whiteWheelRadius, whiteWheelPaint);
//    }
//
//    private void drawMarker(Canvas canvas) {
////        float markerWidth = markerBitmap.getWidth();
////        float markerHeight = markerBitmap.getHeight();
////        // 指定图片在屏幕上显示的区域(原图大小)
////        float left = (markerPoint.x - (markerWidth / 2));
////        float top = (markerPoint.y - markerHeight) + markerHeight * 1 / 10;
////        RectF dst = new RectF(left, top, left + markerWidth, top + markerHeight);
////        //点标记上的颜色显示
////        float markerRadius = markerWidth / 3;
////        markerPaint.setColor(currentColor);//设置颜色
////        canvas.drawBitmap(markerBitmap, null, dst, null);
////        canvas.drawCircle(left + markerWidth / 2, top + markerWidth / 2 - 4, markerRadius, markerPaint);
//    }
//
//    @Override
//    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
//        centerX = width / 2;
//        centerY = height / 2;
//        if (height < width) {//如果最终的高度小于宽度
//            width = height;
//        }
//        colorWheelRadius = (int) (width * RADIUS_WIDTH_RATIO);
//        whiteWheelRadius = (int) (width * RADIUS_WIDTH_WHITE_RATIO);
//        colorRingRadius = (int) (width * RADIUS_WIDTH_COLOR_RING_RATIO);
//        //对色盘将要绘制的位置进行调整
//        centerY -= ((height / 2) - colorRingRadius) / 2;
//
//        ringWidth = whiteWheelRadius - colorWheelRadius;
//        colorRingPaint.setStrokeWidth(ringWidth);
//        mColorWheelRect = new Rect(centerX - colorWheelRadius, centerY - colorWheelRadius, centerX + colorWheelRadius, centerY + colorWheelRadius);
//        mColorRingRect = new Rect(centerX - colorRingRadius - ringWidth / 2, centerY - colorRingRadius - ringWidth / 2, centerX + colorRingRadius + ringWidth / 2, centerWheelY + colorRingRadius + ringWidth / 2);
//        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);
//        colorRingBitmap = createColorRingBitmap(colorRingRadius * 2 + ringWidth, colorRingRadius * 2 + ringWidth);
//        //色盘中心位置
//        centerWheelX = mColorWheelRect.left + colorWheelRadius;
//        centerWheelY = mColorWheelRect.top + colorWheelRadius;
//        //默认设置点标记坐标在圆心
//        markerPoint.x = centerWheelX;
//        markerPoint.y = centerWheelY;
//    }
//
//    //创建彩色圆环bitmap
//    private Bitmap createColorRingBitmap(int width, int height) {
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        int colorCount = 12;
//        int colorAngleStep = 360 / colorCount;
//        int colors[] = new int[colorCount + 1];
//        float hsv[] = new float[]{0f, 1f, 1f};
//        for (int i = 0; i < colors.length; i++) {
//            hsv[0] = 360 - (i * colorAngleStep) % 360;
//            colors[i] = Color.HSVToColor(hsv);
//        }
//        colors[colorCount] = colors[0];
//        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
//        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorRingRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
//        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
//        colorRingPaint.setShader(composeShader);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawCircle(width / 2, height / 2, colorRingRadius, colorRingPaint);
//        return bitmap;
//    }
//
//    //创建色盘Bitmap
//    private Bitmap createColorWheelBitmap(int width, int height) {
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        int colorCount = 12;
//        int colorAngleStep = 360 / colorCount;
//        int colors[] = new int[colorCount + 1];
//        float hsv[] = new float[]{0f, 1f, 1f};
//        for (int i = 0; i < colors.length; i++) {
//            hsv[0] = 360 - (i * colorAngleStep) % 360;
//            colors[i] = Color.HSVToColor(hsv);
//        }
//        colors[colorCount] = colors[0];
//        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
//        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
//        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
//        colorWheelPaint.setShader(composeShader);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);
//
//        //默认取圆心颜色
//        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y);
//        return bitmap;
//    }
//
//    private static int colorTmp;///用于判断颜色是否发生改变
//    private PointF downPointF = new PointF();//按下的位置
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                colorTmp = currentColor;
//                downPointF.x = event.getX();
//                downPointF.y = event.getY();
//            case MotionEvent.ACTION_MOVE:
//                update(event);
//                return true;
//            case MotionEvent.ACTION_UP:
//                if (colorTmp != currentColor) {
//                    onColorPickerChanger();
//                }
//                break;
//            default:
//                return true;
//        }
//        return super.onTouchEvent(event);
//    }
//
//
//    private void update(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        updateSelector(x, y);
//        updateRingSelector(x, y);
//    }
//
//
//    /**
//     *  根据坐标获取颜色
//     * @param eventX
//     * @param eventY
//     * @return
//     */
//    private int getColorAtPoint(float eventX, float eventY) {
//        float x = eventX - centerWheelX;
//        float y = eventY - centerWheelY;
//        double r = Math.sqrt(x * x + y * y);
//        float[] hsv = {0, 0, 1};
//        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
//        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / colorWheelRadius)));
//        return Color.HSVToColor(hsv);
//    }
//
//    /**
//     * 根据坐标获取HSV颜色值
//     * @param eventX
//     * @param eventY
//     * @return
//     */
//    private float[] getHSVColorAtPoint(float eventX, float eventY) {
//        float x = eventX - centerWheelX;
//        float y = eventY - centerWheelY;
//        double r = Math.sqrt(x * x + y * y);
//        float[] hsv = {0, 0, 1};
//        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
//        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / colorWheelRadius)));
//        return hsv;
//
//    }
//
//
//    /**
//     * 刷新s色盘所选择的颜色
//     * @param eventX
//     * @param eventY
//     */
//    private void updateSelector(float eventX, float eventY) {
//        float x = eventX - centerWheelX;
//        float y = eventY - centerWheelY;
//        double r = Math.sqrt(x * x + y * y);
//        //判断是否在圆内
//        if (r > colorWheelRadius) {
//            //不在圆形范围内
//            return;
//        }
//        //旋转外圈滑动按钮
//        colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, eventX, eventY), centerWheelX, centerWheelY);
//        currentPoint.x = x + centerWheelX;
//        currentPoint.y = y + centerWheelY;
//        markerPoint.x = currentPoint.x;//改变点标记位置
//        markerPoint.y = currentPoint.y;
//        currentColor = getColorAtPoint(eventX, eventY);//获取到的颜色
//        invalidate();
//    }
//
//    /**
//     * 刷新色环选择
//     *
//     * @param eventX
//     * @param eventY
//     */
//    private void updateRingSelector(float eventX, float eventY) {
//        float x = downPointF.x - centerWheelX;
//        float y = downPointF.y - centerWheelY;
//        double r = Math.sqrt(x * x + y * y);//按下位置的半径
//        //判断是否在圆内,或者色环上
//        if ((r < colorRingRadius + ringWidth && r > colorRingRadius - ringWidth)) {
//            colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, eventX, eventY), centerWheelX, centerWheelY);
//            currentColor = getColorAtPoint(eventX, eventY);//int值颜色
//            float[] hsv = getHSVColorAtPoint(eventX, eventY);//hsv值颜色
//            float h = hsv[0];//hsv色盘色点角度
//            float s = hsv[1];//hsv色盘色点相对于半径的比值
//            float colorDotRadius = colorWheelRadius * s;//色点半径
//            //根据角度和半径获取坐标
//            float radian = (float) Math.toRadians(-h);
//            float colorDotX = (float) (centerWheelX + Math.cos(radian) * colorDotRadius);
//            float colorDotY = (float) (centerWheelY + Math.sin(radian) * colorDotRadius);
//            markerPoint.x = colorDotX;
//            markerPoint.y = colorDotY;
//            invalidate();
//        }
//    }
//
//
//    /**
//     * 获取两条线的夹角
//     *
//     * @param centerX
//     * @param centerY
//     * @param xInView
//     * @param yInView
//     * @return
//     */
//    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
//        double rotation = 0;
//        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
//        double k2 = (double) (yInView - centerY) / (xInView - centerX);
//        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;
//
//        if (xInView > centerX && yInView < centerY) {  //第一象限
//            rotation = 90 - tmpDegree;
//        } else if (xInView > centerX && yInView > centerY) //第二象限
//        {
//            rotation = 90 + tmpDegree;
//        } else if (xInView < centerX && yInView > centerY) { //第三象限
//            rotation = 270 - tmpDegree;
//        } else if (xInView < centerX && yInView < centerY) { //第四象限
//            rotation = 270 + tmpDegree;
//        } else if (xInView == centerX && yInView < centerY) {
//            rotation = 0;
//        } else if (xInView == centerX && yInView > centerY) {
//            rotation = 180;
//        }
//
//        return (int) rotation;
//    }
//
//    private void onColorPickerChanger() {
//        if (onColorPickerChangerListener != null) {
//            int red = (currentColor & 0xff0000) >> 16;
//            int green = (currentColor & 0x00ff00) >> 8;
//            int blue = (currentColor & 0x0000ff);
//            onColorPickerChangerListener.onColorPickerChanger(currentColor, red, green, blue);
//        }
//
//    }
//
//    public void setColor(int color) {
//        float[] hsv = {0, 0, 1};
//        Color.colorToHSV(color, hsv);
//        //根据hsv角度及半径获取坐标
//        //根据角度和半径获取坐标
//        float radian = (float) Math.toRadians(-hsv[0]);
//        float colorDotRadius = hsv[1] * colorWheelRadius;
//        float colorDotX = (float) (centerWheelX + Math.cos(radian) * colorDotRadius);
//        float colorDotY = (float) (centerWheelY + Math.sin(radian) * colorDotRadius);
//        //设置marker位置
//        markerPoint.x = colorDotX;
//        markerPoint.y = colorDotY;
//        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y);//设置当前颜色
//        //设置色环按钮位置
//        colorRingMatrix.preRotate(getRotationBetweenLines(centerWheelX, centerWheelY, markerPoint.x, markerPoint.y), centerWheelX, centerWheelY);
//        invalidate();
//        // paint.setColor(Color.rgb(red, green, blue)); 
//    }
//
//    public int getColor() {
//        return currentColor;
//    }
//
//    public int[] getColorRGB() {
//        return new int[]{
//                (currentColor & 0xff0000) >> 16,
//                (currentColor & 0x00ff00) >> 8,
//                (currentColor & 0x0000ff)
//        };
//    }
//
//
//    private OnColorPickerChangerListener onColorPickerChangerListener;//颜色选择变化监听
//
//    public void setOnColorPickerChangerListener(OnColorPickerChangerListener onColorPickerChangerListener) {
//        this.onColorPickerChangerListener = onColorPickerChangerListener;
//    }
//
//    public interface OnColorPickerChangerListener {
//        void onColorPickerChanger(int currentColor, int red, int green, int blue);
//    }
//
//}