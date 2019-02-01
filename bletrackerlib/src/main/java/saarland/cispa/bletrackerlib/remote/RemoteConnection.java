package saarland.cispa.bletrackerlib.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.parser.DateParser;

public class RemoteConnection {

    private String url;
    private final RequestQueue queue;
    private final SendMode sendMode;
    private Map<Integer,String> sentBeacons = new HashMap<>();

    private ArrayList<RemoteRequestReceiver> remoteRequestReceivers = new ArrayList<>();

    private static final String TAG = "RemoteConnection";

    /**
     * Creates a new connection to an RESTful endpoint
     * @param url the URL
     * @param context the application context
     * @param sendMode the send mode. This specifies how sendBeacons() and sendAllBeacons() will behave
     */
    public RemoteConnection(String url, Context context, SendMode sendMode) {
        this.url = url;
        this.sendMode = sendMode;
        queue = Volley.newRequestQueue(context);
    }

    /**
     * Request beacons in the specified range.
     * You get the response sent to all RemoteRequestReceiver callbacks you have registered.
     * @param longitudeStart the longitude start coordinate
     * @param longitudeEnd the longitude end coordinate
     * @param latitudeStart the latitude start coordinate
     * @param latitudeEnd the latitude end coordinate
     */
    public void requestBeacons(double longitudeStart, double longitudeEnd, double latitudeStart, double latitudeEnd) {
        request(longitudeStart, longitudeEnd, latitudeStart, latitudeEnd, remoteRequestReceivers);
    }

    /**
     * Request beacons in the specified range
     * You get the response sent ONLY to the RemoteRequestReceiver you have passed as argument!
     * @param longitudeStart the longitude start coordinate
     * @param longitudeEnd the longitude end coordinate
     * @param latitudeStart the latitude start coordinate
     * @param latitudeEnd the latitude end coordinate
     * @param receiver the callback which receives the requested beacons
     */
    public void requestBeacons(double longitudeStart, double longitudeEnd, double latitudeStart, double latitudeEnd, final RemoteRequestReceiver receiver)
    {
        ArrayList<RemoteRequestReceiver> dummyList = new ArrayList<>();
        dummyList.add(receiver);
        request(longitudeStart, longitudeEnd, latitudeStart, latitudeEnd, dummyList);
    }

    private void request(double longS, double longE, double latS, double latE, final ArrayList<RemoteRequestReceiver> receivers) {
        String apiUrl = String.format("%s/%d/%f/%f/%f/%f", url, RemoteSettings.GetConfirmations(), longS, longE, latS, latE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                response -> {

                    RemoteBeaconObject[] rcvBeacons = new Gson().fromJson(response, RemoteBeaconObject[].class);
                    ArrayList<SimpleBeacon> simpleBeacons = new ArrayList<>();
                    for (RemoteBeaconObject rmt: rcvBeacons) {
                        simpleBeacons.add(rmt.GetSimpleBeacon());
                    }
                    for (RemoteRequestReceiver receiver : receivers) {
                        receiver.onBeaconsReceived(simpleBeacons);
                    }
                }, error -> {
                    for (RemoteRequestReceiver receiver : receivers) {
                        receiver.onBeaconReceiveError(error.getMessage());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void send(SimpleBeacon simpleBeacon)
    {
        JSONObject beaconAsJson = null;
        try {
            beaconAsJson = new JSONObject(new Gson().toJson(new RemoteBeaconObject(simpleBeacon)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,beaconAsJson,
                response -> {
                    //TODO: Give user feedback of successfull submission?
                Log.d(TAG,"send successful");
                }, error -> {
                    // TODO: Handle error
                    Log.d(TAG,"send error: "+error.getMessage());
                });
        queue.add(jsonObjectRequest);
    }

    /**
     * Send a beacon.
     * Be aware that this beacon only gets send if SendMode allows this.
     * @param simpleBeacon the beacon to send
     */
    public void sendBeacon(SimpleBeacon simpleBeacon) {
        if(sentBeacons.containsKey(simpleBeacon.hashcode))
        {


                Date lastSend = DateParser.stringDateToDate(sentBeacons.get(simpleBeacon.hashcode));
                Date currentSend = DateParser.stringDateToDate(simpleBeacon.timestamp);
                long timediff = currentSend.getTime() - lastSend.getTime();
                if(timediff < 15 * 1000)
                    return;



        }
        sentBeacons.put(simpleBeacon.hashcode,simpleBeacon.timestamp);




        switch (sendMode) {
            case DO_SEND_BEACONS:
                send(simpleBeacon);
                break;

            case DO_ONLY_SEND_IF_BEACONS_HAVE_GPS:
                if (simpleBeacon.location != null) {
                    send(simpleBeacon);
                }
                break;
            case DO_NOT_SEND_BEACONS:
                break;
            default:
                break;
        }
    }

    /**
     * Send all beacons.
     * Be aware that they only get send if SendMode allows this.
     * @param simpleBeacons the beacons to send
     */
    public void sendAllBeacons(List<SimpleBeacon> simpleBeacons) {
        for (SimpleBeacon simpleBeacon : simpleBeacons) {
            sendBeacon(simpleBeacon);
        }
    }

    /**
     * Adds a callback to callback list which gets fired if beacons are received.
     * @param callback the callback
     */
    public void addRemoteReceiver(RemoteRequestReceiver callback) {
        remoteRequestReceivers.add(callback);
    }

    /**
     * Removes this callback from list.
     * @param callback the callback
     * @return true if it was successful
     */
    public boolean removeRemoteReceiver(RemoteRequestReceiver callback) {
        return remoteRequestReceivers.remove(callback);
    }

    /**
     * Clears the callback list.
     */
    public void clearRemoteReceivers() {
        remoteRequestReceivers.clear();
    }
}
