package saarland.cispa.bletrackerlib.data;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

public enum SimpleBeaconParser {
    EDDYSTONE_UID_LAYOUT(BeaconParser.EDDYSTONE_UID_LAYOUT) {
        /**
         * Create SimpleBeacon with Eddystone UID attributes
         * Type is EDDYSTONE_UID_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            // This is a Eddystone-UID frame
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            String namespaceId = beacon.getId1().toHexString();
            String instanceId = beacon.getId2().toHexString();

            simpleBeacon.setNamespaceId(namespaceId);
            simpleBeacon.setInstanceId(instanceId);

            Log.d(TAG, getIdentifier() + ": namespace id: "+namespaceId+
                    " and instance id: "+instanceId);

            return simpleBeacon;
        }
    },
    EDDYSTONE_TLM_LAYOUT(BeaconParser.EDDYSTONE_TLM_LAYOUT) {
        /**
         * Create SimpleBeacon with Eddystone UID and TLM attributes
         * Type is EDDYSTONE_TLM_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            // Parse the Eddystone UID frame
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            String namespaceId = beacon.getId1().toHexString();
            String instanceId = beacon.getId2().toHexString();

            simpleBeacon.setNamespaceId(namespaceId);
            simpleBeacon.setInstanceId(instanceId);

            // Parse the additional data TLM
            if (beacon.getExtraDataFields().size() > 0) {
                long telemetryVersion = beacon.getExtraDataFields().get(0);
                long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                long pduCount = beacon.getExtraDataFields().get(3);
                long uptime = beacon.getExtraDataFields().get(4);

                simpleBeacon.setTelemetryVersion(telemetryVersion);
                simpleBeacon.setBatteryMilliVolts(batteryMilliVolts);
                simpleBeacon.setPduCount(pduCount);
                simpleBeacon.setUptime(uptime);

                Log.d(TAG, getIdentifier() + ": telemetry version "+telemetryVersion+
                        ", has been up for : "+uptime+" seconds"+
                        ", has a battery level of "+batteryMilliVolts+" mV"+
                        ", and has transmitted "+pduCount+" advertisements.");
            }
            return simpleBeacon;
        }
    },
    EDDYSTONE_URL_LAYOUT(BeaconParser.EDDYSTONE_URL_LAYOUT) {
        /**
         * Create SimpleBeacon with Eddystone URL attributes
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
            simpleBeacon.setUrl(url);

            Log.d(TAG, getIdentifier() + ": url: " + url);

            return simpleBeacon;
        }
    },
    ALTBEACON_LAYOUT(BeaconParser.ALTBEACON_LAYOUT) {
        /**
         * Create SimpleBeacon with Altbeacon attributes
         * Type is ALTBEACON_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            //TODO: Maybe we get more data
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            String uuid = beacon.getId1().toHexString();
            String major = beacon.getId2().toHexString();
            String minor = beacon.getId3().toHexString();

            simpleBeacon.setUuid(uuid);
            simpleBeacon.setMajor(major);
            simpleBeacon.setMinor(minor);

            Log.d(TAG, getIdentifier() + ": uuid: "+uuid+" major: "+major+" minor: "+minor);
            return simpleBeacon;
        }
    },
    URI_BEACON_LAYOUT(BeaconParser.URI_BEACON_LAYOUT) {
        /**
         * Create SimpleBeacon with URI Beacon attributes
         * Type is URI_BEACON_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            //TODO: Implement
            return simpleBeacon;
        }
    },
    IBEACON_LAYOUT("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24") {
        /**
         * Create SimpleBeacon with iBeacon attributes
         * Type is IBEACON_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            //TODO: Maybe we get more data
            SimpleBeacon simpleBeacon = parseStandardData(beacon);

            String uuid = beacon.getId1().toHexString();
            String major = beacon.getId2().toHexString();
            String minor = beacon.getId3().toHexString();

            simpleBeacon.setUuid(uuid);
            simpleBeacon.setMajor(major);
            simpleBeacon.setMinor(minor);

            Log.d(TAG, getIdentifier() + ": uuid: "+uuid+" major: "+major+" minor: "+minor);
            return simpleBeacon;
        }
    },
    RUUVI_LAYOUT("m:0-2=0499,i:4-19,i:20-21,i:22-23,p:24-24") {
        /**
         * Create SimpleBeacon with RuuviTag attributes
         * Type is IBEACON_LAYOUT
         * @param beacon the received Beacon
         * @return the created simple beacon
         */
        @Override
        public SimpleBeacon parse(Beacon beacon) {
            SimpleBeacon simpleBeacon = parseStandardData(beacon);
            //TODO: Implement
            return simpleBeacon;
        }
    };

    private static final String TAG = "SimpleBeaconParser";

    private String layout;

    SimpleBeaconParser(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return layout;
    }

    public String getIdentifier() {
        return name();
    }

    public abstract SimpleBeacon parse(Beacon beacon);

    public SimpleBeacon parseStandardData(Beacon beacon) {
        int signalStrength = beacon.getRssi();
        int transmitterPower = beacon.getTxPower();

        int manufacturer = beacon.getManufacturer();
        String bluetoothAddress = beacon.getBluetoothAddress();
        String bluetoothName = beacon.getBluetoothName();

        double distance = beacon.getDistance();

        return new SimpleBeacon(getIdentifier(), signalStrength, transmitterPower, manufacturer, bluetoothAddress, bluetoothName, distance);
    }

}
