package app.vincenthu.citrix.com.storming.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.vincenthu.citrix.com.storming.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class Utils {

    public enum days {Yesterday, Today, Tomorrow, the_day_after_tomorrow, notinscope}

    public static String formatTemperature(Context context, double temperature) {
        return context.getString(R.string.format_temperature, temperature);
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static String getWindDirection(float degrees){
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return direction;
    }

    public static String parseTimefromRealtoStringDate(long seconds, String format){
        long milliseconds = seconds * 1000;
        Date dateData = new Date(milliseconds);
        SimpleDateFormat desiredFormat = new SimpleDateFormat(format);
        String date = desiredFormat.format(dateData);
        return date;
    }

    public static long getdaytotoday(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTimeInMillis();
    }

    public static days getDayoftime(long seconds){
        long todayinMillis = getdaytotoday();
        if(todayinMillis <= seconds * 1000){
            if((todayinMillis + 24 * 60 * 60 * 1000) > seconds * 1000){
                return days.Today;
            }
            else if((todayinMillis + 2 * 24 * 60 * 60 * 1000) > seconds * 1000){
                return days.Tomorrow;
            }
            else if((todayinMillis + 3 * 24 * 60 * 60 * 1000) > seconds * 1000){
                return days.notinscope;
            }else{
                return days.notinscope;
            }
        }else{
            if(((todayinMillis - 24 * 60 * 60 * 1000) <= seconds * 1000)){
                return days.Yesterday;
            }else{
                return days.notinscope;
            }
        }
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
