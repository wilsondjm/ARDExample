package app.vincenthu.citrix.com.storming;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class forcastFragment extends Fragment {

    ArrayAdapter<String> forcastAdapter = null;

    public forcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] list = {};

        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(list));
        forcastAdapter = new ArrayAdapter<String>(
                getActivity(),
                app.vincenthu.citrix.com.storming.R.layout.list_item_forecast,
                app.vincenthu.citrix.com.storming.R.id.list_item_forecast_textview,
                arrayList
        );

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(app.vincenthu.citrix.com.storming.R.layout.fragment_forcast, container, false);
        ListView view = (ListView) rootview.findViewById(app.vincenthu.citrix.com.storming.R.id.listview_forecast);
        view.setAdapter(forcastAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), clickedItem, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, clickedItem);
                startActivity(intent);

            }
        });
        return rootview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(app.vincenthu.citrix.com.storming.R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == app.vincenthu.citrix.com.storming.R.id.action_refresh){
            updateWeather();
            return true;
        }

        if (id == app.vincenthu.citrix.com.storming.R.id.action_settings){
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == app.vincenthu.citrix.com.storming.R.id.action_locatemap){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = preferences.getString(getString(app.vincenthu.citrix.com.storming.R.string.pref_general_location_key), getString(app.vincenthu.citrix.com.storming.R.string.pref_general_location_default));
            Uri geouri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geouri);
            if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                startActivity(intent);
            }else{
                Log.e(this.getClass().getSimpleName(), "Failed to resolve the map intent wit geo" + geouri.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // ---- custom functions
    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(app.vincenthu.citrix.com.storming.R.string.pref_general_location_key), getString(app.vincenthu.citrix.com.storming.R.string.pref_general_location_default));
        String unit = preferences.getString(getString(app.vincenthu.citrix.com.storming.R.string.pref_general_units_key), getString((app.vincenthu.citrix.com.storming.R.string.pref_general_degreeunits_default)));
        new FetchWeatherTask(this.getActivity(), forcastAdapter).execute(location, unit);
    }

}
