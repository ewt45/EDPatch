package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

public abstract class FSMState2 {
    private static final String TAG = "FSMState2";
    transient private GestureContext2 context;
    transient private final int[] allowedEvents;
    /**
     * 用户自定义别名
     */
    private String niceName;
    transient private GestureMachine machine;
    /**
     * 一个实例对应一个id，如果状态走回之前的状态，则反序列化时可以通过id判断出来，不会再新建一个状态
     */
    private int id = FSMR.value.stateIdInvalid;
    /**
     * 唯一标识 对应一个具体状态子类. 序列化只记录这个int就行了，events和isAction在反序列化的时候都可以从具体类的注解里获取
     */
    @SerializedName(value = Const.GsonField.st_StateType)
    private int stateTag;

    public FSMState2(String niceName) {
        StateTag ant = getClass().getAnnotation(StateTag.class);
        if (ant == null)
            throw new RuntimeException("state类缺少注解StateTag");
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
    public static int getClassTag(Class<? extends FSMState2> clz) {
        StateTag ant = clz.getAnnotation(StateTag.class);
        if (ant == null)
            throw new RuntimeException("state类缺少注解StateTag");
        return ant.tag();
    }


    protected GestureContext2 getContext() {
        return this.context;
    }

    /**
     * 目前可能被调用不止一次
     */
    public final void attach(GestureMachine finiteStateMachine) {
        if(machine!=null)
            Log.w(TAG, "attach: 已经attach过至少一次了！不确定重复初始化是否会带来问题");
//        Assert.state(this.machine == null, "Already attached to FSM!");
        Assert.state(Const.getGestureContext()!=null, "context不应该为null");
        this.context = Const.getGestureContext();
        this.machine = finiteStateMachine;

        //TODO 目前这个attach在machine.addtransition的时候，会在添加到model的idlist之前被调用，所以不会出现id为-1的情况。要不还是改成在构造函数里分配，然后model每次反序列化的时候，init的时候重新分配一遍
        //分配id
        if(id == FSMR.value.stateIdInvalid)
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

    public void addTouchListener(TouchAdapter touchEventAdapter) {
        context.getFingerEventsSource().addListener(touchEventAdapter);
    }

    public void removeTouchListener(TouchAdapter touchEventAdapter) {
        context.getFingerEventsSource().removeListener(touchEventAdapter);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNiceName() {
        return (niceName != null && niceName.trim().length() > 0) ? niceName : getClass().getSimpleName();
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

    /**
     * 用户属性编辑可视化界面
     */
    public View createPropEditView(Context c){
        throw new RuntimeException("尚未实现编辑内容");
    };
}
