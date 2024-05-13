package com.example.datainsert.exagear.controlsV2.edit.gestures;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.text.DynamicLayout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class EditGestureDrawView extends View implements View.OnTouchListener {
    public int maxWidth=0, maxHeight=0;
    private final OneGestureArea mModel;
    private GradientDrawable mStateBgDrawable;
    private Paint mStateBgPaint;
    private Paint mActionBgPaint;
    //记录全部状态。LinkedList的元素代表第几列的全部状态。List<LinedState> 记录这一列的每一个状态
    // 注意可能会向前的状态转移，即后状态在之前已经作为前状态出现过。此时这个后状态不应再加入到List中
    //最后一列为空的列表
    private List<List<LState>> linkedStates = new ArrayList<>();
    private Path mStatePath = new Path();//状态框的path和转移连线的path。绘制时先画线再画框
    private Path mActionPath = new Path();
    private Path mTranPath = new Path();
    private DynamicLayout mTextLayout;
    private TextPaint mTextPaint;

    public EditGestureDrawView(Context context, OneGestureArea model) {
        super(context);
        this.mModel = model;

        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
//        setOnTouchListener(this);

        List<LState> currCol;
        List<Adjustable> adjustableList = new ArrayList<>();
        linkedStates.add(Collections.singletonList(new LState(mModel.getInitState())));
        //第一遍先加上default吧。然后如果preState是default的话就跳过

        //结束循环条件：上一次循环不再新增下一列的内容
        while (!(currCol = linkedStates.get(linkedStates.size()-1)).isEmpty()) {
            List<LState> nextCol = new ArrayList<>();
            linkedStates.add(nextCol);

            for(LState preState : currCol) {
                if(preState.state == mModel.getDefaultState())
                    continue;
                //对当前列的每个state，作为prestate，找出其所有event对应的postState，根据情况加入下一列中
                // 情况：如果postState没在现有列中出现过，则加入下一列，否则忽略
                for(int eventCode : preState.events) {
                    List<Integer> tran = mModel.getTransition(preState.state, eventCode);
                    FSMState2 postState = mModel.findStateById(tran.get(2));
                    List<Integer> actions = tran.subList(3,tran.size());
                    LState lPostState = null;
                    for(List<LState> a: linkedStates)
                        for(LState b : a)
                            if (b.state == postState) {
                                lPostState = b;
                                //如果postState所在列比pre靠左，第二遍循环考虑将其后移
                                int preColIdx = linkedStates.indexOf(currCol);
                                int postColIdx = linkedStates.indexOf(a);
                                if(postColIdx <= preColIdx) {
                                    Adjustable adjustable = new Adjustable(preState, lPostState, preColIdx, postColIdx);
                                    adjustableList.add(adjustable);
                                }
                                break;
                            }
                    if(lPostState == null) {
                        lPostState = new LState(postState);
                        nextCol.add(lPostState);
                    }
                    preState.actionsMap.put(eventCode, actions);
                    preState.postMap.put(eventCode, lPostState);
                }
            }

        }

        //第二遍把postState尽可能向后移，减少pre在后，post在前的情况
        for(Adjustable adj: adjustableList) {
            if(hasRevertPath(adj.postState, adj.preState))
                continue;
            //如果可以，将postState放到preState的下一列
            int betterPostColIdx = adj.preColIdx + 1;
            if(linkedStates.size() == betterPostColIdx) {
                linkedStates.add(new ArrayList<>());
            }
            linkedStates.get(adj.postColIdx).remove(adj.postState);
            linkedStates.get(betterPostColIdx).add(adj.postState);
        }

        //第三遍循环，将每个LState的位置信息确定下来，方便之后绘制
        for(int colIdx=0; colIdx<linkedStates.size(); colIdx++) {
            List<LState> col = linkedStates.get(colIdx);
            float frameTop = 0;
            float frameLeft = colIdx * (widthState + marginStateRightToActionLeft + widthAction + marginActionRightToNextStateLeft);
            Path colPath = new Path();
            for(int rowIdx=0; rowIdx<col.size(); rowIdx++) {
                LState lstate = col.get(rowIdx);
                lstate.colIdx = colIdx;
                lstate.pos.top = frameTop;
                lstate.pos.left = frameLeft;
                lstate.pos.right = frameLeft + widthState;
                lstate.pos.bottom = frameTop + heightStateTitle + marginEventTopToTitleBottom + (heightEventText + marginEventVertical) * lstate.events.length;

                //先确定state框的位置。下一遍循环再确定连线的位置
                colPath.addRoundRect(lstate.pos, radiusRoundRect, radiusRoundRect, Path.Direction.CW);

                frameTop = frameTop + lstate.pos.height()+ marginStateVertical;
            }
            maxHeight = (int) frameTop;
            maxWidth = (int) (frameLeft + widthState);
            mStatePath.addPath(colPath, 0, 0);
        }

        //设置自身宽高
//        setLayoutParams(new ViewGroup.LayoutParams(maxWidth, maxHeight));

        mStateBgDrawable = new GradientDrawable();
        mStateBgDrawable.setStroke(dp8/2, new ColorStateList(
                new int[][]{{android.R.attr.state_selected}, StateSet.WILD_CARD},
                new int[]{Color.BLACK,Color.BLUE}
        ));
        mStateBgDrawable.setColor(0xd0525151);

        mStateBgPaint = new Paint();
        mStateBgPaint.setStyle(Paint.Style.FILL);
        mStateBgPaint.setColor(colorStateBg);

        mActionBgPaint = new Paint();
        mActionBgPaint.setStyle(Paint.Style.FILL);
        mActionBgPaint.setColor(colorActionBg);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(colorText);
        SpannableStringBuilder builder = new SpannableStringBuilder();

//        mTextLayout = new DynamicLayout(builder, );
    }

    /**
     * 检查一个转移中的postState是否有路径通向preState。检查到后状态为defaultState为止
     */
    private boolean hasRevertPath(LState postState, LState preState) {
        List<LState> waitingList = new ArrayList<>();
        waitingList.add(postState);
        while (!waitingList.isEmpty()) {
            LState currState = waitingList.remove(0);
            for(LState nextState : currState.postMap.values()) {
                if(nextState == preState)
                    return true;
                else if(nextState.state != mModel.getDefaultState())
                    waitingList.add(nextState);
            }
        }
        return false;
    }
    //宽度：widthState, marginStateRightToActionLeft, widthAction, marginActionRightToNextStateLeft
    //高度：heightStateTitle, marginEventTopToTitleBottom, (heightEventText+marginEventVertical) * event个数, marginStateVertical
    private int widthState = dp8*17; //state框的宽度
    private int widthAction = widthState /2; //action框的宽度
    private int marginStateRightToActionLeft = dp8; //state框右侧到action框左侧的间隔
    private int marginActionRightToNextStateLeft = dp8*10; //action框右侧到下一列state框左侧的距离
    private int marginStateVertical = dp8*5; //两个竖排state框之间的间隔
    private int heightStateTitle = dp8*6; //state框内 标题（名字）的高度 (一行16dp,最多3行）
    private int marginEventTopToTitleBottom = dp8*3; //event起始top距离title底部的间隔
    private int widthEventText = widthState * 2 / 3; //event的文字宽度（2/3框宽，靠右对齐）
    private int heightEventText = dp8 * 7 / 2; //event和其对应的的文字高度（一行14dp，最多2行）
    private int marginEventVertical = dp8*2; //两个event之间的竖向margin
    private int radiusRoundRect = 20; //圆角矩形 圆角半径

    private int colorStateBg = 0xd0525151;//state框 背景色
    private int colorActionBg = 0xd0a17272;//action框 背景色
    private int colorText = 0xffffffff; //文字颜色

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawPath(mStatePath, mStateBgPaint);

        canvas.save();
        canvas.translate(offXY.x, offXY.y);

        for(int colIdx=0; colIdx<linkedStates.size(); colIdx++) {
            List<LState> col = linkedStates.get(colIdx);
            float frameTop = 0;
            float frameLeft = colIdx * (widthState + marginStateRightToActionLeft + widthAction + marginActionRightToNextStateLeft);
            for(int rowIdx=0; rowIdx<col.size(); rowIdx++) {
                LState lstate = col.get(rowIdx);
                lstate.colIdx = colIdx;
                lstate.pos.top = frameTop;
                lstate.pos.left = frameLeft;
                lstate.pos.right = frameLeft + widthState;
                lstate.pos.bottom = frameTop + heightStateTitle + marginEventTopToTitleBottom + (heightEventText + marginEventVertical) * lstate.postMap.keySet().size();

                //先确定state框的位置。下一遍循环再确定连线的位置
                canvas.drawRoundRect(lstate.pos, radiusRoundRect, radiusRoundRect, mStateBgPaint);

                float actionLeft = lstate.pos.right + marginStateRightToActionLeft;
                float actionRight = actionLeft + widthAction;
                float eventTop = lstate.pos.top + heightStateTitle + marginEventTopToTitleBottom;
                for(int idx=0; idx<lstate.events.length; idx++) {
                    int eventCode = lstate.events[idx];
                    List<Integer> actions = lstate.actionsMap.get(eventCode);
                    if(actions == null || actions.isEmpty())
                        continue;
                    float actionTop = eventTop + (heightEventText * marginEventVertical) * idx;
                    canvas.drawRoundRect(actionLeft, actionTop, actionRight, actionTop+heightEventText,
                            radiusRoundRect, radiusRoundRect, mActionBgPaint);
                }

                frameTop = frameTop + lstate.pos.height()+ marginStateVertical;
            }
        }

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalWidthSpec = makeMeasureSpec(maxWidth,MeasureSpec.EXACTLY);
        int finalHeightSpec = makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);

        super.onMeasure(finalWidthSpec, finalHeightSpec);
    }

    PointF lastPos = new PointF();
    PointF offXY = new PointF(); //绘制时应该偏移的大小

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        PointF latestPos = new PointF(event.getRawX(), event.getRawY());
//        Log.d("TAG", "onTouch: "+latestPos);
//        if(event.getAction() != MotionEvent.ACTION_DOWN) {
//            offXY.set(latestPos.x-lastPos.x + offXY.x, latestPos.y-lastPos.y + offXY.y);
//        }
//        lastPos = latestPos;
//        invalidate();
//        return true;
////        return super.onTouchEvent(event);
//    }

    @Override
    public boolean onTouch(View touchedV, MotionEvent event) {
        PointF latestPos = new PointF(event.getRawX(), event.getRawY());
        Log.d("TAG", "onTouch: "+latestPos);
//        if (!(getParent() instanceof FrameLayout && getLayoutParams() instanceof FrameLayout.LayoutParams))
//            return false;

        FrameLayout.LayoutParams paramsUpd = (FrameLayout.LayoutParams) getLayoutParams();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastPos = latestPos;
                return true;
            }
            default:
                offXY.set(latestPos.x-lastPos.x + offXY.x, latestPos.y-lastPos.y + offXY.y);
                lastPos = latestPos;
                invalidate();
                return true;
//            case MotionEvent.ACTION_MOVE: {
//                paramsUpd.leftMargin = downXY[0] + latestPos[0] - downPos[0];
//                paramsUpd.topMargin = downXY[1] + latestPos[1] - downPos[1];
//                setLayoutParams(paramsUpd);
//
//                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//                if (Math.abs(latestPos[0] - downPos[0]) > slop || Math.abs(latestPos[1] - downPos[1]) > slop)
//                    noClickWhenFinish = true;
////                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getLeft(), v.getTop(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
////                        Log.d(TAG, "onTouch: " + String.format("%d, %d, %d, %d, %s, %s", v.getBottom(), ((FrameLayout) v.getParent()).getHeight(), v.getRight(), v.getBottom(), v.getWidth() == v.getRight() - v.getLeft(), v.getHeight() == v.getBottom() - v.getTop()));
//                return true;
//            }
//            //其实这里应该也应该同ACTION_MOVE更新坐标
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL: {
//                setPressed(false);
//
//                if (!noClickWhenFinish)
//                    performClick();
//                noClickWhenFinish=false;
//                return true;
//            }
        }
//        return false;
    }

    private static class LState {
        RectF pos = new RectF();
        int colIdx;
        FSMState2 state;
        int[] events;
        LinkedHashMap<Integer, List<Integer>> actionsMap = new LinkedHashMap<>();
        LinkedHashMap<Integer, LState> postMap = new LinkedHashMap<>(); //走向默认状态且无附加操作的话，返回null

        public LState(FSMState2 state) {
            this.state = state;
            events = FSMState2.getStateAnt(state.getClass()).events();
        }

    }

    private static class Adjustable {
        LState preState; //preState但是在更后列
        LState postState; //postState但是在更前列
        int preColIdx; //在第几列
        int postColIdx;

        public Adjustable(LState preState, LState lPostState, int preColIdx, int postColIdx) {
            this.preState = preState;
            this.postState = lPostState;
            this.preColIdx = preColIdx;
            this.postColIdx = postColIdx;
        }
    }
}
