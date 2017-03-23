package it.repix.android;

import android.media.ExifInterface;
import java.io.IOException;
import java.util.*;

public class ExifHelper
{

    public static final String EXIF_KEYS[] = {
        "FNumber", "DateTime", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", 
        "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSTimeStamp", "ImageLength", "ImageWidth", "ISOSpeedRatings", "Make", "Model", "Orientation", 
        "WhiteBalance"
    };
    HashMap map;

    public ExifHelper()
    {
        reset();
    }

    public String getAttribute(String s)
    {
        return (String)map.get(s);
    }

    public void readExif(String s)
        throws IOException
    {
        ExifInterface exifinterface = new ExifInterface(s);
        for(int i = 0; i < EXIF_KEYS.length; i++)
        {
            String s1 = EXIF_KEYS[i];
            String s2 = exifinterface.getAttribute(s1);
            if(s2 != null)
            {
                map.put(s1, s2);
            }
        }

    }

    public void reset()
    {
        map = new HashMap();
    }

    public void setAttribute(String s, String s1)
    {
        map.put(s, s1);
    }

    public void writeExif(String s)
        throws IOException
    {
        ExifInterface exifinterface = new ExifInterface(s);
        Iterator iterator = map.keySet().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s1 = (String)iterator.next();
            String s2 = (String)map.get(s1);
            if(s1 != null && s2 != null)
            {
                exifinterface.setAttribute(s1, s2);
            }
        } while(true);
        exifinterface.saveAttributes();
    }

}
