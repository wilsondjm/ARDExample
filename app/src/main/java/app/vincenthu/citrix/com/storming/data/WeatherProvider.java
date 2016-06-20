package app.vincenthu.citrix.com.storming.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
                return null;
            case WEATHER_WITH_LOCATION:
                cursor =  getWeatherbyLocation(uri, projection, sortOrder);
                break;
            case LOCATION:
                return null;
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
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
    }

    @Override
    public Uri insert(
            Uri uri, ContentValues values) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return null;
    }

    @Override
    public int delete(
            Uri uri, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
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
}