package app.vincenthu.citrix.com.storming;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.util.Utils;

/**
 * Created by Administrator on 7/19/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAILACTIVITY_LOADER = 1;
    private ShareActionProvider mShareActionProvider = null;

    //views
    private ImageView weatherImgView;
    private TextView dateView;
    private TextView dayView;
    private TextView maxDegreeView;
    private TextView minDegreeView;
    private TextView weatherDescView;
    private TextView humidityView;
    private TextView pressureView;
    private TextView windView;

    public DetailFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(app.vincenthu.citrix.com.storming.R.layout.fragment_detail, container, false);

        weatherImgView = (ImageView) rootView.findViewById(R.id.detail_item_weatherImg);
        dateView = (TextView) rootView.findViewById(R.id.detail_item_Date);
        dayView = (TextView) rootView.findViewById(R.id.detail_item_day);
        maxDegreeView = (TextView) rootView.findViewById(R.id.detail_item_maxDegree);
        minDegreeView = (TextView) rootView.findViewById(R.id.detail_item_minDegree);
        weatherDescView = (TextView) rootView.findViewById(R.id.detail_item_weather);
        humidityView = (TextView) rootView.findViewById(R.id.detail_item_humidity);
        pressureView = (TextView) rootView.findViewById(R.id.detail_item_pressure);
        windView = (TextView) rootView.findViewById(R.id.detail_item_wind);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(DETAILACTIVITY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == app.vincenthu.citrix.com.storming.R.id.action_settings){
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if(intent != null){
            Uri uri = intent.getData();
            return new CursorLoader(getActivity(), uri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()){
            return;
        }
        long timefDB = data.getLong(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME));
        String location = Utils.getPreferredLocation(getActivity());
        String Weather = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION));
        String weatherID = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_ID));
        String temperature_max = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX));
        String temperature_min = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN));
        String weather_desc = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION));
        String humidity = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_HUMIDITY));
        String wind = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND));
        String wind_direction = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WIND_DIRECTION));
        String pressure = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_PRESSURE));

        Utils.days day = Utils.getDayoftime(timefDB);
        if(day == Utils.days.Today || day == Utils.days.Tomorrow || day == Utils.days.Yesterday){
            dateView.setText(day.name());
        }else{
            dateView.setText(Utils.parseTimefromRealtoStringDate(timefDB, "EEEE"));
        }
        dayView.setText(Utils.parseTimefromRealtoStringDate(timefDB, "MMM dd"));
        maxDegreeView.setText(Utils.formatTemperature(getActivity(), Double.parseDouble(temperature_max)));
        minDegreeView.setText(Utils.formatTemperature(getActivity(), Double.parseDouble(temperature_min)));
        weatherImgView.setImageResource(Utils.getArtResourceForWeatherCondition(Integer.parseInt(weatherID)));
        humidityView.setText(getActivity().getString(R.string.format_humidity, Float.parseFloat(humidity)));
        windView.setText(getActivity().getString(R.string.format_wind, Float.parseFloat(wind), Utils.getWindDirection(Float.parseFloat(wind_direction))));
        pressureView.setText(getActivity().getString(R.string.format_pressure, Float.parseFloat(pressure)));


        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            String valueforShare = String.format("%s %s\n%s:\n%s\n%s - %s", Utils.parseTimefromRealtoStringDate(timefDB, "EE M/D"), location, Weather, weather_desc, temperature_min, temperature_max);
            mShareActionProvider.setShareIntent(createShareForecastIntent(valueforShare));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareForecastIntent(String value) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                value);
        return shareIntent;
    }
}