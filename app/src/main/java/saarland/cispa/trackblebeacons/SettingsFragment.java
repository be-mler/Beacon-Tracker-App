package saarland.cispa.trackblebeacons;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    //TODO: take a look at https://github.com/XinyueZ/preference-demo
    //TODO: take a look at https://developer.android.com/guide/topics/ui/settings/
    //TODO: Use BleTrackerPreferences to share preferences with lib 
}
