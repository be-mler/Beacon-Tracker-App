package saarland.cispa.trackblebeacons.fragments;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.TileDownloader;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.remote.RemoteRequestReceiver;
import saarland.cispa.trackblebeacons.R;
import saarland.cispa.trackblebeacons.helpers.MapHelper;

public class MapFragment extends Fragment implements ItemizedIconOverlay.OnItemGestureListener {

    private MapView map = null;
    private MyLocationNewOverlay myLocationOverlay;
    private ImageButton btnFollowMe;
    private View btnZoomDefault;

    private ItemizedOverlayWithFocus<OverlayItem> beaconsOverlay;

    private final double DEFAULT_ZOOM_LEVEL = 18;
    private final double MAX_ZOOM_LEVEL = 20;

    private static final String TAG = "MapFragment";

    private View rootView = null;

    private BleTracker bleTracker;
    private boolean apiRequestRunning = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bleTracker = BleTracker.getInstance();

        initRemoteReceiver();

        //Initialize map and set default location
        initMap();
    }

    private void initMap()
    {


        //ext 2 Lines recommended by OsmDroid
        Context ctx = this.getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = rootView.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMaxZoomLevel(MAX_ZOOM_LEVEL);


        MapHelper.getInstance().InitMapHelper(getActivity(), map);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        map.setMultiTouchControls(true);

        //Building level zoom
        final IMapController mapController = map.getController();
        mapController.setZoom(DEFAULT_ZOOM_LEVEL);

        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(this.getActivity());
        locationProvider.addLocationSource(LocationManager.PASSIVE_PROVIDER);
        myLocationOverlay = new MyLocationNewOverlay(locationProvider, map);
        myLocationOverlay.setDrawAccuracyEnabled(true);
//        DrawableConverter converter = new DrawableConverter(this);
//        Bitmap icon = converter.toBitmap(getResources().getDrawable(R.drawable.ic_my_location_icon), 36, 36);
//        myLocationOverlay.setPersonIcon(icon);
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);

        // Add compass
        CompassOverlay compassOverlay = new CompassOverlay(this.getActivity(), new InternalCompassOrientationProvider(this.getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // Button to enable follow me
        btnFollowMe = rootView.findViewById(R.id.ic_gps_fixed);
        btnFollowMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myLocationOverlay.isFollowLocationEnabled()) {
                    myLocationOverlay.enableFollowLocation();
                    btnFollowMe.setImageResource(R.drawable.ic_gps_fixed);
                } else {
                    myLocationOverlay.disableFollowLocation();
                    btnFollowMe.setImageResource(R.drawable.ic_gps_not_fixed);
                }
            }
        });

        btnZoomDefault = rootView.findViewById(R.id.ic_zoom_default);
        btnZoomDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.setZoom(DEFAULT_ZOOM_LEVEL);
            }
        });

        // Set Image of btnFollowMe to the corresponding icon if there is a zoom or a scroll event
        // Zoom and scroll by multitouch disables location following
        map.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (!myLocationOverlay.isFollowLocationEnabled()) {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_not_fixed);
                } else {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_fixed);
                }
                loadBeacons();
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (!myLocationOverlay.isFollowLocationEnabled()) {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_not_fixed);
                } else {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_fixed);
                }
                loadBeacons();
                return true;
            }
        });

        //Initializing Beacon Overlay
        //TODO: Make fancy icon + circle of range
        ArrayList<OverlayItem> items = new ArrayList<>();
        //Add Default marker, CISPA (NOT A BEACON)
        //items.add(new OverlayItem("Cispa", "Helmholtz Center for Information Security, ", new GeoPoint(49.25950, 7.05168)));
        beaconsOverlay= new ItemizedOverlayWithFocus<>(items,this,this.getActivity());

        beaconsOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(beaconsOverlay);

        loadBeacons();


    }


    private void addBeaconToOverlay(SimpleBeacon beacon)
    {
        if (beacon.location != null){
            for (OverlayItem x:beaconsOverlay.getDisplayedItems()) {
                if(x.getSnippet().equals(String.valueOf(beacon.id))) {
                    return;
                }
            }
            beaconsOverlay.addItem(new OverlayItem("Beacon", String.valueOf(beacon.id),
                    new GeoPoint(beacon.location.locationLat, beacon.location.locationLong)));
        }

    }

    private void initRemoteReceiver()
    {
        RemoteRequestReceiver receiver = new RemoteRequestReceiver() {
            @Override
            public void onBeaconsReceived(ArrayList<SimpleBeacon> beacons) {
                for (SimpleBeacon beacon:beacons) {
                    /*TODO: Perform Check if already loaded this beacon */
                    /*((MainActivity)getActivity()).simpleBeacons*/

                    addBeaconToOverlay(beacon);
                }
                apiRequestRunning = false;
            }

            @Override
            public void onBeaconReceiveError(String errorMessage) {
                apiRequestRunning = false;
                Log.d("API","Failed to receiver beacons from api");
            }
        };
        bleTracker.getCispaConnection().addRemoteReceiver(receiver);
    }



    private void loadBeacons()
    {
        if(apiRequestRunning)
            return;
        apiRequestRunning = true;
        double latStart = this.map.getMapCenter().getLatitude() - (map.getLatitudeSpanDouble()/2.0);
        double longStart = this.map.getMapCenter().getLongitude() - (map.getLongitudeSpanDouble()/2.0);

        bleTracker.getCispaConnection().requestBeacons(longStart,longStart + map.getLatitudeSpanDouble(), latStart, latStart +map.getLatitudeSpanDouble());

    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public boolean onItemSingleTapUp(int index, Object item) {
        return false;
    }

    @Override
    public boolean onItemLongPress(int index, Object item) {


        return false;
    }




}
