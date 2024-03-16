package com.example.datainsert.exagear.controlsV2;

import android.util.Log;

import com.example.datainsert.exagear.controlsV2.axs.AndroidPointReporter;
import com.example.datainsert.exagear.controlsV2.gestureMachine.adapter.MouseMoveAdapter;

class ConstExagearExtension {
    class MouseMoveCameraAdapter extends MouseMoveAdapter {

        @Override
        public void moveTo(float x, float y) {
            Log.e("MouseMoveCameraAdapter", "moveTo:视角转动尚未实现 ");
            AndroidPointReporter.pointerMove(x,y);
        }

        @Override
        public void prepareMoving(float x, float y) {

        }
    }
}
