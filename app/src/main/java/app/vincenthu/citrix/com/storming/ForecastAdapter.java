package app.vincenthu.citrix.com.storming;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.util.Utils;

/**
 * Created by Administrator on 7/4/2016.
 */
public class ForecastAdapter extends CursorAdapter{

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView)view;

    }

    private String getContentFromCursor(Cursor cursor){
        long time = cursor.getLong(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME));
        String weather = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION));
        String temperature_max = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX));
        String temperature_min = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN));
        String weather_desc = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION));
        String date = Utils.parseTimefromRealtoStringDate(time, "EE M/dd");

        return String.format("%s   %s/%s - %s/%s", date, weather, weather_desc, temperature_max, temperature_min);
    }
}
