package app.vincenthu.citrix.com.storming.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Administrator on 6/16/2016.
 */
public class WeatherProvider extends ContentProvider {
    private static final UriMatcher uriMacher = buildUriMacher();
    private StormingDBHelper dbHelper;
    private static final SQLiteQueryBuilder view_weatherbylocationquerybuilder;

    static{
        view_weatherbylocationquerybuilder = new SQLiteQueryBuilder();
        view_weatherbylocationquerybuilder.setTables(
                String.format("%s INNER JOIN %s ON %s.%s = %s.%s",
                        StormingContract.WeatherInfoEntry.TABLE_NAME,
                        StormingContract.LocationEntry.TABLE_NAME,
                        StormingContract.WeatherInfoEntry.TABLE_NAME,
                        StormingContract.WeatherInfoEntry.COLUMN_NAME_LOCATION_ID,
                        StormingContract.LocationEntry.TABLE_NAME,
                        StormingContract.LocationEntry._ID)
        );
    }

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int LOCATION = 200;

    static UriMatcher buildUriMacher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(StormingContract.CONTENT_AUTHORITY, StormingContract.PATH_WEATHER, WEATHER);
        matcher.addURI(StormingContract.CONTENT_AUTHORITY, StormingContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(StormingContract.CONTENT_AUTHORITY, StormingContract.PATH_LOCATION, LOCATION);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new StormingDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (uriMacher.match(uri)){
            case WEATHER:
                cursor =  getWeather(uri, projection, sortOrder);
                break;
            case WEATHER_WITH_LOCATION:
                cursor =  getWeatherbyLocation(uri, projection, sortOrder);
                break;
            case LOCATION:
                cursor = getLocation(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMacher.match(uri);

        switch (match){
            case WEATHER:
                return StormingContract.WeatherInfoEntry.CONTENT_TYPE;
            case LOCATION:
                return StormingContract.LocationEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return StormingContract.WeatherInfoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int affectedRows = 0;
        int match = uriMacher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match){
            case WEATHER:
                affectedRows = db.update(StormingContract.WeatherInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LOCATION:
                affectedRows = db.update(StormingContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMacher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long _id = -1;
        Uri result = null;
        switch (match){
            case WEATHER:
                _id = db.insert(StormingContract.WeatherInfoEntry.TABLE_NAME, null, values);
                if( _id < 0 ){
                    throw new SQLException("Unable to insert in to Weather table");
                }
                result = StormingContract.WeatherInfoEntry.buildWeatherUri(_id);
            case LOCATION:
                _id = db.insert(StormingContract.LocationEntry.TABLE_NAME, null, values);
                if( _id < 0 ){
                    throw new SQLException("Unable to insert in to Location table");
                }
                result = StormingContract.WeatherInfoEntry.buildWeatherUri(_id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesList){
        final int match = uriMacher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long _id = -1;
        int nCount = 0;
        switch (match) {
            case WEATHER:
                try {
                    db.beginTransaction();
                    for (ContentValues values : valuesList) {
                        _id = db.insert(StormingContract.WeatherInfoEntry.TABLE_NAME, null, values);
                        if (_id < 0) {
                            throw new SQLException(String.format("Unable to insert with data count : %d", _id));
                        }
                        nCount++;
                    }
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case LOCATION:
                try {
                    db.beginTransaction();
                    for (ContentValues values : valuesList) {
                        _id = db.insert(StormingContract.LocationEntry.TABLE_NAME, null, values);
                        if (_id < 0) {
                            throw new SQLException(String.format("Unable to insert with data count : %d", _id));
                        }
                        nCount++;
                    }
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                return super.bulkInsert(uri, valuesList);
        }
        return nCount;
    }

    @Override
    public int delete(
            Uri uri, String selection, String[] selectionArgs) {
        int affectedRows = 0;
        final int match = uriMacher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch(match){
            case WEATHER:
                affectedRows = db.delete(StormingContract.WeatherInfoEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case LOCATION:
                affectedRows = db.delete(StormingContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("no delein in this uri:" + uri);
        }
        if (affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    private Cursor getWeatherbyLocation(Uri uri, String[] projection, String sortOrder){
        String location = StormingContract.WeatherInfoEntry.getLocationSegement(uri);

        StringBuilder selection = new StringBuilder("");
        selection.append(StormingContract.LocationEntry.TABLE_NAME).append(".").append(StormingContract.LocationEntry.COLUMN_NAME_Name).append(" = ?");
        String []selectionArgs = new String[]{location};


        Cursor cursor = view_weatherbylocationquerybuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection.toString(),
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    private Cursor getWeather(Uri uri, String[] projection, String sortOrder){
        Cursor cursor = dbHelper.getReadableDatabase().query(StormingContract.WeatherInfoEntry.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    private Cursor getLocation(Uri uri, String[] projection, String sortOrder){
        Cursor cursor = dbHelper.getReadableDatabase().query(StormingContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }
}
