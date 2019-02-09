package saarland.cispa.trackblebeacons;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import saarland.cispa.trackblebeacons.helpers.MapHelper;

public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_settings, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapHelper.getInstance().setActivity(this);
    }
    //take a look at https://github.com/XinyueZ/preference-demo
    //take a look at https://developer.android.com/guide/topics/ui/settings/
    //Use BleTrackerPreferences to share preferences with lib
}
