package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.options;

public abstract class AbstractOption {
    abstract public void run();
    public String getName(){
        return getClass().getSimpleName();
    }
}
