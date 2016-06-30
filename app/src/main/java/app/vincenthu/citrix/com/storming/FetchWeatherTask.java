package app.vincenthu.citrix.com.storming;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.URIResolver;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.util.Utils;

/**
 * Created by Administrator on 6/28/2016.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Integer> {
    String logTag = this.getClass().getSimpleName();
    HttpURLConnection urlConnection = null;
    String responseJSON = null;

    Activity activity;
    ArrayAdapter<String> forecastAdapter;

    public FetchWeatherTask(Activity _activity, ArrayAdapter<String> _forecastAdapter){
        super();
        activity = _activity;
        forecastAdapter = _forecastAdapter;
    }

    private String parseDateTime(long seconds){
        return Utils.parseTimefromRealtoStringDate(seconds, "EE M/dd");
    }

    private Uri parseGeoUri(double lat, double lon){
        Uri resultUrl = Uri.parse(String.format("geo:%f,%f", lat, lon));
        return resultUrl;
    }

    private int ManageLocation(String name, String latitude, String longitude){

        Cursor cursor = activity.getContentResolver().query(StormingContract.LocationEntry.CONTENT_URI,
                null,
                String.format("%s.%s = ?", StormingContract.LocationEntry.TABLE_NAME, StormingContract.LocationEntry.COLUMN_NAME_Name),
                new String[]{name},
                null);

        int locationID = -1;
        if (cursor.moveToFirst()){
            locationID = cursor.getInt(cursor.getColumnIndex(StormingContract.LocationEntry._ID));
        }else{
            ContentValues values = new ContentValues();
            values.put(StormingContract.LocationEntry.COLUMN_NAME_Name, name);
            values.put(StormingContract.LocationEntry.COLUMN_NAME_Longitude, longitude);
            values.put(StormingContract.LocationEntry.COLUMN_NAME_Latitude, latitude);
            Uri uri = activity.getContentResolver().insert(StormingContract.LocationEntry.CONTENT_URI, values);
            locationID = (int)ContentUris.parseId(uri);
        }

        return locationID;
    }

    private int ManageWeatherInfo(List<ContentValues> valuesList){
        ContentResolver resolver = activity.getContentResolver();
        int nCount = resolver.bulkInsert(StormingContract.WeatherInfoEntry.CONTENT_URI, valuesList.toArray(new ContentValues[valuesList.size()]));
        return nCount;
    }

    private String[] queryDBforDisplayWeather(Uri uri, int limitCount){
        activity.getContentResolver().query(StormingContract.WeatherInfoEntry.CONTENT_URI, null, null, null, "time DESC + LIMIT 7");
    }

    private ContentValues FromStringstoValue(String wind,
                                             String weatherDsc,
                                             String weather,
                                             String temperature_min,
                                             String temperature_max,
                                             Long time,
                                             String humidity,
                                             String pressure,
                                             int locationID){
        ContentValues values = new ContentValues();
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND, wind);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION, weatherDsc);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION, weather);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN, temperature_min);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME, time);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX, temperature_max);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_HUMIDITY, humidity);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_PRESSURE, pressure);
        values.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_LOCATION_ID, locationID);
        return values;
    }

    @Override
    protected Integer doInBackground(String... params) {
        Log.i(this.getClass().getSimpleName(), String.format("Location : %s, in unit: %s", params[0], params[1]));
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("forecast")
                    .appendPath("daily")
                    .appendQueryParameter("q", String.format("%s, cn", params[0]))
                    .appendQueryParameter("cnt", "7")
                    .appendQueryParameter("units", params[1])
                    .appendQueryParameter("appid", "c635c84e0de6b0a983ff2fe6ccff74e4");
            Log.i(logTag, String.format("City : %s", params[0]));
            //URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,cn&cnt=7&units=metric&appid=c635c84e0de6b0a983ff2fe6ccff74e4", params[0]));
            URL url = new URL(builder.build().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                System.out.print("Nothing returned from the weather API");
            }
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = bufferReader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
            responseJSON = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        ArrayList<String> rows = new ArrayList<>();
        ArrayList<ContentValues> valueList = new ArrayList<ContentValues>();

        try {
            JSONObject result = new JSONObject(responseJSON);
            Log.d(logTag, result.toString());

            String name = result.getJSONObject("city").getString("name");
            String country = result.getJSONObject("city").getString("country");
            String longitude = result.getJSONObject("city").getJSONObject("coord").getString("lon");
            String latitude = result.getJSONObject("city").getJSONObject("coord").getString("lat");
            int locationID = ManageLocation(name, latitude, longitude);
            JSONArray days = result.getJSONArray("list");
            for (int i = 0; i < days.length(); i++) {
                JSONObject day = days.getJSONObject(i);
                //date time txt :
                String time = parseDateTime(day.getLong("dt"));
                Long timeinSeconds = day.getLong("dt");
                JSONArray weatherArray = day.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String weather = (String)weatherObject.get("main");
                String weatherDsc = weatherObject.getString("description");
                String wind = day.getString("speed");
                String pressure = day.getString("pressure");
                String humidity = day.getString("humidity");
                String temperature_min = Double.toString(day.getJSONObject("temp").getDouble("min"));
                String temperature_max = Double.toString(day.getJSONObject("temp").getDouble("max"));

                String row = String.format("%s   %s - %s/%s", time, weather, temperature_max, temperature_min);
                rows.add(row);

                valueList.add(FromStringstoValue(wind, weatherDsc, weather, temperature_min, temperature_max, timeinSeconds, humidity, pressure ,locationID));
            }
        } catch (Exception JSONException) {
            Log.e(logTag, JSONException.getMessage());
        }
        return ManageWeatherInfo(valueList);
    }

    @Override
    protected void onPostExecute(Integer nCount) {
        forecastAdapter.clear();


        forecastAdapter.addAll();
    }
}
