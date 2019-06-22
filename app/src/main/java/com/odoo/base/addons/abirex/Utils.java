package com.odoo.base.addons.abirex;

import android.content.Context;
import android.graphics.Bitmap;

import com.odoo.core.utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {


    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getLowerBound(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  format.format(calendar.getTime());
    }

    public static String getUpperBound(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return format.format(calendar.getTime());
    }

    public static Bitmap getBitmapFromString(Context context, String bitmapString){
        Bitmap bitmap;
        if (bitmapString.equals("false") || bitmapString.isEmpty()) {
            bitmap = BitmapUtils.getAlphabetImage(context, bitmapString);
        } else {
            bitmap = BitmapUtils.getBitmapImage(context, bitmapString);
        }
        return bitmap;
    }

}
