/*
 * Copyright (C) 2018 CyberAgent, Inc.
 * Copyright (C) 2010 jsemler
 *
 * Original publication without License
 * http://www.anddev.org/android-2d-3d-graphics-opengl-tutorials-f2/possible-to-do-opengl-off-screen-rendering-in-android-t13232.html#p41662
 */

package jp.co.cyberagent.android.gpuimage;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_HEIGHT;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_WIDTH;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class PixelBuffer {
    private final static String TAG = "PixelBuffer";
    private final static boolean LIST_CONFIGS = false;
    private final int width;
    private final int height;
    private final EGL10 egl10;
    private final EGLDisplay eglDisplay;
    private final EGLConfig eglConfig;
    private final EGLContext eglContext;
    private final EGLSurface eglSurface;
    private final GL10 gl10;
    private final String mThreadOwner;
    private GLSurfaceView.Renderer renderer; // borrow this interface
    private Bitmap bitmap;
    private EGLConfig[] eglConfigs;

    public PixelBuffer(final int width, final int height) {
        this.width = width;
        this.height = height;

        int[] version = new int[2];
        int[] attribList = new int[]{
                EGL_WIDTH, this.width,
                EGL_HEIGHT, this.height,
                EGL_NONE
        };

        // No error checking performed, minimum required code to elucidate logic
        egl10 = (EGL10) EGLContext.getEGL();
        eglDisplay = egl10.eglGetDisplay(EGL_DEFAULT_DISPLAY);
        egl10.eglInitialize(eglDisplay, version);
        eglConfig = chooseConfig(); // Choosing a config is a little more
        // complicated

        // eglContext = egl10.eglCreateContext(eglDisplay, eglConfig,
        // EGL_NO_CONTEXT, null);
        int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        int[] attrib_list = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, attrib_list);

        eglSurface = egl10.eglCreatePbufferSurface(eglDisplay, eglConfig, attribList);
        egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);

        gl10 = (GL10) eglContext.getGL();

        // Record thread owner of OpenGL context
        mThreadOwner = Thread.currentThread().getName();
    }

    public void setRenderer(final GLSurfaceView.Renderer renderer) {
        this.renderer = renderer;

        // Does this thread own the OpenGL context?
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "setRenderer: This thread does not own the OpenGL context.");
            return;
        }

        // Call the renderer initialization routines
        this.renderer.onSurfaceCreated(gl10, eglConfig);
        this.renderer.onSurfaceChanged(gl10, width, height);
    }

    public Bitmap getBitmap() {
        // Do we have a renderer?
        if (renderer == null) {
            Log.e(TAG, "getBitmap: Renderer was not set.");
            return null;
        }

        // Does this thread own the OpenGL context?
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.");
            return null;
        }

        // Call the renderer draw routine (it seems that some filters do not
        // work if this is only called once)
        renderer.onDrawFrame(gl10);
        renderer.onDrawFrame(gl10);
        convertToBitmap();
        return bitmap;
    }

    public void destroy() {
        renderer.onDrawFrame(gl10);
        renderer.onDrawFrame(gl10);
        egl10.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

        egl10.eglDestroySurface(eglDisplay, eglSurface);
        egl10.eglDestroyContext(eglDisplay, eglContext);
        egl10.eglTerminate(eglDisplay);
    }

    private EGLConfig chooseConfig() {
        int[] attribList = new int[]{
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_NONE
        };

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        int[] numConfig = new int[1];
        egl10.eglChooseConfig(eglDisplay, attribList, null, 0, numConfig);
        int configSize = numConfig[0];
        eglConfigs = new EGLConfig[configSize];
        egl10.eglChooseConfig(eglDisplay, attribList, eglConfigs, configSize, numConfig);

        if (LIST_CONFIGS) {
            listConfig();
        }

        return eglConfigs[0]; // Best match is probably the first configuration
    }

    private void listConfig() {
        Log.i(TAG, "Config List {");

        for (EGLConfig config : eglConfigs) {
            int d, s, r, g, b, a;

            // Expand on this logic to dump other attributes
            d = getConfigAttrib(config, EGL_DEPTH_SIZE);
            s = getConfigAttrib(config, EGL_STENCIL_SIZE);
            r = getConfigAttrib(config, EGL_RED_SIZE);
            g = getConfigAttrib(config, EGL_GREEN_SIZE);
            b = getConfigAttrib(config, EGL_BLUE_SIZE);
            a = getConfigAttrib(config, EGL_ALPHA_SIZE);
            Log.i(TAG, "    <d,s,r,g,b,a> = <" + d + "," + s + "," +
                    r + "," + g + "," + b + "," + a + ">");
        }

        Log.i(TAG, "}");
    }

    private int getConfigAttrib(final EGLConfig config, final int attribute) {
        int[] value = new int[1];
        return egl10.eglGetConfigAttrib(eglDisplay, config,
                attribute, value) ? value[0] : 0;
    }

    private void convertToBitmap() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        GPUImageNativeLibrary.adjustBitmap(bitmap);
    }
}
