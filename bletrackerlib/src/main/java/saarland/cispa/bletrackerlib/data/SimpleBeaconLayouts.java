package saarland.cispa.bletrackerlib.data;

import org.altbeacon.beacon.BeaconParser;

public enum SimpleBeaconLayouts {
    EDDYSTONE_UID_LAYOUT(BeaconParser.EDDYSTONE_UID_LAYOUT),
    EDDYSTONE_TLM_LAYOUT(BeaconParser.EDDYSTONE_TLM_LAYOUT),
    EDDYSTONE_URL_LAYOUT(BeaconParser.EDDYSTONE_URL_LAYOUT),
    // Not used till now
    EDDYSTONE_URI_LAYOUT(BeaconParser.URI_BEACON_LAYOUT),
    ALTBEACON_LAYOUT(BeaconParser.ALTBEACON_LAYOUT),
    IBEACON_LAYOUT("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"),
    RUUVI_LAYOUT("m:0-2=0499,i:4-19,i:20-21,i:22-23,p:24-24");

    private static final String TAG = "SimpleBeaconLayouts";

    private String layout;

    SimpleBeaconLayouts(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return layout;
    }
}
