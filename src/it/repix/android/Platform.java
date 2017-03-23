package it.repix.android;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.*;

import com.flurry.android.FlurryAgent;

// Referenced classes of package it.repix.android:
//            RepixActivity, PurchaseManager, GL2JNILib, GL2JNIView

public class Platform
{

    public static final boolean DEBUG = false;
    public static final String TAG = "repix";

    public Platform()
    {
    }

    public static boolean isProductPurchased(String s)
    {
        SharedPreferences sharedpreferences = RepixActivity.getInstance().getPreferences();
        String s1 = sharedpreferences.getString("it.repix.brushpack.all2", null);
        boolean flag = PurchaseManager.getInstance().isProductPurchased("it.repix.brushpack.all2", s1);
        if(flag)
        {
            return flag;
        }
        String s2 = sharedpreferences.getString(s, null);
        boolean flag1 = PurchaseManager.getInstance().isProductPurchased(s, s2);
        if(flag1)
        {
            return flag1;
        } else
        {
            String s3 = sharedpreferences.getString(PurchaseManager.p2a(s), null);
            return PurchaseManager.getInstance().isProductPurchased(s, s3);
        }
    }

    public static byte[] loadBinaryFile(String s)
        throws IOException
    {
        InputStream inputstream;
        inputstream = it.repix.android.Platform.class.getResourceAsStream((new StringBuilder()).append("/").append(s).toString());
        if(inputstream == null)
        {
            return null;
        }
        try{
	        ByteArrayOutputStream bytearrayoutputstream;
	        byte abyte0[];
	        bytearrayoutputstream = new ByteArrayOutputStream();
	        abyte0 = new byte[4096];
	        while(true){
		        int i = inputstream.read(abyte0);
		        if(i > 0)
		        {
		        	bytearrayoutputstream.write(abyte0, 0, i);
		        	continue;
		        }
		        break;
	        }
	        byte abyte1[] = bytearrayoutputstream.toByteArray();
	        inputstream.close();
	        return abyte1;
        }catch(Exception exception){
        	try{
	        	inputstream.close();
	        	throw exception;
        	}catch(Exception e){
        		return null;
        	}
        }
        
        
    }

    public static Bitmap loadBitmap(String s)
        throws IOException
    {
        return BitmapFactory.decodeStream(it.repix.android.Platform.class.getResourceAsStream((new StringBuilder()).append("/").append(s).toString()));
    }

    public static void log(String s)
    {
    }

    public static void platformCommand(String s)
    {
        if(s.startsWith("toolbartitle"))
        {
            RepixActivity.getInstance().setHeadingTitle(s.substring(13));
        } else
        {
            if(s.startsWith("toolbar"))
            {
                RepixActivity.getInstance().setToolbar(s.substring(8));
                return;
            }
            FlurryAgent.logEvent(s);
            if(s.startsWith("button_long_pressed:"))
            {
                RepixActivity.getInstance().performHapticFeedback(0);
            }
            if(s.startsWith("toggle_clicked:") || s.startsWith("button_clicked:") || s.startsWith("brush:") || s.startsWith("frame:") || s.startsWith("preset:") || s.startsWith("crop:"))
            {
                RepixActivity.getInstance().performHapticFeedback(1);
            }
            if(s.startsWith("buy:"))
            {
                String s1 = s.substring(4);
                PurchaseManager.getInstance().buy(s1);
            }
            if(s.equals("restore"))
            {
                PurchaseManager.getInstance().restorePurchases(false);
            }
            if(s.equals("camera"))
            {
                RepixActivity.getInstance().openCamera();
            }
            if(s.equals("photoroll"))
            {
                RepixActivity.getInstance().openGallery();
            }
            if(s.equals("editor_back"))
            {
                RepixActivity.getInstance().toggleMenu();
            }
            if(s.equals("editor_done"))
            {
                RepixActivity.getInstance().toggleShareMenu();
            }
            if(s.equals("reset_to_original"))
            {
                RepixActivity.getInstance().confirm("Reset to original?", new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialoginterface, int i)
                    {
                        RepixActivity.getInstance().queueEvent(new Runnable() {
                            public void run()
                            {
                                GL2JNILib.resetToOriginal();
                                RepixActivity.glView.requestRender();
                            }
                        });
                    }

                });
                return;
            }
        }
    }

    public static void requestProductDetails(final String as[], final int i, final int j)
    {
        (new Thread(new Runnable() {
            public void run()
            {
                PurchaseManager.getInstance().requestProductDetails(as, i, j);
            }
        })).start();
    }
}
