package it.repix.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL10;

public class TestView extends GLSurfaceView
{
    class ContextFactory implements android.opengl.GLSurfaceView.EGLContextFactory
    {
        public EGLContext createContext(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig)
        {
            if(context == null)
            {
                Log.w("repix", "creating OpenGL ES 2.0 context");
                int ai[] = new int[3];
                ai[0] = TestView.EGL_CONTEXT_CLIENT_VERSION;
                ai[1] = 2;
                ai[2] = 12344;
                context = egl10.eglCreateContext(egldisplay, eglconfig, EGL10.EGL_NO_CONTEXT, ai);
            }
            Log.w("repix", (new StringBuilder()).append("createContext = ").append(context).toString());
            return context;
        }

        public void destroyContext(EGL10 egl10, EGLDisplay egldisplay, EGLContext eglcontext)
        {
            Log.d("TAG", (new StringBuilder()).append("destroyContext ").append(eglcontext).toString());
        }

        ContextFactory()
        {
            super();
        }
    }

    class MyGLRenderView implements android.opengl.GLSurfaceView.Renderer
    {
        public void onDrawFrame(GL10 gl10)
        {
            gl10.glClearColor(0.0F, (float)Math.random(), 0.0F, 0.5F);
            gl10.glClear(16640);
        }

        public void onSurfaceChanged(GL10 gl10, int i, int j)
        {
            gl10.glViewport(0, 0, i, j);
        }

        public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig)
        {
        }

        MyGLRenderView()
        {
            super();
        }
    }


    private static int EGL_CONTEXT_CLIENT_VERSION = 0;
    public static final String TAG = "repix";
    EGLContext context;
    MyGLRenderView renderer;

    public TestView(Context context1, AttributeSet attributeset)
    {
        super(context1, attributeset);
        context = null;
        renderer = new MyGLRenderView();
        setEGLContextClientVersion(2);
        setRenderer(renderer);
    }

    static 
    {
        EGL_CONTEXT_CLIENT_VERSION = 12440;
    }

}
