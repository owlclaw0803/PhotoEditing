package it.repix.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.util.Log;
import java.io.*;
import java.net.*;

// Referenced classes of package it.repix.android:
//            RepixActivity

public class DownloaderDialog
{
    class HttpRunnable implements Runnable
    {
        public void run()
        {
            HttpURLConnection httpurlconnection = null;
            URLConnection urlconnection;
            int i;
            try{
	            urlconnection = (new URL(uri.toString())).openConnection();
	            urlconnection.setConnectTimeout(30000);
	            urlconnection.setReadTimeout(30000);
	            urlconnection.setDefaultUseCaches(true);
	            i = urlconnection.getContentLength();
	            Log.i("repix", (new StringBuilder()).append("http length ").append(i).toString());
	            httpurlconnection = null;
	            if(i <= 0)
	            {
	                //break MISSING_BLOCK_LABEL_109;
	            	return;
	            }
	            progress.setIndeterminate(false);
	            progress.setMax(i);
	            boolean flag = urlconnection instanceof HttpURLConnection;
	            httpurlconnection = null;
	            if(!flag)
	            {
	                //break MISSING_BLOCK_LABEL_129;
	            	return;
	            }
	            httpurlconnection = (HttpURLConnection)urlconnection;
	            long l;
	            InputStream inputstream;
	            l = System.currentTimeMillis();
	            inputstream = urlconnection.getInputStream();
	            if(inputstream == null)
	            {
	                //break MISSING_BLOCK_LABEL_182;
	            	return;
	            }
	            RepixActivity.getInstance().openPhoto(BitmapFactory.decodeStream(new ProgressInputStream(new BufferedInputStream(inputstream, 32768))));
	            inputstream.close();
	            Log.i("repix", (new StringBuilder()).append("http complete ").append(System.currentTimeMillis() - l).append(" ms").toString());
	            progress.dismiss();
	            if(httpurlconnection != null)
	            {
	                httpurlconnection.disconnect();
	            }
            }catch(IOException ioexception){
            	if(cancelled)
                    RepixActivity.getInstance().alert((new StringBuilder()).append("Failed to download image\n").append(ioexception.getMessage()).toString(), null);
                progress.dismiss();
                if(httpurlconnection != null)
                	httpurlconnection.disconnect();
                return;
            }catch(Exception exception){
            	progress.dismiss();
                if(httpurlconnection != null)
                    httpurlconnection.disconnect();
                try{
                	throw exception;
                }catch(Exception e){}
            }
        }

        HttpRunnable()
        {
            super();
        }
    }

    class ProgressInputStream extends InputStream
    {
        InputStream is;

        public int available()
            throws IOException
        {
            return is.available();
        }

        public void close()
            throws IOException
        {
            progress.dismiss();
            super.close();
        }

        protected void incrementProgress(int i)
        {
            if(i > 0 && progress != null)
            {
                progress.incrementProgressBy(i);
            }
        }

        public void mark(int i)
        {
            is.mark(i);
        }

        public boolean markSupported()
        {
            return is.markSupported();
        }

        public int read()
            throws IOException
        {
            return is.read();
        }

        public int read(byte abyte0[], int i, int j)
            throws IOException
        {
            if(cancelled)
            {
                throw new IOException("Cancelled");
            } else
            {
                int k = is.read(abyte0, i, j);
                incrementProgress(k);
                return k;
            }
        }

        public void reset() throws IOException
        {
        	try{
	            synchronized(this){
	            	is.reset();
	            }
        	}catch(Exception e){}
        }

        public long skip(long l)
            throws IOException
        {
            long l1 = is.skip(l);
            incrementProgress((int)l1);
            return l1;
        }

        ProgressInputStream(InputStream inputstream)
        {
            super();
            is = inputstream;
        }
    }


    public static final String TAG = "repix";
    boolean cancelled;
    Thread httpThread;
    ProgressDialog progress;
    Uri uri;

    public DownloaderDialog(Uri uri1)
    {
        uri = uri1;
        httpThread = new Thread(new HttpRunnable());
    }

    public static void install(File file)
        throws IOException
    {
        HttpResponseCache.install(new File(file, "http"), 0xa00000L);
    }

    public void show()
    {
        progress = new ProgressDialog(RepixActivity.getInstance());
        progress.setMessage("Downloading...");
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialoginterface)
            {
                cancelled = true;
            }
        });
        progress.setProgressStyle(1);
        progress.show();
        httpThread.start();
    }
}
