package saarland.cispa.bletrackerlib.service;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import saarland.cispa.bletrackerlib.data.SimpleBeaconParser;

public class LayoutManager {

    public static void setAllLayouts(BeaconManager beaconManager) {
        beaconManager.getBeaconParsers().clear();
        for(SimpleBeaconParser layout : SimpleBeaconParser.values()) {
            addLayout(beaconManager, layout);
        }
    }

    public static void addLayout(BeaconManager beaconManager, SimpleBeaconParser layout) {
        beaconManager.getBeaconParsers().add(new BeaconParser(layout.getIdentifier()).setBeaconLayout(layout.getLayout()));
    }
}
