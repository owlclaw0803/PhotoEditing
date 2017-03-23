package it.repix.android;

import android.content.Intent;

// Referenced classes of package it.repix.android:
//            GooglePlayAdapter

public abstract class PurchaseManager
{
    static class ProductResponse
    {

        String description;
        int error;
        String price;
        String productId;
        String title;

        public String toString()
        {
            return (new StringBuilder()).append("ProductResponse id:").append(productId).append(" t:").append(title).append(" d:").append(description).append(" p:").append(price).toString();
        }

        ProductResponse()
        {
        }
    }


    public static final boolean DEBUG = false;
    public static final int RC_BUY = 1001;
    public static final String TAG = "repix";
    private static PurchaseManager instance = new GooglePlayAdapter();

    public PurchaseManager()
    {
    }

    public static String a2p(String s)
    {
        if("it.repix.brushpack.artistic".equals(s))
        {
            s = "it.repix.brushpack.paint";
        }
        return s;
    }

    public static PurchaseManager getInstance()
    {
        return instance;
    }

    public static void log(String s)
    {
    }

    public static String p2a(String s)
    {
        if("it.repix.brushpack.paint".equals(s))
        {
            s = "it.repix.brushpack.artistic";
        }
        return s;
    }

    public abstract void bind();

    public abstract void buy(String s);

    public abstract boolean isProductPurchased(String s, String s1);

    public void onRequestBuyResult(int i, Intent intent)
    {
    }

    public abstract void requestProductDetails(String as[], int i, int j);

    public abstract void restorePurchases(boolean flag);

    public abstract void unbind();

}
