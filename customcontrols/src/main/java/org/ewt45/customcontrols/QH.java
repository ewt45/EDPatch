package org.ewt45.customcontrols;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static org.ewt45.customcontrols.QH.dimen.margin8Dp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class QH {


    /**
     * dialog的自定义视图，最外部加一层Nested滚动视图，并且添加padding<br/>
     * 滚动视图会获取焦点，以阻止edittext自动弹出输入法，和解决自动滚动到回收视图的位置而非第一个视图位置的问题
     */
    public static NestedScrollView wrapAsDialogScrollView(View view) {
        Context c = view.getContext();
        NestedScrollView scrollView = new NestedScrollView(c);
        scrollView.setPadding(dialogPadding(c), 0, dialogPadding(c), 0);
        scrollView.addView(view);
        //阻止edittext获取焦点弹出输入法 / 回收视图获取焦点自动滚动到回收视图的位置
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.requestFocus();
        return scrollView;
    }

    public static int dialogPadding(Context c) {
        return QH.px(c, 24);
    }

    public static int px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static LinearLayout getOneLineWithTitle(Context c, @Nullable String title, @Nullable View view, boolean vertical) {
        LinearLayout linearLayout = new LinearLayout(c);
        linearLayout.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        if (title != null && !title.equals(""))
            linearLayout.addView(getTitleTextView(c, title));

        if (view != null) {
            LinearLayout.LayoutParams params = view.getLayoutParams() != null
                    ? new LinearLayout.LayoutParams(view.getLayoutParams())
                    : new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (linearLayout.getChildCount() > 0)
                params.setMarginStart(margin8Dp(c));
            if (vertical)
                params.topMargin = margin8Dp(c);
            linearLayout.addView(view, params);
        }
        linearLayout.setPadding(0, margin8Dp(c), 0, 0);
        return linearLayout;
    }

    public static TextView getTitleTextView(Context c, String title) {
        TextView textView = new TextView(c);
        textView.setText(title);
        textView.setTextColor(attr.textColorPrimary(c));
        //加粗一下吧
        textView.getPaint().setFakeBoldText(true);
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.invalidate();
        return textView;
    }

    public static class attr {

        public static int textColorPrimary(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
            int color = array.getColor(0, Color.BLACK);
            array.recycle();
            return color;
        }

        public static int colorPrimary(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
            int color = array.getColor(0, 0xff2196F3);
            array.recycle();
            return color;
        }

        public static Drawable selectableItemBackground(Context c) {
            TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
            Drawable d = array.getDrawable(0);
            array.recycle();
            return d;
        }

    }

    public static class dimen {

        public static int margin8Dp(Context c) {
            return QH.px(c, 8);
        }

        /**
         * 48dp对应的px值
         */
        public static int minCheckSize(Context c) {
            return QH.px(c, 48);
        }

        /**
         * 24dp对应的px值
         */
        public static int dialogPadding(Context c) {
            return QH.px(c, 24);
        }

    }

    public interface SimpleSeekChangeListener extends SeekBar.OnSeekBarChangeListener {
        @Override
        default void onStartTrackingTouch(SeekBar seekBar){};

        @Override
        default void onStopTrackingTouch(SeekBar seekBar){};
    }

    public static class LPLinear {
        int w=-1;
        int h=-2;
        float weight=0;
        int[] margins = {0,0,0,0};
        int gravity=-111;
        private Context c;
        public LPLinear(Context context){
         this.c = context;
        }

        /**
         * 宽为match，高为rap
         */
        public static LPLinear one(Context c){
            return new LPLinear(c);
        }
        public static LPLinear one(Context c,int w, int h){
            LPLinear linear= new LPLinear(c);
            linear.w=w;
            linear.h=h;
            return linear;
        }
        public LPLinear gravity(int pg){
            gravity = pg;
            return this;
        }

        public LPLinear weight(float pw){
            weight=pw;
            return this;
        }

        public LPLinear weight(){
            weight=1;
            return this;
        }

        /**
         * 顶部margin设为8dp
         */
        public LPLinear top(){
            margins[1] = margin8Dp(c);
            return this;
        }
        public LPLinear top(int margin){
            margins[1] = margin;
            return this;
        }
        public LPLinear left(){
            margins[0] = margin8Dp(c);
            return this;
        }
        public LPLinear left(int margin){
            margins[0] = margin;
            return this;
        }
        public LinearLayout.LayoutParams to(){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h,weight);
            params.weight = weight;
            if(gravity!=-111)
                params.gravity = gravity;
            params.setMargins(margins[0],margins[1],margins[2],margins[3]);
            this.c = null;
            return params;
        }
    }
}
