package com.example;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.eltechs.axs.Globals;
import com.eltechs.ed.R;
import com.eltechs.ed.fragments.ManageContainersFragment;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.example.datainsert.exagear.FAB.dialogfragment.DriveD;
import com.example.datainsert.exagear.controls.CursorToggle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class test extends AppCompatActivity {
    private static final File mUserAreaDir = DriveD.getDriveDDir();
    Object i2;

    public test() {

    }
//
//    private void test1(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//       logthis(i1,f1,f2,f3,f4,f5,i2,f6,b);
//        SoftInput.toggle();
//    }
//    private void logthis(int i1,float f1,float f2, float f3, float f4, float f5, int i2, float f6, boolean b){
//        QH.logD("placeRectangle参数："+i1+","+f1+","+f2+","+f3+","+f4+","+f5+","+i2+","+f6+","+b);
//    }

    public test(Map<String, Object> map) {
    }

    public static void reflectInvoke(ManageContainersFragment fragment) {
        for (Class<?> clz : fragment.getClass().getDeclaredClasses()) {
            //clz.getSimpleName().equals("ContAsyncTask")
            if (AsyncTask.class.equals(clz.getSuperclass())) {
                try {
                    Constructor<AsyncTask<GuestContainer, Void, Void>> constructor = ((Class<AsyncTask<GuestContainer, Void, Void>>) clz).getDeclaredConstructor(fragment.getClass(), int.class);//参考smali中实例化的时候，传入外部类实例和参数
                    constructor.setAccessible(true); //允许访问private
                    AsyncTask<GuestContainer, Void, Void> task =  constructor.newInstance(fragment, 0);
                    task.execute();
                } catch (NoSuchMethodException | InvocationTargetException |
                         IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void test2(float a) {
        CursorToggle cursorToggle;
        Toolbar toolbar = new Toolbar(Globals.getAppContext());
        toolbar.setBackgroundResource(R.drawable.someimg);
        Class<test> s = test.class;
        try {
            s.getDeclaredConstructor(Map.class).newInstance(new HashMap<>());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void test3(int i) {
        while (true) {
            try {
                File file = new File("");
                file.createNewFile();

                i--;
                if (i == 0)
                    throw new RuntimeException();
                synchronized (i2) {
                    try {
                        i -= 2;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {

            }
        }

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode!=10001){
//            SelectObbFragment.receiveResultManually(this,requestCode,resultCode,data);
//            return;
//        }
//        assert resultCode==2||resultCode==0;
//        int i=0;
//        test2(1f);
//    }
}
