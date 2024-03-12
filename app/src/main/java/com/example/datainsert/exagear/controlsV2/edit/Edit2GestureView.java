package com.example.datainsert.exagear.controlsV2.edit;

import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchArea;
import com.example.datainsert.exagear.controlsV2.TouchAreaModel;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR;
import com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.model.ModelProvider;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.touchArea.TouchAreaGesture;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
import com.example.datainsert.exagear.controlsV2.widget.RecyclerAdapter;
import com.example.datainsert.exagear.controlsV2.widget.TabPagerLayout;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class Edit2GestureView extends LinearLayout implements EditConfigWindow.OnReEnterListener {
    private final StateAdapter mStateAdapter;
    private final ActionAdapter mActionAdapter;
    private final List<Integer> stateTypeCreatable = new ArrayList<>();
    private final List<String> stateNameCreatable = new ArrayList<>();

    private final List<Integer> actionTypeCreatable = new ArrayList<>();
    private final List<String> actionNameCreatable = new ArrayList<>();

    public Edit2GestureView(Context c) {
        super(c);
        setOrientation(VERTICAL);

        for (int stateType : ModelProvider.stateTypeInts) {
            Class<?> clz = ModelProvider.getStateClass(stateType);
            //是action
            if (FSMAction2.class.isAssignableFrom(clz)) {
                actionTypeCreatable.add(stateType);
                actionNameCreatable.add(FSMR.getStateS(stateType));
            }
            //否则是state，但要求不是默认和初始状态，
            else if (!clz.equals(StateNeutral.class) && !clz.equals(StateWaitForNeutral.class)) {
                stateTypeCreatable.add(stateType);
                stateNameCreatable.add(FSMR.getStateS(stateType));
            }
        }

        //状态编辑
        Button btnAddState = new Button(c);
        btnAddState.setText(String.format("%s (%s)", getS(RR.global_add), getS(RR.ctr2_ges_state)));
        btnAddState.setOnClickListener(v -> gotoCreateStateView(false));

        RecyclerView recyclerState = new RecyclerView(c);
        recyclerState.setLayoutManager(new LinearLayoutManager(c));
        recyclerState.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        mStateAdapter = new StateAdapter();
        recyclerState.setAdapter(mStateAdapter);

        //操作编辑
        Button btnAddAction = new Button(c);
        btnAddAction.setText(String.format("%s (%s)", getS(RR.global_add), getS(RR.ctr2_ges_action)));
        btnAddAction.setOnClickListener(v -> gotoCreateStateView(true));

        RecyclerView recyclerAction = new RecyclerView(c);
        recyclerAction.setLayoutManager(new LinearLayoutManager(c));
        recyclerAction.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        mActionAdapter = new ActionAdapter();
        recyclerAction.setAdapter(mActionAdapter);

        //调试
        //TODO 部分状态依赖xserver才能运行，所以在xserver外无法只会发送固定事件，因此不应在xserver外启用这个实时显示
        // （所以gestureArea里编辑模式下不需要新建状态机了？）（还是说调整一下让状态在xserver外也能正常处理事件？）
        // 另外这里修改之后状态机不会刷新，实时显示的就变成错的了，还得想办法刷新一下，是用户点按钮手动刷新，还是model改成适配machine的格式，能实时获取最新的转移？ 如果machine里直接遍历转移列表再用id搜state，效率会低多少？
        // 如果改变状态机内容时，当前有未完成的操作会不会出现异常？
        CheckBox checkGestureHistory = new CheckBox(c);
        checkGestureHistory.setText(getS(RR.ctr2_ges_debug_realtimeInfo));//实时显示状态转移
        checkGestureHistory.setChecked(Const.getTouchView().getGestureHistoryTextView().getVisibility()==VISIBLE);
        checkGestureHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Const.getTouchView().getGestureHistoryTextView().setVisibility(isChecked ? VISIBLE : GONE);
            if(!isChecked)  Const.getTouchView().getGestureHistoryTextView().clearHistory();
        });

        Button btnPreviewInGraphic = new Button(c);
        btnPreviewInGraphic.setText(getS(RR.ctr2_ges_debug_graph));//查看当前的状态转移图
        btnPreviewInGraphic.setOnClickListener(v -> {
            TestHelper.showConfirmDialog(c, "Not implemented yet!", (dialog, which) -> {
            });
        });

        //放入tabPager
        LinearLayout linearState = new LinearLayout(c);
        linearState.setOrientation(VERTICAL);
        linearState.addView(btnAddState);
        linearState.addView(recyclerState);

        LinearLayout linearAction = new LinearLayout(c);
        linearAction.setOrientation(VERTICAL);
        linearAction.addView(btnAddAction);
        linearAction.addView(recyclerAction);

        LinearLayout linearDebug = new LinearLayout(c);
        linearDebug.setOrientation(VERTICAL);
        linearDebug.addView(checkGestureHistory);
        linearDebug.addView(btnPreviewInGraphic);

        String[] tabTitles = RR.getSArr(RR.ctr2_ges_subTitles);
        TabPagerLayout tabPagerLayout = new TabPagerLayout(c)
                .addTabAndPage(tabTitles[0], linearState)//状态
                .addTabAndPage(tabTitles[1], linearAction)//附加操作
                .addTabAndPage(tabTitles[2], linearDebug);//调试

        addView(tabPagerLayout);
    }

    private static OneGestureArea getGestureAreaModel() {
        return Const.getActiveProfile().getGestureAreaModel();
    }

    private static TouchAreaGesture getGestureArea() {
        List<TouchArea<? extends TouchAreaModel>> list = Const.getActiveProfile().getTouchAreaList();
        return (TouchAreaGesture) list.get(list.size() - 1);
    }

    /**
     * 进入新建状态/操作界面
     *
     * @param isAction true为新建操作，false为新建状态
     */
    private void gotoCreateStateView(boolean isAction) {
        Context c = getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        linearRoot.setPadding(dp8, dp8, dp8, dp8);

        Spinner spinState = new Spinner(c);
        spinState.setAdapter(new ArrayAdapter<>(c, android.R.layout.simple_list_item_1, isAction ? actionNameCreatable : stateNameCreatable));
        spinState.setSelection(0);

        LimitEditText editNiceName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setStringValue("");

        //新建状态
        Button btnFinish = new Button(c);
        btnFinish.setText(getS(RR.global_done));//完成

        btnFinish.setOnClickListener(v -> {
            String alias = editNiceName.getStringValue().trim();
            //应该强制用户自定义一个名称，以防在状态编辑界面 用户把实例和类搞混，在想为什么选项变少了
            if (alias.isEmpty()) {
                TestHelper.showConfirmDialog(v.getContext(), getS(RR.ctr2_ges_fillInAlias), (dialog, which) -> {
                });
                return;
            }
            List<Integer> stateTypeList = isAction ? actionTypeCreatable : stateTypeCreatable;
            int stateType = stateTypeList.get(spinState.getSelectedItemPosition());
            FSMState2 newState = ModelProvider.getStateInstance(stateType);
            newState.setNiceName(alias);

            //调用attach以分配id
            newState.attach(getGestureArea().getGestureContext().getMachine());
            getGestureAreaModel().addStates(newState);
            Const.getEditWindow().toPreviousView();
            //需要重新设置列表，因为这个列表不于allStateList同步
            if (isAction)
                mActionAdapter.setDataList(getGestureAreaModel().getEditableActionList());
            else mStateAdapter.setDataList(getGestureAreaModel().getEditableStateList());
        });

        linearRoot.addView(QH.getOneLineWithTitle(c, getS(RR.global_type/*类型*/), spinState, true));
        linearRoot.addView(QH.getOneLineWithTitle(c, getS(RR.global_alias)/*别名*/, editNiceName, true));
        linearRoot.addView(btnFinish, QH.LPLinear.one(-1, -2).top().to());

        Const.getEditWindow().toNextView(linearRoot, getS(RR.global_add) + " (" + getS(isAction ? RR.ctr2_ges_action : RR.ctr2_ges_state) + ")"); //新建 (状态) 或 新建 (操作)
    }

    @Override
    public void onReEnter() {
        //状态重命名后刷新
        mStateAdapter.setDataList(getGestureAreaModel().getEditableStateList());
        mActionAdapter.setDataList(getGestureAreaModel().getEditableActionList());
    }

    private static class StateAdapter extends RecyclerAdapter<FSMState2> {
        public StateAdapter() {
            super(getGestureAreaModel().getEditableStateList());
        }

        @Override
        public void onBindViewHolder(@NonNull RegularViewHolder holder, int position) {
            FSMState2 state = getDataList().get(position);
            Context c = holder.root.getContext();

            holder.imageView.setVisibility(GONE);
            holder.text1.setText(state.getNiceName());
            holder.text2.setVisibility(GONE);

            //初始和默认操作是固定的，不能删除（编辑还是要留下，因为最好可以查看状态转换）
            boolean shouldNotDelete = state instanceof StateNeutral || state instanceof StateWaitForNeutral;
            PopupMenu popupMenu = new PopupMenu(c, holder.btnMenu);
            popupMenu.getMenu().add(/*编辑*/getS(RR.global_edit)).setOnMenuItemClickListener(item -> {
                String[] editTabTitles = getSArr(RR.ctr2_ges_state_edit_tabTitles);
                TabPagerLayout tabPagerLayout = new TabPagerLayout(c)
                        .addTabAndPage(/*状态转移*/editTabTitles[0], state.createTranEditView(c, getGestureAreaModel()))
                        .addTabAndPage(/*属性*/editTabTitles[1], state.createPropEditView(c));
//                "编辑 - " + FSMR.getStateS(FSMState2.getStateTag(state.getClass()))
                Const.getEditWindow().toNextView(tabPagerLayout, tabPagerLayout.detachTabLayout());
                return true;
            });
            popupMenu.getMenu().add(/*删除*/getS(RR.global_del)).setEnabled(!shouldNotDelete).setOnMenuItemClickListener(item -> {
                String[] warnMessages = getSArr(RR.ctr2_ges_stateDelWarns);
                int deleteId = state.getId();
                StringBuilder builder = new StringBuilder();
                OneGestureArea model = getGestureAreaModel();
                for (List<Integer> oneTran : model.getTransitionList())
                    if (oneTran.get(0).equals(deleteId) || oneTran.get(2).equals(deleteId))
                        builder.append("\n").append(model.findStateById(oneTran.get(0)).getNiceName())
                                .append(" ---").append(FSMR.getEventS(oneTran.get(1))).append("---> ")
                                .append(model.findStateById(oneTran.get(2)).getNiceName());
                if (builder.length() > 0)
                    builder.insert(0, "\n\n"+warnMessages[1]);//以下转移将会被删除:
                //确定要删除吗？与此状态相关的状态转移也会被一并删除。
                TestHelper.showConfirmDialog(c, warnMessages[0] + builder.toString(), (dialog, which) -> {
                    getGestureAreaModel().removeState(state);
                    setDataList(getGestureAreaModel().getEditableStateList()); //需要重新设置列表，因为这个列表不于allStateList同步
                });
                return true;
            });

            holder.btnMenu.setOnClickListener(v -> popupMenu.show());
        }
    }

    private static class ActionAdapter extends RecyclerAdapter<FSMAction2> {

        public ActionAdapter() {
            super(getGestureAreaModel().getEditableActionList());
        }

        @Override
        public void onBindViewHolder(@NonNull RegularViewHolder holder, int position) {
            FSMAction2 action = getDataList().get(position);
            Context c = holder.root.getContext();

            holder.imageView.setVisibility(GONE);
            holder.text1.setText(action.getNiceName());
            holder.text2.setVisibility(GONE);

            //初始和默认操作是固定的，不能删除（编辑还是要留下，因为最好可以查看状态转换）
            PopupMenu popupMenu = new PopupMenu(c, holder.btnMenu);
            popupMenu.getMenu().add(/*编辑*/getS(RR.global_edit)).setOnMenuItemClickListener(item -> {
                Const.getEditWindow().toNextView(action.createPropEditView(c), getS(RR.global_edit));
                return true;
            });
            popupMenu.getMenu().add(/*删除*/getS(RR.global_del)).setOnMenuItemClickListener(item -> {
                String[] warnMessages = getSArr(RR.ctr2_ges_actionDelWarns);
                int deleteId = action.getId();
                StringBuilder builder = new StringBuilder();
                OneGestureArea model = getGestureAreaModel();
                for (List<Integer> oneTran : TestHelper.filterList(model.getTransitionList(), item1 -> item1.indexOf(deleteId) >= 3))
                    builder.append("\n").append(model.findStateById(oneTran.get(0)).getNiceName())
                            .append(" ---").append(FSMR.getEventS(oneTran.get(1))).append("---> ")
                            .append(model.findStateById(oneTran.get(2)).getNiceName());
                if (builder.length() > 0)
                    builder.insert(0, "\n\n"+warnMessages[1]);

                TestHelper.showConfirmDialog(c, /*确定要删除吗*/warnMessages[0] + builder.toString(), (dialog, which) -> {
                    getGestureAreaModel().removeState(action);
                    setDataList(getGestureAreaModel().getEditableActionList()); //需要重新设置列表，因为这个列表不于allStateList同步
                });
                return true;
            });

            holder.btnMenu.setOnClickListener(v -> popupMenu.show());
        }
    }


}
