package app.vincenthu.citrix.com.storming;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.util.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class forcastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    ForecastAdapter forecastAdapter = null;
    private static int forececast_loader_id = 0;

    public forcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(app.vincenthu.citrix.com.storming.R.layout.fragment_forcast, container, false);
        ListView view = (ListView) rootview.findViewById(app.vincenthu.citrix.com.storming.R.id.listview_forecast);
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        view.setAdapter(forecastAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null){
                    String location = Utils.getPreferredLocation(getActivity());
                    Uri uri = StormingContract.WeatherInfoEntry.buildWeatherUriwithLocationandtime(location,
                            cursor.getLong(cursor.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME)));
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(uri);
                    startActivity(intent);
                }
            }
        });
        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(forececast_loader_id, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    // ---- custom functions
    private void updateWeather() {
        String location = Utils.getPreferredLocation(getActivity());
        String unit = Utils.getPreferredMetric(getActivity());
        new FetchWeatherTask(this.getActivity()).execute(location, unit);
    }

    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(forececast_loader_id, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String location = Utils.getPreferredLocation(getActivity());
        long time =Utils.getdaytotoday();
        return new CursorLoader(getActivity(), StormingContract.WeatherInfoEntry.buildWeatherUriwithLocationandStarttime(location, time/1000l),
                null, null, null, StormingContract.WeatherInfoEntry.COLUMN_NAME_TIME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        forecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        forecastAdapter.swapCursor(null);
    }
}
