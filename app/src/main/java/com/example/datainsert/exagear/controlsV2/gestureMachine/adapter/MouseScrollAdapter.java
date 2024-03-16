package com.example.datainsert.exagear.controlsV2.gestureMachine.adapter;

import com.eltechs.axs.GuestAppActionAdapters.ScrollDirections;
import com.eltechs.axs.xserver.Pointer;
import com.example.datainsert.exagear.controlsV2.axs.AndroidPointReporter;
import com.example.datainsert.exagear.controlsV2.Const;

public class MouseScrollAdapter {
//    private static final Map<ScrollDirections.DirectionY, Integer> directionToButtonCodeY = new EnumMap<>(ScrollDirections.DirectionY.class);
//
//    static {
//        directionToButtonCodeY.put(ScrollDirections.DirectionY.UP, Pointer.BUTTON_SCROLL_UP);
//        directionToButtonCodeY.put(ScrollDirections.DirectionY.NONE, 0);
//        directionToButtonCodeY.put(ScrollDirections.DirectionY.DOWN, Pointer.BUTTON_SCROLL_DOWN);
//    }


    public MouseScrollAdapter() {
    }

    public void notifyStart() {
    }

    public void notifyStop() {
    }

    //TODO 如何能横向滚动？（看看tx11？）
    private void scrollImpl(ScrollDirections.DirectionY directionY, int times) {
        if (directionY == ScrollDirections.DirectionY.NONE) {
            return;
        }
        for (int i = 0; i < times; i++) {
            int pointerButton;// = directionToButtonCodeY.get(directionY);
            if (directionY == ScrollDirections.DirectionY.UP)
                pointerButton = Pointer.BUTTON_SCROLL_UP;
            else if (directionY == ScrollDirections.DirectionY.DOWN)
                pointerButton = Pointer.BUTTON_SCROLL_DOWN;
            else pointerButton = 0;
            if (pointerButton != 0) {
                AndroidPointReporter.buttonPressed(pointerButton);
                AndroidPointReporter.buttonReleased(pointerButton);
            }
        }
    }

    public void scroll(ScrollDirections.DirectionX directionX, ScrollDirections.DirectionY directionY, int times) {
        scrollImpl(directionY, times);
    }
}
