package app.vincenthu.citrix.com.storming;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private String FRAGMENT_TAG = "Forecast";
    private String mLocation = "";
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_general_location_key),
                getString(R.string.pref_general_location_default));
        setContentView(app.vincenthu.citrix.com.storming.R.layout.activity_main);
        if (findViewById(R.id.detail_forcast_container) != null){
            isTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_forcast_container, new forcastFragment(), FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        boolean equals = mLocation.equalsIgnoreCase(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_general_location_key),
                getString(R.string.pref_general_location_default)));
        if(!equals){
            forcastFragment fragment = (forcastFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            fragment.onLocationChanged();
            mLocation = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_general_location_key),
                    getString(R.string.pref_general_location_default));
        }

    }


}
