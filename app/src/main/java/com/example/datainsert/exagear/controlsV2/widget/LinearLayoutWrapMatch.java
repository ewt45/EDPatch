package com.example.datainsert.exagear.controlsV2.widget;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 线性布局
 * <br/>以Vertical为例，添加的每一行子布局宽度均设置为match_parent（emmm好像也不是，看情况wrap吧）
 * ，但是最终显示效果是以wrap_content情况下最大的那个子布局的宽度为准
 * @deprecated 没啥用，带weight的linear行又不行，还不如用relativeLayout
 */
@Deprecated
public class LinearLayoutWrapMatch extends LinearLayout {
    private int mLimitSize;
    public LinearLayoutWrapMatch(Context context, int limitSize) {
        super(context);
        if(limitSize<0)
            throw new RuntimeException("限制宽度不能小于0");
        mLimitSize = limitSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isVertical = getOrientation() == VERTICAL;

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int maxSubWidth=0, maxSubHeight=0;
        for(int i=0; i<getChildCount(); i++){
            View child = getChildAt(i);
            //先将要测量的方向设置成wrap，否则测出来不是最大的
            LayoutParams param = (LayoutParams) child.getLayoutParams();

            int tmp = isVertical?param.width:param.height;
            if(isVertical) param.width = -2;
            else param.height = -2;
            child.setLayoutParams(param);

            child.measure(makeMeasureSpec(parentWidth,AT_MOST),makeMeasureSpec(parentHeight,AT_MOST));

            if(isVertical) param.width = tmp;
            else param.height = tmp;
            child.setLayoutParams(param);

            maxSubWidth = Math.max(maxSubWidth,getChildAt(i).getMeasuredWidth());
            maxSubHeight = Math.max(maxSubHeight,getChildAt(i).getMeasuredHeight());
        }

        int finalWidthSpec =  !isVertical?widthMeasureSpec:makeMeasureSpec(Math.min(maxSubWidth,mLimitSize),AT_MOST);
        int finalHeightSpec = isVertical?heightMeasureSpec:makeMeasureSpec(Math.min(maxSubHeight,mLimitSize),AT_MOST);
        super.onMeasure(finalWidthSpec,finalHeightSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(
//                !isVertical?getMeasuredWidth():TestHelper.min(maxSubWidth,mLimitSize,parentWidth),
//                isVertical?getMeasuredWidth():TestHelper.min(maxSubHeight,mLimitSize,parentHeight)
//        );
    }
}
