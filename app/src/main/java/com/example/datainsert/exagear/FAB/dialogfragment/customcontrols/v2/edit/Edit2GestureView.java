package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.edit;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.ViewGroup;
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
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.RegularViewHolder;
import com.example.datainsert.exagear.QH;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edit2GestureView extends LinearLayout {
    Edit0Main mHost;
    Adapter mAdapter;

    public Edit2GestureView(Edit0Main host) {
        super(host.getContext());
        mHost = host;
        Context c = getContext();
        setOrientation(VERTICAL);

        Button btnAdd = new Button(c);
        btnAdd.setText("新建状态");
        btnAdd.setOnClickListener(v -> gotoCreateStateView());

        RecyclerView recyclerView = new RecyclerView(c);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);


        addView(btnAdd);
        addView(recyclerView);
    }

    private static OneGestureArea getGestureAreaModel() {
        return Const.touchAreaViewRef.get().getProfile().getGestureAreaModel();
    }

    private static TouchAreaGesture getGestureArea() {
        List<TouchArea<? extends TouchAreaModel>> list = Const.touchAreaViewRef.get().getProfile().getTouchAreaList();
        return (TouchAreaGesture) list.get(list.size() - 1);
    }

    private void gotoCreateStateView() {
        Context c = getContext();
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(VERTICAL);
        linearRoot.setPadding(dp8, dp8, dp8, dp8);

        List<String> stateNames = new ArrayList<>();
        for (int stateId : ModelProvider.stateTypeInts) {
            Class<?> clz = ModelProvider.getStateClassByTypeInt(stateId);
            //不是默认和初始状态，不是action
            if (!clz.equals(StateNeutral.class) && !clz.equals(StateWaitForNeutral.class)
                    && !Objects.equals(clz.getSuperclass(), FSMAction2.class))
                stateNames.add(FSMR.getStateS(stateId));
        }


        Spinner spinState = new Spinner(c);
        spinState.setAdapter(new ArrayAdapter<>(c, android.R.layout.simple_list_item_1, stateNames));
        spinState.setSelection(0);

        EditText editNiceName = new EditText(c);
        editNiceName.setSingleLine(true);
        editNiceName.setText("");

        //新建状态
        Button btnFinish = new Button(c);
        btnFinish.setText("创建");
        btnFinish.setOnClickListener(v -> {
            int pos = spinState.getSelectedItemPosition();
            try {
                FSMState2 newState = ModelProvider.stateClasses[pos].newInstance();
                newState.setNiceName(editNiceName.getText().toString());
                //调用attach以分配id
                newState.attach(getGestureArea().getGestureContext().getMachine());
                getGestureAreaModel().addStates(newState);
                Const.getEditWindow().toPreviousView();
                mAdapter.notifyDataSetChanged();

            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });

        linearRoot.addView(QH.getOneLineWithTitle(c, "状态", spinState, true));
        linearRoot.addView(QH.getOneLineWithTitle(c, "自定义名称", editNiceName, true));
        linearRoot.addView(btnFinish, QH.LPLinear.one(-1, -2).top().to());

        Const.getEditWindow().toNextView(linearRoot, "新建");
    }

    private static class Adapter extends RecyclerView.Adapter<RegularViewHolder> {
        private final OneGestureArea mModel;

        public Adapter() {
            mModel = getGestureAreaModel();
        }

        @NonNull
        @Override
        public RegularViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            Context c = viewGroup.getContext();
            RegularViewHolder holder = new RegularViewHolder(c);
//            holder.imageView.setImageDrawable(TestHelper.getAssetsDrawable(c, "controls/check.xml"));

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RegularViewHolder holder, int pos) {
            FSMState2 state = mModel.getAllStateList().get(pos);
            Context c = holder.root.getContext();

            holder.text1.setText(state.getNiceName());

            //初始和默认操作是固定的，不能编辑不能删除
            holder.btnMenu.setVisibility((state instanceof StateNeutral || state instanceof StateWaitForNeutral) ? INVISIBLE : VISIBLE);

            PopupMenu popupMenu = new PopupMenu(c, holder.btnMenu);
            popupMenu.getMenu().add("编辑").setOnMenuItemClickListener(item->{
                Const.getEditWindow().toNextView(state.createPropEditView(c), "编辑 - " + FSMR.getStateS(FSMState2.getClassTag(state.getClass())));
                return true;
            });
            popupMenu.getMenu().add("删除").setOnMenuItemClickListener(item->{
                TestHelper.showConfirmDialog(c, "确定要删除吗？与此状态相关的状态转移也会被一并删除。", (dialog, which) -> mModel.deleteState(state));
                return true;
            });
            holder.btnMenu.setOnClickListener(v -> popupMenu.show());

        }

        @Override
        public int getItemCount() {
            return mModel.getAllStateList().size();
        }

    }

}
