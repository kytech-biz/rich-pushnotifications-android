package biz.kytech.pushnotification.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final String TAG = DateTimeUtil.class.getSimpleName();

    static public String UTCiso8601StringToLocalYYYYMMDDString(String strISO) {
        Date localDate = null;
        String localTimeString = "";
        Exception exception = null;

        String[] formatList = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss"
        };

        for (String format : formatList) {
            try {
                DateFormat df = new SimpleDateFormat(format);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                localDate = df.parse(strISO);
                break;
            } catch (ParseException e) {
                exception = e;
            }
        }

        if (localDate != null) {
            DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            df.setTimeZone(TimeZone.getDefault());
            localTimeString = df.format(localDate);
        } else {
            Log.e(TAG, "UTCiso8601StringToLocalYYYYMMDDString() error:" + exception.toString());
        }

        return localTimeString;
    }
}
