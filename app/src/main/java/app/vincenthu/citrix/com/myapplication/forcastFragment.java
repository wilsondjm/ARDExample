package app.vincenthu.citrix.com.myapplication;


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

    // ---- custom functions
    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_general_location_key), getString(R.string.pref_general_location_default));
        String unit = preferences.getString(getString(R.string.pref_general_units_key), getString((R.string.pref_general_degreeunits_default)));
        new FetchWeatherTask().execute(location, unit);
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
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                arrayList
        );

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_forcast, container, false);
        ListView view = (ListView) rootview.findViewById(R.id.listview_forecast);
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
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == R.id.action_refresh){
            updateWeather();
            return true;
        }

        if (id == R.id.action_settings){
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_locatemap){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = preferences.getString(getString(R.string.pref_general_location_key), getString(R.string.pref_general_location_default));
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

    private class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {
        String logTag = this.getClass().getSimpleName();
        HttpURLConnection urlConnection = null;
        String responseJSON = null;

        private String parseDateTime(long seconds){
            long milliseconds = seconds * 1000;
            Date dateData = new Date(milliseconds);
            SimpleDateFormat desiredFormat = new SimpleDateFormat("EE MM/dd");
            String date = desiredFormat.format(dateData);
            return date;
        }

        private Uri parseGeoUri(double lat, double lon){
            Uri resultUrl = Uri.parse(String.format("geo:%f,%f", lat, lon));
            return resultUrl;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.i(this.getClass().getSimpleName(), String.format("Location : %s, in unit: %s", params[0], params[1]));
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", String.format("%s, cn", params[0]))
                        .appendQueryParameter("cnt", "7")
                        .appendQueryParameter("units", params[1])
                        .appendQueryParameter("appid", "c635c84e0de6b0a983ff2fe6ccff74e4");
                Log.i(logTag, String.format("City : %s", params[0]));
                //URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,cn&cnt=7&units=metric&appid=c635c84e0de6b0a983ff2fe6ccff74e4", params[0]));
                URL url = new URL(builder.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                 if (inputStream == null) {
                     System.out.print("Nothing returned from the weather API");
                 }
                 BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
                 StringBuffer buffer = new StringBuffer();
                 String line;
                 while ((line = bufferReader.readLine()) != null) {
                     buffer.append(line);
                     buffer.append('\n');
                 }
                 responseJSON = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            ArrayList<String> rows = new ArrayList<String>();

            try {
                JSONObject result = new JSONObject(responseJSON);
                Log.d(logTag, result.toString());
                JSONArray days = result.getJSONArray("list");

                for (int i = 0; i < days.length(); i++) {
                    JSONObject day = days.getJSONObject(i);
                    //date time txt :
                    String time = parseDateTime(day.getLong("dt"));
                    JSONArray weatherArray = (JSONArray)day.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String weather = (String)weatherObject.get("main");
                    String degree = Double.toString(day.getJSONObject("temp").getDouble("day"));
                    String row = String.format("%s   %s - %s", time, weather, degree);
                    rows.add(row);
                }
            } catch (Exception JSONException) {
                Log.e(logTag, JSONException.getMessage());
            }

            return rows;
        }
        @Override
        protected void onPostExecute(ArrayList<String> result) {
             forcastAdapter.clear();
             forcastAdapter.addAll(result);
        }
    }

}
