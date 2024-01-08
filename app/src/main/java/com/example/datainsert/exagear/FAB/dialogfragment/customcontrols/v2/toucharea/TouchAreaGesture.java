package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.toucharea;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchArea;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.TouchAreaView;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.model.OneGestureArea;

public class TouchAreaGesture extends TouchArea<OneGestureArea> {
    public TouchAreaGesture(TouchAreaView host, @NonNull OneGestureArea data, @NonNull TouchAdapter adapter) {
        super(host, data, adapter);
    }

    @Override
    public void onDraw(Canvas canvas) {

    }
}
