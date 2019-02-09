package saarland.cispa.trackblebeacons.helpers;

import org.osmdroid.util.BoundingBox;

public class DistanceCalculator {

    public static final int EARTH_RADIUS = 6378137;

    public static float distanceBetween(double longitude1, double latitude1, double longitude2, double latitude2) {

        double dLat = Math.toRadians(latitude2-latitude1);
        double dLng = Math.toRadians(longitude2-longitude1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (EARTH_RADIUS * c);

        return dist;
    }

    /**
     * Creates a bounding box of a coordinate and a range in m
     * @param latitude the latitude
     * @param longitude the longitude
     * @param radius the radius in m
     * @return a BoundingBox
     */
    public static BoundingBox createBoundingBox(double latitude, double longitude, double radius) {
        double latTopLeft = latitude + (180/Math.PI)*(radius/2/EARTH_RADIUS);
        double lonTopLeft = longitude + (180/Math.PI)*(radius/2/EARTH_RADIUS)/Math.cos(Math.PI/180.0*latitude);

        double latBotRight = latitude - (180/Math.PI)*(radius/2/EARTH_RADIUS);
        double lonBotRight = longitude - (180/Math.PI)*(radius/2/EARTH_RADIUS)/Math.cos(Math.PI/180.0*latitude);

        return new BoundingBox(lonTopLeft, latBotRight, lonBotRight, latTopLeft);
    }

}
