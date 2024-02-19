package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMAction2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMR;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.FSMState2;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.ModelProvider;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.TouchAreaModel;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.touchArea.TouchAreaGesture;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.RecyclerAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.TabPagerLayout;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;

public class Edit2GestureView extends LinearLayout {
    private final Edit0Main mHost;
    private final StateAdapter mStateAdapter;
    private final ActionAdapter mActionAdapter;
    private final List<Integer> stateTypeCreatable = new ArrayList<>();
    private final List<String> stateNameCreatable = new ArrayList<>();

    private final List<Integer> actionTypeCreatable = new ArrayList<>();
    private final List<String> actionNameCreatable = new ArrayList<>();

    public Edit2GestureView(Edit0Main host) {
        super(host.getContext());
        mHost = host;
        Context c = getContext();
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
        btnAddState.setText("新建 (状态)");
        btnAddState.setOnClickListener(v -> gotoCreateStateView(false));

        RecyclerView recyclerState = new RecyclerView(c);
        recyclerState.setLayoutManager(new LinearLayoutManager(c));
        recyclerState.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        mStateAdapter = new StateAdapter();
        recyclerState.setAdapter(mStateAdapter);

        //操作编辑
        Button btnAddAction = new Button(c);
        btnAddAction.setText("新建 (操作)");
        btnAddAction.setOnClickListener(v -> gotoCreateStateView(true));

        RecyclerView recyclerAction = new RecyclerView(c);
        recyclerAction.setLayoutManager(new LinearLayoutManager(c));
        recyclerAction.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        mActionAdapter = new ActionAdapter();
        recyclerAction.setAdapter(mActionAdapter);

        //放入tabPager
        LinearLayout linearState = new LinearLayout(c);
        linearState.setOrientation(VERTICAL);
        linearState.addView(btnAddState);
        linearState.addView(recyclerState);

        LinearLayout linearAction = new LinearLayout(c);
        linearAction.setOrientation(VERTICAL);
        linearAction.addView(btnAddAction);
        linearAction.addView(recyclerAction);

        TabPagerLayout tabPagerLayout = new TabPagerLayout(c)
                .addTabAndPage("状态", linearState)
                .addTabAndPage("附加操作", linearAction);

        addView(tabPagerLayout);
    }

    private static OneGestureArea getGestureAreaModel() {
        return Const.touchAreaViewRef.get().getProfile().getGestureAreaModel();
    }

    private static TouchAreaGesture getGestureArea() {
        List<TouchArea<? extends TouchAreaModel>> list = Const.touchAreaViewRef.get().getProfile().getTouchAreaList();
        return (TouchAreaGesture) list.get(list.size() - 1);
    }

    /**
     * 进入新建状态/操作界面
     *
     * @param isAction true为新建操作，false为新建状态
     */
    @SuppressLint("NotifyDataSetChanged")
    private void gotoCreateStateView(boolean isAction) {
        Context c = getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        linearRoot.setPadding(dp8, dp8, dp8, dp8);

        Spinner spinState = new Spinner(c);
        spinState.setAdapter(new ArrayAdapter<>(c, android.R.layout.simple_list_item_1, isAction ? actionNameCreatable : stateNameCreatable));
        spinState.setSelection(0);

        EditText editNiceName = new EditText(c);
        editNiceName.setSingleLine(true);
        editNiceName.setText("");

        //新建状态
        Button btnFinish = new Button(c);
        btnFinish.setText("创建");
        btnFinish.setOnClickListener(v -> {
            List<Integer> stateTypeList = isAction ? actionTypeCreatable : stateTypeCreatable;
            int stateType = stateTypeList.get(spinState.getSelectedItemPosition());
            try {
                FSMState2 newState = ModelProvider.getStateClass(stateType).newInstance();
                newState.setNiceName(editNiceName.getText().toString());
                //TODO 要不这里重新构建一次状态机？另外如果改变状态机内容时，当前有未完成的操作会不会出现异常？
                //调用attach以分配id
                newState.attach(getGestureArea().getGestureContext().getMachine());
                getGestureAreaModel().addStates(newState);
                Const.getEditWindow().toPreviousView();
                //需要重新设置列表，因为这个列表不于allStateList同步
                if (isAction)
                    mActionAdapter.setDataList(getGestureAreaModel().getEditableActionList());
                else mStateAdapter.setDataList(getGestureAreaModel().getEditableStateList());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });

        linearRoot.addView(QH.getOneLineWithTitle(c, isAction?"附加操作":"状态", spinState, true));
        linearRoot.addView(QH.getOneLineWithTitle(c, "别名", editNiceName, true));
        linearRoot.addView(btnFinish, QH.LPLinear.one(-1, -2).top().to());

        Const.getEditWindow().toNextView(linearRoot, isAction?"新建 (操作)":"新建 (状态)");
    }

    private static class StateAdapter extends RecyclerAdapter<FSMState2> {
        public StateAdapter() {
            super(getGestureAreaModel().getEditableStateList());
        }

        @Override
        public void onBindViewHolder(@NonNull RegularViewHolder holder, int position) {
            FSMState2 state = getDataList().get(position);
            Context c = holder.root.getContext();

            holder.text1.setText(state.getNiceName());

            //TODO 编辑别名后，返回此界面不会刷新名称，所以最好整一个refreshable接口，ConfigWindow那边toPreviews的时候调用这个接口？
            //初始和默认操作是固定的，不能删除（编辑还是要留下，因为最好可以查看状态转换）
            boolean shouldNotDelete = state instanceof StateNeutral || state instanceof StateWaitForNeutral;
            PopupMenu popupMenu = new PopupMenu(c, holder.btnMenu);
            popupMenu.getMenu().add("编辑").setOnMenuItemClickListener(item -> {
                TabPagerLayout tabPagerLayout = new TabPagerLayout(c)
                        .addTabAndPage("属性", state.createPropEditView(c))
                        .addTabAndPage("状态转移", state.createTranEditView(c, getGestureAreaModel()));
//                "编辑 - " + FSMR.getStateS(FSMState2.getStateTag(state.getClass()))
                Const.getEditWindow().toNextView(tabPagerLayout, tabPagerLayout.detachTabLayout());
                return true;
            });
            popupMenu.getMenu().add("删除").setEnabled(!shouldNotDelete).setOnMenuItemClickListener(item -> {
                TestHelper.showConfirmDialog(c, "确定要删除吗？与此状态相关的状态转移也会被一并删除。", (dialog, which) -> {
                    //TODO 列出具体的，包含该状态的状态转移
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

            holder.text1.setText(action.getNiceName());

            //初始和默认操作是固定的，不能删除（编辑还是要留下，因为最好可以查看状态转换）
            PopupMenu popupMenu = new PopupMenu(c, holder.btnMenu);
            popupMenu.getMenu().add("编辑").setOnMenuItemClickListener(item -> {
                Const.getEditWindow().toNextView(action.createPropEditView(c), "编辑");
                return true;
            });
            popupMenu.getMenu().add("删除").setOnMenuItemClickListener(item -> {
                TestHelper.showConfirmDialog(c, "确定要删除吗？", (dialog, which) -> {
                    //TODO 列出具体的包含该操作的状态转移
                    getGestureAreaModel().removeState(action);
                    setDataList(getGestureAreaModel().getEditableActionList()); //需要重新设置列表，因为这个列表不于allStateList同步
                });
                return true;
            });

            holder.btnMenu.setOnClickListener(v -> popupMenu.show());
        }
    }


}
