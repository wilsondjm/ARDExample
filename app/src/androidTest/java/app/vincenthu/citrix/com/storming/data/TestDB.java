package app.vincenthu.citrix.com.storming.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.data.StormingDBHelper;

/**
 * Created by Administrator on 6/3/2016.
 */
public class TestDB extends AndroidTestCase{
    public TestDB(){
        super();
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        mContext.deleteDatabase(StormingDBHelper.DATABASE_NAME);
    }

    public void testCreateDB() throws Throwable{
        SQLiteDatabase db = new StormingDBHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        assertEquals(false, db.isReadOnly());

        //check if any table has been created in sqlite
        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        Vector<String> tables = new Vector<String>();
        tables.add(StormingContract.LocationEntry.TABLE_NAME);
        tables.add(StormingContract.WeatherInfoEntry.TABLE_NAME);
        tables.add("android_metadata");
        do{
            tables.removeElement(c.getString(0));
        }while (c.moveToNext());
        assertTrue("This check if the TABLE has been created " + tables.size(),
                tables.isEmpty());
        c.moveToNext();

        //check if contains the Location table right columns.
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + StormingContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        Vector<String> columns = new Vector<String>();
        columns.add(StormingContract.LocationEntry._ID);
        columns.add(StormingContract.LocationEntry.COLUMN_NAME_Name);
        columns.add(StormingContract.LocationEntry.COLUMN_NAME_Latitude);
        columns.add(StormingContract.LocationEntry.COLUMN_NAME_Longitude);
        int column_index = c.getColumnIndex(StormingContract.LocationEntry.COLUMN_NAME_Name);
        do{
            columns.remove(c.getString(column_index));
        }while(c.moveToNext());
        assertEquals(true, columns.isEmpty());
        c.close();
        db.close();
    }

    public void testLocationTable(){
        SQLiteDatabase db = new StormingDBHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        assertEquals(false, db.isReadOnly());

        ContentValues testLocationTableValues = new ContentValues();
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Name, "Nanjing");
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Latitude, 32.06 );
        testLocationTableValues.put(StormingContract.LocationEntry.COLUMN_NAME_Longitude, 118.78);

        Long entry_ID = db.insert(StormingContract.LocationEntry.TABLE_NAME, null, testLocationTableValues);
        assertTrue(entry_ID != -1);

        Vector<String> insertedIDs = new Vector<String>();
        insertedIDs.add(entry_ID.toString());
        //query the inserted data
        Cursor c = db.query(StormingContract.LocationEntry.TABLE_NAME,
                null, //all columns
                null, // columns for where clause
                null, // values for where clause
                null, // grouby columns
                null, // having columns
                null); //sort order
        assertTrue("Error: No data has been found when tried to retrieve data from Location table",
                c.moveToFirst());

        do{
           String[] columns =  c.getColumnNames();
            Long id = c.getLong(0);
            insertedIDs.remove(id.toString());
        }while(c.moveToNext());

        assertTrue("Error, Not all the inserted entries have been detected",
                insertedIDs.isEmpty());

        c.close();
        db.close();
    }

    public void testWeatherTable(){

        //insert location entry first
        SQLiteDatabase db = new StormingDBHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        assertEquals(false, db.isReadOnly());

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
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME, 12334);
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION, "Cloudy");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION, "Thick Clouds");
        weatherValues.put(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND, "3.83");

        Long weatherID = db.insert(StormingContract.WeatherInfoEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Error, failed to insert the weather data into db",
                weatherID != -1);

        //Verfied the detailes:
        Cursor c = db.query(StormingContract.WeatherInfoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error, cannot load the inserted data",
                c.moveToFirst());

        Set<Map.Entry<String,Object>> expected_values = weatherValues.valueSet();
        for( Map.Entry<String, Object> entry : expected_values){
            int columnIndex = c.getColumnIndex(entry.getKey());
            assertTrue("Error, no column for " + entry.getKey(), columnIndex != -1);
            String valuefromDB = c.getString(columnIndex);
            String expected_value = entry.getValue().toString();
            assertEquals(String.format("Column %s with value %s matched", entry.getKey(),entry.getValue()), valuefromDB, expected_value);
        }
        c.close();
        db.close();
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
