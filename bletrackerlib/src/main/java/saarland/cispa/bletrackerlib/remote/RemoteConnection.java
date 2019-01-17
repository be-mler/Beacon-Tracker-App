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

import java.util.LinkedList;
import java.util.List;

import saarland.cispa.bletrackerlib.data.SimpleBeacon;

public class RemoteConnection {

    private String url;
    private final Context context;
    private final RequestQueue queue;
    private boolean sendOnlyWithGpsCoords;

    public RemoteConnection(String url, Context context, boolean sendOnlyWithGpsCoords) {
        this.url = url;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        this.sendOnlyWithGpsCoords = sendOnlyWithGpsCoords;
    }

    public void requestBeacons(final RemoteReceiver receiver,double longS,double longE,double latS,double latE)
    {
        String apiUrl = String.format("%s/%d/%f/%f/%f/%f", url, RemoteSettings.GetConfirmations(),longS,longE,latS,latE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        RemoteBeaconObject[] rcvBeacons = new Gson().fromJson(response, RemoteBeaconObject[].class);
                        LinkedList<SimpleBeacon> rcConvert = new LinkedList<>();
                        for (RemoteBeaconObject rmt: rcvBeacons
                             ) {
                            rcConvert.add(rmt.GetSimpleBeacon());

                        }
                        SimpleBeacon[] parsedbeacons = new SimpleBeacon[rcConvert.size()];
                        parsedbeacons = rcConvert.toArray(parsedbeacons);
                        receiver.onBeaconReceive(parsedbeacons);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                receiver.onBeaconReceiveError();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void sendBeacon(SimpleBeacon simpleBeacon)
    {
        JSONObject beaconAsJson = null;
        try {
            beaconAsJson = new JSONObject(new Gson().toJson(new RemoteBeaconObject(simpleBeacon)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,beaconAsJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Give user feedback of successfull submission?
                    Log.d("BEACON","SENDBEACON");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d("BEACON","SENDBEACON_ERROR");
            }
        });
        queue.add(jsonObjectRequest);
    }





    public void send(SimpleBeacon simpleBeacon) {
        if (sendOnlyWithGpsCoords) {
            if (simpleBeacon.location != null) {
                sendBeacon(simpleBeacon);
            }
        } else {
            sendBeacon(simpleBeacon);
        }
    }

    public void sendAll(List<SimpleBeacon> simpleBeacons) {
        for (SimpleBeacon simpleBeacon : simpleBeacons) {
            send(simpleBeacon);
        }
    }
}
