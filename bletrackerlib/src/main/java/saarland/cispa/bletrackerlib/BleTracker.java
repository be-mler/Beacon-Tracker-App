package saarland.cispa.bletrackerlib;

import android.content.Context;

import saarland.cispa.bletrackerlib.remote.RemoteConnection;
import saarland.cispa.bletrackerlib.service.BeaconService;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;

public class BleTracker {

    private static boolean serviceExists = false;
    private final Context context;
    private BeaconService service;
    private final BeaconStateNotifier notifier;
    private final RemoteConnection cispaConnection;

    /**
     * Creates the beacon service with the specified operation mode
     * @param context The application context
     * @param notifier Callback to get beacons
     */
    public BleTracker(Context context, boolean sendToCispa, BeaconStateNotifier notifier) {
        this.context = context;
        this.notifier = notifier;
        if (sendToCispa) {
            cispaConnection = new RemoteConnection("http://192.168.122.21:5000/api/beacon", context);
        } else {
            cispaConnection = null;
        }
    }

    /**
     * Set a custom RESTful API connection
     * @param connection
     */
    public void setRemoteConnection(RemoteConnection connection) {
        service.setRemoteConnection(connection);
    }

    /**
     * Returns the connection to CISPA if sendToCispa=true in constructor
     * @return the connection to CISPA
     */
    public RemoteConnection getCispaConnection() {
        return cispaConnection;
    }

    /**
     * Creates a background service which
     * This causes low battery drain but also the refresh rate is low
     * @throws ServiceAlreadyExistsException if you try to create more than one ble service
     */
    public void startBackgroundService() throws ServiceAlreadyExistsException {
        if (!serviceExists) {
            service = new BeaconService(context, cispaConnection, notifier);
            serviceExists = true;
        } else {
            throw new ServiceAlreadyExistsException();
        }
    }

    /**
     * Creates a service which also operates in background but will never go asleep thus your app stays in foreground
     * This causes huge battery drain but also gives a very good refresh rate
     * @param notificationIcon is needed to provide a permanent notification
     * @param notificationText is needed to provide a permanent notification
     * @throws ServiceAlreadyExistsException if you try to create more than one ble service
     */
    public void startForegroundService(int notificationIcon, String notificationText) throws ServiceAlreadyExistsException {
        if (!serviceExists) {
            service = new BeaconService(context, cispaConnection, notifier, notificationIcon, notificationText);
            serviceExists = true;
        } else {
            throw new ServiceAlreadyExistsException();
        }
    }
}
