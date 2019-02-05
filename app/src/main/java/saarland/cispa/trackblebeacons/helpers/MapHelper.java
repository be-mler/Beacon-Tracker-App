package saarland.cispa.trackblebeacons.helpers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;



import static androidx.core.content.ContextCompat.checkSelfPermission;

public class MapHelper {

    private static MapHelper mapHelper;
    private  MapTileProviderBase mapTileProviderBase;
    private CacheManager cacheManager;
    private Context context;


    public static MapHelper getInstance() {
        if (mapHelper == null) {
            mapHelper = new MapHelper();
            Configuration.getInstance().setTileFileSystemCacheMaxBytes(1024L*1024L*1024L * 3L);

        }

        return mapHelper;
    }

    public void InitMapHelper(Context context ,MapTileProviderBase mapTileProviderBase, CacheManager cacheManager)
    {

        this.context = context;
        this.cacheManager = cacheManager;
        this.mapTileProviderBase = mapTileProviderBase;
        mapHelper.LoadSettings();


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
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        setMapOnline(prefManager.getBoolean("mapOnline", true));


    }
    public void SaveSettings()
    {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putBoolean("mapOnline", isMapOnline());

        e.apply();
    }

    public boolean dlSurroundingMapArea(double range)
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            dlMapArea(location.getLatitude(), location.getLongitude(),range);

        }else
        {
            return false;
        }
        return true;
    }


    public void dlMapArea(double alat, double along, double range)
    {

        int zoomMin = 1;
        int zoomMax = 13;


        GeoPoint tl = new GeoPoint(alat - range /2,along - range /2);
        GeoPoint br = new GeoPoint(alat + range /2,along - range /2);
        BoundingBox boundingBox = new BoundingBox(tl.getLatitude(),tl.getLongitude(),br.getLatitude(),br.getLongitude());


        int possibleTiles = cacheManager.possibleTilesInArea(boundingBox,zoomMin,zoomMax);
        cacheManager.downloadAreaAsyncNoUI(context,boundingBox,zoomMin,zoomMax,new CacheManager.CacheManagerCallback() {
            private Toast progressToast = null;
            @Override
            public void onTaskComplete() {
                Toast.makeText(context, "Download complete!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onTaskFailed(int errors) {
                Toast.makeText(context, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();

            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                if(progressToast == null)
                    progressToast =  Toast.makeText(context, "", Toast.LENGTH_LONG);

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
}
