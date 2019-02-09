package saarland.cispa.trackblebeacons.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class MapHelper {

    private static MapHelper mapHelper;
    private  MapTileProviderBase mapTileProviderBase;
    private CacheManager cacheManager;
    private Context activity;
    private IConfigurationProvider configuration;

    private static final int ZOOM_MIN = 1;
    private static final int ZOOM_MAX = 18;

    public static MapHelper getInstance() {
        if (mapHelper == null) {
            mapHelper = new MapHelper();
        }
        return mapHelper;
    }

    public void InitMapHelper(Activity activity, MapView map) {
        this.activity = activity;
        this.cacheManager = new CacheManager(map);
        this.mapTileProviderBase = map.getTileProvider();
        this.configuration = Configuration.getInstance();
        configuration.setExpirationOverrideDuration(1000L*60L*60L*24L*7L*4L); // Save tiles for 4 weeks

        configuration.setCacheMapTileOvershoot((short) 256); // Increase the in memory cache
        configuration.setCacheMapTileCount((short) 256); // Increase the in memory cache
    }

    public boolean isMapOnline()
    {
        return mapTileProviderBase.useDataConnection();
    }

    public void setMapOnline(boolean online)
    {
        mapTileProviderBase.setUseDataConnection(online);
        SaveSettings();
    }

    public void LoadSettings()
    {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(activity);
        setMapOnline(prefManager.getBoolean("mapOnline", true));


    }
    public void SaveSettings()
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        e.putBoolean("mapOnline", isMapOnline());

        e.apply();
    }

    /**
     * Downloads the corresponding area around your gps location
     * @param range the area in m
     * @return false if no gps is available
     */
    public boolean dlSurroundingMapArea(float range)
    {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            dlMapArea(location.getLatitude(), location.getLongitude(),range);
            return true;
        }
        return false;
    }


    /**
     * Donloads the corresponding area around a specified location
     * @param alat the latitude
     * @param along the longitude
     * @param range the area in m
     */
    public void dlMapArea(double alat, double along, float range)
    {
        BoundingBox boundingBox = DistanceCalculator.createBoundingBox(alat, along, range);

        int possibleTiles = cacheManager.possibleTilesInArea(boundingBox, ZOOM_MIN, ZOOM_MAX);
        cacheManager.downloadAreaAsync(activity,boundingBox, ZOOM_MIN, ZOOM_MAX,new CacheManager.CacheManagerCallback() {
            private Toast progressToast = null;
            @Override
            public void onTaskComplete() {
                Toast.makeText(activity, "Download complete!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onTaskFailed(int errors) {
                Toast.makeText(activity, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();

            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                if(progressToast == null)
                    progressToast =  Toast.makeText(activity, "", Toast.LENGTH_LONG);

                progressToast.setText("Download Progress " + progress + "/" + possibleTiles);
                progressToast.show();

            }

            @Override
            public void downloadStarted() {

            }

            @Override
            public void setPossibleTilesInArea(int total) {

            }});
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}
