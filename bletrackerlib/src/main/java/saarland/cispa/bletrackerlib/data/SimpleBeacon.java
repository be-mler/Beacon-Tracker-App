package saarland.cispa.bletrackerlib.data;

public class SimpleBeacon {

    public static class Location {
        /**
         * The locations longitude
         */
        public double locationLong;
        /**
         * The locations latitude
         */
        public double locationLat;

        /**
         * @param locationLong The locations longitude
         * @param locationLat The locations latitude
         */
        public Location(double locationLong, double locationLat) {
            this.locationLong = locationLong;
            this.locationLat = locationLat;
        }
    }

    public static class Telemetry {
        /**
         * Specifies the format for the additional data. This is always 0 at the moment
         */
        public long telemetryVersion;

        /**
         * Indicates the battery voltage in mV. If the beacon is powered by USB the value is zero
         */
        public long batteryMilliVolts;

        /**
         * The temperature of the beacon in °C. If the beacon has no sensor the value is zero
         */
        public double temperature;

        /**
         * A counter how many advertisement packets the beacon has sent till last power on
         */
        public long pduCount;

        /**
         * The uptime of the beacon in s. If the beacon has no time counter the value is zero
         */
        public long uptime;

        /**
         * @param telemetryVersion Specifies the format for the additional data. This is always 0 at the moment
         * @param batteryMilliVolts Indicates the battery voltage in mV. If the beacon is powered by USB the value is zero
         * @param temperature The temperature of the beacon in °C. If the beacon has no sensor the value is zero
         * @param pduCount A counter how many advertisement packets the beacon has sent till last power on
         * @param uptime The uptime of the beacon in s. If the beacon has no time counter the value is zero
         */
        public Telemetry(long telemetryVersion, long batteryMilliVolts, double temperature, long pduCount, long uptime) {
            this.telemetryVersion = telemetryVersion;
            this.batteryMilliVolts = batteryMilliVolts;
            this.temperature = temperature;
            this.pduCount = pduCount;
            this.uptime = uptime;
        }
    }

    public static class AltbeaconIBeaconData {

        /**
         * The UUID of the beacon. Differs most time from company to company
         */
        public String uuid;

        /**
         * The Major ID. This could be used to indicate a certain store of this company
         */
        public String major;
        /**
         * The Minor. This could be used to indicate the beacon in the store
         */
        public String minor;

        /**
         * @param uuid The UUID of the beacon. Differs most time from company to company
         * @param major The Major ID. This could be used to indicate a certain store of this company
         * @param minor The Minor. This could be used to indicate the beacon in the store
         */
        public AltbeaconIBeaconData(String uuid, String major, String minor) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
        }
    }

    public static class Ruuvi {
        /**
         * The air humidity in %
         */
        public int humidity;

        /**
         * The airPressure in hPa
         */
        public int airPressure;

        /**
         * The temperature in °C
         */
        public int temperature;

        /**
         * @param humidity The air humidity in %
         * @param airPressure The airPressure in hPa
         * @param temperature The temperature in °C
         */
        public Ruuvi(int humidity, int airPressure, int temperature) {
            this.humidity = humidity;
            this.airPressure = airPressure;
            this.temperature = temperature;
        }
    }

    public static class EddystoneURL {
        /**
         * The URL the beacon advertises
         */
        public String url;

        /**
         * @param url The URL the beacon advertises
         */
        public EddystoneURL(String url) {
            this.url = url;
        }
    }

    public static class EddystoneUID {

        /**
         * The namespace ID
         */
        public String namespaceId;

        /**
         * The instance ID
         */
        public String instanceId;

        /**
         * @param namespaceId The namespace ID
         * @param instanceId The instance ID
         */
        public EddystoneUID(String namespaceId, String instanceId) {
            this.namespaceId = namespaceId;
            this.instanceId = instanceId;
        }
    }

    // The following data has every beacon

    //TODO: something useful with id...
    public long id;

    /**
     *  The beaconType of beacon is directly deserved from SimpleBeaconLayouts enum entity
     */
    public String beaconType;

    /**
     * The signal strength in dBm
     */
    public int signalStrength;

    /**
     * The transmit power TX
     */
    public int transmitPower;

    /**
     * The manufacturer
     */
    public int manufacturer;

    /**
     * The bluetooth MAC address
     */
    public String bluetoothAddress;

    /**
     * The bluetooth name
     */
    public String bluetoothName;

    /**
     * The accumulative distance in meters
     */
    public double distance;

    /**
     * Time when beacon was discovered
     */
    public String timestamp;

    // This data is set accordingly which beacon we deal with. It is in relation to the beacon beaconType
    public Location location;
    public Telemetry telemetry;
    public AltbeaconIBeaconData altbeaconIBeaconData;
    public Ruuvi ruuvi;
    public EddystoneUID eddystoneUidData;
    public EddystoneURL eddystoneUrlData;

    public SimpleBeacon() {

    }

    /**
     * @param beaconType The beaconType of beacon is directly deserved from @SimpleBeaconLayouts enum entity
     * @param signalStrength The signal strength in dBm
     * @param transmitPower The transmit power TX
     * @param manufacturer The manufacturer
     * @param bluetoothAddress The bluetooth MAC address
     * @param bluetoothName The bluetooth name
     * @param distance The accumulative distance in meters
     */

    public SimpleBeacon(String beaconType, int signalStrength, int transmitPower, int manufacturer, String bluetoothAddress, String bluetoothName, double distance, String timestamp) {
        this.beaconType = beaconType;
        this.signalStrength = signalStrength;
        this.transmitPower = transmitPower;
        this.manufacturer = manufacturer;
        this.bluetoothAddress = bluetoothAddress;
        this.bluetoothName = bluetoothName;
        this.distance = distance;
        this.timestamp = timestamp;
    }
}
