package it.repix.android;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MuseoTextView extends TextView
{

    public MuseoTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MuseoSansRounded-300.otf"));
    }
}
