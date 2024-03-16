/**
 * 记录全部状态机的可用状态
 * <br/>由于需要序列化和反序列化，为向后兼容起见，成员变量名一旦确定不应再更改（还是再单独用一个model？）
* <br/> 每个状态机的全部state实例，需要用唯一id区分。此id在state第一次attach时分配。
 * <br/> 每个State会被复用。attach只会在初始时调用一次。之后每次进入该状态调用notify active，退出状态调用notify inactive。注意在active初始化成员变量，inactive把成员变量不用的都变null
 * <br/> 所有外部可编辑的属性，改为m开头，添加gson注解  {@link com.google.gson.annotations.SerializedName}，
 * <br/>序列化用不到的一定要用transient关键字定义，
 * <br/>对应的stateTag和class记录在ModelProvider中, 对应的文本记录在FSMR的static块中
 * <br/> 需要重写{@link com.example.datainsert.exagear.controlsV2.gestureMachine.FSMState2#createPropEditView(android.content.Context)} 以支持编辑功能
 * <br/><br/> State发送转移事件时，可能会执行附带的动作（Action），action在初始化时和其他State一同传入状态机，执行时调用run方法，不发送事件。tag的isAction为true
 *
 */
package com.example.datainsert.exagear.controlsV2.gestureMachine.state;
