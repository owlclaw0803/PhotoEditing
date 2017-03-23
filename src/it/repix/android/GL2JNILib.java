package it.repix.android;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

// Referenced classes of package it.repix.android:
//            RepixActivity

public class GL2JNILib
{

    public static final int DEVICE_IPAD_1X = 1;
    public static final int DEVICE_IPAD_2X = 2;
    public static final int DEVICE_IPHONE_2X = 0;
    public static final int DEVICE_IPHONE_3X = 3;
    public static final String TAG = "repix";

    public GL2JNILib()
    {
    }

    public static native boolean canRedo();

    public static native void clear(int ai[], int i, int j);

    public static native boolean closeStore();

    public static native void didReceiveMemoryWarning();

    public static int getDeviceType()
    {
        boolean flag1;
label0:
        {
            Resources resources = RepixActivity.getInstance().getResources();
            byte byte0 = 1;
            if(resources.getDisplayMetrics().density > 1.5F)
            {
                boolean flag;
                DisplayMetrics displaymetrics;
                if((0xf & resources.getConfiguration().screenLayout) >= 3)
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
                displaymetrics = new DisplayMetrics();
                RepixActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                if(displaymetrics.densityDpi > 320)
                {
                    flag1 = true;
                } else
                {
                    flag1 = false;
                }
                if(!flag)
                {
                    break label0;
                }
                byte0 = 2;
            }
            return byte0;
        }
        return !flag1 ? 0 : 3;
    }

    public static native Bitmap getProcessedPhoto();

    public static void init(int i, int j)
    {
        init(i, j, getDeviceType());
    }

    private static native void init(int i, int j, int k);

    public static native void openStore();

    public static native void productRequestComplete(PurchaseManager.ProductResponse aproductresponse[], int i, int j);

    public static native void purchaseCompleted(String s, int i);

    public static native void redo();

    public static native void resetToOriginal();

    public static native int step();

    public static native void undo();

    public static native void updatePurchasedFlags();

    static 
    {
        System.loadLibrary("repix");
    }
}
