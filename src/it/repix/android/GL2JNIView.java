package it.repix.android;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import java.io.PrintStream;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL10;

// Referenced classes of package it.repix.android:
//            RepixActivity, GL2JNILib

class GL2JNIView extends GLSurfaceView
{
    private static class ConfigChooser implements android.opengl.GLSurfaceView.EGLConfigChooser
    {
        private static int EGL_OPENGL_ES2_BIT;
        private static int s_configAttribs2[];
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int mValue[];

        private int findConfigAttrib(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig, int i, int j)
        {
            if(egl10.eglGetConfigAttrib(egldisplay, eglconfig, i, mValue))
            {
                j = mValue[0];
            }
            return j;
        }

        private void printConfig(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig)
        {
            int ai[] = {
                12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 
                12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 
                12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 
                12351, 12352, 12354
            };
            String as[] = {
                "EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", 
                "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", 
                "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", 
                "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"
            };
            int ai1[] = new int[1];
            int i = 0;
            while(i < ai.length) 
            {
                int j = ai[i];
                String s = as[i];
                if(egl10.eglGetConfigAttrib(egldisplay, eglconfig, j, ai1))
                {
                    String s1 = GL2JNIView.TAG;
                    Object aobj[] = new Object[2];
                    aobj[0] = s;
                    aobj[1] = Integer.valueOf(ai1[0]);
                    Log.w(s1, String.format("  %s: %d\n", aobj));
                } else
                {
                    while(egl10.eglGetError() != 12288) ;
                }
                i++;
            }
        }

        private void printConfigs(EGL10 egl10, EGLDisplay egldisplay, EGLConfig aeglconfig[])
        {
            int i = aeglconfig.length;
            String s = GL2JNIView.TAG;
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            Log.w(s, String.format("%d configurations", aobj));
            for(int j = 0; j < i; j++)
            {
                String s1 = GL2JNIView.TAG;
                Object aobj1[] = new Object[1];
                aobj1[0] = Integer.valueOf(j);
                Log.w(s1, String.format("Configuration %d:\n", aobj1));
                printConfig(egl10, egldisplay, aeglconfig[j]);
            }

        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay egldisplay)
        {
            int ai[];
            int i;
            ai = new int[1];
            egl10.eglChooseConfig(egldisplay, s_configAttribs2, null, 0, ai);
            i = ai[0];
            Log.d(GL2JNIView.TAG, (new StringBuilder()).append("chooseConfig numConfigs ").append(i).toString());
            if(i > 0)
            {
            	EGLConfig aeglconfig[] = new EGLConfig[i];
                egl10.eglChooseConfig(egldisplay, s_configAttribs2, aeglconfig, i, ai);
                printConfigs(egl10, egldisplay, aeglconfig);
                return chooseConfig(egl10, egldisplay, aeglconfig);
            }
            RepixActivity.getInstance().alert("Failed to create suitable EGL configuration", new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i)
                {
                    RepixActivity.getInstance().finish();
                    Process.killProcess(Process.myPid());
                }
            });
            synchronized(this){
                try
                {
                    wait();
                }
                catch(Exception exception) { }
                throw new IllegalArgumentException("No configs match configSpec");
            }
        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay egldisplay, EGLConfig aeglconfig[])
        {
            int i;
            int j;
            int i1;
            int j1;
            int k1;
            int l1;
            EGLConfig eglconfig;
            i = aeglconfig.length;
            j = 0;
            while(true){
	            if(j >= i)
	            {
	            	return aeglconfig[0];
	            }
	            eglconfig = aeglconfig[j];
	            int k = findConfigAttrib(egl10, egldisplay, eglconfig, 12325, 0);
	            int l = findConfigAttrib(egl10, egldisplay, eglconfig, 12326, 0);
	            if(k >= mDepthSize && l >= mStencilSize)
	            {
	            	i1 = findConfigAttrib(egl10, egldisplay, eglconfig, 12324, 0);
	                j1 = findConfigAttrib(egl10, egldisplay, eglconfig, 12323, 0);
	                k1 = findConfigAttrib(egl10, egldisplay, eglconfig, 12322, 0);
	                l1 = findConfigAttrib(egl10, egldisplay, eglconfig, 12321, 0);
	                if(i1 != mRedSize || j1 != mGreenSize || k1 != mBlueSize || l1 != mAlphaSize){
	                	j++;
	                	continue;
	                }else{
	                	return eglconfig;
	                }
	            }
	            j++;
            }
        }

        static 
        {
            EGL_OPENGL_ES2_BIT = 4;
            int ai[] = new int[11];
            ai[0] = 12321;
            ai[1] = 8;
            ai[2] = 12324;
            ai[3] = 8;
            ai[4] = 12323;
            ai[5] = 8;
            ai[6] = 12322;
            ai[7] = 8;
            ai[8] = 12352;
            ai[9] = EGL_OPENGL_ES2_BIT;
            ai[10] = 12344;
            s_configAttribs2 = ai;
        }

        public ConfigChooser(int i, int j, int k, int l, int i1, int j1)
        {
            mValue = new int[1];
            mRedSize = i;
            mGreenSize = j;
            mBlueSize = k;
            mAlphaSize = l;
            mDepthSize = i1;
            mStencilSize = j1;
        }
    }

    private static class ContextFactory
        implements android.opengl.GLSurfaceView.EGLContextFactory
    {

        private static int EGL_CONTEXT_CLIENT_VERSION = 12440;
        static EGLContext context = null;

        public EGLContext createContext(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig)
        {
            if(context == null)
            {
                Log.w(GL2JNIView.TAG, "creating OpenGL ES 2.0 context");
                GL2JNIView.checkEglError("Before eglCreateContext", egl10);
                int ai[] = new int[3];
                ai[0] = EGL_CONTEXT_CLIENT_VERSION;
                ai[1] = 2;
                ai[2] = 12344;
                context = egl10.eglCreateContext(egldisplay, eglconfig, EGL10.EGL_NO_CONTEXT, ai);
                GL2JNIView.checkEglError("After eglCreateContext", egl10);
            }
            Log.w(GL2JNIView.TAG, (new StringBuilder()).append("createContext = ").append(context).toString());
            return context;
        }

        public void destroyContext(EGL10 egl10, EGLDisplay egldisplay, EGLContext eglcontext)
        {
            Log.d("TAG", (new StringBuilder()).append("destroyContext ").append(eglcontext).toString());
        }


        private ContextFactory()
        {
        }

    }

    private class Renderer implements android.opengl.GLSurfaceView.Renderer
    {
        int frames;

        public void onDrawFrame(GL10 gl10)
        {
            if(RepixActivity.pendingBitmap != null)
            {
                GL2JNILib.clear(null, 0, 0);
                final Bitmap bitmap = RepixActivity.pendingBitmap;
                RepixActivity.pendingBitmap = null;
                queueEvent(new Runnable() {
                    public void run()
                    {
                        setPhoto(bitmap);
                        bitmap.recycle();
                        System.gc();
                    }
                });
            }
            if(GL2JNILib.step() != 0 || frames < 10)
            {
                requestRender();
            }
            if(frames == 0)
            {
                RepixActivity.getInstance().openEditor();
            }
            if(frames == 1)
            {
                RepixActivity.getInstance().hideSplash();
            }
            RepixActivity.getInstance().setRedoEnabled(GL2JNILib.canRedo());
            frames = 1 + frames;
        }

        public void onSurfaceChanged(GL10 gl10, int i, int j)
        {
            Log.d(GL2JNIView.TAG, (new StringBuilder()).append("onSurfaceChanged ").append(i).append(", ").append(j).toString());
            GL2JNILib.init(i, j);
            GLES20.glViewport(0, 0, i, j);
            GLES20.glDisable(3024);
            frames = 0;
        }

        public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig)
        {
            Log.d(GL2JNIView.TAG, (new StringBuilder()).append("onSurfaceCreated ").append(Thread.currentThread()).toString());
        }

        private Renderer()
        {
            super();
            frames = 0;
        }

    }

    private class ScaleListener extends android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        float x;
        float y;

        public boolean onScale(ScaleGestureDetector scalegesturedetector)
        {
            float f = scalegesturedetector.getScaleFactor();
            xtouchEvent(5, 0, 0, scalegesturedetector.getFocusX(), scalegesturedetector.getFocusY(), f);
            xtouchEvent(4, 0, 0, scalegesturedetector.getFocusX() - x, scalegesturedetector.getFocusY() - y, 0.0F);
            x = scalegesturedetector.getFocusX();
            y = scalegesturedetector.getFocusY();
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector scalegesturedetector)
        {
            x = scalegesturedetector.getFocusX();
            y = scalegesturedetector.getFocusY();
            return super.onScaleBegin(scalegesturedetector);
        }

        public void onScaleEnd(ScaleGestureDetector scalegesturedetector)
        {
            y = 0.0F;
            x = 0.0F;
        }

        private ScaleListener()
        {
            super();
        }
    }


    private static final boolean DEBUG = true;
    static final int HOVER_BEGIN = 6;
    static final int HOVER_END = 8;
    static final int HOVER_MOVE = 7;
    static final int PAN = 4;
    static final int PINCH = 5;
    private static String TAG = "repix";
    static final int TOUCH_BEGIN = 0;
    static final int TOUCH_CANCEL = 3;
    static final int TOUCH_END = 2;
    static final int TOUCH_MOVE = 1;
    static boolean inited = false;
    int deny;
    int mask;
    ScaleGestureDetector pinch;

    public GL2JNIView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mask = 0;
        deny = 0;
        setPreserveEGLContextOnPause(true);
        init(false, 0, 0);
        pinch = new ScaleGestureDetector(context, new ScaleListener());
        setOnHoverListener(new android.view.View.OnHoverListener() {
            public boolean onHover(View view, MotionEvent motionevent)
            {
                onHoverEvent(motionevent);
                return false;
            }
        });
    }

    private static void checkEglError(String s, EGL10 egl10)
    {
        do
        {
            int i = egl10.eglGetError();
            if(i != 12288)
            {
                String s1 = TAG;
                Object aobj[] = new Object[2];
                aobj[0] = s;
                aobj[1] = Integer.valueOf(i);
                Log.e(s1, String.format("%s: EGL error: 0x%x", aobj));
            } else
            {
                return;
            }
        } while(true);
    }

    private void init(boolean flag, int i, int j)
    {
        Log.d("TAG", (new StringBuilder()).append("GL init ").append(flag).toString());
        setDebugFlags(3);
        setEGLContextFactory(new ContextFactory());
        ConfigChooser configchooser;
        if(flag)
        {
            configchooser = new ConfigChooser(8, 8, 8, 8, i, j);
        } else
        {
            configchooser = new ConfigChooser(8, 8, 8, 0, i, j);
        }
        setEGLConfigChooser(configchooser);
        setRenderer(new Renderer());
        setRenderMode(0);
    }

    float clamp(float f)
    {
        if(f > 1.0F)
        {
            f = 1.0F;
        } else
        if(f < 0.0F)
        {
            return 0.0F;
        }
        return f;
    }

    public boolean onHoverEvent(MotionEvent motionevent)
    {
        int i;
        i = motionevent.getToolType(0);
        Log.d(TAG, (new StringBuilder()).append("ZZZZ onHoverEvent ").append(motionevent).toString());
        switch(motionevent.getAction()){
        default:case 8:
        	break;
        case 7:
        	xtouchEvent(7, 0, i, motionevent.getX(), motionevent.getY(), 0.0F);
        	break;
        case 9:
        	xtouchEvent(6, 0, i, motionevent.getX(), motionevent.getY(), 0.0F);
        	break;
        case 10:
        	xtouchEvent(8, 0, i, motionevent.getX(), motionevent.getY(), 0.0F);
        	break;
        }
        requestRender();
        return super.onHoverEvent(motionevent);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        pinch.onTouchEvent(motionevent);
        int i = motionevent.getToolType(0);
        float f = motionevent.getPressure();
        if(i == 1)
        {
            f *= 6F;
        }
        if(i == 2)
        {
            f = 1.33F * (float)(0.050000000000000003D + Math.pow(f, 0.75D));
        }
        float f1 = clamp(f);
        switch(motionevent.getAction())
        {
        default:
            return true;

        case 0: // '\0'
            xtouchEvent(0, 0, i, motionevent.getX(), motionevent.getY(), f1);
            return true;

        case 1: // '\001'
            xtouchEvent(2, 0, i, motionevent.getX(), motionevent.getY(), f1);
            return true;

        case 2: // '\002'
            xtouchEvent(1, 0, i, motionevent.getX(), motionevent.getY(), f1);
            return true;

        case 3: // '\003'
            xtouchEvent(3, 0, i, motionevent.getX(), motionevent.getY(), f1);
            break;
        }
        return true;
    }

    void printSamples(MotionEvent motionevent)
    {
        int i = motionevent.getHistorySize();
        int j = motionevent.getPointerCount();
        for(int k = 0; k < i; k++)
        {
            PrintStream printstream2 = System.out;
            Object aobj2[] = new Object[1];
            aobj2[0] = Long.valueOf(motionevent.getHistoricalEventTime(k));
            printstream2.printf("At time %d:", aobj2);
            for(int i1 = 0; i1 < j; i1++)
            {
                PrintStream printstream3 = System.out;
                Object aobj3[] = new Object[3];
                aobj3[0] = Integer.valueOf(motionevent.getPointerId(i1));
                aobj3[1] = Float.valueOf(motionevent.getHistoricalX(i1, k));
                aobj3[2] = Float.valueOf(motionevent.getHistoricalY(i1, k));
                printstream3.printf("  pointer %d: (%f,%f)", aobj3);
            }

        }

        PrintStream printstream = System.out;
        Object aobj[] = new Object[1];
        aobj[0] = Long.valueOf(motionevent.getEventTime());
        printstream.printf("At time %d:", aobj);
        for(int l = 0; l < j; l++)
        {
            PrintStream printstream1 = System.out;
            Object aobj1[] = new Object[3];
            aobj1[0] = Integer.valueOf(motionevent.getPointerId(l));
            aobj1[1] = Float.valueOf(motionevent.getX(l));
            aobj1[2] = Float.valueOf(motionevent.getY(l));
            printstream1.printf("   pointer %d: (%f,%f)", aobj1);
        }

    }

    void setPhoto(Bitmap bitmap)
    {
        Log.d(TAG, (new StringBuilder()).append("view setPhoto ").append(bitmap).toString());
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int pixels[] = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        queueEvent(new Runnable() {
            public void run()
            {
                Log.i("sumo", "queueEvent clear");
                GL2JNILib.clear(pixels, w, h);
            }
        });
    }

    protected native void touchEvent(int i, int j, int k, float f, float f1, float f2);

    protected void xtouchEvent(final int type, final int index, final int tool, final float x, final float y, final float z)
    {
        if(type == 4 || type == 5)
        {
            if(deny == 0)
            {
                xtouchEvent(3, index, tool, x, y, z);
            }
            deny = 6 | deny;
        }
        if((deny & 1 << type) == 0)
        {
            queueEvent(new Runnable() {
                public void run()
                {
                    touchEvent(type, index, tool, x, y, z);
                    requestRender();
                }
            });
        }
        if(type == 2)
        {
            deny = 0;
            mask = 0;
        }
        mask = mask | 1 << type;
    }
}
