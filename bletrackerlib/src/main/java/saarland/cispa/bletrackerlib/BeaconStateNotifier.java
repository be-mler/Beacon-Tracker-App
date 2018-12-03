package saarland.cispa.bletrackerlib;

import java.util.ArrayList;

import saarland.cispa.bletrackerlib.types.SimpleBeacon;

public interface BeaconStateNotifier {
    /**
     * Callback which returns all nearby beacons
     * Can be used to display live updates inside the app
     * @param beacons the nearby beacons
     */
    public void onUpdate(ArrayList<SimpleBeacon> beacons);

    /**
     * Callback which get's fired if there is a beacon found nearby
     * Can be used to show notifications etc.
     */
    public void onBeaconNearby();

    /**
     * Callback if beacon is ready to send.
     * This get's fired if we have good location and best distance to the beacon
     * @param beacon the beacon
     */
    public void onReadyToSend(SimpleBeacon beacon);
}
