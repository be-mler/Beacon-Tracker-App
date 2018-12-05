package saarland.cispa.bletrackerlib;

import android.content.Context;

public class BleTrackerLib {

    private static boolean serviceExists = false;
    private final Context context;
    private final BeaconStateNotifier notifier;

    /**
     * Creates the beacon service with the specified operation mode
     * @param context The application context
     * @param notifier Callback to get beacons
     */


    public BleTrackerLib(Context context, BeaconStateNotifier notifier) {
        this.context = context;
        this.notifier = notifier;
    }

    /**
     * Creates a background service which
     * This causes low battery drain but also the refresh rate is low
     * @throws ServiceAlreadyExistsException if you try to create more than one ble service
     */
    public void startBackgroundService() throws ServiceAlreadyExistsException {
        if (!serviceExists) {
            new BeaconService(context, notifier);
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
            new BeaconService(context, notifier, notificationIcon, notificationText);
            serviceExists = true;
        } else {
            throw new ServiceAlreadyExistsException();
        }
    }
}
