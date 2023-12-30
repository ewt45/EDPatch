package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.colorpicker;

import static android.graphics.Shader.TileMode.REPEAT;
import static android.graphics.drawable.GradientDrawable.OVAL;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;
import static android.view.Gravity.CENTER_VERTICAL;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.margin8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.ColorUtils;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.eltechs.axs.helpers.AndroidHelpers;
import com.eltechs.ed.R;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.QH;

/**
 * 创建一个选择颜色的视图
 */
public class ColorPicker extends LinearLayout {
    static final int max_color_count = 255 * 255 * 255;
    static final int max_alpha_count = 255;
    static final int strokeWidth = AndroidHelpers.dpToPx(2);
    static final int thumbSize = AndroidHelpers.dpToPx(18);
    private final ColoredSeekbar mSeekH, mSeekS, mSeekV;
    private final ColoredSeekbar mSeekA;
    private final ImageView mImageResult;
    private final EditText mEditHex;
    private final OnColorChangeListener mChangeListener;

    public ColorPicker(Context context, int initColor, OnColorChangeListener listener) {
        super(context);
        setOrientation(HORIZONTAL);
        mChangeListener = listener;

        Context c = context;

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
            @Override
            protected void updateProgressDrawable(int color) {

            }


            private int getColorValue() {
                return Color.HSVToColor(new float[]{getHSVValue(), 1f, 1f});
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                int color = getColorValue();
                mSeekS.setBaseColor(color);
                mSeekV.setBaseColor(color);
                mSeekA.setBaseColor(color);
                updateResultAndHex();
            }

            @Override
            protected void updateThumbDrawable() {
                ((GradientDrawable) mSeekH.getThumb()).setColor(getColorValue());
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                updateResultAndHex();
            }

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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                updateResultAndHex();
            }

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
//            GradientDrawable mThumbGradient;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                updateResultAndHex();
            }

            @Override
            protected Drawable createProgressDrawable(int[] colors) {
                mProgressGradient = createGradientDrawable(new int[]{0, 0});
                return wrapAlphaAlertBg(getContext(), mProgressGradient);
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
            public void setHSVValue(float value) {
            }
        };

        //TODO scrollview的时候 手机上边距还是太大。
        // 颜色结果要不直接铺满整个视图背景吧，不然滚动的话可能看不到了
        mImageResult = new ImageView(c);
        GradientDrawable resultD = new GradientDrawable();
        resultD.setColor(Color.BLACK);
        resultD.setCornerRadius(10);
//        resultD.setSize(QH.px(c,72),QH.px(c,36));
        mImageResult.setImageDrawable(resultD);
        mImageResult.setBackground(wrapAlphaAlertBg(c, new GradientDrawable()));

        mEditHex = new EditText(c);
        mEditHex.setText("000000");
        mEditHex.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditHex.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        mEditHex.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            for (int i = 0; i < s.length(); i++) {
                char c1 = s.charAt(i);
                if ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f') || (c1 >= 'A' && c1 <= 'F'))
                    continue;
                s.delete(i, i + 1);
                return; //只要更改一处，就不往下走了，因为这次更改会再触发一次监听
            }
        });
        //TODO 不仅手动输入的时候会进入监听，拖拽seekbar的时候也会改变文字进入监听。要不改成输入法确定？或者平时textview，点击变edittext。
        // 这个设置会修改seekH的progress，触发监听导致又修改了EditText的值，导致输入和输出不等，不过还好差的不多
        mEditHex.setOnEditorActionListener((v, actionId, event) -> {
            String s = v.getText().toString();
            updateResultAndHSV(0xff000000 | Integer.valueOf(s.length() == 0 ? "0" : s, 16));
            return true;
        });

//        LinearLayout linearResults = new LinearLayout(c);
//        linearResults.setOrientation(HORIZONTAL);
//        linearResults.addView(mImageResult, QH.LPLinear.one(0, QH.px(c, 36)).weight(1).gravity(CENTER_VERTICAL).to());
//        linearResults.addView(QH.getOneLineWithTitle(c, "RGB", mEditHex, false), QH.LPLinear.one(0, -2).weight(2).gravity(CENTER_VERTICAL).left(margin8 * 2).to());

        LayoutParams seekParams = new LayoutParams(-1, -2);
        seekParams.topMargin = margin8;

        LinearLayout linearHSVA = new LinearLayout(c);
        linearHSVA.setOrientation(VERTICAL);
        linearHSVA.addView(QH.getOneLineWithTitle(c, "H", mSeekH, false));
        linearHSVA.addView(QH.getOneLineWithTitle(c, "S", mSeekS, false), seekParams);
        linearHSVA.addView(QH.getOneLineWithTitle(c, "V", mSeekV, false), seekParams);
        linearHSVA.addView(QH.getOneLineWithTitle(c, "A", mSeekA, false), seekParams);

//        addView(linearResults);
        addView(mImageResult, QH.LPLinear.one(QH.px(c, 32), QH.px(c, 64)).gravity(CENTER_VERTICAL).to());
        addView(linearHSVA, QH.LPLinear.one(0, -2).weight().left(margin8 * 2).to());

        //初始化值
        float[] initHsv = new float[3];
        Color.colorToHSV(initColor, initHsv);
        int hValueBefore = mSeekH.getProgress();
        mSeekH.setHSVValue(initHsv[0]);
        //一般会自动触发H的onProgressChanged来修改SV的背景色，但是如果没变化，就要手动触发了
        if(mSeekH.getProgress() == hValueBefore)
            mSeekH.onProgressChanged(mSeekH,hValueBefore,false);
        mSeekS.setHSVValue(initHsv[1]);
        mSeekV.setHSVValue(initHsv[2]);
        mSeekA.setProgress(((initColor & 0xff000000) >> 24) & 0x00ff);
    }

    public static LayerDrawable wrapAlphaAlertBg(Context c, GradientDrawable topDrawable) {
        Drawable bgAlertSingleDrawable = c.getDrawable(R.drawable.alpha_bg);
        assert bgAlertSingleDrawable != null;
        Bitmap bgAlertSingleBitmap = Bitmap.createBitmap(bgAlertSingleDrawable.getIntrinsicWidth(), bgAlertSingleDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas tmpCanvas = new Canvas(bgAlertSingleBitmap);
        bgAlertSingleDrawable.setBounds(0, 0, bgAlertSingleBitmap.getWidth(), bgAlertSingleBitmap.getHeight());
        bgAlertSingleDrawable.draw(tmpCanvas);
        BitmapDrawable bgAlertRepeatDrawable = new BitmapDrawable(c.getResources(), bgAlertSingleBitmap);
        bgAlertRepeatDrawable.setTileModeXY(REPEAT, REPEAT);
        return new LayerDrawable(new Drawable[]{bgAlertRepeatDrawable, topDrawable});
    }


    private String colorToHexString(int color) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toHexString(color & 0xffffff));
        while (builder.length() < 6)
            builder.insert(0, "0");
        return builder.toString();
    }

    private int computeColorResult() {
        return Color.HSVToColor(mSeekA.getProgress(), new float[]{mSeekH.getHSVValue(), mSeekS.getHSVValue(), mSeekV.getHSVValue()});
    }

    /**
     * 每次刷新颜色结果显示时调用此函数。
     */
    private void updateResultAndHex() {
        int color = computeColorResult();
        ((GradientDrawable) mImageResult.getDrawable()).setColor(color);
        mEditHex.setText(colorToHexString(color));

        mChangeListener.onColorChange(color);
    }

    /**
     * 每次刷新颜色结果显示时调用此函数。
     * @param color 请确保透明度为ff
     */
    private void updateResultAndHSV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        mSeekH.setHSVValue(hsv[0]);
        mSeekS.setHSVValue(hsv[1]);
        mSeekV.setHSVValue(hsv[2]);

        mChangeListener.onColorChange(color);
    }

    public interface OnColorChangeListener{
        void onColorChange(int argb);
    }
    private abstract static class ColoredSeekbar extends android.support.v7.widget.AppCompatSeekBar
            implements TestHelper.SimpleSeekbarListener {
        protected int mColor;

        public ColoredSeekbar(Context context, int max, int[] colors) {
            super(context);
            setMax(max);
//            setMinimumHeight(minTouchSize);
            setProgressDrawableTiled(createProgressDrawable(colors));
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
        }

        abstract protected void updateProgressDrawable(int color);

        abstract protected void updateThumbDrawable();


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
