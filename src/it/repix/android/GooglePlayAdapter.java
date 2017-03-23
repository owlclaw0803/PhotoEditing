package it.repix.android;

import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import com.android.vending.billing.IInAppBillingService;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;

// Referenced classes of package it.repix.android:
//            PurchaseManager, RepixActivity, GL2JNILib

public class GooglePlayAdapter extends PurchaseManager
{

    public static final int API_VERSION = 3;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final String NO_PLAY_STORE = "Unable to connect to Play Store";
    IInAppBillingService mService;
    ServiceConnection mServiceConn;

    public GooglePlayAdapter()
    {
        mServiceConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentname, IBinder ibinder)
            {
                mService = com.android.vending.billing.IInAppBillingService.Stub.asInterface(ibinder);
            }

            public void onServiceDisconnected(ComponentName componentname)
            {
                mService = null;
            }
        };
    }

    private static void convertProductDetails(HashMap hashmap, Bundle bundle)
        throws JSONException
    {
        if(bundle != null){
        	ArrayList arraylist;
        	if((arraylist = bundle.getStringArrayList("DETAILS_LIST")) != null)
            {
                int i = 0;
                while(i < arraylist.size()) 
                {
                    JSONObject jsonobject = new JSONObject((String)arraylist.get(i));
                    PurchaseManager.ProductResponse productresponse = new PurchaseManager.ProductResponse();
                    productresponse.error = 0;
                    productresponse.productId = jsonobject.getString("productId");
                    productresponse.title = jsonobject.getString("title");
                    if(productresponse.title != null)
                    {
                        productresponse.title = productresponse.title.replaceAll("\\s\\(.*\\)", "");
                    }
                    productresponse.description = jsonobject.getString("description");
                    productresponse.price = jsonobject.getString("price");
                    hashmap.put(productresponse.productId, productresponse);
                    i++;
                }
            }
        }
    }

    private Bundle getProductDetails(String as[])
        throws RemoteException
    {
        if(mService == null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("RESPONSE_CODE", 6);
            return bundle;
        }
        ArrayList arraylist = new ArrayList();
        for(int i = 0; i < as.length; i++)
        {
            arraylist.add(as[i]);
        }

        Bundle bundle1 = new Bundle();
        bundle1.putStringArrayList("ITEM_ID_LIST", arraylist);
        return mService.getSkuDetails(3, getPackageName(), "inapp", bundle1);
    }

    private String storePurchase(String s, String s1)
    {
        String s2;
        JSONObject jsonobject;
        if(s1 == null)
            s1 = "";
        s2 = null;
        try
        {
        	if(s == null)
        		return s2;
            jsonobject = new JSONObject(s);
            s2 = jsonobject.getString("productId");
            if(jsonobject.optInt("purchaseState") == 1)
                s1 = null;
            android.content.SharedPreferences.Editor editor = RepixActivity.getInstance().getPreferences().edit();
            editor.putString(s2, s1);
            editor.commit();
        }
        catch(JSONException jsonexception)
        {
            return s2;
        }
        return s2;
    }

    public void bind()
    {
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        if(!RepixActivity.getInstance().getPackageManager().queryIntentServices(intent, 0).isEmpty())
        {
            RepixActivity.getInstance().bindService(intent, mServiceConn, 1);
        }
    }

    public void buy(String s)
    {
        String s1 = p2a(s);
        IInAppBillingService iinappbillingservice = mService;
        Bundle bundle;
        bundle = null;
        if(iinappbillingservice != null){
        	try{
		        Bundle bundle1 = mService.getBuyIntent(3, getPackageName(), s1, "inapp", UUID.randomUUID().toString());
		        bundle = bundle1;
        	}catch(RemoteException remoteexception){
        		remoteexception.printStackTrace();
                log(remoteexception.toString());
                bundle = null;
        	}
        }
        if(bundle != null){
        	PendingIntent pendingintent = (PendingIntent)bundle.getParcelable("BUY_INTENT");
            switch(getResponseCodeFromBundle(bundle)){
            default:case 1:
            	return;
            case 0:
            	try {
                    RepixActivity.getInstance().startIntentSenderForResult(pendingintent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0).intValue(), Integer.valueOf(0).intValue(), Integer.valueOf(0).intValue());
                } catch(android.content.IntentSender.SendIntentException sendintentexception) {
                    RepixActivity.getInstance().alert(sendintentexception.getMessage(), null);
                }
                return;
            case 7:
            	restorePurchases(false);
                return;
            }
        }
        RepixActivity.getInstance().alert("Unable to connect to Play Store", null);
    }

    public String getPackageName()
    {
        return RepixActivity.getInstance().getPackageName();
    }

    int getResponseCodeFromBundle(Bundle bundle)
    {
        Object obj = bundle.get("RESPONSE_CODE");
        if(obj == null)
        {
            return 0;
        }
        if(obj instanceof Integer)
        {
            return ((Integer)obj).intValue();
        }
        if(obj instanceof Long)
        {
            return (int)((Long)obj).longValue();
        } else
        {
            throw new RuntimeException((new StringBuilder()).append("Unexpected type for bundle response code: ").append(obj.getClass().getName()).toString());
        }
    }

    public boolean isProductPurchased(String s, String s1)
    {
        return s1 != null;
    }

    public void onRequestBuyResult(int i, Intent intent)
    {
        String s = null;
        if(intent != null)
        {
            intent.getExtras();
            int j = intent.getIntExtra("RESPONSE_CODE", 0);
            s = null;
            if(i == -1)
            {
                s = null;
                if(j == 0)
                {
                    s = storePurchase(intent.getStringExtra("INAPP_PURCHASE_DATA"), intent.getStringExtra("INAPP_DATA_SIGNATURE"));
                }
            }
        }
        byte byte0 = 0;
        final String skuComplete;
        final int errorComplete;
        if(s == null)
        {
            byte0 = -1;
        }
        if(s == null)
        {
            s = "";
        }
        skuComplete = s;
        errorComplete = byte0;
        RepixActivity.getInstance().queueEvent(new Runnable() {
            public void run()
            {
                GL2JNILib.purchaseCompleted(PurchaseManager.a2p(skuComplete), errorComplete);
            }
        });
    }

    public void requestProductDetails(String as[], final int callback, final int context)
    {
        HashMap hashmap;
        Bundle bundle;
        final PurchaseManager.ProductResponse productList[];
        hashmap = new HashMap();
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            PurchaseManager.ProductResponse productresponse = new PurchaseManager.ProductResponse();
            productresponse.productId = p2a(s);
            productresponse.title = "";
            productresponse.description = "";
            productresponse.price = "";
            productresponse.error = -1000;
            hashmap.put(s, productresponse);
        }

        bundle = null;
        int j = 6;
        int i1;
        int j1;
        try
        {
            bundle = getProductDetails(as);
            j1 = bundle.getInt("RESPONSE_CODE");
            j = j1;
        }
        catch(RemoteException remoteexception)
        {
            remoteexception.printStackTrace();
        }
        switch(j){
        default:
        	RepixActivity.getInstance().alert("Unable to connect to Play Store", null);
        	break;
        case 0:
        	try {
				convertProductDetails(hashmap, bundle);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	break;
        case 1:
        	break;
        }
        productList = new PurchaseManager.ProductResponse[as.length];
        try{
	        for(i1 = 0; i1 < productList.length; i1++)
	        {
	            productList[i1] = (PurchaseManager.ProductResponse)hashmap.get(as[i1]);
	            productList[i1].productId = a2p(productList[i1].productId);
	        }
	        RepixActivity.getInstance().queueEvent(new Runnable(){
	        	public void run()
	            {
	                PurchaseManager.log("updatePurchasedFlags");
	                GL2JNILib.updatePurchasedFlags();
	                RepixActivity.getInstance().requestRender();
	            }
	        });
	        return;
        }catch(Exception exception){
            for(int l = 0; l < productList.length; l++)
            {
                productList[l] = (PurchaseManager.ProductResponse)hashmap.get(as[l]);
                productList[l].productId = a2p(productList[l].productId);
            }
            RepixActivity.getInstance().queueEvent(new Runnable() {
                public void run()
                {
                    GL2JNILib.productRequestComplete(productList, callback, context);
                }
            });
        }
    }

    public void restorePurchases(boolean flag)
    {
        IInAppBillingService iinappbillingservice = mService;
        Bundle bundle;
        bundle = null;
        if(iinappbillingservice == null)
        {
            return;
        }
        try{
	        Bundle bundle1 = mService.getPurchases(3, getPackageName(), "inapp", null);
	        bundle = bundle1;
        }catch(Exception e1){
        	bundle = null;
        }
        if(bundle != null){
        	if(getResponseCodeFromBundle(bundle) == 0)
            {
                final ArrayList ownedSkus = bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                final ArrayList purchaseDataList = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList arraylist = bundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                bundle.getString("INAPP_CONTINUATION_TOKEN");
                for(int i = 0; i < purchaseDataList.size(); i++)
                {
                    storePurchase((String)purchaseDataList.get(i), (String)arraylist.get(i));
                }

                if(!flag)
                {
                    RepixActivity.getInstance().queueEvent(new Runnable() {
                        public void run()
                        {
                            for(int j = 0; j < purchaseDataList.size(); j++)
                            {
                                GL2JNILib.purchaseCompleted(PurchaseManager.a2p((String)ownedSkus.get(j)), 0);
                            }

                            RepixActivity.getInstance().requestRender();
                        }
                    });
                }
            } else
            if(!flag)
            {
                RepixActivity.getInstance().queueEvent(new Runnable() {
                    public void run()
                    {
                        GL2JNILib.purchaseCompleted("", -1);
                        RepixActivity.getInstance().requestRender();
                    }
                });
            }
            if(!flag)
            	return;
            log("schedule updatePurchasedFlags");
            RepixActivity.getInstance().queueEvent(new Runnable() {
                public void run()
                {
                    PurchaseManager.log("updatePurchasedFlags");
                    GL2JNILib.updatePurchasedFlags();
                    RepixActivity.getInstance().requestRender();
                }
            });
            return;
        }
        if(!flag)
            RepixActivity.getInstance().alert("Unable to connect to Play Store", null);
        return;
    }

    public void unbind()
    {
        if(mServiceConn != null)
        {
            RepixActivity.getInstance().unbindService(mServiceConn);
        }
    }
}
