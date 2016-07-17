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
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    //    ((TextView) view).setText(getContentFromCursor(cursor));
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date);
        TextView weatherView = (TextView) view.findViewById(R.id.list_item_weather);
        TextView maxTemp = (TextView) view.findViewById(R.id.list_item_maxDegree);
        TextView minTemp = (TextView) view.findViewById(R.id.list_item_minDegree);

        String[] dataStrings = getContentFromCursor(cursor);
        dateView.setText(dataStrings[0]);
        weatherView.setText(dataStrings[1]);
        maxTemp.setText(dataStrings[2]);
        minTemp.setText(dataStrings[3]);
    }

    private String[] getContentFromCursor(Cursor cursor){
        long time = cursor.getLong(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME));
        String weather = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION));
        String temperature_max = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX));
        String temperature_min = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN));
        String weather_desc = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION));


        String date = Utils.parseTimefromRealtoStringDate(time, "EE M/dd");
        if (Utils.getDayoftime(time) != Utils.days.notinscope) {
            date = Utils.getDayoftime(time).name();
        }

        return new String[]{date, weather, temperature_max, temperature_min};
    }
}
