package app.vincenthu.citrix.com.storming;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private final int TODAY_VIEW_TYPE = 0;
    private final int NONE_TODAY_VIEW_TYPE = 1;

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return TODAY_VIEW_TYPE;
        }
        return NONE_TODAY_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int itemView = R.layout.list_item_forecast;
        if (viewType == TODAY_VIEW_TYPE){
            itemView = R.layout.list_item_today;
        }

        View view =  LayoutInflater.from(context).inflate(itemView, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    //    ((TextView) view).setText(getContentFromCursor(cursor));
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String[] dataStrings = getContentFromCursor(cursor);
        viewHolder.dateView.setText(dataStrings[0]);
        viewHolder.weatherView.setText(dataStrings[1]);
        viewHolder.maxDegree.setText(Utils.formatTemperature(context, Double.parseDouble(dataStrings[2])));
        viewHolder.minDegree.setText(Utils.formatTemperature(context, Double.parseDouble(dataStrings[3])));

        int viewType = getItemViewType(cursor.getPosition());
        if(viewType == TODAY_VIEW_TYPE){
            viewHolder.weatherImg.setImageResource(Utils.getArtResourceForWeatherCondition(Integer.parseInt(dataStrings[4])));
        }else{
            viewHolder.weatherImg.setImageResource(Utils.getIconResourceForWeatherCondition(Integer.parseInt(dataStrings[4])));
        }
    }

    private String[] getContentFromCursor(Cursor cursor){
        long time = cursor.getLong(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME));
        String weather = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION));
        String temperature_max = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX));
        String temperature_min = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN));
        String weather_desc = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION));
        String weatherID = cursor.getString(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_ID));


        String date = Utils.parseTimefromRealtoStringDate(time, "EE M/dd");
        if (Utils.getDayoftime(time) != Utils.days.notinscope) {
            date = Utils.getDayoftime(time).name();
        }

        return new String[]{date, weather_desc, temperature_max, temperature_min, weatherID};
    }


    public static class ViewHolder {
        public final ImageView weatherImg;
        public final TextView dateView;
        public final TextView weatherView;
        public final TextView maxDegree;
        public final TextView minDegree;

        public ViewHolder(View view) {
            weatherImg = (ImageView) view.findViewById(R.id.list_item_weatherImg);
            dateView = (TextView) view.findViewById(R.id.list_item_date);
            weatherView = (TextView) view.findViewById(R.id.list_item_weather);
            maxDegree = (TextView) view.findViewById(R.id.list_item_maxDegree);
            minDegree = (TextView) view.findViewById(R.id.list_item_minDegree);
        }
    }
}
