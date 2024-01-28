package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine;

import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.Const;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.google.gson.annotations.SerializedName;

public abstract class AbstractFSMState2 {
    transient private final ContextGesture context;
    public String niceName; //调试时方便理解的名称
    transient private GestureMachine machine;
    transient private int[] allowedEvents;
    @SerializedName(value = Const.GsonField.st_StateType)
    private int stateTag; //唯一标识 对应一个具体状态子类. 序列化只记录这个int就行了，events和isAction在反序列化的时候都可以从具体类的注解里获取

    public AbstractFSMState2(String niceName) {
        this.context = Const.getGestureContext();
        StateTag ant = getClass().getAnnotation(StateTag.class);
        if (ant == null)
            throw new RuntimeException("state类缺少注解StateTag");
        stateTag = ant.tag();
        allowedEvents = ant.events();
        this.niceName = (niceName != null && niceName.trim().length() == 0) ? niceName : getClass().getSimpleName();
    }

    public AbstractFSMState2() {
        this(null);
    }

    /**
     * 用于序列化时，表明自己是哪个State子类的tag，读取子类的注解{@link StateTag}. 应该不重复
     */
    public static int getClassTag(Class<? extends AbstractFSMState2> clz) {
        StateTag ant = clz.getAnnotation(StateTag.class);
        if (ant == null)
            throw new RuntimeException("state类缺少注解StateTag");
        return ant.tag();
    }


    protected ContextGesture getContext() {
        return this.context;
    }

    public final void attach(GestureMachine finiteStateMachine) {
        Assert.state(this.machine == null, "Already attached to FSM!");
        this.machine = finiteStateMachine;
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
}
