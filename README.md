# Beacon Tracker App #
## Intro ##
The BLE Tracker Lib is an android app to track Bluetooth Low Energy (BLE) Beacons. You can view them on a map and get details what they are sending.

## Motivation ##
We are two cybersecurity students from [CISPA](https://cispa.saarland/). Our motivation is to get a view where and how much BLE trackers used for benign reasons and or tracking you. 

## Screenshots ##
<img src="https://github.com/be-mler/Beacon-Tracker-App/blob/master/screenshots/map.png?raw=true" height="500">
<img src="https://github.com/be-mler/Beacon-Tracker-App/blob/master/screenshots/nearby.png?raw=true" height="500">
<img src="https://github.com/be-mler/Beacon-Tracker-App/blob/master/screenshots/scan.png?raw=true" height="500">

## Features ##
#### General: ####
This app starts a background service for scanning and sending beacons.
Already tracked beacons will be shown at a map.
The map automatically caches to reduce mobile data usage.

#### Supported beacons: ####
- iBeacons 
- AltBeacons
- Eddystone URL 
- Eddystone UID
- RuuviTags
## Requirements ##
- At least API level 21 (Andorid 5.0).
- Bluetooth of course
- GPS


## Technical Background ##
For more details take a look at our [Java Doc](https://be-mler.github.io/Beacon-Tracker-App/).

## Libraries ##
**We use the following libraries:**
- **Beacon-Tracker-Lib** (the heart of the app)
- **OSMdroid** (for the map)
- **App Intro** (for the introduction at first start)

## Credits ##
- [Android Beacon Scanner](https://github.com/Bridouille/android-beacon-scanner) form where we have adopted the scan and nearby beacon layout.

## Todo before release ##
- Map tile rendering is currently done at OSM server but this is not allowed for apps which are released to public. The reason behind that is that this causes heavy workload at their servers. This violates the [OSM tile usage policy](https://operations.osmfoundation.org/policies/tiles/).
Therefore we have to [set up a own server](https://switch2osm.org/serving-tiles/) for providing the tiles.

## License ##
	Copyright 2019 Max Bäumler, Tobias Faber
	Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

This software is available under the Apache License 2.0