package saarland.cispa.bletrackerlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import saarland.cispa.bletrackerlib.remote.SendMode;

public class BleTrackerPreferences {

    //TODO: include all preferences
    private boolean sendToCispa = true;
    private boolean showBeaconNotifications = true;
    private int scanInterval = 1000;
    private int locationAccuracy = 50;
    private int locationFreshness = 1000 * 30;


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
     * Are notifications shown?
     * @return true if they are false if not
     */
    public boolean isShowBeaconNotifications() {
        return showBeaconNotifications;
    }

    /**
     * Do you want to show notifications
     * @param showBeaconNotifications true if yes, false if not
     */
    public void setShowBeaconNotifications(boolean showBeaconNotifications) {
        this.showBeaconNotifications = showBeaconNotifications;
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
    }

    public BleTrackerPreferences() {

    }

    public BleTrackerPreferences(boolean sendToCispa, boolean showBeaconNotifications) {
        this.sendToCispa = sendToCispa;
        this.showBeaconNotifications = showBeaconNotifications;
    }

    public void LoadSettings(Context context)
    {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        sendToCispa = prefManager.getBoolean("sendToCispa", true);
        showBeaconNotifications = prefManager.getBoolean("showBeaconNotifications", true);
        //minConfirmations = prefManager.getInt("minConfirmations", 1);

    }
    public void SaveSettings(Context context)
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putBoolean("sendToCispa", sendToCispa);
        e.putBoolean("showBeaconNotifications", showBeaconNotifications);
        //e.putInt("minConfirmations", minConfirmations);
        e.apply();
    }



}
