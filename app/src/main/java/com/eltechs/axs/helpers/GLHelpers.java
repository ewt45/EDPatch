package com.eltechs.axs.helpers;

import android.opengl.GLES20;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

/* loaded from: classes.dex */
public abstract class GLHelpers {
    private static Boolean have_GL_OES_texture_npot;

    private GLHelpers() {
    }

    public static synchronized boolean have_GL_OES_texture_npot() {
        boolean booleanValue;
        synchronized (GLHelpers.class) {
            if (have_GL_OES_texture_npot == null) {
                have_GL_OES_texture_npot = Boolean.valueOf(isGLExtensionAvailable("GL_OES_texture_npot"));
            }
            booleanValue = have_GL_OES_texture_npot.booleanValue();
        }
        return booleanValue;
    }

    public static synchronized boolean isGLExtensionAvailable(String str) {
        synchronized (GLHelpers.class) {
            String glGetString = GLES20.glGetString(7939);
            if (glGetString != null) {
                for (String str2 : glGetString.split(" ")) {
                    if (str2.equals(str)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static synchronized boolean isEGLExtensionAvailable(String str) {
        synchronized (GLHelpers.class) {
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            String eglQueryString = egl10.eglQueryString(egl10.eglGetCurrentDisplay(), 12373);
            if (eglQueryString != null) {
                for (String str2 : eglQueryString.split(" ")) {
                    if (str2.equals(str)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}