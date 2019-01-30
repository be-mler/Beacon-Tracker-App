package saarland.cispa.trackblebeacons;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_settings, new SettingsFragment())
                .commit();
    }


    //TODO: take a look at https://github.com/XinyueZ/preference-demo
    //TODO: take a look at https://developer.android.com/guide/topics/ui/settings/
    //TODO: Use BleTrackerPreferences to share preferences with lib
}
