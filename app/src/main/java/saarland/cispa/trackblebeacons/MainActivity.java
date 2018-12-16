package saarland.cispa.trackblebeacons;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.exceptions.ServiceAlreadyExistsException;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.remote.RemoteReceiver;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;
import saarland.cispa.bletrackerlib.service.ForegroundNotification;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ItemizedIconOverlay.OnItemGestureListener {

    private static final String TAG = "MainActivity";
    private BleTracker bleTracker;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private String DEFAULT_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOTIFICATION_CHANNEL_ID";
    private final float DEFAULT_ZOOM_LEVEL = 20.0F;
    private final String FIRST_START_PROPERTY_KEY = "firstStart";

    MapView map = null;
    ItemizedOverlayWithFocus<OverlayItem> beaconsOverlay;
    private MyLocationNewOverlay myLocationOverlay;
    private ImageButton btnFollowMe;

    private void showIntroAtFirstStart() {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean(FIRST_START_PROPERTY_KEY, true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

                    //  Make a new Preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        showIntroAtFirstStart();

        //ext 2 Lines recommended by OsmDroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize the tracker lib
        initTracker();

        //Initialize map and set default location
        initMap();
    }

    private void initTracker() {
        // TODO: Respect settings for operation mode
        bleTracker = new BleTracker(this, true, new BeaconStateNotifier() {
            @Override
            public void onUpdate(ArrayList<SimpleBeacon> beacons) {

            }

            @Override
            public void onBeaconNearby() {
                if (!haveDetectedBeaconsSinceBoot) {
                    Log.d(TAG, "auto launching MainActivity");

                    // The very first time since boot that we detect an beacon, we launch the
                    // MainActivity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Important:  make sure to add android:launchMode="singleInstance" in the manifest
                    // to keep multiple copies of this activity from getting created if the user has
                    // already manually launched the app.
                    MainActivity.this.startActivity(intent);
                    haveDetectedBeaconsSinceBoot = true;
                }
                sendNotification();
            }
        });
        try {
            Notification notification = ForegroundNotification.create(this, R.drawable.ic_stat_name, DEFAULT_NOTIFICATION_CHANNEL_ID);
            bleTracker.startForegroundService(notification);

        } catch (ServiceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }


    private void initMap()
    {
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        map.setMultiTouchControls(true);

        //Building level zoom
        final IMapController mapController = map.getController();
        mapController.setZoom(DEFAULT_ZOOM_LEVEL);

        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(this);
        locationProvider.addLocationSource(LocationManager.PASSIVE_PROVIDER);
        myLocationOverlay = new MyLocationNewOverlay(locationProvider, map);
        myLocationOverlay.setDrawAccuracyEnabled(true);
//        DrawableConverter converter = new DrawableConverter(this);
//        Bitmap icon = converter.toBitmap(getResources().getDrawable(R.drawable.ic_my_location_icon), 36, 36);
//        myLocationOverlay.setPersonIcon(icon);
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);

        // Add compass
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // Button to enable follow me
        btnFollowMe = findViewById(R.id.ic_gps_fixed);
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
        beaconsOverlay= new ItemizedOverlayWithFocus<>(items,this,this);

        beaconsOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(beaconsOverlay);

        loadBeacons();

    }


    private void addBeaconToOverlay(SimpleBeacon beacon)
    {
        beaconsOverlay.addItem(new OverlayItem("Beacon", beacon.getUuid(), new GeoPoint(beacon.getLocationLat(), beacon.getLocationLong())));
    }

    private void loadBeacons()
    {
        bleTracker.getCispaConnection().requestBeacons(new RemoteReceiver() {
            @Override
            public void onBeaconReceive(SimpleBeacon[] beacons) {
                for (SimpleBeacon beacon:beacons) {
                    addBeaconToOverlay(beacon);
                }
            }

            @Override
            public void onBeaconReceiveError() {
                Log.d("API","Failed to receiver beacons from api");
            }
        });
    }

    @Override
    public boolean onItemSingleTapUp(int index, Object item) {
        //Show strength of beacon or something
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, Object item) {
        //Show detailed information?
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getTitle())
                .setContentText("Beacon nearby!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reports) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_info) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
