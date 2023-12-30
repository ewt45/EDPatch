package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.support.v4.graphics.ColorUtils.calculateContrast;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.margin8;
import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.minTouchSize;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.adapter.EditMoveAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneButton;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea.TouchAreaButton;
import com.example.datainsert.exagear.QH;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class TestHelper {
    private static final String TAG = "TestHelper";

    public static void addTouchAreas(Context c, TouchAreaView touchAreaView) {
//        OneProfile oneProfile = new OneProfile();
//
//        //最底层手势
//        oneProfile.addTouchArea(new TouchArea<OneGestureArea>(
//                touchAreaView,
//                new OneGestureArea(),
//                new ClickAdapter(0.1f, () -> showEditOptions(c))) {
//            @Override
//            public void onDraw(Canvas canvas) {
//
//            }
//        });
        //添加一个悬浮窗。点击可以展开或折叠
    }

    public static void showEditOptions(Context c) {

    }

    public static void onTouchMoveView(View touchedView, View movedView) {
        if (movedView.getParent() != null && !(movedView.getParent() instanceof FrameLayout))
            throw new RuntimeException("能够拖拽移动的视图父布局必须为FrameLayout");
        //TODO 这里设置了别的focusable会不会影响到TouchAreaView？
        touchedView.setFocusable(true);
        touchedView.setFocusableInTouchMode(true);
        touchedView.setClickable(true);

        touchedView.setOnTouchListener(new View.OnTouchListener() {
            int[] downPos = new int[2];
            int[] downXY = new int[2];

            @Override
            public boolean onTouch(View touchedV, MotionEvent event) {
                int[] latestPos = new int[]{(int) event.getRawX(), (int) event.getRawY()};
                if (!(movedView.getParent() instanceof FrameLayout && movedView.getLayoutParams() instanceof FrameLayout.LayoutParams))
                    return false;

                FrameLayout.LayoutParams paramsUpd = (FrameLayout.LayoutParams) movedView.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downPos = latestPos;
                        downXY = new int[]{paramsUpd.leftMargin, paramsUpd.topMargin};
                        touchedView.setPressed(true);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        paramsUpd.leftMargin = downXY[0] + latestPos[0] - downPos[0];
                        paramsUpd.topMargin = downXY[1] + latestPos[1] - downPos[1];
                        movedView.setLayoutParams(paramsUpd);
//                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getLeft(), v.getTop(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
//                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getBottom(), ((FrameLayout) v.getParent()).getHeight(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        touchedView.setPressed(false);
                        if (latestPos[0] == downPos[0] && latestPos[1] == downPos[1])
                            touchedView.performClick();

                        //如果移出了父视图边界，应该回到边界内
                        //TODO 应该是留在父视图内部的剩余部分，应该保持最小宽度即可，出一点边界没关系
                        // 目前只有下方和右方这么改了，左和上还是不能出界。目前这个布局没问题因为移动的按下位置就在左上角，但是为了兼容性之后还是改一下吧）

                        // 因为初始时gravity为center，导致leftMargin为0时不是贴的父视图左边，所以不能直接读写leftMargin的值

                        if (movedView.getLeft() < margin8)
                            paramsUpd.leftMargin += margin8 - movedView.getLeft();
                        if (movedView.getTop() < margin8)
                            paramsUpd.topMargin += margin8 - movedView.getTop();
                        //已经调整和左右了，如果宽高还不够就说明是太靠右或下 (出界的话width仍然是right-left，是完整的宽而非裁切后剩下的宽）
                        // 能够用left和top是因为一直在修改margin的left和top。right只有在width给定精确值时才会出界，如果是wrap_content这种，那么宽度已经开始变小了，right也不会出界。
                        FrameLayout parent = (FrameLayout) movedView.getParent();

                        //烦死了 onMeasure那里AT_MOST的话貌似又不用限制最小宽度也可能出界了。干脆右-左和 右对比父右 都看一下就行了
                        if (movedView.getRight() - movedView.getLeft() < minTouchSize)
                            paramsUpd.leftMargin -= minTouchSize - (movedView.getRight() - movedView.getLeft());
                        else if (parent.getWidth() - movedView.getLeft() < minTouchSize)
                            paramsUpd.leftMargin -= minTouchSize - (parent.getWidth() - movedView.getLeft());

                        if (movedView.getBottom() - movedView.getTop() < minTouchSize)
                            paramsUpd.topMargin -= minTouchSize - (movedView.getBottom() - movedView.getTop());
                        else if (parent.getHeight() - movedView.getTop() < minTouchSize)
                            paramsUpd.topMargin -= minTouchSize - (parent.getHeight() - movedView.getTop());
                        movedView.setLayoutParams(paramsUpd);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static TextView getTextView16sp(Context c) {
        TextView tv = new TextView(c);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return tv;
    }

    public static Button getTextButton(Context c, String text) {
        Button btn = new Button(c);
        QH.setRippleBackground(btn);
        btn.setText(text);
        btn.setAllCaps(false);
        return btn;
    }

    public static int getBGColor(Context c) {
        TypedArray array = c.obtainStyledAttributes(new int[]{android.R.attr.colorBackground});
        int color = array.getColor(0, BLACK);
        array.recycle();
        return ColorUtils.blendARGB(0xababab, color, 0.3f);

    }

    public static NestedScrollView wrapAsScrollView(View view) {
        int p = margin8 * 2;
        NestedScrollView scrollView = QH.wrapAsDialogScrollView(view);
        scrollView.setPadding(p, p, p, p);
        return scrollView;
    }


    public static NestedScrollView wrapAsScrollView(View view, int left, int top, int right, int bottom) {
        NestedScrollView scrollView = QH.wrapAsDialogScrollView(view);
        scrollView.setPadding(left, top, right, bottom);
        return scrollView;
    }

    /**
     * 在视图onMeasure时，确保宽高是相等的（变成正方形）。<br/>
     * 如果获取的宽高是-1或-2则无法正确处理
     *
     * @return 数组，第一个是宽Spec，第二个是高Spec
     */
    public static int[] makeSquareOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int oriW = getSize(widthMeasureSpec);
        int oriH = getSize(heightMeasureSpec);
        int sameSpec = makeMeasureSpec(Math.min(oriH, oriW), EXACTLY);
        return new int[]{sameSpec, sameSpec};
    }

    /**
     * 输出area的区域
     */
    public static void logTouchAreaRect(TouchAreaModel mModel) {
        Log.d(TAG, String.format("logTouchAreaRect: 左-上-右-下=%d,%d,%d,%d", mModel.getLeft(), mModel.getTop(), mModel.getLeft() + mModel.getWidth(), mModel.getTop() + mModel.getHeight()));
    }

    /**
     * 仅修改一个颜色中的透明度值。
     */
    public static int setColorAlpha(int alpha, int argb) {
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    /**
     * 根据给定颜色，返回黑色或白色（看哪个对比度更高）
     */
    public static int getContrastColor(int oriColor) {
        int alpha = oriColor & 0xff000000;
        int colorNoA = oriColor | 0xff000000;
        int pureC = calculateContrast(WHITE, colorNoA) > calculateContrast(BLACK, colorNoA) ? WHITE : BLACK;
        return (pureC & 0x00ffffff) | alpha;
    }

    /**
     * 创建一个新的触摸区域。根据model类型添加对应的adapter。<br/>
     * 注意area最终使用的model并非传入的实例，所以在调用此方法后应通过area.getModel()来获取实际的model
     * @param reference 用于提供新建触摸区域的属性，该类型不会直接作为touchArea的model，而是会根据它再新建一个，以防多一个区域共用一个model。
     */
    public static TouchArea<? extends TouchAreaModel> newAreaEditable(TouchAreaView host, TouchAreaModel reference, EditMoveAdapter.OnFocusListener focusListener) {
        if (reference instanceof OneButton) {
            OneButton finalModel = OneButton.newInstance(reference);
            return new TouchAreaButton(host, finalModel, new EditMoveAdapter(host, finalModel, focusListener));
        }

        throw new RuntimeException("无法创建该类型的TouchArea" + reference);

    }

    /**
     * 从apk/assets中读取一个编译后的安卓二进制xml，创建drawable
     * <br/> 使用的函数是 a.getAssets().openXmlResourceParser("assets/cc/ic_apk_document.xml")
     * @param c context
     * @param name 字符串，只需传入相对路径，如“cc/ic_apk_document.xml” 即可
     * @return drawable
     */
    public static Drawable getAssetsDrawable(Context c, String name) {
        try (XmlResourceParser parser = c.getAssets().openXmlResourceParser("assets/"+name)){
            return Drawable.createFromXml(c.getResources(),parser,c.getTheme());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return new ColorDrawable(0xff000000);
        }
    }

    public static interface SimpleSeekbarListener extends SeekBar.OnSeekBarChangeListener {
        @Override
        default void onStartTrackingTouch(SeekBar seekBar) {
        }

        ;

        @Override
        default void onStopTrackingTouch(SeekBar seekBar) {
        }

        ;
    }
}
