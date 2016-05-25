package app.vincenthu.citrix.com.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    ArrayAdapter<String> forcastAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView view = (ListView) this.findViewById(R.id.listview_forecast);

        new FetchWeatherTask().execute();
        String[] list = {
                "a",
                "b",
                "c",
                "d"
        };

        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(list));
       forcastAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                arrayList
        );
        view.setAdapter(forcastAdapter);
    }


    private class FetchWeatherTask extends AsyncTask<Void, Void, ArrayList<String>> {
        String logTag = this.getClass().getSimpleName();
        HttpURLConnection urlConnection = null;
        String responseJSON = null;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,us&cnt=7&units=metric&appid=c635c84e0de6b0a983ff2fe6ccff74e4");
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
                System.out.print(responseJSON);

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
                JSONArray days = result.getJSONArray("list");
                Log.d(logTag, days.toString());
                for (int i = 0; i < days.length(); i++) {
                    JSONObject day = days.getJSONObject(i);
                    //date time txt :
                    String time = day.getString("dt_txt").split(" ")[1];
                    JSONArray weatherArray = (JSONArray)day.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String weather = (String)weatherObject.get("main");
                    String degree = Double.toString(day.getJSONObject("main").getDouble("temp"));
                    String row = time + " - " + weather + "/" + degree + " C.";
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
