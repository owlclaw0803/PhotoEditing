package it.repix.android;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public abstract class SideListView extends ListView
{
    class SideAdapter extends ArrayAdapter
    {
        public View getView(int i, View view, ViewGroup viewgroup)
        {
            View view1 = super.getView(i, view, viewgroup);
            TextView textview = (TextView)view1.findViewById(0x7f060010);
            if(font != null)
            {
                textview.setTypeface(font);
            }
            ImageView imageview = (ImageView)view1.findViewById(0x7f06000f);
            if(imageview != null)
            {
                setIcon(i, imageview);
            }
            return view1;
        }

        public SideAdapter(Context context)
        {
            super(context, 0x7f030001, 0x7f060010, getValues());
            Log.d("repix", "SideAdapter");
        }
    }


    public static final String TAG = "repix";
    Typeface font;

    public SideListView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        Log.d("repix", "SideListView");
        init();
    }

    abstract String[] getValues();

    public void init()
    {
        Log.d("repix", "init");
        font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MuseoSansRounded-300.otf");
        setAdapter(new SideAdapter(getContext()));
        Log.d("repix", "init done");
    }

    abstract void setIcon(int i, ImageView imageview);
}
