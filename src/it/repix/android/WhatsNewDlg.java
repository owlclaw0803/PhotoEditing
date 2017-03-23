package it.repix.android;

import android.app.*;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;

// Referenced classes of package it.repix.android:
//            RepixActivity

public class WhatsNewDlg extends DialogFragment
{

    private int mMaxHeight;
    private int mMaxWidth;

    public WhatsNewDlg(int i, int j)
    {
        mMaxWidth = i;
        mMaxHeight = j;
    }

    private void resize(Dialog dialog)
    {
        int i = (int)TypedValue.applyDimension(1, 412F, getResources().getDisplayMetrics());
        int j = (int)TypedValue.applyDimension(1, 300F, getResources().getDisplayMetrics());
        if(j > mMaxWidth)
        {
            j = (int)(0.95F * (float)mMaxWidth);
            i = (j * 412) / 300;
        }
        if(i > mMaxHeight)
        {
            i = (int)(0.95F * (float)mMaxHeight);
            j = (i * 300) / 412;
        }
        dialog.getWindow().setLayout(j, i);
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), 0x1030075));
        View view = getActivity().getLayoutInflater().inflate(0x7f030003, null);
        ((ImageButton)view.findViewById(0x7f060012)).setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view1)
            {
                dismiss();
            }
        });
        ((ImageView)view.findViewById(0x7f060013)).setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view1)
            {
                dismiss();
                RepixActivity.downloadCamu(getActivity());
            }
        });
        builder.setView(view);
        android.app.AlertDialog alertdialog = builder.create();
        resize(alertdialog);
        return alertdialog;
    }

    public void onResume()
    {
        super.onResume();
        resize(getDialog());
    }
}
