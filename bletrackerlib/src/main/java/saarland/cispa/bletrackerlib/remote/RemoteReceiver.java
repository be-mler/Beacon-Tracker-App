package saarland.cispa.bletrackerlib.remote;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public interface RemoteReceiver {

    void onBeaconReceive(SimpleBeacon[] beacons);

    void onBeaconReceiveError();
}
