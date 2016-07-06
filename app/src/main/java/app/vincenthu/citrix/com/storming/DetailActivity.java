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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;

import app.vincenthu.citrix.com.storming.data.StormingContract;
import app.vincenthu.citrix.com.storming.util.Utils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(app.vincenthu.citrix.com.storming.R.layout.activity_detail);
        getSupportFragmentManager().beginTransaction().add(app.vincenthu.citrix.com.storming.R.id.detail_activity, new fragment_detail()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    public static class fragment_detail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        private static final int DETAILACTIVITY_LOADER = 1;
        private ShareActionProvider mShareActionProvider = null;

        public fragment_detail(){
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
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(getClass().getSimpleName(), "Share Action Provider is null?");
            }
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
            String time = Utils.parseTimefromRealtoStringDate(timefDB, "EE M/dd");
            String Weather = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_CONDITION));
            String temperature_max = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MAX));
            String temperature_min = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_TEMPERATURE_MIN));
            String weather_desc = data.getString(data.getColumnIndex(StormingContract.WeatherInfoEntry.COLUMN_NAME_WEATHER_DESCRIPTION));

            TextView textView = (TextView)getView().findViewById(R.id.detailedText);
            textView.setText(String.format("%s %s\n%s:\n%s\n%s - %s", time, location, Weather, weather_desc, temperature_min, temperature_max));

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");

            TextView textView = (TextView)getView().findViewById(R.id.detailedText);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    textView.getText());
            return shareIntent;
        }
    }
}
