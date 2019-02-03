package saarland.cispa.trackblebeacons;

import android.content.Intent;
import android.net.Uri;
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
        Preference feedback = findPreference("feedback");

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

        feedback.setOnPreferenceClickListener(preference -> {
            composeEmail(new String[]{"ble-app@cispa.saarland"},"BLE Tracker Feedback");
            return true;
        });


        switch_sendToCispa.setChecked(bleTrackerPreferences.isSendToCispa());
        switch_showBeaconNotifications.setChecked(bleTrackerPreferences.isShowBeaconNotifications());


    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(intent);

    }


    //TODO: take a look at https://github.com/XinyueZ/preference-demo
    //TODO: take a look at https://developer.android.com/guide/topics/ui/settings/
    //TODO: Use BleTrackerPreferences to share preferences with lib 
}
