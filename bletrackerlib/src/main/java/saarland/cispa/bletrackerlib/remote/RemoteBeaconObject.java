package saarland.cispa.bletrackerlib.remote;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts;

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
    
    public SimpleBeacon GetSimpleBeacon()
    {

        SimpleBeacon simpleBeacon = new SimpleBeacon();
        simpleBeacon.id = this.ID;
        simpleBeacon.beaconType = this.BeaconType;
        simpleBeacon.timestamp = this.DiscoveryTime;


        if(this.LocationLat != 0 && LocationLat != 0) {
            simpleBeacon.location = new SimpleBeacon.Location(this.LocationLong,this.LocationLat,this.LocationRadius);
        }

        //UNUSED?
        //Our Currently unused
        //this.Information

        //Data by EveryBeaon
        simpleBeacon.distance = this.Distance ;
        simpleBeacon.signalStrength = this.SignalStrength;
        simpleBeacon.transmitPower = this.TransmitterPower;
        simpleBeacon.manufacturer = this.Manufacturer ;
        simpleBeacon.bluetoothAddress = this.BluetoothAddress ;
        simpleBeacon.bluetoothName = this.BluetoothName ;


        if(this.BeaconType == null)
            return simpleBeacon;

        //AltBeacon / iBeacon
        if(this.BeaconType.equals(SimpleBeaconLayouts.IBEACON_LAYOUT.name()) || this.BeaconType.equals(SimpleBeaconLayouts.ALTBEACON_LAYOUT.name())) {
            simpleBeacon.altbeaconIBeaconData = new SimpleBeacon.AltbeaconIBeaconData(this.UUID ,this.Major,this.Minor);

        }
        //Eddystone
        if(this.BeaconType.equals(SimpleBeaconLayouts.EDDYSTONE_UID_LAYOUT.name())) {
            simpleBeacon.eddystoneUidData = new SimpleBeacon.EddystoneUID(this.NamespaceID,this.InstanceID);

        }

        //All Eddystones??
        simpleBeacon.telemetry = new SimpleBeacon.Telemetry(this.TelemetryVersion,this.BatterMilliVolts,this.Temperature
        ,this.PduCount,this.Uptime);



        if(this.BeaconType.equals(SimpleBeaconLayouts.EDDYSTONE_URL_LAYOUT.name())) {
             simpleBeacon.eddystoneUrlData = new SimpleBeacon.EddystoneURL(this.Url);
        }

        //RuuviTag
        if(this.BeaconType.equals(SimpleBeaconLayouts.RUUVI_LAYOUT.name())) {
            simpleBeacon.ruuvi = new SimpleBeacon.Ruuvi(this.Humidity,this.AirPressure,this.Temperature);
        }
        return simpleBeacon;
    }
    
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
