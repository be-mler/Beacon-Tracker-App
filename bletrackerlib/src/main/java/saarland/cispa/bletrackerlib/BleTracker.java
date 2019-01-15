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
import saarland.cispa.bletrackerlib.exceptions.OtherServiceStillRunningException;
import saarland.cispa.bletrackerlib.remote.RemoteConnection;
import saarland.cispa.bletrackerlib.service.BleTrackerService;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;

public class BleTracker {

    private Context context;
    private BleTrackerService service;
    private final ArrayList<BeaconStateNotifier> notifiers = new ArrayList<>();
    private final RemoteConnection cispaConnection;

    /**
     * Creates the beacon service with the specified operation mode
     * @param context The application context
     */
    public BleTracker(Context context, boolean sendToCispa) {
        service = (BleTrackerService) context.getApplicationContext();
        if (sendToCispa) {
            cispaConnection = new RemoteConnection("http://192.168.122.21:5000/api/beacon", context, false);
        } else {
            cispaConnection = null;
        }
    }

    /**
     * Adds a notifier callback
     * @param notifier the callback
     */
    public void addNotifier(BeaconStateNotifier notifier) {
        notifiers.add(notifier);
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
        service.createBackgroundService(cispaConnection, notifiers);
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
        service.createForegroundService(cispaConnection, notifiers, foregroundNotification);
    }

    /**
     * Starts the service
     */
    public void start() {
        showDialogIfGpsIsOff();
        showDialogIfBluetoothIsOff();
        service.enableMonitoring();
    }

    /**
     * Stops the service
     */
    public void stop() {
        service.disableMonitoring();
    }

    /**
     * Indicates if the services is running
     * @return true if service is running
     */
    public boolean isRunning() {
        return service.isMonitoring();
    }

    /**
     * Open a dialog and explain the user to turn on GPS and Network location
     */
    private void showDialogIfGpsIsOff() {
        if(!isGpsOn()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(R.string.gps_network_not_enabled);
            dialog.setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent gpsOptionsIntet = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(gpsOptionsIntet);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    showAppFunctionalityLimitedWithout(R.string.functionality_limited_gps);
                }
            });
            dialog.show();
        }
    }

    /**
     * Open a dialog and explain the user to turn on Bluetooth
     */
    private void showDialogIfBluetoothIsOff() {
        final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba != null) {
            if (!isBluetoothOn()) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(R.string.bluetooth_not_enabled);
                dialog.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ba.enable();
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAppFunctionalityLimitedWithout(R.string.functionality_limited_bluetooth);
                    }
                });
                dialog.show();
            }
        }
    }

    private void showAppFunctionalityLimitedWithout(int message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * Check if bluetooth is on
     * @return return true if on
     */
    private boolean isBluetoothOn() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba != null) {
            return ba.isEnabled();
        }
        return false;
    }

    /**
     * Check if gps is on and location mode high accuracy
     * @return true if gps is on and location mode is high accuracy
     */
    private boolean isGpsOn() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        //TODO: What to do with devices which have no network provider (most devices without play services installed)
        //boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {

        }

//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {
//
//        }
        return gps_enabled; //&& network_enabled;
    }

    public void setContext(Activity activityContext) {
        this.context = activityContext;
        if (context != null) {
            service = (BleTrackerService) context.getApplicationContext();
        }
    }
}
