package saarland.cispa.bletrackerlib.service;

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
}
