package com.eltechs.axs.proto.input.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes.dex */
public @interface ParamLength {
    String value();
}
