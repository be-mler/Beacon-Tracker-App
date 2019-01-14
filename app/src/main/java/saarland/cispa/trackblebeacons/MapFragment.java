package saarland.cispa.trackblebeacons;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public class MapFragment extends Fragment implements ItemizedIconOverlay.OnItemGestureListener {

    private MapView map = null;
    private MyLocationNewOverlay myLocationOverlay;
    private ImageButton btnFollowMe;

    private ItemizedOverlayWithFocus<OverlayItem> beaconsOverlay;

    private final float DEFAULT_ZOOM_LEVEL = 20.0F;
    private static final String TAG = "MapFragment";

    private View rootView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                Log.i(TAG, "btnFollowMe clicked");
                if (!myLocationOverlay.isFollowLocationEnabled()) {
                    myLocationOverlay.enableFollowLocation();
                    mapController.setZoom(DEFAULT_ZOOM_LEVEL);
                    btnFollowMe.setImageResource(R.drawable.ic_gps_fixed);
                } else {
                    myLocationOverlay.disableFollowLocation();
                    btnFollowMe.setImageResource(R.drawable.ic_gps_not_fixed);
                }
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
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (!myLocationOverlay.isFollowLocationEnabled()) {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_not_fixed);
                } else {
                    btnFollowMe.setImageResource(R.drawable.ic_gps_fixed);
                }
                return true;
            }
        });

        //Initializing Beacon Overlay
        //TODO: Make fancy icon + circle of range
        ArrayList<OverlayItem> items = new ArrayList<>();
        //Add Default marker, CISPA (NOT A BEACON)
        items.add(new OverlayItem("Cispa", "Helmholtz Center for Information Security, ", new GeoPoint(49.25950, 7.05168)));
        beaconsOverlay= new ItemizedOverlayWithFocus<>(items,this,this.getActivity());

        beaconsOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(beaconsOverlay);

//        loadBeacons();

    }


    private void addBeaconToOverlay(SimpleBeacon beacon)
    {
        if (beacon.location != null && beacon.eddystoneUidData != null)
        beaconsOverlay.addItem(new OverlayItem("Beacon", beacon.altbeaconIBeaconData.uuid,
                new GeoPoint(beacon.location.locationLat, beacon.location.locationLong)));
    }

//    private void loadBeacons()
//    {
//        bleTracker.getCispaConnection().requestBeacons(new RemoteReceiver() {
//            @Override
//            public void onBeaconReceive(SimpleBeacon[] beacons) {
//                for (SimpleBeacon beacon:beacons) {
//                    addBeaconToOverlay(beacon);
//                }
//            }
//
//            @Override
//            public void onBeaconReceiveError() {
//                Log.d("API","Failed to receiver beacons from api");
//            }
//        });
//    }

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
