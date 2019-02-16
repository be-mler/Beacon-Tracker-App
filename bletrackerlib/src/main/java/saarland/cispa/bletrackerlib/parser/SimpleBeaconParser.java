package saarland.cispa.bletrackerlib.parser;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.BleTrackerPreferences;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts;
import saarland.cispa.bletrackerlib.exceptions.ParseException;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class SimpleBeaconParser implements LocationListener {

    private static final String TAG = "SimpleBeaconLayouts";

    private BleTrackerPreferences preferences = BleTracker.getPreferences();
    private Context context;

    public SimpleBeaconParser(Context context) {
        this.context = context;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, preferences.getLocationFreshness(), -1, this);
        }
    }

    /**
     * Parses a Beacon from altbeacon lib to SimpleBeacon
     *
     * @param beacon the beacon from altbeacon lib
     * @return SimpleBeacon
     * @throws ParseException if something went wrong while parsing
     */
    public SimpleBeacon parse(Beacon beacon) throws ParseException {
        try {
            SimpleBeacon simpleBeacon = new SimpleBeacon("", beacon.getRssi(), beacon.getTxPower(),
                    beacon.getManufacturer(), beacon.getBluetoothAddress(), beacon.getBluetoothName(),
                    beacon.getDistance(), DateParser.getUTCdatetimeAsString());
            simpleBeacon.hashcode = beacon.hashCode();
            if (beacon.getServiceUuid() == 0xfeaa) {    //Eddystone Format

                if (beacon.getExtraDataFields().size() >= 5) {
                    long telemetryVersion = beacon.getExtraDataFields().get(0);
                    long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                    long temperature = beacon.getExtraDataFields().get(2);
                    long pduCount = beacon.getExtraDataFields().get(3);
                    long uptime = beacon.getExtraDataFields().get(4);

                    simpleBeacon.telemetry = new SimpleBeacon.Telemetry(telemetryVersion, batteryMilliVolts,
                            getTemperatureFromTlmField(temperature), pduCount, uptime);
                }
                switch (beacon.getBeaconTypeCode()) {
                    case 0x00: {
                        simpleBeacon.beaconType = SimpleBeaconLayouts.EDDYSTONE_UID_LAYOUT.name();
                        simpleBeacon.eddystoneUidData = new SimpleBeacon.EddystoneUID(beacon.getId1().toString(),
                                beacon.getId2().toString());
                        break;
                    }
                    case 0x10: {
                        simpleBeacon.beaconType = SimpleBeaconLayouts.EDDYSTONE_URL_LAYOUT.name();
                        String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                        simpleBeacon.eddystoneUrlData = new SimpleBeacon.EddystoneURL(url);

                        if (url.startsWith("https://ruu.vi/#")) {
                            simpleBeacon.beaconType = SimpleBeaconLayouts.RUUVI_LAYOUT.name();
                            String hash = url.split("#")[1];
                            RuuviParser ruuviParser = new RuuviParser(hash);
                            simpleBeacon.ruuvi = new SimpleBeacon.Ruuvi(ruuviParser.getHumidity(),
                                    ruuviParser.getAirPressure(), ruuviParser.getTemp());
                        }
                        break;
                    }
                    default:
                        break;
                }
            } else {
                if (beacon.getBeaconTypeCode() == 0xbeac) {
                    simpleBeacon.beaconType = SimpleBeaconLayouts.ALTBEACON_LAYOUT.name();
                } else {
                    simpleBeacon.beaconType = SimpleBeaconLayouts.IBEACON_LAYOUT.name();
                }
                simpleBeacon.altbeaconIBeaconData = new SimpleBeacon.AltbeaconIBeaconData(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
            }

            addLocationIfPossible(simpleBeacon);
            return simpleBeacon;

        } catch (Exception e) {
            throw new ParseException(e.getCause());
        }
    }

    private double getTemperatureFromTlmField(long temperature) {
        long unsignedTemp = (temperature >> 8);
        return unsignedTemp > 128 ? unsignedTemp - 256 : unsignedTemp + (temperature & 0xff) / 256.0;
    }

    private void addLocationIfPossible(SimpleBeacon simpleBeacon) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (isLocationFresh(location) && isLocationAccurate(location)) {
                simpleBeacon.location = new SimpleBeacon.Location(location.getLongitude(), location.getLatitude(),location.getAccuracy());
            }
        }
    }



    private boolean isLocationFresh(Location location) {
        if (location == null) {
            return false;
        }
        long gpsTime = location.getTime();
        long systemTime = System.currentTimeMillis();
        return (systemTime - gpsTime) <= preferences.getLocationFreshness();
    }

    private boolean isLocationAccurate(Location location) {
        if (location == null) {
            return false;
        }
        float accuracy = location.getAccuracy();
        return accuracy > 0 && accuracy <= preferences.getLocationAccuracy();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
