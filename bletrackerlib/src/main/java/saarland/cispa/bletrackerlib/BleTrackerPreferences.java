package saarland.cispa.bletrackerlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import saarland.cispa.bletrackerlib.remote.SendMode;

public class BleTrackerPreferences {


    //TODO: include all preferences
    private boolean sendToCispa = true;
    private boolean showBeaconNotifications = true;
    private int minConfirmations = 1;

    public SendMode getSendMode() {
        return sendMode;
    }

    public void setSendMode(SendMode sendMode) {
        this.sendMode = sendMode;
    }

    private SendMode sendMode = SendMode.DO_ONLY_SEND_IF_BEACONS_HAVE_GPS;

    public boolean isSendToCispa() {
        return sendToCispa;
    }

    public void setSendToCispa(boolean sendToCispa) {
        this.sendToCispa = sendToCispa;
    }

    public boolean isShowBeaconNotifications() {
        return showBeaconNotifications;
    }

    public void setShowBeaconNotifications(boolean showBeaconNotifications) {
        this.showBeaconNotifications = showBeaconNotifications;
    }

    public int getMinConfirmations() {
        return minConfirmations;
    }

    public void setMinConfirmations(int minConfirmations) {
        this.minConfirmations = minConfirmations;
    }



    public BleTrackerPreferences() {

    }

    public BleTrackerPreferences(boolean sendToCispa, boolean showBeaconNotifications, int minConfirmations, SendMode sendMode) {
        this.sendToCispa = sendToCispa;
        this.showBeaconNotifications = showBeaconNotifications;
        this.minConfirmations = minConfirmations;
        this.sendMode = sendMode;
    }

    public void LoadSettings(Context context)
    {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        sendToCispa = prefManager.getBoolean("sendToCispa", true);
        showBeaconNotifications = prefManager.getBoolean("showBeaconNotifications", true);
        minConfirmations = prefManager.getInt("minConfirmations", 1);

    }
    public void SaveSettings(Context context)
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putBoolean("sendToCispa", sendToCispa);
        e.putBoolean("showBeaconNotifications", showBeaconNotifications);
        e.putInt("minConfirmations", minConfirmations);
        e.apply();

    }



}
