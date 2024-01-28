package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface IntSegmentEditable {
    int[] segments();
    int defVal();
    int tag();
}
