package saarland.cispa.bletrackerlib.remote;

import java.util.ArrayList;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public interface RemoteRequestReceiver {

    void onBeaconsReceived(ArrayList<SimpleBeacon> beacons);

    void onBeaconReceiveError(String errorMessage);
}
