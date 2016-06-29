package app.vincenthu.citrix.com.storming.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
