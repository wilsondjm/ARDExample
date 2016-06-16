package app.vincenthu.citrix.com.storming.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 6/2/2016.
 */
public final class StormingContract {

    public static final String CONTENT_AUTHORITY = "com.citrix.vincenthu.app.storming";
    public static final Uri BASE_CONTENT_URI = Uri.parse(String.format("content://%s", CONTENT_AUTHORITY));

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public StormingContract(){

    }

    public static abstract class WeatherInfoEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        //the table name
        public static final String TABLE_NAME = "WeatherInfo";

        //Fields
        public static final String COLUMN_NAME_WEATHER_CONDITION = "weather_condition";
        public static final String COLUMN_NAME_WEATHER_DESCRIPTION = "weather_description";
        public static final String COLUMN_NAME_TEMPERATURE_MIN = "temperature_min";
        public static final String COLUMN_NAME_TEMPERATURE_MAX = "temperature_max";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_LOCATION_ID = "location_id";
        public static final String COLUMN_NAME_WIND = "wind";
        public static final String COLUMN_NAME_HUMIDITY = "humidity";
        public static final String COLUMN_NAME_PRESSURE = "pressure";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherWithLocationUri(String location){
            return CONTENT_URI.buildUpon().appendPath(location).build();
        }
    }

    public static abstract class LocationEntry implements BaseColumns{
        public static final String TABLE_NAME = "Location";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String COLUMN_NAME_Latitude = "latitude";
        public static final String COLUMN_NAME_Longitude = "longitude";
        public static final String COLUMN_NAME_Name = "name";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    public static final String WeatherInfoEntry_Create_Table = "CREATE TABLE %s (" +
            WeatherInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeatherInfoEntry.COLUMN_NAME_HUMIDITY + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_LOCATION_ID + " INTEGER NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_PRESSURE + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_TIME + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION + " TEXT NOT NULL," +
            WeatherInfoEntry.COLUMN_NAME_WIND + " TEXT NOT NULL," +
            " FOREIGN KEY ( " + WeatherInfoEntry.COLUMN_NAME_LOCATION_ID + " ) REFERENCES " + LocationEntry.TABLE_NAME + " ( " + LocationEntry._ID + " ), " +
            " UNIQUE ( " + WeatherInfoEntry.COLUMN_NAME_TIME + ", " + WeatherInfoEntry.COLUMN_NAME_LOCATION_ID + " ) ON CONFLICT REPLACE);";

    public static final String LocationEntry_Create_Table = "CREATE TABLE %s (" +
            LocationEntry._ID + " INTEGER PRIMARY KEY," +
            LocationEntry.COLUMN_NAME_Latitude + " TEXT NOT NULL," +
            LocationEntry.COLUMN_NAME_Longitude + " TEXT NOT NULL," +
            LocationEntry.COLUMN_NAME_Name + " TEXT NOT NULL" +
            " );";
}
