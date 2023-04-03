package com.example.datainsert.exagear.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLSurfaceView extends GLSurfaceView {
    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setRenderer(new MyRender());
    }

    private static class MyRender implements Renderer {
        private FloatBuffer mVB;

        MyRender() {
            float coords[] = {
                    -0.5f, 0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
                    0.5f, 0.5f, 0.0f,
                    -0.5f, 0.5f, 0.0f
            };

            ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            mVB = vbb.asFloatBuffer();
            mVB.put(coords);
            mVB.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVB);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
        }
    }
}