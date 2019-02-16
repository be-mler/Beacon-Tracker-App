package saarland.cispa.trackblebeacons.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.BleTrackerPreferences;
import saarland.cispa.trackblebeacons.Preferences;
import saarland.cispa.trackblebeacons.R;
import saarland.cispa.trackblebeacons.helpers.MapHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        BleTrackerPreferences bleTrackerPreferences = BleTracker.getPreferences();
        bleTrackerPreferences.load(this.getActivity());
        SwitchPreferenceCompat switch_sendToCispa = (SwitchPreferenceCompat)findPreference("switch_sendToCispa");
        SwitchPreferenceCompat switch_showBeaconNotifications = (SwitchPreferenceCompat)findPreference("switch_showBeaconNotifications");
        SwitchPreferenceCompat onlineMaps = (SwitchPreferenceCompat)findPreference("onlineMaps");
        Preference feedback = findPreference("feedback");
        Preference download = findPreference("download");

        switch_sendToCispa.setOnPreferenceClickListener(preference -> {
            bleTrackerPreferences.setSendToCispa(((SwitchPreferenceCompat)preference).isChecked());
            bleTrackerPreferences.save(this.getActivity());
            return true;
        });
        switch_showBeaconNotifications.setOnPreferenceClickListener(preference -> {
            Preferences.setShowBeaconNotifications(((SwitchPreferenceCompat)preference).isChecked());
            bleTrackerPreferences.save(this.getActivity());
            return true;
        });

        onlineMaps.setOnPreferenceClickListener(preference -> {
            MapHelper.getInstance().setMapOnline(((SwitchPreferenceCompat)preference).isChecked());
            return true;
        });
        feedback.setOnPreferenceClickListener(preference -> {
            composeEmail(new String[]{"ble-app@cispa.saarland"},"BLE Tracker Feedback");
            return true;
        });

        download.setOnPreferenceClickListener(preference -> {
            if( !MapHelper.getInstance().dlSurroundingMapArea(2000))
            {
                Toast.makeText(getActivity(), "Can't access current location", Toast.LENGTH_LONG).show();
            }
            return true;
        });


        switch_sendToCispa.setChecked(bleTrackerPreferences.isSendToCispa());
        switch_showBeaconNotifications.setChecked(Preferences.isShowBeaconNotifications());
        onlineMaps.setChecked(MapHelper.getInstance().isMapOnline());


    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(intent);

    }




    //take a look at https://github.com/XinyueZ/preference-demo
    //take a look at https://developer.android.com/guide/topics/ui/settings/
    //Use BleTrackerPreferences to share preferences with lib
}