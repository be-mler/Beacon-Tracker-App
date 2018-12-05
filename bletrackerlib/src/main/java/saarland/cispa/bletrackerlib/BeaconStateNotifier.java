package saarland.cispa.bletrackerlib;

import java.util.ArrayList;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public interface BeaconStateNotifier {
    /**
     * Callback which returns all nearby beacons
     * Can be used to display live updates inside the app
     * @param beacons the nearby beacons
     */
    void onUpdate(ArrayList<SimpleBeacon> beacons);

    /**
     * Callback which get's fired if there is a beacon found nearby
     * Can be used to show notifications etc.
     */
    void onBeaconNearby();

    /**
     * Callback if beacon is ready to send.
     * This get's fired if we have good location and best distance to the beacon
     * @param beacon the beacon
     */
    void onReadyToSend(SimpleBeacon beacon);
}
