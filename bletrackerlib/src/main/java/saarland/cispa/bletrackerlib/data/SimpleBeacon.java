package saarland.cispa.bletrackerlib.data;

public class SimpleBeacon {
    //TODO: Something for local db... no idea how to realize a unique id for a beacon which has none...
    public long id;

    // GPS coordinates
    private float locationLong;
    private float locationLat;

    // Type of beacon to extract the right data at endpoint
    // Type is directly deserved from @SimpleBeaconParser enum entity
    private String type;

    // Data which has every beacon
    private final int signalStrength;
    private final int transmitterPower;
    private final int manufacturer;
    private final String bluetoothAddress;
    private final String bluetoothName;
    private final double distance;

    // AltBeacon and iBeacon
    private String uuid;
    private String major;
    private String minor;

    // Eddystone UID and Eddystone TLM
    private String namespaceId;
    private String instanceId;

    // Eddystone TLM
    private long telemetryVersion;
    private long batteryMilliVolts;
    private long pduCount;
    private long uptime;

    // Eddystone URL
    private String url;

    // RuuviTag
    private double airPressure;
    private double temprature;
    private double humidity;

    public SimpleBeacon(String type, int signalStrength, int transmitterPower, int manufacturer, String bluetoothAddress, String bluetoothName, double distance) {
        this.type = type;
        this.signalStrength = signalStrength;
        this.transmitterPower = transmitterPower;
        this.manufacturer = manufacturer;
        this.bluetoothAddress = bluetoothAddress;
        this.bluetoothName = bluetoothName;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public float getLocationLong() {
        return locationLong;
    }

    public float getLocationLat() {
        return locationLat;
    }

    public void setLocation(float locationLong, float locationLat) {
        this.locationLong = locationLong;
        this.locationLat = locationLat;
    }

    public String getType() {
        return type;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public int getTransmitterPower() {
        return transmitterPower;
    }

    public int getManufacturer() {
        return manufacturer;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public double getDistance() {
        return distance;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public long getTelemetryVersion() {
        return telemetryVersion;
    }

    public void setTelemetryVersion(long telemetryVersion) {
        this.telemetryVersion = telemetryVersion;
    }

    public long getBatteryMilliVolts() {
        return batteryMilliVolts;
    }

    public void setBatteryMilliVolts(long batteryMilliVolts) {
        this.batteryMilliVolts = batteryMilliVolts;
    }

    public long getPduCount() {
        return pduCount;
    }

    public void setPduCount(long pduCount) {
        this.pduCount = pduCount;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(double airPressure) {
        this.airPressure = airPressure;
    }

    public double getTemprature() {
        return temprature;
    }

    public void setTemprature(double temprature) {
        this.temprature = temprature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
