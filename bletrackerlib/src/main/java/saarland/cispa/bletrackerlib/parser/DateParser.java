package saarland.cispa.bletrackerlib.parser;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateParser {

    private static final String DATEFORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss";
    private static final String DATEFORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String TAG = "DateParser";

    public static Date GetUTCdatetimeAsDate()
    {
        //note: doesn't check for null
        return stringDateToDate(getUTCdatetimeAsString());
    }

    public static String getUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_ISO8601);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date stringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_ISO8601);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public static String utcToLocalDate(Context context, String utcDate) {
        Locale locale = context.getResources().getConfiguration().locale;

        try {
            SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_ISO8601, locale);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(utcDate);
            df.setTimeZone(TimeZone.getDefault());
            return df.format(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_ISO8601, locale);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(utcDate);
            df.setTimeZone(TimeZone.getDefault());
            return df.format(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static String makeDisplayDate(String isoDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_ISO8601);
            Date date = df.parse(isoDate);
            df = new SimpleDateFormat(DATEFORMAT_DISPLAY);
            return df.format(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }
}
