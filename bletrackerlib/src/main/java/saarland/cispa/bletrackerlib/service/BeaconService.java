package saarland.cispa.bletrackerlib.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import saarland.cispa.bletrackerlib.remote.RemoteConnection;

public final class BeaconService implements BootstrapNotifier {

    private static final String TAG = "BeaconService";
    private final RemoteConnection cispaConnection;
    private BeaconManager beaconManager;
    private Context context;
    private final Region region = new Region("AllBeaconsRegion", null, null, null);
    private final RegionBootstrap regionBootstrap;
    private final BackgroundPowerSaver backgroundPowerSaver;
    private BeaconStateNotifier stateNotifier;
    private RangeNotifierImpl rangeNotifier;


    /**
     * Creates a service which operates in background
     * Do not change the order of the calls in this constructor without knowing what you are doing!
     * @param context app context
     * @param stateNotifier notification callback
     */
    public BeaconService(Context context, RemoteConnection cispaConnection, BeaconStateNotifier stateNotifier) {
        this.context = context;
        this.cispaConnection = cispaConnection;
        this.stateNotifier = stateNotifier;
        this.rangeNotifier = new RangeNotifierImpl(stateNotifier, cispaConnection);

        beaconManager = BeaconManager.getInstanceForApplication(context);

        backgroundPowerSaver = new BackgroundPowerSaver(context);

        beaconManager.addRangeNotifier(rangeNotifier);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));

        regionBootstrap = new RegionBootstrap(this, region);
    }

    /**
     * Creates a service which also operates in background but will never go asleep
     * Do not change the order of the calls in this constructor without knowing what you are doing!
     * @param context app context
     * @param stateNotifier notification callback
     * @param notificationIcon icon for permanent notification
     * @param notificationText text for permanent notification
     */
    public BeaconService(Context context, RemoteConnection cispaConnection, BeaconStateNotifier stateNotifier, int notificationIcon, String notificationText) {
        this.context = context;
        this.cispaConnection = cispaConnection;
        this.stateNotifier = stateNotifier;
        this.rangeNotifier = new RangeNotifierImpl(stateNotifier, cispaConnection);

        beaconManager = BeaconManager.getInstanceForApplication(context);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(notificationIcon);
        builder.setContentTitle(notificationText);
        Intent intent = new Intent(context, context.getApplicationContext().getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        beaconManager.enableForegroundServiceScanning(builder.build(), 0);
        beaconManager.setEnableScheduledScanJobs(false);

        backgroundPowerSaver = null;

        beaconManager.addRangeNotifier(rangeNotifier);

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
     * @param region the beacons to look for
     */

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG, "I just saw an beacon for the first time!");
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        stateNotifier.onBeaconNearby();
    }

    /**
     * This method is called if there all no beacons seen anymore
     * @param region the beacons to look for
     */

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "I no longer see an beacon");
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called if beacons switching state from seen/not seen
     * @param state the state
     * @param region the beacons to look for
     */
    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        // Don't care
    }


    public void setRemoteConnection(RemoteConnection connection) {
        rangeNotifier.setRemoteConnection(connection);
    }
}
