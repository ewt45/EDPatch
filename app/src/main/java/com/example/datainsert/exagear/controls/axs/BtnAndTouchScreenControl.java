package com.example.datainsert.exagear.controls.axs;

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

        //改一下，找到一个就结束
        for (BtnTouchArea touchArea : this.btnTouchAreas) {
            if(touchArea.handleBtnFingerDown(finger)){
                btnFingerList.add(finger);
                return;
            }

        }
        //如果不在任何按钮区域内再交给触屏区域处理。
        gestureTouchArea.handleFingerDown(finger);
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerUp(Finger finger) {
        //同move
        if(btnFingerList.contains(finger)){
            for (BtnTouchArea touchArea : this.btnTouchAreas) {
                touchArea.handleBtnFingerUp(finger);
            }
            //草之前一直都没移出过，那岂不是越来越多
            btnFingerList.remove(finger);
        }else{
            gestureTouchArea.handleFingerUp(finger);
        }
    }

    @Override // com.eltechs.axs.TouchScreenControl
    public void handleFingerMove(Finger finger) {
        //如果finger在btnFingerList中，调用全部按钮toucharea的handlemove，否则调用触屏的handlemove
        //改一下，如果有一个toucharea处理了就返回
        if(btnFingerList.contains(finger)){
            for (BtnTouchArea touchArea : this.btnTouchAreas) {
                if(touchArea.handleBtnFingerMove(finger))
                    return;
            }
        }else{
            gestureTouchArea.handleFingerMove(finger);
        }

    }

}
