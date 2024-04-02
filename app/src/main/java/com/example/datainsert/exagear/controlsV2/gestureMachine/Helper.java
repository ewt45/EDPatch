package com.example.datainsert.exagear.controlsV2.gestureMachine;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.TestHelper.wrapWithTipBtn;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.getStateS;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2.getStateAnt;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2.getStateTag;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.widget.DrawableNumber;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;

import java.util.List;

public class Helper {
    /**
     * 为默认和初始状态创造空的属性编辑视图
     */
    public static View createEmptyPropEditView(Context c) {
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = QH.getTitleTextView(c, RR.getS(RR.global_empty));//空
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearRoot.addView(tv,QH.LPLinear.one(-1,-2).gravity(Gravity.CENTER).left().right().top().bottom().to());
        return linearRoot;
    }

    /**
     * 创造对应状态的属性编辑视图。
     * @see FSMState2#createEditViewQuickly(FSMState2, Context, String[][], View[]) 
     */
    public static View createEditViewQuickly(FSMState2 state, Context c, String[][] titleAndHelps, View[] editViews) {
        if (titleAndHelps.length != editViews.length)
            throw new RuntimeException("标题个数与视图个数不等");
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        //state类型
        TextView tvStateType = QH.TV.one(c).text16Sp().solidColor().text(getStateS(getStateTag(state.getClass()))).to();
        //用户自定义别名
        LimitEditText editNiceName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setStringValue(state.niceName == null ? "" : state.niceName) //如果没有自定义名称就留空，以保证多次进入后还是空，否则用getNiceName 进入一次就会从空变为对应的翻译文本了
                .setUpdateListener(editText -> state.setNiceName(editText.getStringValue()));

        linearRoot.addView(QH.getOneLineWithTitle(c,getS(RR.global_type),tvStateType,true),QH.LPLinear.one(-1, -2).left().right().top().to());
        linearRoot.addView(QH.getOneLineWithTitle(c, getS(RR.global_alias), editNiceName, true), QH.LPLinear.one(-1, -2).left().right().top().to());

        for (int i = 0; i < titleAndHelps.length; i++) {
            String[] titleAndHelp = titleAndHelps[i];
            LinearLayout linear1 = QH.getOneLineWithTitle(c, titleAndHelp[0], editViews[i], true);
            if (titleAndHelp.length>1 && titleAndHelp[1] != null && !titleAndHelp[1].isEmpty()){
                View tvTitle = linear1.getChildAt(0);
                linear1.removeView(tvTitle);
                linear1.addView(wrapWithTipBtn(tvTitle,titleAndHelp[1]),0);
            }

            int bottomMargin = i == titleAndHelps.length - 1 ? dp8 : 0;
            linearRoot.addView(linear1, QH.LPLinear.one(-1, -2).margin(dp8, dp8, dp8, bottomMargin).to());
        }

        return linearRoot;
    }


    /**
     * 创造对应状态的状态转移编辑视图
     * @see FSMState2#createTranEditView(FSMState2, Context, OneGestureArea) 
     */
    public static View createTranEditView(FSMState2 state, Context c, OneGestureArea model) {

        final int EVENT_TEXT_WIDTH = QH.px(c, 100);

        RelativeLayout root = new RelativeLayout(c);

        //一行 三项对应的weight,为保证能宽度撑起，一行的最外层的宽度要设成wrap_content而不是match
        LinearLayout.LayoutParams lp1 = QH.LPLinear.one(EVENT_TEXT_WIDTH, -2).weight(0).gravity(Gravity.CENTER_VERTICAL).left().to();
        LinearLayout.LayoutParams lp2 = QH.LPLinear.one(0, -2).weight(1).gravity(Gravity.CENTER).left().to();
        LinearLayout.LayoutParams lp3 = QH.LPLinear.one(QH.px(c, 80), -2).weight(0).gravity(Gravity.CENTER).left().right().to();

        TextView tvInfo = QH.TV.one(c).text16Sp().to();
        tvInfo.setText(/*说明*/getS(RR.global_instructions));
        root.addView(wrapWithTipBtn(tvInfo,getS(RR.ctr2_ges_transInfo)), QH.LPRelative.one(-2,-2).top().left().to());//QH.LPLinear.one(-2, -2).top().left().to()

        //表格头
        LinearLayout linearHeader = new LinearLayout(c);
        linearHeader.setOrientation(LinearLayout.HORIZONTAL);

        String[] headerNames = getSArr(RR.ctr2_ges_state_edit_tran_tableHeaders);//可发送事件$下一个状态$附加操作
        linearHeader.addView(QH.TV.one(c).text(headerNames[0]).solidColor().bold().textGravity(CENTER_HORIZONTAL).to(), lp1);
        linearHeader.addView(QH.TV.one(c).text(headerNames[1]).solidColor().bold().textGravity(CENTER_HORIZONTAL).to(), lp2);
        linearHeader.addView(QH.TV.one(c).text(headerNames[2]).solidColor().bold().textGravity(CENTER_HORIZONTAL).to(), lp3);

        root.addView(linearHeader, getRParams(root,-2,-2));//QH.LPLinear.one(-1, -2).top().to()
        root.addView(TestHelper.getDividerView(c, false), getRParams(root,-2,QH.px(c,2)));//QH.LPLinear.one(-1, QH.px(c, 2)).top().to()

        StateTag ant = getStateAnt(state.getClass());
        for (int evtIdx = 0; evtIdx < ant.events().length; evtIdx++) {
            int event = ant.events()[evtIdx];
            //默认的postState和action是走向WaitForNeutral的
            List<Integer> targetTransition = model.getTransition(state, event);//用于修改后应用到model
            int postStateId = targetTransition.get(2);

            //可选的postState编辑
            List<FSMState2> availableStateList = TestHelper.filterList(model.getAllStateList(), item -> {
                if (state instanceof StateWaitForNeutral)
                    return item instanceof StateNeutral; //WaitForNeutral只能走到Neutral
                if (item == state) return false; //不能从自身走到自身
                else
                    return !(item instanceof FSMAction2 || item instanceof StateNeutral); //只有WaitForNeutral能走到Neutral。其他最多只能走到WaitForNeutral
            });

            int[] availableStateIds = new int[availableStateList.size()];
            String[] availableStateNames = new String[availableStateIds.length];
            for (int i = 0; i < availableStateList.size(); i++) {
                FSMState2 oneState = availableStateList.get(i);
                availableStateIds[i] = oneState.getId();
                availableStateNames[i] = oneState.getNiceName();
            }

            TextView tvEvent = QH.TV.one(c).solidColor().text(FSMR.getEventS(event)).to();

            LimitEditText editPostState = new LimitEditText(c)
                    .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                    .setSelectableOptions(availableStateIds, availableStateNames)
                    .setSelectedValue(postStateId)
                    .setUpdateListener(editText -> {
                        targetTransition.remove(2);
                        targetTransition.add(2, editText.getSelectedValue());
                    });
            editPostState.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            //第二部分，显示当前转移对应的action
            //点击相应附加操作以选择/取消选择。被选中的操作会按前方数字从小到大顺序执行。

            //子视图每一行是一个action，不应该有别的行。子视图getTag可以获取对应的Id
            LinearLayout linearActionsPart = new LinearLayout(c);
            linearActionsPart.setOrientation(LinearLayout.VERTICAL);

            //未展开action时，显示action个数。
            TextView tvActionCnt = new TextView(c);
            tvActionCnt.setPadding(0,0,dp8/2,0);
            tvActionCnt.setText(String.valueOf(targetTransition.size()-3));

            //action展开/隐藏按钮
            ImageView btnActions = new ImageView(c);
            btnActions.setImageDrawable(TestHelper.getAssetsDrawable(c, "controls/eye.xml"));
            btnActions.setOnClickListener(v -> {
                TransitionManager.beginDelayedTransition(Const.getEditWindow());//这里放linearRoot的话，只能让一行action有动画，外层还是高度直接变化。用最外层的editconfigwindow可以但是最好不要调用外部视图？
                ViewGroup.LayoutParams recyP = linearActionsPart.getLayoutParams();
                recyP.height = recyP.height == 0 ? -2 : 0;
                linearActionsPart.setLayoutParams(recyP);
            });

            RelativeLayout relativeActionBtnAndCnt = new RelativeLayout(c);
            relativeActionBtnAndCnt.addView(btnActions,QH.LPRelative.one(-2,-2).centerInParent().to());
            relativeActionBtnAndCnt.addView(tvActionCnt,QH.LPRelative.one(-2,-2).centerVertical().leftOf(btnActions).to());

            //全部可用action行
            List<FSMAction2> existActionList = model.getEditableActionList();
            for (FSMAction2 oneAction : existActionList) {
                int oneActionId = oneAction.getId();
                ImageView imageActStep = new ImageView(c);
                imageActStep.setImageDrawable(new DrawableNumber(c));
                imageActStep.getDrawable().setLevel(getActionStep(targetTransition, oneActionId));

                TextView tvAction = QH.TV.one(c).text(oneAction.getNiceName()).solidColor().text14Sp().to();

                LinearLayout linearOneActionLine = new LinearLayout(c);
                linearOneActionLine.setOrientation(LinearLayout.HORIZONTAL);
                linearOneActionLine.setVerticalGravity(Gravity.CENTER_VERTICAL);
                linearOneActionLine.setTag(oneActionId); //存入id。之后刷新序号的时候从这获取action对应的id
                linearOneActionLine.addView(imageActStep, QH.LPLinear.one(dp8 * 3, dp8 * 3).left().to());
                linearOneActionLine.addView(tvAction, QH.LPLinear.one(0, -2).left().weight().to());

                linearActionsPart.addView(linearOneActionLine, QH.LPLinear.one(-1, -2).margin(0,(linearActionsPart.getChildCount() == 0?dp8:0),0,dp8).to());

                //点击后切换选中状态以及序号 (不能直接用targetTransition.indexOf和remove，因为事件的int会和id冲突）
                View.OnClickListener changeSelectListener = v -> {
                    //如果选中，则添加到末尾，若取消选中则删除
                    int selectedIdx = targetTransition.subList(3, targetTransition.size()).indexOf(oneActionId);
                    if (selectedIdx >= 0) targetTransition.remove(selectedIdx + 3);
                    else targetTransition.add(oneActionId);
                    //遍历action重新排序
                    for (int i = 0; i < linearActionsPart.getChildCount(); i++) {
                        LinearLayout tmpOneActLine = (LinearLayout) linearActionsPart.getChildAt(i);
                        ((ImageView) tmpOneActLine.getChildAt(0)).getDrawable().setLevel(getActionStep(targetTransition, (int) tmpOneActLine.getTag()));
                        ((ImageView) tmpOneActLine.getChildAt(0)).invalidate();
                    }
                    tvActionCnt.setText(String.valueOf(targetTransition.size()-3));
                };

                imageActStep.setOnClickListener(changeSelectListener);
                tvAction.setOnClickListener(changeSelectListener);
                linearOneActionLine.setOnClickListener(changeSelectListener);
            }


            //默认状态不允许编辑
            if (state instanceof StateWaitForNeutral) {
                editPostState.setEnabled(false);
                editPostState.setOnClickListener(null);
                btnActions.setEnabled(false);
                btnActions.setOnClickListener(null);
            }

            //主体部分，持久显示。LinearActionPart仅在点击“查看”按钮后显示或隐藏
            LinearLayout linearMainPart = new LinearLayout(c);
            linearMainPart.setOrientation(LinearLayout.HORIZONTAL);
            linearMainPart.setVerticalGravity(Gravity.CENTER_VERTICAL);
            linearMainPart.addView(tvEvent,new LinearLayout.LayoutParams(lp1));
            linearMainPart.addView(editPostState, lp2);
            linearMainPart.addView(relativeActionBtnAndCnt, lp3);

            LinearLayout linearOneTransitionLine = new LinearLayout(c);
            linearOneTransitionLine.setOrientation(LinearLayout.VERTICAL);
            linearOneTransitionLine.addView(linearMainPart);
            linearOneTransitionLine.addView(linearActionsPart, QH.LPLinear.one(-1,0).left(EVENT_TEXT_WIDTH+dp8).to());

//            LinearLayout.LayoutParams lpLineWithLast = QH.LPLinear.one(-1, -2).top().to();
            RelativeLayout.LayoutParams lpLineWithLast = getRParams(root,-2,-2);
            if (evtIdx == ant.events().length - 1) lpLineWithLast.bottomMargin = dp8;
            root.addView(linearOneTransitionLine, lpLineWithLast);
        }
        return root;
    }

    /**
     * 创建状态转移编辑视图时，生成相对布局的params
     */
    private static RelativeLayout.LayoutParams getRParams(RelativeLayout root, int w, int h){
        QH.LPRelative  lpRelative = QH.LPRelative.one(w,h).top().alignParentWidth();
        if(root.getChildCount()==0)
            lpRelative.alignParentTop();
        else
            lpRelative.below(root.getChildAt(root.getChildCount()-1));
        return lpRelative.to();
    }

    /**
     * 用于构建状态转移编辑视图时，更新当前转移的附加操作的执行顺序，
     *
     * @return 大于等于0，0为不执行
     */
    private static int getActionStep(List<Integer> transition, int actionId) {
        return transition.subList(3, transition.size()).indexOf(actionId) + 1;
    }
}
