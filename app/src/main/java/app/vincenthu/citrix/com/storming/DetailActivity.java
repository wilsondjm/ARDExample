package app.vincenthu.citrix.com.storming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.vincenthu.citrix.com.storming.util.Utils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(app.vincenthu.citrix.com.storming.R.layout.activity_detail);
        getSupportFragmentManager().beginTransaction().add(app.vincenthu.citrix.com.storming.R.id.detail_activity, new fragment_detail()).commit();
    }

    public static class fragment_detail extends Fragment{
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
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(app.vincenthu.citrix.com.storming.R.layout.fragment_detail, container, false);

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String message = intent.getStringExtra(Intent.EXTRA_TEXT);
                long time = Long.parseLong(message);
                String time_text = Utils.parseTimefromRealtoStringDate(time, "EE M/dd");
                TextView textView = (TextView) rootView.findViewById(app.vincenthu.citrix.com.storming.R.id.detailedText);
                textView.setText(time_text);
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(app.vincenthu.citrix.com.storming.R.menu.menu_detail, menu);
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
    }
}
