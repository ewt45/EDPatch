package com.ewt45.exagearsupportv7;

import android.content.Context;
import android.graphics.Matrix;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.gestureMachine.State.StateCountDownMeasureSpeed;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.IntRangeEditable;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.annotation.StateTag;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.v2.widget.RangeSeekbar;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void 测试_矩阵先平移再缩放和先缩放再平移有区别吗(){
        Matrix matrix = new Matrix();
        System.out.println("初始矩阵：");
        print_matrix(matrix);

        matrix.postTranslate(1,2);
        matrix.postScale(3.1f,2.4f);
        matrix.postTranslate(3,4);
        System.out.println("先缩放再平移：");
        print_matrix(matrix);

        matrix.postTranslate(1,2);
        matrix.postTranslate(3,4);
        matrix.postScale(3.1f,2.4f);
        System.out.println("先平移再缩放：");
        print_matrix(matrix);
    }

    public static void print_matrix(Matrix matrix){
        float[] values =new float[9];
        matrix.getValues(values);
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                System.out.print(values[i * 3 + j] +"  \t");
            }
            System.out.print("\n");
        }
    }


    public static class ParentTest{
        public ParentTest(){
            StateTag ant = getClass().getAnnotation(StateTag.class);
            if(ant==null)
                throw new RuntimeException("state类缺少注解StateTag");
            System.out.println("ParentTest: 子类tag为："+ant.tag());
            Log.d(TAG, "ParentTest: 子类tag为："+ant.tag());
        }
    }
    @StateTag(tag = 1)
    public static class ChildTest extends ParentTest{
        public ChildTest(){
            super();
        }
    }

    @Test
    public void test_father_class_get_child_annotation(){
        new ChildTest();
    }
    @Test
    public void test_get_field_annotations(){
        for(Field field: StateCountDownMeasureSpeed.class.getDeclaredFields()){
            for(Annotation annotation: field.getAnnotations()){
                Class<?> aCls = annotation.annotationType();
                Log.d(TAG, "test: class="+aCls);
                if(aCls.equals(IntRangeEditable.class)){
                    field.setAccessible(true);
                    IntRangeEditable intRangeEditable = (IntRangeEditable) annotation;
                    int[] range = intRangeEditable.range();
                    int defaultValue = intRangeEditable.defVal();
                    Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    RangeSeekbar seekbar = new RangeSeekbar(c,range[0],range[1]) {
                        @Override
                        protected int rawToFinal(int rawValue) {
                            return rawValue;
                        }

                        @Override
                        protected int finalToRaw(int finalValue) {
                            return finalValue;
                        }
                    };
                    seekbar.setValue(defaultValue);
                }
            }
        }

    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ewt45.exagearsupportv7", appContext.getPackageName());
    }

    @Test
    public void getAssets() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.ewt45.exagearsupportv7", appContext.getPackageName());
        try (InputStream is = appContext.getAssets().open("pulseaudio-xsdl.zip");){
            Log.d(TAG, "getAssets: 读取不到？");
        }

    }
}