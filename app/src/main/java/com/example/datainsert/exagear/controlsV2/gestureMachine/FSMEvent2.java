package com.example.datainsert.exagear.controlsV2.gestureMachine;

import android.support.annotation.NonNull;

public class FSMEvent2 {
    private final String name;

    public FSMEvent2(String str) {
        this.name = str;
    }

    public FSMEvent2() {
        this.name = FSMEvent2.class.toString();
    }

    @NonNull
    public String toString() {
        return this.name;
    }
}