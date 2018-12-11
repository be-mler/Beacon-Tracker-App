package saarland.cispa.bletrackerlib.data;

public class SimpleBeacon {

    private float locationLong;
    private float locationLat;
    private String uuid;
    //TODO: This is a dummy class at the moment

    public float getLocationLong() {
        return locationLong;
    }

    public float getLocationLat() {
        return locationLat;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * Contains the Beacon data
     */
    public SimpleBeacon() {

    }

    public SimpleBeacon(float longitude, float latitude, String uuid) {
        this.locationLong = longitude;
        this.locationLat = latitude;
        this.uuid = uuid;
    }
}
