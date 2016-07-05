package app.vincenthu.citrix.com.storming.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.vincenthu.citrix.com.storming.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class Utils {

    public static String parseTimefromRealtoStringDate(long seconds, String format){
        long milliseconds = seconds * 1000;
        Date dateData = new Date(milliseconds);
        SimpleDateFormat desiredFormat = new SimpleDateFormat(format);
        String date = desiredFormat.format(dateData);
        return date;
    }

    public static String getPreferredLocation(Context context){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String location = p.getString(context.getString(R.string.pref_general_location_key), context.getString(R.string.pref_general_location_default));
        Log.i("Location in preference:", location);
        return location;
    }

    public static String getPreferredMetric(Context context){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        return p.getString(context.getString(R.string.pref_general_units_key),context.getString( R.string.pref_general_units_metric));
    }
}
