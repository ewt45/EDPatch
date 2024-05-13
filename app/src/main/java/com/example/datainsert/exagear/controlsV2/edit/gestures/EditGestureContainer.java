package com.example.datainsert.exagear.controlsV2.edit.gestures;

import com.example.datainsert.exagear.controlsV2.model.OneGestureArea;

public class EditGestureContainer {
    private boolean isUpdateNeeded = false;

    public void inflate(OneGestureArea model) {

    }

    public boolean isUpdateNeeded() {
        return isUpdateNeeded;
    }

    public void consumeUpdateNeeded() {
        isUpdateNeeded = false;
    }
}
