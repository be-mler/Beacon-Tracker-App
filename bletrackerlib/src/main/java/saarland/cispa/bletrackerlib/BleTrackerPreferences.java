package saarland.cispa.bletrackerlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import saarland.cispa.bletrackerlib.remote.SendMode;

public class BleTrackerPreferences {
    private static final int CISAP_LOCATION_FRESHNESS_MINIMUM = 1000 * 10;
    private static final int CISAP_LOCATION_ACCURACY_MINIMUM = 50;

    //TODO: include all preferences
    private boolean sendToCispa = true;
    private int scanInterval = 1000;
    private int locationAccuracy = CISAP_LOCATION_ACCURACY_MINIMUM;
    private int locationFreshness = CISAP_LOCATION_FRESHNESS_MINIMUM;



    /**
     * do you want to send values to cispa?
     * default is true
     * @return true if yes and false if no
     */
    public boolean isSendToCispa() {
        return sendToCispa;
    }

    /**
     * do you want to send values to cispa?
     * default is true
     * @param sendToCispa true if yes false if no
     */
    public void setSendToCispa(boolean sendToCispa) {
        this.sendToCispa = sendToCispa;
    }

    /**
     * get the scan interval in ms
     * default is 1000ms
     * @return the scan interval
     */
    public int getScanInterval() {
        return scanInterval;
    }

    /**
     * set the scan interval in ms
     * default is 1000ms
     * @param scanInterval in ms
     */
    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    /**
     * get the location accuracy in m
     * default is 50m
     * @return the accuracy
     */
    public int getLocationAccuracy() {
        return locationAccuracy;
    }

    /**
     * set the location accuracy in m
     * default is 50m
     * @param locationAccuracy in m
     */
    public void setLocationAccuracy(int locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
        if (locationAccuracy > CISAP_LOCATION_ACCURACY_MINIMUM) {
            sendToCispa = false;
        }
    }

    /**
     * get the time in which a location is seen as fresh
     * default is 30,000ms (30s)
     * @return the time in ms
     */
    public int getLocationFreshness() {
        return locationFreshness;
    }

    /**
     * set the time in which a location is seen as fresh
     * default is 30,000ms (30s)
     * @param locationFreshness in ms
     */
    public void setLocationFreshness(int locationFreshness) {
        this.locationFreshness = locationFreshness;
        if (locationFreshness > CISAP_LOCATION_FRESHNESS_MINIMUM) {
            sendToCispa = false;
        }
    }

    public BleTrackerPreferences() {

    }

    public BleTrackerPreferences(boolean sendToCispa) {
        this.sendToCispa = sendToCispa;
    }

    public void load(Context context)
    {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        sendToCispa = prefManager.getBoolean("sendToCispa", true);

    }
    public void save(Context context)
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putBoolean("sendToCispa", sendToCispa);
        e.apply();
    }
}
