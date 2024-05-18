package com.example.datainsert.exagear.controlsV2.widget;

import static android.graphics.drawable.GradientDrawable.OVAL;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;
import static android.view.Gravity.CENTER_VERTICAL;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.QH;

import java.util.Arrays;
import java.util.Collections;

/**
 * 创建一个选择颜色的视图
 *
 * hsv和hex，只能同时更改一种。
 * 设置一个flag，0为谁都不更新，1为hsv数值变化时修改结果。2为hex数值变化时修改结果。
 * 使用hsv时，在数值变化的回调中修改结果颜色，edittext显示文字， edittext处于禁用状态。
 * 使用hex时，edittext处于启用状态，只有启用时才会修改结果颜色。
 */
public class ColorPicker extends LinearLayout {
    private static final int EDIT_NONE = 0, EDIT_HSV = 1, EDIT_HEX = 2;
    static final int max_color_count = 255 * 255 * 255;
    static final int max_alpha_count = 255;
    static final int strokeWidth = AndroidHelpers.dpToPx(2);
    static final int thumbSize = AndroidHelpers.dpToPx(18);
    private final ColoredSeekbar mSeekH, mSeekS, mSeekV, mSeekA;
    private final ImageView mImageResult;
    private final LimitEditText mEditHex;
    private int editFlag = EDIT_NONE;
    private final OnColorChangeListener mChangeListener;

    public ColorPicker(Context c, int initColor, OnColorChangeListener listener) {
        super(c);
        setOrientation(HORIZONTAL);
        mChangeListener = listener;

        int colorCount = 36;
        int step = 360 / colorCount;
        int[] colorsMain = new int[colorCount + 1];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colorsMain.length; i++) {
            hsv[0] = (i * step) % 360;
            colorsMain[i] = Color.HSVToColor(hsv);
        }
        colorsMain[colorCount] = colorsMain[0];

        mSeekH = new ColoredSeekbar(c, max_color_count, colorsMain) {

            private int getColorValue() {
                return Color.HSVToColor(new float[]{getHSVValue(), 1f, 1f});
            }

            @Override
            protected void updateThumbDrawable() {
                int color = getColorValue();
                mSeekS.setBaseColor(color);
                mSeekV.setBaseColor(color);
                mSeekA.setBaseColor(color);
                ((GradientDrawable) mSeekH.getThumb()).setColor(color);
            }

            @Override
            public float getHSVValue() {
                return 360f * getProgress() / max_color_count;
            }

            @Override
            public void setHSVValue(float value) {
                setProgress((int) (max_color_count * value / 360f));
            }
        };

        //0-1 越小越白。默认1
        mSeekS = new ColoredSeekbar(c, 100) {

            @Override
            protected void updateProgressDrawable(int color) {
                ((GradientDrawable) getProgressDrawable()).setColors(new int[]{0xffffffff, color});
            }

            @Override
            protected void updateThumbDrawable() {
                int compositeColors = ColorUtils.compositeColors((int) (0xff * (1 - getHSVValue())) << 24 | 0x00ffffff, mColor);
                ((GradientDrawable) getThumb()).setColor(compositeColors);
                ((GradientDrawable) getThumb()).setStroke(strokeWidth, Color.BLACK);
            }

            @Override
            public float getHSVValue() {
                return getProgress() / 100f;
            }

            @Override
            public void setHSVValue(float value) {
                setProgress((int) (value * 100));
            }
        };

        // 0-1 越小越黑.默认1
        mSeekV = new ColoredSeekbar(c, 100) {

            @Override
            protected void updateProgressDrawable(int color) {
                ((GradientDrawable) getProgressDrawable()).setColors(new int[]{0xff000000, color});
            }

            @Override
            protected void updateThumbDrawable() {
                //加一层半透明覆盖的，用ColorUtils.compositeColors。 blendArgb不行
                int compositeColors = ColorUtils.compositeColors((int) (0xff * (1 - getHSVValue())) << 24 & 0xff000000, mColor);
                ((GradientDrawable) getThumb()).setColor(compositeColors);
            }

            @Override
            public float getHSVValue() {
                return getProgress() / 100f;
            }

            @Override
            public void setHSVValue(float value) {
                setProgress((int) (value * 100));
            }
        };

        //0-255 越小越透明，默认255
        mSeekA = new ColoredSeekbar(c, 255) {
            GradientDrawable mProgressGradient;

            @Override
            protected Drawable createProgressDrawable(int[] colors) {
                mProgressGradient = createGradientDrawable(new int[]{0, 0});
                return wrapAlphaAlertBg(getContext(), mProgressGradient, 400);
            }


            @Override
            protected void updateProgressDrawable(int color) {
                mProgressGradient.setColors(new int[]{0x00ffffff & color, color});
            }

            @Override
            protected void updateThumbDrawable() {
                int compositeColors = ((int) (0xff * getProgress() / 255f) << 24 & 0xff000000) | (mColor & 0x00ffffff);
                ((GradientDrawable) getThumb()).setColor(compositeColors);
                ((GradientDrawable) getThumb()).setStroke(strokeWidth, Color.BLACK);
            }

            @Override
            public float getHSVValue() {
                return 0;
            }

            @Override
            public void setHSVValue(float value) {}
        };

        mImageResult = new ImageView(c);
        GradientDrawable resultD = new GradientDrawable();
        resultD.setColor(Color.BLACK);
        resultD.setCornerRadius(10);
//        resultD.setSize(QH.px(c,72),QH.px(c,36));
        mImageResult.setImageDrawable(resultD);
        mImageResult.setBackground(DrawableMosaic.repeatedBitmapDrawable(c.getResources(), 10));

        mEditHex = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_HEX_COLOR_ARGB)
                .setHexColorARGBValue(initColor)
                .setUpdateListener(editText -> {
                    //如果自身不处于编辑状态，则仅供显示hex数值，不会触发回调。
                    if(editFlag == EDIT_HEX)
                        updateResult(editText.getHexColorARGBValue());
                });
        mEditHex.setEnabled(false);

        TextView btnDoneHex = QH.TV.one(c).button().bold().text16Sp().to();
        boolean[] hexEditing = {false};
        btnDoneHex.setText("✎");
        btnDoneHex.setOnClickListener(v -> {
            hexEditing[0] = !hexEditing[0];
            btnDoneHex.setText(hexEditing[0] ? "✓" : "✎"); //✔  ✓
            mEditHex.setEnabled(hexEditing[0]);
            mSeekH.setEnabled(!hexEditing[0]);
            mSeekS.setEnabled(!hexEditing[0]);
            mSeekV.setEnabled(!hexEditing[0]);
            mSeekA.setEnabled(!hexEditing[0]);
            editFlag = hexEditing[0] ? EDIT_HEX : EDIT_HSV;
        });

        LinearLayout linearHex = new LinearLayout(c);
        linearHex.setOrientation(HORIZONTAL);
        linearHex.addView(mEditHex, QH.LPLinear.one(0, -2).weight().to());
        linearHex.addView(btnDoneHex, QH.LPLinear.one(-2, -2).left().right().to());

//        LinearLayout linearResults = new LinearLayout(c);
//        linearResults.setOrientation(HORIZONTAL);
//        linearResults.addView(mImageResult, QH.LPLinear.one(0, QH.px(c, 36)).weight(1).gravity(CENTER_VERTICAL).to());
//        linearResults.addView(QH.getOneLineWithTitle(c, "RGB", mEditHex, false), QH.LPLinear.one(0, -2).weight(2).gravity(CENTER_VERTICAL).left(margin8 * 2).to());

        LayoutParams seekParams = new LayoutParams(-1, -2);
        seekParams.topMargin = dp8;

        LinearLayout linearHsvHex = new LinearLayout(c);
        linearHsvHex.setOrientation(VERTICAL);
        linearHsvHex.addView(getOneLine("H", mSeekH), QH.LPLinear.one(-1, -2).bottom().to());
        linearHsvHex.addView(getOneLine("S", mSeekS), QH.LPLinear.one(-1, -2).bottom().to());
        linearHsvHex.addView(getOneLine("V", mSeekV), QH.LPLinear.one(-1, -2).bottom().to());
        linearHsvHex.addView(getOneLine("A", mSeekA), QH.LPLinear.one(-1, -2).bottom().to());
        linearHsvHex.addView(getOneLine("HEX", linearHex), QH.LPLinear.one(-1, -2).to());

        ScrollView scrollHSVA = new ScrollView(c);
        scrollHSVA.addView(linearHsvHex);

        addView(mImageResult, QH.LPLinear.one(dp8*4, -1).gravity(CENTER_VERTICAL).to());
        addView(scrollHSVA, QH.LPLinear.one(0, -2).weight().left(dp8 * 2).to());

        //初始化值
        updateResult(initColor);
        //一般会自动触发H的onProgressChanged来修改SV的背景色，但是如果初始颜色就是0，则setProgress没变化不会触发回调，此时就要手动触发了
        mSeekH.onProgressChanged(mSeekH, mSeekH.getProgress(),false);

        editFlag = EDIT_HSV;
    }

    private ViewGroup getOneLine(String title, View view) {
        Context c = view.getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(HORIZONTAL);
        TextView tv = QH.TV.one(c).bold().solidColor().text16Sp().text(title).to();
        tv.setMinWidth(dp8*3); //不然HSV三个字母宽度不一样。。
        linearRoot.addView(tv, QH.LPLinear.one(-2, -2).left().gravity(CENTER_VERTICAL).to());
        linearRoot.addView(view, QH.LPLinear.one(0, -2).weight().left().right().gravity(CENTER_VERTICAL).to());
        return linearRoot;
    }

    /**
     * 在给定drawable下面再加上一个黑白格背景作为透明度提示
     * <br/> 需要注意给topDrawable设置一个宽高，否则会按照bitmap的宽高来，非常小
     */
    public static LayerDrawable wrapAlphaAlertBg(Context c, GradientDrawable topDrawable, float cornerRadius) {
        topDrawable.setCornerRadius(cornerRadius);
        return new LayerDrawable(new Drawable[]{DrawableMosaic.repeatedBitmapDrawable(c.getResources(), cornerRadius), topDrawable});
    }

    private String colorToHexString(int color) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toHexString(color & 0xffffff));
        while (builder.length() < 6)
            builder.insert(0, "0");
        return builder.toString();
    }

    private int computeColorResultFromHSV() {
        return Color.HSVToColor(mSeekA.getProgress(), new float[]{mSeekH.getHSVValue(), mSeekS.getHSVValue(), mSeekV.getHSVValue()});
    }

    /**
     * 每次刷新颜色结果显示时调用此函数。根据正在编辑的控件，修改其他控件的颜色
     */
    private void updateResult(int color) {
        ((GradientDrawable) mImageResult.getDrawable()).setColor(color);

        if(editFlag != EDIT_HSV) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            mSeekH.setHSVValue(hsv[0]);
            mSeekS.setHSVValue(hsv[1]);
            mSeekV.setHSVValue(hsv[2]);
            mSeekA.setProgress(TestHelper.getColorAlpha(color));
        }

        if(editFlag != EDIT_HEX) {
            mEditHex.setText(colorToHexString(color));
        }

        if(editFlag != EDIT_NONE)
            mChangeListener.onColorChange(color);
    }

    public interface OnColorChangeListener{
        void onColorChange(int argb);
    }
    private abstract class ColoredSeekbar extends android.support.v7.widget.AppCompatSeekBar
            implements TestHelper.SimpleSeekbarListener {
        protected int mColor;

        public ColoredSeekbar(Context context, int max, int[] colors) {
            super(context);
            setMax(max);
//            setMinimumHeight(minTouchSize);
            setProgressDrawable(createProgressDrawable(colors));
            setThumb(createThumbDrawable());
            setSplitTrack(false);
            setOnSeekBarChangeListener(this);
        }

        public ColoredSeekbar(Context context, int max) {
            this(context, max, new int[]{0, 0});
        }


        protected Drawable createProgressDrawable(int[] colors) {
            return createGradientDrawable(colors);
        }

        protected GradientDrawable createGradientDrawable(int[] colors) {
            GradientDrawable drawableProgress = new GradientDrawable(LEFT_RIGHT, colors);
            drawableProgress.setCornerRadius(40);
            drawableProgress.setDither(true);
//        drawableProgress.setSize(QH.px(c,16),QH.px(c,16));
            return drawableProgress;
        }

        protected Drawable createThumbDrawable() {
            GradientDrawable drawableThumb = new GradientDrawable();
            drawableThumb.setColor(Color.TRANSPARENT);
            drawableThumb.setShape(OVAL);
            drawableThumb.setStroke(strokeWidth, Color.WHITE);
            drawableThumb.setSize(thumbSize, thumbSize);//必须要设置宽高否则只有1像素
            return drawableThumb;
        }

        /**
         * 标准色更新。调用此函数刷新进度条的颜色和thumb的颜色
         */
        public void setBaseColor(int color) {
            mColor = color;
            updateProgressDrawable(color);
            updateThumbDrawable();
        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateThumbDrawable();
            if(fromUser && editFlag == EDIT_HSV)
                updateResult(computeColorResultFromHSV());
        }

        protected void updateProgressDrawable(int color) { }

        abstract protected void updateThumbDrawable();
//        /** 当onProgressChanged被调用时，如果传入参数fromUser为true，则调用此函数更新数据*/
//         protected void updateValueFromUser(int progress){};


        /**
         * 调用此函数获取此seekbar对应hsv分量，存入数组中
         */
        abstract public float getHSVValue();

        /**
         * 调用此函数调整进度位置
         */
        abstract public void setHSVValue(float value);

    }
}
