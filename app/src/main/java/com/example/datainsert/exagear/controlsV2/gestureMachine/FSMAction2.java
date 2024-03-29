package com.example.datainsert.exagear.controlsV2.gestureMachine;

/**
 * action，state发送事件时，可以顺便执行多个action
 * <br/> 实现action请重写run，执行时不调用active和inactive, 不发送事件，因此不能单独作为状态
 * <br/>  StateTag 的isAction应该为true
 */
public abstract class FSMAction2 extends FSMState2 {

    public FSMAction2(){
        super();
        StateTag ant = getClass().getAnnotation(StateTag.class);
        assert ant != null;
        if(ant.events().length!=0)
            throw new RuntimeException("action类型不能发送事件");
        if(!ant.isAction())
            throw new RuntimeException("action类型的注解isAction必须为true");
    }
    @Override
    public final void notifyBecomeActive() {
        throw new RuntimeException("action请勿使用此方法");
    }

    @Override
    public final void notifyBecomeInactive() {
        throw new RuntimeException("action请勿使用此方法");
    }

    /**
     * action执行时调用此方法
     */
    public abstract void run();

}
