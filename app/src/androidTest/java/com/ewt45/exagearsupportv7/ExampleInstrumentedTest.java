package com.ewt45.exagearsupportv7;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";
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