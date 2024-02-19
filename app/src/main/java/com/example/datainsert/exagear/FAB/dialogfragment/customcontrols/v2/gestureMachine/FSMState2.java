package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import static com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const.dp8;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TestHelper;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateWaitForNeutral;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.LimitEditText;
import com.example.datainsert.exagear.QH;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class FSMState2 {
    private static final String TAG = "FSMState2";
    transient private final int[] allowedEvents;
    transient private GestureContext2 context;
    /**
     * 用户自定义别名
     */
    private String niceName;
    transient private GestureMachine machine;
    /**
     * 一个实例对应一个id，如果状态走回之前的状态，则反序列化时可以通过id判断出来，不会再新建一个状态
     * 注意此id是标识一个示例，而非state类型（stateTag）
     */
    private int id = FSMR.value.stateIdInvalid;
    /**
     * 唯一标识 对应一个具体状态子类. 序列化只记录这个int就行了，events和isAction在反序列化的时候都可以从具体类的注解里获取
     */
    @SerializedName(value = Const.GsonField.st_StateType)
    private int stateTag;

    public FSMState2(String niceName) {
        StateTag ant = getStateAnt(getClass());
        stateTag = ant.tag();
        allowedEvents = ant.events();
        this.niceName = niceName;
    }

    public FSMState2() {
        this(null);
    }

    /**
     * 用于序列化时，表明自己是哪个State子类的tag，读取子类的注解{@link StateTag}. 应该不重复
     */
    public static int getStateTag(Class<? extends FSMState2> clz) {
        StateTag ant = getStateAnt(clz);
        return ant.tag();
    }

    public static StateTag getStateAnt(Class<? extends FSMState2> clz) {
        StateTag ant = clz.getAnnotation(StateTag.class);
        if (ant == null)
            throw new RuntimeException("state类缺少注解StateTag");
        return ant;
    }


    protected GestureContext2 getContext() {
        return this.context;
    }

    /**
     * 目前可能被调用不止一次
     */
    public final void attach(GestureMachine finiteStateMachine) {
        if (machine != null)
            Log.w(TAG, "attach: 已经attach过至少一次了！不确定重复初始化是否会带来问题");
//        Assert.state(this.machine == null, "Already attached to FSM!");
        Assert.state(Const.getGestureContext() != null, "context不应该为null");
        this.context = Const.getGestureContext();
        this.machine = finiteStateMachine;

        //TODO 目前这个attach在machine.addtransition的时候，会在添加到model的idlist之前被调用，所以不会出现id为-1的情况。要不还是改成在构造函数里分配，然后model每次反序列化的时候，init的时候重新分配一遍
        //分配id
        if (id == FSMR.value.stateIdInvalid)
            id = context.getTouchArea().getModel().generateStateId();

        onAttach();
    }


//    protected final void sendEvent(FSMEvent2 fSMEvent) {
//        this.machine.sendEvent(this, fSMEvent);
//    }

//    /* JADX INFO: Access modifiers changed from: protected */
//    protected final void sendEventIfActive(FSMEvent2 fSMEvent) {
//        synchronized (this.machine) {
//            if (this.machine.isActiveState(this)) {
//                sendEvent(fSMEvent);
//            }
//        }
//    }

    protected final void sendEvent(int fSMEvent) {
        for (int i : allowedEvents)
            if (i == fSMEvent) {
                this.machine.sendEvent(this, fSMEvent);
                return;
            }
        throw new RuntimeException("该状态没有在tag中声明该事件：" + fSMEvent);

    }

    public void addTouchListener(TouchAdapter touchEventAdapter) {
        context.getFingerEventsSource().addListener(touchEventAdapter);
    }

    public void removeTouchListener(TouchAdapter touchEventAdapter) {
        context.getFingerEventsSource().removeListener(touchEventAdapter);
    }

    /**
     * 注意此id是标识一个示例，而非state类型（stateTag）
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNiceName() {
        return (niceName != null && niceName.trim().length() > 0) ? niceName : FSMR.getStateS(stateTag);
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    protected final GestureMachine getMachine() {
        return this.machine;
    }

    /**
     * 添加到状态机时。由于需要序列化，所以构造函数无参，在此时应初始化自身所需的成员变量
     */
    protected abstract void onAttach();

    public abstract void notifyBecomeActive();

    public abstract void notifyBecomeInactive();

    ;

    /**
     * 用户属性编辑可视化界面
     *
     * @see #createTranEditView(Context, OneGestureArea)
     */
    public View createPropEditView(Context c) {
        throw new RuntimeException("尚未实现编辑内容");
    }


    /**
     * 根据给定标题，说明，单个属性编辑视图，创建完整的编辑湿度
     *
     * @param titleAndHelps 每个字符串数组第一个元素是属性名称，第二个为null或者是这个属性的说明，在名称右上角显示一个问号，用户可点击
     * @param editViews     一般都是LimitEditText。单个属性的编辑视图
     */
    protected View createEditViewQuickly(Context c, String[][] titleAndHelps, View[] editViews) {
        if (titleAndHelps.length != editViews.length)
            throw new RuntimeException("标题个数与视图个数不等");
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        //用户自定义别名
        LimitEditText editNiceName = new LimitEditText(c)
                .setCustomInputType(LimitEditText.TYPE_TEXT_SINGLE_LINE)
                .setStringValue(niceName == null ? "" : niceName) //如果没有自定义名称就留空，以保证多次进入后还是空，否则用getNiceName 进入一次就会从空变为对应的翻译文本了
                .setUpdateListener(editText -> setNiceName(editText.getStringValue()));
        LinearLayout linearEditName = QH.getOneLineWithTitle(c, "别名", editNiceName, true);
        linearRoot.addView(linearEditName, QH.LPLinear.one(-1, -2).left().right().top().to());

        for (int i = 0; i < titleAndHelps.length; i++) {
            String[] titleAndHelp = titleAndHelps[i];
            LinearLayout linear1 = QH.getOneLineWithTitle(c, titleAndHelp[0], editViews[i], true);
            if (titleAndHelp[1] != null && titleAndHelp[1].trim().length() > 0)
                TestHelper.addHelpBadgeToView(linear1.getChildAt(0), titleAndHelp[1]);

            int bottomMargin = i == titleAndHelps.length - 1 ? dp8 : 0;
            linearRoot.addView(linear1, QH.LPLinear.one(-1, -2).margin(dp8, dp8, dp8, bottomMargin).to());
        }

        return linearRoot;
    }

    /**
     * 用户状态转移可视化编辑界面
     *
     * @see #createPropEditView(Context)
     */
    public final View createTranEditView(Context c, OneGestureArea model) {
        LinearLayout linearRoot = new LinearLayout(c);
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        //一行 三项对应的weight
        LinearLayout.LayoutParams lp1 = QH.LPLinear.one(QH.px(c, 100), -2).weight(0).gravity(Gravity.CENTER).left().to();
        LinearLayout.LayoutParams lp2 = QH.LPLinear.one(0, -2).weight(1).gravity(Gravity.CENTER).left().to();
        LinearLayout.LayoutParams lp3 = QH.LPLinear.one(QH.px(c, 80), -2).weight(0).gravity(Gravity.CENTER).left().right().to();

        TextView tvInfo = TestHelper.getTextView16sp(c);
        tvInfo.setPadding(0, 0, dp8 * 2, 0);
        tvInfo.setText("说明");
        TestHelper.addHelpBadgeToView(tvInfo,
                "状态转移的基本概念：[当前状态]运行 -> 满足某个条件 -> 发送对应[事件] -> 执行对应[附加操作] -> 停止当前状态，运行[下一个状态]。\n为每个事件设置不同的下一状态，即可走向不同分支。" +
                        "\n\n可发送事件: 该状态可能发送的全部事件" +
                        "\n\n下一个状态: 用户可自定义此状态。只能从当前已创建的状态列表中选择。" +
                        "\n\n 附加操作: 用户可自定义此操作。注意执行多个操作时有先后顺序，从上到下依次执行。");
        linearRoot.addView(tvInfo, QH.LPLinear.one(-2, -2).top().left().to());

        //表格头
        LinearLayout linearHeader = new LinearLayout(c);
        linearHeader.setOrientation(LinearLayout.HORIZONTAL);
        linearHeader.addView(QH.getTitleTextView(c, "可发送事件"), lp1);
        linearHeader.addView(QH.getTitleTextView(c, "下一个状态"), lp2);
        linearHeader.addView(QH.getTitleTextView(c, "附加操作"), lp3);

        linearRoot.addView(linearHeader, QH.LPLinear.one(-1, -2).top().to());
        linearRoot.addView(TestHelper.getDividerView(c, false), QH.LPLinear.one(-1, QH.px(c, 2)).top().to());

        StateTag ant = getStateAnt(getClass());
        for (int evtIdx = 0; evtIdx < ant.events().length; evtIdx++) {
            int event = ant.events()[evtIdx];
            //默认的postState和action是走向WaitForNeutral的
            List<Integer> targetTransition = model.getTransition(this, event);//用于修改后应用到model
            int postStateId = targetTransition.get(2);
            int[] actions = new int[targetTransition.size() - 3];
            for (int actIdx = 0; actIdx < actions.length; actIdx++)
                actions[actIdx] = targetTransition.get(actIdx + 3);

            //可选的postState编辑
            List<FSMState2> availableStateList = TestHelper.filterList(model.getAllStateList(), item -> {
                if (FSMState2.this instanceof StateWaitForNeutral)
                    return item instanceof StateNeutral; //WaitForNeutral只能走到Neutral
                if (item == FSMState2.this) return false; //不能从自身走到自身
                else
                    return !(item instanceof StateNeutral); //只有WaitForNeutral能走到Neutral。其他最多只能走到WaitForNeutral
            });

            int[] availableStateIds = new int[availableStateList.size()];
            String[] availableStateNames = new String[availableStateIds.length];
            for (int i = 0; i < availableStateList.size(); i++) {
                FSMState2 state = availableStateList.get(i);
                availableStateIds[i] = state.getId();
                availableStateNames[i] = state.getNiceName();
            }

            LimitEditText editPostState = new LimitEditText(c)
                    .setCustomInputType(LimitEditText.TYPE_GIVEN_OPTIONS)
                    .setSelectableOptions(availableStateIds, availableStateNames)
                    .setSelectedValue(postStateId)
                    .setUpdateListener(editText -> {
                        targetTransition.remove(2);
                        targetTransition.add(2, editText.getSelectedValue());
                    });
            editPostState.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            Button btnActions = new Button(c);
            btnActions.setText("查看");
            btnActions.setOnClickListener(v -> {
                LinearLayout linearActionRoot = new LinearLayout(c);
                linearActionRoot.setOrientation(LinearLayout.VERTICAL);
                Const.getEditWindow().toNextView(linearActionRoot, "编辑 - 附加操作");
            });

            //默认状态不允许编辑
            if (this instanceof StateWaitForNeutral) {
                editPostState.setEnabled(false);
                editPostState.setOnClickListener(v -> {
                });
                btnActions.setEnabled(false);
                btnActions.setOnClickListener(v -> {
                });
            }

            LinearLayout linearOneTransitionLine = new LinearLayout(c);
            linearOneTransitionLine.setOrientation(LinearLayout.HORIZONTAL);
            linearOneTransitionLine.addView(QH.getTitleTextView(c, FSMR.getEventS(event)), lp1);
            linearOneTransitionLine.addView(editPostState, lp2);
            linearOneTransitionLine.addView(btnActions, lp3);

            LinearLayout.LayoutParams lpLineWithLast = QH.LPLinear.one(-2, -2).top().to();
            if (evtIdx == ant.events().length - 1) lpLineWithLast.bottomMargin = dp8;
            linearRoot.addView(linearOneTransitionLine, lpLineWithLast);
        }

        //TODO 现在这个视图宽度不会撑开父布局，所以父布局多宽自己就多宽，导致可能会很窄。好像要没啥好办法，只能固定为configWindow最大宽度了。
        // 根本原因是一行一行添加的线性布局，但是想让全部行的宽度都相等，这样每一行的每一格宽度就等都相等了。所以一行的包裹linear是match_parent，但是三格都是wrap了所以不会把宽度撑开。
        // 解决办法：一行的包裹linear宽度设成wrap，给其中两格固定宽度，第三个设置一个weight 这样就能宽度撑满了
        // （要不window也一直固定宽度吧，然后加个横向的滚动视图）
//        NestedScrollView scrollView = new NestedScrollView(c);
//        scrollView.addView(linearRoot, QH.LPLinear.one(Const.getEditWindow().mMaxWidth, -2).to());
//        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(c);
//        horizontalScrollView.addView(scrollView);
//        return horizontalScrollView;
        return linearRoot;
    }
}
