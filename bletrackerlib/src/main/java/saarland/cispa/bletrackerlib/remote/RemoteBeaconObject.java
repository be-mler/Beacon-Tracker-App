package saarland.cispa.bletrackerlib.remote;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public class RemoteBeaconObject {

    public int ID ;


    public String BeaconType ;


    public String DiscoveryTime ;
    public double LocationLong ;
    public double LocationLat ;
    public double LocationRadius ;

    //Our Currently unused
    public String Information ;

    //Data by EveryBeaon
    public double Distance ;
    public int SignalStrength ;
    public int TransmitterPower ;
    public int Manufacturer ;
    public String BluetoothAddress ;
    public String BluetoothName ;


    //AltBeacon / iBeacon
    public String UUID ;
    public String Major ;
    public String Minor ;

    //Eddystone
    public String NamespaceID ;
    public String InstanceID ;

    public long TelemetryVersion ;
    public long BatterMilliVolts ;
    public long PduCount ;
    public long Uptime ;

    public String Url ;

    //RuuviTag
    public double AirPressure ;
    public double Temperature ;
    public double Humidity ;


    public int UUIDUniqueID ; //UUID's are not unique so if distance to high to existing ones create additional here.
    public int NumConfirmations ;
    public String LastConfirmation ;
    
    //Empty Constructor for Deserialization (not sure if this is required in java)
    public RemoteBeaconObject(){};
    
    
    
    public RemoteBeaconObject(SimpleBeacon simpleBeacon)
    {
        this.BeaconType =  simpleBeacon.beaconType;


        this.DiscoveryTime = simpleBeacon.timestamp;
        if(simpleBeacon.location != null) {
            this.LocationLong = simpleBeacon.location.locationLong;
            this.LocationLat = simpleBeacon.location.locationLat;
            this.LocationRadius = simpleBeacon.location.locationRadius;
        }

        //UNUSED?
        //Our Currently unused
        //this.Information

        //Data by EveryBeaon
        this.Distance = simpleBeacon.distance;
        this.SignalStrength = simpleBeacon.signalStrength;
        this.TransmitterPower = simpleBeacon.transmitPower;
        this.Manufacturer =simpleBeacon.manufacturer;
        this.BluetoothAddress = simpleBeacon.bluetoothAddress;
        this.BluetoothName = simpleBeacon.bluetoothName;


        //AltBeacon / iBeacon
        if(simpleBeacon.altbeaconIBeaconData != null) {
            this.UUID = simpleBeacon.altbeaconIBeaconData.uuid;
            this.Major = simpleBeacon.altbeaconIBeaconData.major;
            this.Minor = simpleBeacon.altbeaconIBeaconData.minor;
        }
        //Eddystone
        if(simpleBeacon.eddystoneUidData != null) {
            this.NamespaceID = simpleBeacon.eddystoneUidData.namespaceId;
            this.InstanceID = simpleBeacon.eddystoneUidData.instanceId;
        }

        if(simpleBeacon.telemetry != null) {
            this.TelemetryVersion = simpleBeacon.telemetry.telemetryVersion;
            this.BatterMilliVolts = simpleBeacon.telemetry.batteryMilliVolts;
            this.PduCount = simpleBeacon.telemetry.pduCount;
            this.Uptime = simpleBeacon.telemetry.uptime;
        }

        if(simpleBeacon.eddystoneUrlData != null) {
            this.Url = simpleBeacon.eddystoneUrlData.url;
        }

        //RuuviTag
        if(simpleBeacon.ruuvi != null) {
            this.AirPressure = simpleBeacon.ruuvi.airPressure;
            this.Temperature = simpleBeacon.ruuvi.temperature;
            this.Humidity = simpleBeacon.ruuvi.humidity;
        }


        
        
        
        
    }


}
