package app.vincenthu.citrix.com.storming.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 6/2/2016.
 */
public class StormingDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Storming.db";

    public StormingDBHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQL_CREATE_ENTRIES);
        String createWeatherDB_Statement = String.format(StormingContract.WeatherInfoEntry_Create_Table, StormingContract.WeatherInfoEntry.TABLE_NAME);
        String createLocationDB_Statement = String.format(StormingContract.LocationEntry_Create_Table, StormingContract.LocationEntry.TABLE_NAME);

        db.execSQL(createLocationDB_Statement);
        db.execSQL(createWeatherDB_Statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StormingContract.WeatherInfoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StormingContract.LocationEntry.TABLE_NAME);
        onCreate(db);
    }
}
