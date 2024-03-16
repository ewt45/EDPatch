package com.example.datainsert.exagear.controlsV2.options;

public abstract class AbstractOption {
    abstract public void run();
    public String getName(){
        return getClass().getSimpleName();
    }
}
