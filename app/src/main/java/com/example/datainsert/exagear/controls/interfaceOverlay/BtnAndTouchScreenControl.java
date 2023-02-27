package com.example.datainsert.exagear.controls.interfaceOverlay;

import android.util.Log;

import com.eltechs.axs.Finger;
import com.eltechs.axs.TouchArea;
import com.eltechs.axs.TouchScreenControl;
import com.eltechs.axs.graphicsScene.SceneOfRectangles;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员变量：按钮toucharea数组，触屏toucharea，`btnFingerList < Finger>`用于存 down的时候位置在按钮area的finger。`map< toucharea,finger>`
 * down：调用toucharea.inspect()判断是否在自身范围内，循环完如果不在按钮内，再放入触屏。然后如果在按钮内放入btnFingerList（这样没法处理movein和out）
 * move：如果finger在btnFingerList中，调用全部按钮toucharea的handlemove，否则调用触屏的handlemove
 * up：和move相同。
 */
public class BtnAndTouchScreenControl implements TouchScreenControl {
    private static final String TAG="BTNTScreenControl";
    private final List<BtnTouchArea> btnTouchAreas;
    private final TouchArea gestureTouchArea;
    private final List<Finger> btnFingerList = new ArrayList<>();

    public BtnAndTouchScreenControl(List<BtnTouchArea> btnTouchAreas, TouchArea gestureTouchArea) {
        this.btnTouchAreas = btnTouchAreas;
        this.gestureTouchArea = gestureTouchArea;
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void attachedToGLContext(SceneOfRectangles sceneOfRectangles) {

    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void detachedFromGLContext() {

    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerDown(Finger finger) {
        int inBtnArea=0;
        //如果在按钮区域内，就只交给按钮区域处理。并将手指加入列表中，表示该手指初始按下位置是在按钮区域。后面的操作判断均基于初始按下位置。
        for (BtnTouchArea touchArea : this.btnTouchAreas) {
            if(touchArea.handleBtnFingerDown(finger))
                inBtnArea++;
        }
        //如果不在任何按钮区域内再交给触屏区域处理。
        if(inBtnArea==0){
            gestureTouchArea.handleFingerDown(finger);
        }else{
            btnFingerList.add(finger);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerUp(Finger finger) {
        //同move
        if(btnFingerList.contains(finger)){
            for (BtnTouchArea touchArea : this.btnTouchAreas) {
                touchArea.handleBtnFingerUp(finger);
            }
        }else{
            gestureTouchArea.handleFingerUp(finger);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerMove(Finger finger) {
        //如果finger在btnFingerList中，调用全部按钮toucharea的handlemove，否则调用触屏的handlemove
        if(btnFingerList.contains(finger)){
            for (BtnTouchArea touchArea : this.btnTouchAreas) {
                touchArea.handleBtnFingerMove(finger);
            }
        }else{
            gestureTouchArea.handleFingerMove(finger);
        }

    }

}
