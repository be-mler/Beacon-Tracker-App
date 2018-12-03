package saarland.cispa.bletrackerlib;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import saarland.cispa.bletrackerlib.types.SimpleBeacon;

public class RangeNotifierImpl implements RangeNotifier {

    protected static final String TAG = "RangeNotifierImpl";
    private final BeaconStateNotifier stateNotifier;


    public RangeNotifierImpl(BeaconStateNotifier stateNotifier) {
        this.stateNotifier = stateNotifier;
    }

    /**
     * This method is called once per second if we set startRangingBeaconsInRegion and beacons are near
     * @param beacons all beacons which were found nearby
     * @param region the specific region
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        ArrayList<SimpleBeacon> simpleBeacons = new ArrayList<>();
        for (Beacon beacon: beacons) {
            //if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
            // This is a Eddystone-UID frame
            Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();
            Log.d(TAG, "I see a beacon transmitting namespace id: "+namespaceId+
                    " and instance id: "+instanceId+
                    " approximately "+beacon.getDistance()+" meters away.");

            // Do we have telemetry data?
            if (beacon.getExtraDataFields().size() > 0) {
                long telemetryVersion = beacon.getExtraDataFields().get(0);
                long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                long pduCount = beacon.getExtraDataFields().get(3);
                long uptime = beacon.getExtraDataFields().get(4);

                Log.d(TAG, "The above beacon is sending telemetry version "+telemetryVersion+
                        ", has been up for : "+uptime+" seconds"+
                        ", has a battery level of "+batteryMilliVolts+" mV"+
                        ", and has transmitted "+pduCount+" advertisements.");

            }
            simpleBeacons.add(new SimpleBeacon());
        }
        stateNotifier.onUpdate(simpleBeacons);
        //}
    }

    //TODO: Get gps coordinates and send best match between signal strength and gps accuracy
}