package app.vincenthu.citrix.com.storming.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 6/20/2016.
 */
public class TestProvider extends AndroidTestCase{

    public TestProvider(){
        super();
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testRegistry(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), WeatherProvider.class.getName());
        try{
            ProviderInfo info = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: not able to find the provider: %s with coded one: %s", info.authority, StormingContract.CONTENT_AUTHORITY);
        }catch(PackageManager.NameNotFoundException e){
            assertTrue(String.format("Error: WeatherProvider not registered at %s", mContext.getPackageName()), false);
        }
    }

    public void testGetType(){
        String type1 = getContext().getContentResolver().getType(StormingContract.WeatherInfoEntry.CONTENT_URI);
        assertEquals("Error： failed to load the type", type1, StormingContract.WeatherInfoEntry.CONTENT_TYPE);
        String type2 = getContext().getContentResolver().getType(StormingContract.WeatherInfoEntry.buildWeatherWithLocationUri("Nanjing"));
    }

    public void testBasicQuery(){

        //prepare data
        StormingDBHelper dbHelper = new StormingDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testLocationTableValues = new ContentValues();
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Name, "Nanjing");
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Latitude, 32.06 );
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Longitude, 118.78);

        Long entry_ID = db.insert(StormingContract.LocationEntry.TABLE_NAME, null, testLocationTableValues);
        assertTrue(entry_ID != -1);

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_HUMIDITY, "89");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_LOCATION_ID, entry_ID);
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_PRESSURE, "1018.17");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX, "23");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN, "17");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME, "123d");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION, "Cloudy");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION, "Thick Clouds");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND, "3.83");

        Long weatherID = db.insert(StormingContract.WeatherInfoEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Error, failed to insert the weather data into db",
                weatherID != -1);
        db.close();

        //run the test
        Cursor cursor = mContext.getContentResolver().query(
                StormingContract.WeatherInfoEntry.buildWeatherWithLocationUri("Nanjing"),
                null,
                null,
                null,
                null);

        assertTrue("Error: No data has been found when tried to retrieve data from Weather table",
                cursor.moveToFirst());
        Set<Map.Entry<String, Object>> valuesSet = weatherValues.valueSet();
        for(Map.Entry<String, Object> entry : valuesSet){
            String columnName = entry.getKey();
            int columnIndexinDBColumns = cursor.getColumnIndex(columnName);
            assertTrue("Error: Unable to find column " + columnName, columnIndexinDBColumns != -1);

            String value = cursor.getString(columnIndexinDBColumns);
            assertEquals(String.format("Error: the expected value is different from the value in db: %s - %s", value.toString(), entry.getValue().toString()),
                    value.toString(),
                    entry.getValue().toString());
        }
    }

    public void testInsert(){
        ContentValues locationvalues = TestUtility.CreateLocationValues();
        Uri uri = mContext.getContentResolver().insert(StormingContract.LocationEntry.CONTENT_URI, locationvalues);
        Long id = ContentUris.parseId(uri);
        assertTrue("Error when inserting location " + id, id == 1);
    }

    @Override
    protected  void tearDown() throws Exception{
        super.tearDown();
    }
}
