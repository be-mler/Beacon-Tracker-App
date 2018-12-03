package saarland.cispa.bletrackerlib;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

public class BeaconForegroundService implements BeaconConsumer {

    private final BeaconStateNotifier stateNotifier;
    private final Context context;
    private final BeaconManager beaconManager;

    /**
     * Service which only operates if app is running
     * @param context the app context
     * @param stateNotifier the stateNotifier callback
     */

    public BeaconForegroundService(Context context, BeaconStateNotifier stateNotifier) {
        this.context = context;
        this.stateNotifier = stateNotifier;

        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));

        beaconManager.bind(this);
    }

    /**
     * This get's called if service is up
     */
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifierImpl(stateNotifier));
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        beaconManager.unbind(this);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        beaconManager.bind(this);
        return true;
    }
}
