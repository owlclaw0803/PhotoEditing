package it.repix.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RepixTextView extends TextView
{

    public static final String TAG = "RepixTextView";
    Typeface font;
    protected ColorStateList shadowColor2;

    public RepixTextView(Context context)
    {
        this(context, null);
    }

    public RepixTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/MuseoSansRounded-300.otf");
        setTypeface(font);
    }
}
