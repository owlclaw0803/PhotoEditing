package it.repix.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

// Referenced classes of package it.repix.android:
//            SideListView

public class LeftSideListView extends SideListView
{
    public enum Options
    {
    	CAMU("CAMU", 0),
        EDITOR("EDITOR", 1),
        FEED("FEED", 2),
        STORE("STORE", 3),
        PHOTOS("PHOTOS", 4),
        CAMERA("CAMERA", 5),
        HELP("HELP", 6);
        
        static 
        {
            Options aoptions[] = new Options[7];
            aoptions[0] = CAMU;
            aoptions[1] = EDITOR;
            aoptions[2] = FEED;
            aoptions[3] = STORE;
            aoptions[4] = PHOTOS;
            aoptions[5] = CAMERA;
            aoptions[6] = HELP;
        }

        private Options(String s, int i)
        {
        }
    }


    int icons[] = {
        0x7f020023, 0x7f020024, 0x7f020027, 0x7f020030, 0x7f02002d, 0x7f020020, 0x7f02002a
    };

    public LeftSideListView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    String[] getValues()
    {
        return (new String[] {
            "Our Camera App: Camu", "Editor", "Starters", "Store", "Gallery", "Camera", "Guide"
        });
    }

    void setIcon(int i, ImageView imageview)
    {
        imageview.setImageResource(icons[i]);
    }
}
