package saarland.cispa.trackblebeacons;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.BleTrackerPreferences;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        BleTrackerPreferences bleTrackerPreferences = BleTracker.getInstance().getBleTrackerPreferences();
        bleTrackerPreferences.LoadSettings(this.getActivity().getApplicationContext());
        SwitchPreferenceCompat switch_sendToCispa = (SwitchPreferenceCompat)findPreference("switch_sendToCispa");
        SwitchPreferenceCompat switch_showBeaconNotifications = (SwitchPreferenceCompat)findPreference("switch_showBeaconNotifications");

        switch_sendToCispa.setOnPreferenceClickListener(preference -> {
            bleTrackerPreferences.setSendToCispa(((SwitchPreferenceCompat)preference).isChecked());
            bleTrackerPreferences.SaveSettings(this.getActivity().getApplicationContext());
            return true;
        });
        switch_showBeaconNotifications.setOnPreferenceClickListener(preference -> {
            bleTrackerPreferences.setShowBeaconNotifications(((SwitchPreferenceCompat)preference).isChecked());
            bleTrackerPreferences.SaveSettings(this.getActivity().getApplicationContext());
            return true;
        });
        switch_sendToCispa.setChecked(bleTrackerPreferences.isSendToCispa());
        switch_showBeaconNotifications.setChecked(bleTrackerPreferences.isShowBeaconNotifications());


    }



    //TODO: take a look at https://github.com/XinyueZ/preference-demo
    //TODO: take a look at https://developer.android.com/guide/topics/ui/settings/
    //TODO: Use BleTrackerPreferences to share preferences with lib 
}
