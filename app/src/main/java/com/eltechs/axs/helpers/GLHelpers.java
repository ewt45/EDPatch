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
        synchronized (GLHelpers.class) {
            if (have_GL_OES_texture_npot == null) {
                have_GL_OES_texture_npot = isGLExtensionAvailable("GL_OES_texture_npot");
            }
        }
        return have_GL_OES_texture_npot;
    }

    public static synchronized boolean isGLExtensionAvailable(String str) {
        synchronized (GLHelpers.class) {
            String glGetString = GLES20.glGetString(GLES20.GL_EXTENSIONS);
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