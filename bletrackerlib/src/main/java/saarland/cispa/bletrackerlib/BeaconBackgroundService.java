package saarland.cispa.bletrackerlib;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class BeaconBackgroundService implements BootstrapNotifier {

    protected static final String TAG = "BeaconBackgroundService";
    private BeaconManager beaconManager;
    private Context context;
    private final Region region = new Region("AllBeaconsRegion", null, null, null);
    private final RegionBootstrap regionBootstrap;
    private final BackgroundPowerSaver backgroundPowerSaver;
    private BeaconStateNotifier stateNotifier;
    private RangeNotifierImpl rangeNotifier;


    /**
     * Service which operates in background
     * @param context the app context
     * @param stateNotifier the stateNotifier callback
     * @param saveBattery save battery yes/no. reduces battery consumption but also has lower refresh rate in background then
     */
    public BeaconBackgroundService(Context context, BeaconStateNotifier stateNotifier, boolean saveBattery) {
        this.context = context;
        this.stateNotifier = stateNotifier;
        this.rangeNotifier = new RangeNotifierImpl(stateNotifier);

        if (saveBattery) {
            backgroundPowerSaver = new BackgroundPowerSaver(context);
        } else {
            backgroundPowerSaver = null;
        }

        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));

        regionBootstrap = new RegionBootstrap(this, region);
    }

    /**
     * Called by the BeaconManager to get the context of Service or Activity.
     * This method is implemented by Service or Activity.
     * You generally should not override it.
     * @return the application Context
     */
    @Override
    public Context getApplicationContext() {
        return context;
    }

    /**
     * This method is called if a beacon enters the region and send's onBeaconNearby callback to the app
     * This callback can be used to show a notification etc.
     * @param region
     */

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "I just saw an beacon for the first time!");
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(rangeNotifier);
        stateNotifier.onBeaconNearby();
    }

    /**
     * This method is called if there all no beacons seen anymore
     * @param region
     */

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "I no longer see an beacon");
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.removeRangeNotifier(rangeNotifier);

    }

    /**
     * This method is called if beacons switching state from seen/not seen
     * @param state
     * @param region
     */
    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        // Don't care
    }


}
