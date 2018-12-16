package saarland.cispa.bletrackerlib.service;

import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.exceptions.SimpleBeaconParseException;
import saarland.cispa.bletrackerlib.remote.RemoteConnection;

public class RangeNotifierImpl implements RangeNotifier {

    private static final String TAG = "RangeNotifierImpl";
    private final BeaconStateNotifier stateNotifier;
    private final Context context;
    private final SimpleBeaconFactory sbf;
    private RemoteConnection customConnection = null;
    private RemoteConnection cispaConnection = null;

    RangeNotifierImpl(Context context, BeaconStateNotifier stateNotifier, RemoteConnection cispaConnecition) {
        this.context = context;
        this.stateNotifier = stateNotifier;
        this.cispaConnection = cispaConnecition;
        sbf = new SimpleBeaconFactory(context);

    }

    void setRemoteConnection(RemoteConnection connection) {
        this.customConnection = connection;
    }

    /**
     * This method is called once per second if we set startRangingBeaconsInRegion and beacons are near
     * @param beacons all beacons which were found nearby
     * @param region the specific region
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        ArrayList<SimpleBeacon> simpleBeacons = new ArrayList<>();
        for (Beacon beacon: beacons) {
            try {
                SimpleBeacon simpleBeacon = sbf.create(beacon);
                simpleBeacons.add(simpleBeacon);
            } catch (SimpleBeaconParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        sendAll(simpleBeacons);
        stateNotifier.onUpdate(simpleBeacons);
    }

    /**
     * Sends the list of simple beacon to cispa or custom connection if they are not null
     * @param simpleBeacons the simple beacons
     */
    private void sendAll(List<SimpleBeacon> simpleBeacons) {
        if (cispaConnection != null) {
            cispaConnection.sendAll(simpleBeacons);
        }
        if (customConnection != null) {
            customConnection.sendAll(simpleBeacons);
        }
    }
}
