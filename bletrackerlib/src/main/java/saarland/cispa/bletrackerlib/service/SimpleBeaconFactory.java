package saarland.cispa.bletrackerlib.service;

import org.altbeacon.beacon.Beacon;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.data.SimpleBeaconParser;
import saarland.cispa.bletrackerlib.exceptions.SimpleBeaconParseException;

public class SimpleBeaconFactory {

    private static final String TAG = "SimpleBeaconFactory";

    /**
     * Parses a Beacon from altbeacon lib to SimpleBeacon
     *
     * @param beacon the beacon from altbeacon lib
     * @return SimpleBeacon
     * @throws SimpleBeaconParseException if something went wrong while parsing
     */
    public static SimpleBeacon create(Beacon beacon) throws SimpleBeaconParseException {
        SimpleBeacon simpleBeacon = null;
        try {
            SimpleBeaconParser parser = SimpleBeaconParser.valueOf(beacon.getParserIdentifier());
            simpleBeacon = parser.parse(beacon);

        } catch (Exception e) {
            throw new SimpleBeaconParseException(e);
        }
        return simpleBeacon;
    }

    private static void addLocation(SimpleBeacon simpleBeacon) {
        //TODO: Set Location
    }

}
