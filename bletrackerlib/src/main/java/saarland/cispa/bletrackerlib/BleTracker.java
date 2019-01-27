package saarland.cispa.bletrackerlib;

import android.app.Activity;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import saarland.cispa.bletrackerlib.helper.BluetoothHelper;
import saarland.cispa.bletrackerlib.helper.LocationHelper;
import saarland.cispa.bletrackerlib.remote.RemoteSettings;
import saarland.cispa.bletrackerlib.exceptions.OtherServiceStillRunningException;
import saarland.cispa.bletrackerlib.remote.RemoteConnection;
import saarland.cispa.bletrackerlib.remote.SendMode;
import saarland.cispa.bletrackerlib.service.BleTrackerService;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;

public class BleTracker {

    private static BleTracker bleTracker;
    private BleTrackerService service;
    private final ArrayList<BeaconStateNotifier> beaconNotifiers = new ArrayList<>();
    private final ArrayList<ServiceStateNotifier> serviceNotifiers = new ArrayList<>();

    private RemoteConnection cispaConnection;

    public static BleTracker getInstance() {
        if (bleTracker == null) {
            bleTracker = new BleTracker();
        }
        return bleTracker;
    }

    /**
     * Creates the beacon service with the specified operation mode
     * @param activity The application activity
     */
    public void init(Activity activity, boolean sendToCispa) {
        updateActivity(activity);
        RemoteSettings.Init(activity);
        if (sendToCispa) {
            cispaConnection = new RemoteConnection("https://ble.faber.rocks/api/beacon",
                    service.getApplicationContext(), SendMode.DO_ONLY_SEND_IF_BEACONS_HAVE_GPS);
        } else {
            cispaConnection = new RemoteConnection("https://ble.faber.rocks/api/beacon",
                    service.getApplicationContext(), SendMode.DO_NOT_SEND_BEACONS);
        }

    }

    /**
     * Adds a beaconNotifier which get's called if there are beacons near
     * @param beaconNotifier the callback
     */
    public void addBeaconNotifier(BeaconStateNotifier beaconNotifier) {
        beaconNotifiers.add(beaconNotifier);
    }

    /**
     * Adds a serviceNotifier which get's called if the service state changes
     * @param serviceNotifier
     */
    public void addServiceNotifier(ServiceStateNotifier serviceNotifier) {
        serviceNotifiers.add(serviceNotifier);
    }

    /**
     * Add a custom RESTful API connection
     * @param connection a remote connection to a RESTful endpoint
     */
    public void addRemoteConnection(RemoteConnection connection) {
        service.addRemoteConnection(connection);
    }

    /**
     * Returns the connection to CISPA if sendToCispa=true in constructor
     * @return the connection to CISPA
     */
    public RemoteConnection getCispaConnection() {
        return cispaConnection;
    }

    /**
     * Creates a background service which operates in the background and gets called from time to time by the system
     * This causes low battery drain but also the refresh rate is low
     * @throws OtherServiceStillRunningException if an old service is still running. Stop old service first before creating a new one
     */
    public void createBackgroundService() throws OtherServiceStillRunningException {
        if (isRunning()) {
            throw new OtherServiceStillRunningException();
        }
        service.createBackgroundService(cispaConnection, beaconNotifiers);
    }

    /**
     * Creates a service which also operates in background but will never go asleep thus your app stays in foreground
     * This causes huge battery drain but also gives a very good refresh rate
     * @param foregroundNotification this is needed because we need to display a permanent notification if tracking should run as foreground service
     *                               You can use ForegroundNotification.parse() for this type of notification
     * @throws OtherServiceStillRunningException if an old service is still running. Stop old service first before creating a new one
     */
    public void createForegroundService(Notification foregroundNotification) throws OtherServiceStillRunningException {
        if (isRunning()) {
            throw new OtherServiceStillRunningException();
        }
        service.createForegroundService(cispaConnection, beaconNotifiers, foregroundNotification);
    }

    /**
     * Starts the service and asks user to turn on bluetooth and GPS
     * Service then will start even if bluetooth is turned off and will work after they are turned on later
     * @param activity The application activity. Here the message will be displayed to turn on location an bluetooth
     */
    public void start(Activity activity) {
        LocationHelper.showDialogIfGpsIsOff(activity);
        BluetoothHelper.showDialogIfBluetoothIsOff(activity);
        service.enableMonitoring();
        for (ServiceStateNotifier serviceNotifier : serviceNotifiers) {
            serviceNotifier.onStart();
        }
    }

    /**
     * Tries to start the service even if GPS and bluetooth is not turned on
     * This in normal case is not the best idea.
     * Service then will start do work if bluetooth is turned on and gps too
     */
    public void startWithoutChecks() {
        service.enableMonitoring();
        for (ServiceStateNotifier serviceNotifier : serviceNotifiers) {
            serviceNotifier.onStart();
        }
    }

    /**
     * Stops the service
     */
    public void stop() {

        service.disableMonitoring();
        for (ServiceStateNotifier serviceNotifier : serviceNotifiers) {
            serviceNotifier.onStop();
        }
    }

    /**
     * Indicates if the services is running
     * @return true if service is running
     */
    public boolean isRunning() {
        return (service != null) && service.isMonitoring();
    }

    /**
     * Updates the base Activity. Should be called in every Activity.onResume()
     * @param activity
     */
    public void updateActivity(Activity activity) {
        if (activity != null) {
            service = (BleTrackerService) activity.getApplicationContext();
        }
    }
}
