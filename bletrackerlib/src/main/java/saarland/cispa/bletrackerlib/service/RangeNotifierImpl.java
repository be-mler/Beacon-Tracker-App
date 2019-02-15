package saarland.cispa.bletrackerlib.service;

import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.exceptions.ParseException;
import saarland.cispa.bletrackerlib.parser.SimpleBeaconParser;
import saarland.cispa.bletrackerlib.remote.RemoteConnection;

public class RangeNotifierImpl implements RangeNotifier {

    private static final String TAG = "RangeNotifierImpl";
    private final ArrayList<BeaconStateNotifier> stateNotifiers;
    private final SimpleBeaconParser parser;
    private ArrayList<RemoteConnection> customConnections = new ArrayList<>();

    RangeNotifierImpl(Context context, ArrayList<BeaconStateNotifier> stateNotifiers) {
        this.stateNotifiers = stateNotifiers;
        parser = new SimpleBeaconParser(context);

    }

    void addRemoteConnection(RemoteConnection connection) {
        customConnections.add(connection);
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
                SimpleBeacon simpleBeacon = parser.parse(beacon);
                simpleBeacons.add(simpleBeacon);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        if (simpleBeacons.size() > 0) {
            sendAll(simpleBeacons);
            for (BeaconStateNotifier stateNotifier : stateNotifiers) {
                stateNotifier.onUpdate(simpleBeacons);
            }
        }
    }

    /**
     * Sends the list of simple beacon to cispa or custom connection if they are not null
     * @param simpleBeacons the simple beacons
     */
    private void sendAll(List<SimpleBeacon> simpleBeacons) {
        RemoteConnection cispaConnection = BleTracker.getInstance().getCispaConnection();
        if (cispaConnection != null) {
            cispaConnection.sendAllBeacons(simpleBeacons);
        }
        for (RemoteConnection customConnection : customConnections) {
            customConnection.sendAllBeacons(simpleBeacons);
        }
    }
}
