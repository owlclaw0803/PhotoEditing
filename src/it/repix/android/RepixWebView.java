package it.repix.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.*;
import android.widget.Toast;
import java.io.File;

// Referenced classes of package it.repix.android:
//            RepixActivity, DownloaderDialog

public class RepixWebView extends WebView
{
    class GuideWebViewClient extends WebViewClient
    {
        public void onPageFinished(WebView webview, String s)
        {
            Log.d("repix", (new StringBuilder()).append("onPageFinished ").append(s).toString());
            setBackgroundPattern();
            setVisibility(0);
            pageLoaded = true;
        }

        public void onPageStarted(WebView webview, String s, Bitmap bitmap)
        {
            Log.d("repix", (new StringBuilder()).append("onPageStarted ").append(s).toString());
            (new Handler()).postDelayed(new Runnable() {
                public void run()
                {
                    setVisibility(0);
                }
            }, 1500L);
        }

        public void onReceivedError(WebView webview, int i, String s, String s1)
        {
            Log.d("repix", (new StringBuilder()).append("onReceivedError ").append(s1).append(" error:").append(i).append(" ").append(s).toString());
            Toast.makeText(getContext(), s, 1).show();
            if(!pageLoaded)
            {
                pageLoaded = true;
                loadEmpty();
            }
            setVisibility(0);
        }

        public boolean shouldOverrideUrlLoading(WebView webview, String s)
        {
            Log.d("repix", (new StringBuilder()).append("url ").append(s).toString());
            Uri uri = Uri.parse(s);
            if("#safari".equals(uri.getFragment()))
            {
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                getContext().startActivity(intent);
                return true;
            }
            String s1 = uri.getLastPathSegment();
            if(s1 != null)
            {
                s1 = s1.toLowerCase();
            }
            if(s1 != null && (s1.endsWith(".jpg") || s1.endsWith(".png") || s1.endsWith(".jpeg")))
            {
                RepixActivity _tmp = (RepixActivity)getContext();
                (new DownloaderDialog(uri)).show();
                return true;
            } else
            {
                return false;
            }
        }

        GuideWebViewClient()
        {
            super();
        }
    }

    public static final String TAG = "repix";
    boolean pageLoaded;

    public RepixWebView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        configureWebViewSettings();
        setLayerType(1, null);
        setWebViewClient(new GuideWebViewClient());
        setBackgroundPattern();
        loadUrl(attributeset.getAttributeValue(null, "url"));
    }

    private void configureWebViewSettings()
    {
        WebSettings websettings = getSettings();
        File file = getContext().getCacheDir();
        if(websettings != null && file != null)
        {
            websettings.setAppCacheMaxSize(0xa00000L);
            websettings.setAppCachePath(file.getAbsolutePath());
            websettings.setAllowFileAccess(true);
            websettings.setAppCacheEnabled(true);
            websettings.setJavaScriptEnabled(true);
            websettings.setCacheMode(-1);
        }
    }

    private boolean isNetworkAvailable()
    {
        return ((ConnectivityManager)getContext().getSystemService("connectivity")).getActiveNetworkInfo() != null;
    }

    private void setBackgroundPattern()
    {
        setBackgroundColor(0);
        setBackgroundResource(0x7f020002);
    }

    public void loadEmpty()
    {
        loadData("<html style='background:transparent'></html>", "text/html", "utf-8");
    }

    public void loadUrl(String s)
    {
        if(s == null)
        {
            Log.e("repix", "null url");
            return;
        }
        if(!isNetworkAvailable())
        {
            getSettings().setCacheMode(1);
        } else
        {
            getSettings().setCacheMode(-1);
        }
        pageLoaded = false;
        setVisibility(4);
        super.loadUrl(s);
    }

}
