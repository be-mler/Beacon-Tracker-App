package saarland.cispa.bletrackerlib;

import android.content.Context;

import saarland.cispa.bletrackerlib.types.OperationMode;

public class BLETrackerLib {

    BeaconBackgroundService beaconBackgroundService;
    BeaconForegroundService beaconForegroundService;

    /**
     * Creates the beacon service with the specified operation mode
     * @param context The application context
     * @param operationMode operation mode of the service
     * @param notifier Callback to get beacons
     */
    public BLETrackerLib(Context context, OperationMode operationMode, BeaconStateNotifier notifier) {
        switch (operationMode) {
            case RUN_IN_FOREGROUND: {
                beaconForegroundService = new BeaconForegroundService(context, notifier);
            }
            case RUN_IN_BACKGROUND: {
                beaconBackgroundService = new BeaconBackgroundService(context, notifier, false);
            }
            case RUN_IN_BACKGROUND_POWERSAVE: {
                beaconBackgroundService = new BeaconBackgroundService(context, notifier, true);
            }
        }
    }
}
