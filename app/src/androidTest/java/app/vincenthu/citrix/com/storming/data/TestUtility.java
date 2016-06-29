package app.vincenthu.citrix.com.storming.data;

import android.content.ContentValues;

/**
 * Created by Administrator on 6/27/2016.
 */
public class TestUtility {

    public static ContentValues CreateLocationValues(){
        ContentValues testLocationTableValues = new ContentValues();
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Name, "Nanjing");
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Latitude, 32.06 );
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Longitude, 118.78);
        return testLocationTableValues;
    }

    public static ContentValues CreateWeatherValues(int entry_ID){
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_HUMIDITY, "89");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_LOCATION_ID, entry_ID);
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_PRESSURE, "1018.17");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX, "23");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN, "17");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME, 123345);
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION, "Cloudy");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION, "Thick Clouds");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND, "3.83");
        return weatherValues;
    }
}
