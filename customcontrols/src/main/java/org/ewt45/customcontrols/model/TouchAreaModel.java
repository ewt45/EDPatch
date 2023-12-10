package org.ewt45.customcontrols.model;

public abstract class TouchAreaModel {

    public int left=0;
    public int right=0;
    public int top=0;
    public int bottom=0;

    public int getWidth(){
        return right-left;
    }

    public int getHeight(){
        return bottom-top;
    }
}
