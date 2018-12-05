package saarland.cispa.bletrackerlib.data;

public class SimpleBeacon {

    private float longitude;
    private float latitude;
    private String uuid;
    //TODO: This is a dummy class at the moment

    /**
     * Contains the Beacon data
     */
    public SimpleBeacon() {
    }

    public SimpleBeacon(float longitude, float latitude, String uuid) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.uuid = uuid;
    }
}
