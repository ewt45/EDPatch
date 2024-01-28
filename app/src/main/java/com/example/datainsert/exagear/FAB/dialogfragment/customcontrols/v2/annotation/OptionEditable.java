package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * radiogroup类型的，传入int值，显示的时候转换成字符串，存的时候存int
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionEditable {
    int tag();
}
