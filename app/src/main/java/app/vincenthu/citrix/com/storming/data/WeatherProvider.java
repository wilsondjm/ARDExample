package app.vincenthu.citrix.com.storming.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Administrator on 6/16/2016.
 */
public class WeatherProvider extends ContentProvider {
    private static final UriMatcher uriMacher = buildUriMacher();

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
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return true;
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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        return null;
    }
}
