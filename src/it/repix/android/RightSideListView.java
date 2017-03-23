package it.repix.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.util.*;

// Referenced classes of package it.repix.android:
//            SideListView

public class RightSideListView extends SideListView
{
    class SaveResolveInfo extends ResolveInfo
    {
        public Drawable loadIcon(PackageManager packagemanager)
        {
            return getResources().getDrawable(0x7f02001d);
        }

        public CharSequence loadLabel(PackageManager packagemanager)
        {
            return "Save to Gallery";
        }

        public SaveResolveInfo()
        {
            super();
            activityInfo = new ActivityInfo();
            activityInfo.applicationInfo = new ApplicationInfo();
            ApplicationInfo applicationinfo = activityInfo.applicationInfo;
            activityInfo.packageName = "it.repix.android";
            applicationinfo.packageName = "it.repix.android";
        }
    }


    static final String PREFERRED[] = {
        "com.facebook.katana", "com.twitter.android", "com.tumblr", "com.instagram.android", "com.yahoo.mobile.client.android.flickr", "com.whatsapp", "com.dropbox.android", "com.path", "com.google.android.apps.uploader", "org.wordpress.android", 
        "com.google.android.gm", "com.google.android.apps.plus"
    };
    public static final String TAG = "repix";
    ResolveInfo SAVE;
    HashMap customIcons;
    List intentList;

    public RightSideListView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    int getIconForIntent(String s)
    {
        Integer integer = (Integer)customIcons.get(s);
        if(integer == null)
        {
            return 0;
        } else
        {
            return integer.intValue();
        }
    }

    public ResolveInfo getResolveInfoAt(int i)
    {
        return (ResolveInfo)intentList.get(i);
    }

    String[] getValues()
    {
        Log.d("repix", "getValues");
        if(intentList == null)
        {
            init();
        }
        PackageManager packagemanager = getContext().getPackageManager();
        ArrayList arraylist = new ArrayList();
        ResolveInfo resolveinfo;
        for(Iterator iterator = intentList.iterator(); iterator.hasNext(); arraylist.add(resolveinfo.loadLabel(packagemanager).toString()))
        {
            resolveinfo = (ResolveInfo)iterator.next();
            Log.d("repix", resolveinfo.activityInfo.packageName);
        }

        return (String[])arraylist.toArray(new String[arraylist.size()]);
    }

    public void init()
    {
        Log.d("repix", "Right init");
        PackageManager packagemanager = getContext().getPackageManager();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/jpeg");
        List list = packagemanager.queryIntentActivities(intent, 0x10000);
        intentList = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            ResolveInfo resolveinfo = (ResolveInfo)iterator.next();
            Log.d("repix", resolveinfo.toString());
            if(isAccepted(resolveinfo.activityInfo.packageName))
            {
                intentList.add(resolveinfo);
            }
        } while(true);
        SAVE = new SaveResolveInfo();
        intentList.add(0, SAVE);
        customIcons = new HashMap();
        customIcons.put("it.repix.android", Integer.valueOf(0x7f02001b));
        Log.d("repix", (new StringBuilder()).append("customIcons ").append(customIcons).toString());
        super.init();
        Log.d("repix", "Right done");
    }

    boolean isAccepted(String s)
    {
        return !"it.repix.android".equals(s);
    }

    boolean isPreferred(String s)
    {
        String as[] = PREFERRED;
        int i = as.length;
        for(int j = 0; j < i; j++)
        {
            if(s.startsWith(as[j]))
            {
                return true;
            }
        }

        return false;
    }

    void setIcon(int i, ImageView imageview)
    {
        getContext().getPackageManager();
        ResolveInfo resolveinfo = (ResolveInfo)intentList.get(i);
        int j = getIconForIntent(resolveinfo.activityInfo.packageName);
        if(j != 0)
        {
            imageview.setImageResource(j);
            return;
        } else
        {
            imageview.setImageDrawable(resolveinfo.loadIcon(getContext().getPackageManager()));
            return;
        }
    }

}
