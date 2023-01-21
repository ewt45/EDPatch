package com.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.eltechs.axs.Globals;
import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;
import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.controls.CursorToggle;
import com.example.datainsert.exagear.controls.SensitivitySeekBar;
import com.example.datainsert.exagear.input.SoftInput;
import com.example.datainsert.exagear.obb.SelectObbFragment;

import java.io.File;

public class test extends AppCompatActivity {
    private static final File mUserAreaDir = DriveD.getDriveDDir();
//
//    private void test1(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//       logthis(i1,f1,f2,f3,f4,f5,i2,f6,b);
//        SoftInput.toggle();
//    }
//    private void logthis(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//        QH.logD("placeRectangle参数："+i1+","+f1+","+f2+","+f3+","+f4+","+f5+","+i2+","+f6+","+b);
//    }

    private void test2(float a){
        CursorToggle cursorToggle;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode!=10001){
            SelectObbFragment.receiveResultManually(this,requestCode,resultCode,data);
            return;
        }
        assert resultCode==2||resultCode==0;
        int i=0;
        test2(1f);
    }
}
