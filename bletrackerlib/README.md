# BLE Tracker Lib
## Intro
The BLE Tracker Lib is an android app library to easyly track Bluetooth Low Energy (BLE) Beacons. Track in this case means if there is a beacon nearby this lib recognizes it, adds a GPS location to it and sends it to a rest endpoint.
We also provide an **App** using this lib.

## Motivation
We are two cybersecurity students from [CISPA](https://cispa.saarland/). Our motivation is to get a view where and how much BLE trackers used for benign reasons and or tracking you. We want to provide you the ability to include this in your app and help tracking beacons or to 
## Features 
#### General:
- Backgound Tracking (Low power consumption but less accurate)
- Forground Tracking (Higher power consumption but very accurate)
- Send to one ore more REST endpoints
- Send to CISPA REST endpoint
#### Supported beacons: 
- iBeacons 
- AltBeacons
- Eddystone URL 
- Eddystone UID
- RuuviTags
## Usage
### Requirements
- At the moment we only support **AndroidX** based projects. If your project is not migrated yet take a look at [how to migrate to AndroidX](https://developer.android.com/jetpack/androidx/migrate). 
- At least API level 21.

### Include+Basic
####1. Into your gradle

Step 1. Add the jitpack repository to yourroot build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependancy:
```gradle
	dependencies {
		...
		implementation 'com.github.User:Repo:Tag'
	}
```
####2. Into your project

Step 1. Change the application context in your AndroidManifest.xml to **BleTrackerService**
```xml
	<application
		android:name="saarland.cispa.bletrackerlib.service.BleTrackerService"
		...
	</application>
```

Step 2. Initialize the Lib in a **activity context** in our case *MainActivity*.
Do never forget to call **bleTracker.init()** otherwhise you will get a bunch of nullpointer exceptions.
```java
bleTracker = BleTracker.getInstance();
BleTrackerPreferences preferences = new BleTrackerPreferences();
bleTracker.init(MainActivity.this, preferences);
```

Step 3 (optional). Add a callback to get notified if you there are beacons and beacon data
```java
bleTracker.addBeaconNotifier(new BeaconStateNotifier() {
	@Override
	public void onUpdate(ArrayList<SimpleBeacon> beacons) {
		//TODO: Do somethin useful with the tracked beacons which are around you
	}
	Override
	public void onBeaconNearby() {
		//TODO: Display a notification that a beacon is nearby
	}
});
```
Step 4. Start a for forgound sevice (recommended)
```java
if (!bleTracker.isRunning()) {
	Notification notification = ForegroundNotification.create(getApplicationContext(),
		R.drawable.ic_stat_name, MainActivity.class);
	try {
		bleTracker.createForgroundService(notification);
	} catch (OtherServiceStillRunningException e) {
		e.printStackTrace();
	}
	bleTracker.start(MainActivity.this);
```
Step 4 alternative. Start a background service 
**This is not recommended.** It leads to very low detection rate and very worse GPS coordinates due to battery saving options since Android 8. 
Also sending to CISPA is disabled because we want to have accurate data.
If you can deal with this impacts you can proceeed.
```java
if (!bleTracker.isRunning()) {
	try {
		bleTracker.createBackgroundService();
	} catch (OtherServiceStillRunningException e) {
		e.printStackTrace();
	}
	bleTracker.start(MainActivity.this);
}
```
Step 5. **Always update the activity context** in **every** Activity onResume()
```java
@Override
protected void onResume() {
	BleTracker.getInstance().updateActivity(MyActivity.this);
}
```

### Advanced Usage
#### Preferences
You can specify the following preferences for the tracker.
This has to be done at initializing the lib by changing the values in **BleTrackerPreferences**.
**We provide you the following settings**:
- location accuracy (within which accurcy you want that lacation coordinates are added to the beacons)
- location freshness (how old has the last location data has to be that location coordinates are added to the beacons)
- send to CISPA (do you want that your scanned beacons are sent to us?)
- scan interval (the interval in which the scanner looks for beacons the higher you set it the lower energy will cost but the less updates you get)
#### Service state notifies

#### Receive beacons from REST endpoint

#### Send beacons to REST endpoint

#### CISPA Connection
The CISPA connection is also a REST connection used by default. You can send/receive beacons to/from our endpoint. 
This connection is is activated by default but can be deactivated via **BleTrackerPreferences**.
It will **NOT send** if you use a **background scanner** or change this **BleTrackerPreferences** to values:
- location accuracy > 50m
- location freshness > 10s
This is enforced beacuse we want to have a accurate dataset.

You can also take a look at our example project [example project](todo) or at [our app](todo).
## License