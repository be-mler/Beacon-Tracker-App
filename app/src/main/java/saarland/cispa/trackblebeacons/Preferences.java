package saarland.cispa.trackblebeacons;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Here the app preferences get stored
 */

public class Preferences {

    private static boolean showBeaconNotifications = true;

    public static boolean isShowBeaconNotifications() {
        return showBeaconNotifications;
    }

    public static void setShowBeaconNotifications(boolean showBeaconNotifications) {
        Preferences.showBeaconNotifications = showBeaconNotifications;
    }

    public static void load(Context context)
    {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        showBeaconNotifications = prefManager.getBoolean("showBeaconNotifications", true);
        //minConfirmations = prefManager.getInt("minConfirmations", 1);

    }
    public static void save(Context context)
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putBoolean("showBeaconNotifications", showBeaconNotifications);
        //e.putInt("minConfirmations", minConfirmations);
        e.apply();
    }
}
