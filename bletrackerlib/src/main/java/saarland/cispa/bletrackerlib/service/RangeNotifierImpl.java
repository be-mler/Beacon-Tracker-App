package saarland.cispa.bletrackerlib.service;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.exceptions.SimpleBeaconParseException;
import saarland.cispa.bletrackerlib.remote.RemoteConnection;

public class RangeNotifierImpl implements RangeNotifier {

    private static final String TAG = "RangeNotifierImpl";
    private final BeaconStateNotifier stateNotifier;
    private RemoteConnection customConnection = null;
    private RemoteConnection cispaConnection = null;

    RangeNotifierImpl(BeaconStateNotifier stateNotifier, RemoteConnection cispaConnecition) {
        this.stateNotifier = stateNotifier;
        this.cispaConnection = cispaConnecition;
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
                SimpleBeacon simpleBeacon = SimpleBeaconFactory.create(beacon);
                simpleBeacons.add(simpleBeacon);
            } catch (SimpleBeaconParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        stateNotifier.onUpdate(simpleBeacons);
    }
}
