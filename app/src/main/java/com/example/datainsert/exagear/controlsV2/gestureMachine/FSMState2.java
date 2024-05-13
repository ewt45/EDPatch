package com.example.datainsert.exagear.controlsV2.gestureMachine;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.controlsV2.Const.dp8;
import static com.example.datainsert.exagear.controlsV2.TestHelper.wrapWithTipBtn;
import static com.example.datainsert.exagear.controlsV2.gestureMachine.FSMR.getStateS;

import android.content.Context;
import android.support.annotation.NonNull;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.RR;
import com.example.datainsert.exagear.controlsV2.Const;
import com.example.datainsert.exagear.controlsV2.TestHelper;
import com.example.datainsert.exagear.controlsV2.TouchAdapter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateNeutral;
import com.example.datainsert.exagear.controlsV2.gestureMachine.state.StateWaitForNeutral;
import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;
import com.example.datainsert.exagear.controlsV2.widget.DrawableNumber;
import com.example.datainsert.exagear.controlsV2.widget.LimitEditText;
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
    String niceName;
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



    /**
     * 根据给定标题，说明，单个属性编辑视图，创建完整的编辑视图
     * <br/> 如果想创建空的视图。后两个参数，长度设为0即可
     *
     * @param titleAndHelps 每个字符串数组第一个元素是属性名称，第二个为null或者是这个属性的说明，在名称右上角显示一个问号，用户可点击
     * @param editViews     一般都是LimitEditText。单个属性的编辑视图
     */
    protected static View createEditViewQuickly(FSMState2 state, Context c, String[][] titleAndHelps, View[] editViews) {
        if(state instanceof StateNeutral || state instanceof StateWaitForNeutral)
            return Helper.createEmptyPropEditView(c);
        else
            return Helper.createEditViewQuickly(state,c,titleAndHelps,editViews);
    }

    protected GestureContext2 getContext() {
        return this.context;
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

    /**
     * 将状态机附加到自身上。
     * <br/> 自身id若为-1，则在这里生成一个id。所以新建的state在添加到全部状态列表前应该先调用此方法
     * <br/> 目前可能被调用不止一次
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
     * 注意此id是标识一个实例，而非state类型（stateTag）
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 如果没设置过niceName，则返回其对应类型的名称，否则返回自定义名称
     */
    public String getNiceName() {
        return (niceName != null && !niceName.trim().isEmpty()) ? niceName : FSMR.getStateS(stateTag);
    }

    /**
     * 与 {@link #getNiceName()} 不同，返回未经处理的niceName，可能为null
     */
    public String getRawNiceName(){
        return niceName;
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

    ;

    public abstract void notifyBecomeInactive();

    /**
     * 用户属性编辑可视化界面
     *
     * @see #createTranEditView(FSMState2, Context, OneGestureArea)
     */
    public View createPropEditView(Context c) {
        return createEditViewQuickly(this, c, new String[0][0], new View[0]);
    }

    /**
     * 用户状态转移可视化编辑界面
     *
     * @see #createPropEditView(Context)
     */
    public static View createTranEditView(FSMState2 state, Context c, OneGestureArea model) {
        return Helper.createTranEditView(state, c, model);
    }


    @NonNull
    @Override
    public String toString() {
        return getNiceName();
    }

}
