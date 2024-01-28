package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface StateTag {
    /**
     * 唯一标识该State的
     */
    int tag();

    /**
     * 该state允许发送的事件
     */
    int[] events() default {};

    /**
     * 是否为action
     */
    boolean isAction() default false;
}
