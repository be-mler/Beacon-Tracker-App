package saarland.cispa.bletrackerlib.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import org.altbeacon.beacon.Beacon;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.data.SimpleBeaconParser;
import saarland.cispa.bletrackerlib.exceptions.SimpleBeaconParseException;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class SimpleBeaconFactory {

    private static final String TAG = "SimpleBeaconFactory";
    private static final int LOCATION_FRESH_TIMESPAN = 1000 * 60; // in Milliseconds
    private static final int LOCATION_ACCURAY = 50; // in Meters
    private Context context;

    public SimpleBeaconFactory(Context context) {
        this.context = context;
    }

    /**
     * Parses a Beacon from altbeacon lib to SimpleBeacon
     *
     * @param beacon the beacon from altbeacon lib
     * @return SimpleBeacon
     * @throws SimpleBeaconParseException if something went wrong while parsing
     */
    public SimpleBeacon create(Beacon beacon) throws SimpleBeaconParseException {
        SimpleBeacon simpleBeacon = null;
        try {
            SimpleBeaconParser parser = SimpleBeaconParser.valueOf(beacon.getParserIdentifier());
            simpleBeacon = parser.parse(beacon);
            addLocationIfPossible(simpleBeacon);
        } catch (Exception e) {
            throw new SimpleBeaconParseException(e);
        }
        return simpleBeacon;
    }

    private void addLocationIfPossible(SimpleBeacon simpleBeacon) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (isLocationFresh(location) && isLocationAccurate(location)) {
                simpleBeacon.setLocation(location.getLongitude(), location.getLatitude());
            }
        }
    }

    private boolean isLocationFresh(Location location) {
        long gpsTime = location.getTime();
        long systemTime = System.currentTimeMillis();
        return systemTime - gpsTime <= LOCATION_FRESH_TIMESPAN;
    }

    private boolean isLocationAccurate(Location location) {
        float accuracy = location.getAccuracy();
        return accuracy > 0 && accuracy <= LOCATION_ACCURAY;
    }

}
