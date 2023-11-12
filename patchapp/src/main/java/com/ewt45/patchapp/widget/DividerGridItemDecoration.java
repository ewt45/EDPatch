package com.ewt45.patchapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.util.Log;
import android.view.View;

import com.ewt45.patchapp.AndroidUtils;
import com.ewt45.patchapp.R;

/**
 * @author zhy
 *
 * 每个item，rect 上下8dp，左右16dp。（不要了，还是默认都0吧，margin在xml里设置）
 * 如果不是最后一行，下加2dp，如果不是最后一列，右加2dp
 * 仅适用于垂直滚动布局，即左右一定显示在画面内
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "DividerItemDecoration";

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private final Drawable mDivider;
    private final int dividerMargin;
    private final int weight;
    private final int itemHorizentalMargin;
    private final int itemVerticalMargin;
    private final int spanCount;

    public DividerGridItemDecoration(Context context) {

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        dividerMargin = AndroidUtils.toPx(context, 16);
        itemHorizentalMargin = AndroidUtils.toPx(context, 16);
        itemVerticalMargin = AndroidUtils.toPx(context, 8);
        weight = AndroidUtils.toPx(context, 2);

        spanCount = context.getResources().getInteger(R.integer.patch_step_func_item_column);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull State state) {
//        parent.getLayoutManager().getDecoratedBoundsWithMargins();
        //mDivider.getIntrinsicHeight(); 这个没用上，自己定义了2dp

        //为什么要save和restore呢（原来保存的是缩放，平移这种画布信息，不是图案）
        canvas.save();
        canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getWidth() - parent.getPaddingRight(), parent.getHeight() - parent.getPaddingBottom());
        int childCount = parent.getChildCount();
        int itemCount = state.getItemCount();

        for(int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            int adapterPos =  parent.getChildAdapterPosition(child);//绝对位置
            if(isLastRow(itemCount,spanCount,adapterPos)) //最后一行的不加分割线
                continue;
            parent.getDecoratedBoundsWithMargins(child, this.mBounds); //这个是包括刚才getItemOffsets加进去的空余区域
            int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
            int top = bottom - weight;
            int dividerLeft = mBounds.left+dividerMargin,dividerRight = mBounds.right-dividerMargin;
            //默认divider这里是从recycler左一直滑到recycler右，所以两列的话中间不断开。改成item左到item右就行了
            this.mDivider.setBounds(dividerLeft, top, dividerRight, bottom);
            this.mDivider.draw(canvas);
        }
        canvas.restore();
    }


    private final Rect mBounds = new Rect();


    private boolean isLastRow(int itemCount, int spanCount, int itemPosition){
        return itemPosition >= itemCount - Math.max(itemCount % spanCount,spanCount);//两列的话，pos=0和1都是在第一行。 一共有5个或者6个，pos=4，5在最后一行
    }

    private boolean isLastColumn(int spanCount, int itemPosition){
        return (itemPosition + 1) % spanCount == 0;
    }


    /**
     * Retrieve any offsets for the given item. Each field of outRect specifies the
     * number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     * <p>
     * If this ItemDecoration does not affect the positioning of item views,
     * it should set all four fields of outRect (left, top, right, bottom) to zero before returning.
     * <p>
     * If you need to access Adapter for additional data,
     * you can call getChildAdapterPosition(View) to get the adapter position of the View.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {

        int itemPosition = parent.getChildLayoutPosition(view);
//        int spanCount = getSpanCount(parent);
        int itemCount = state.getItemCount(); //这个貌似一直返回的是全部个数，而非在画面内的个数
        Log.d(TAG, String.format("getItemOffsets: itemPosition=%d, spanCount=%d, itemCount=%d",itemPosition,spanCount,itemCount));
        outRect.set(0, 0, 0, 0);
        //如果不是最下面的item，就在item下面绘制水平分割线
        if(!isLastRow(itemCount,spanCount,itemPosition))
            outRect.bottom =  weight ;
        //每一列之间的宽度。每个item左右16dp margin，如果不是最右列，item右侧再多一个分割线的宽度
        if(!isLastColumn(spanCount,itemPosition) )
            outRect.right = weight;
    }

}
