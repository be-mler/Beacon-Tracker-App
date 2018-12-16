package saarland.cispa.bletrackerlib.remote;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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

    public void requestBeacons(final RemoteReceiver receiver)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        SimpleBeacon[] rcvBeacons = new Gson().fromJson(response, SimpleBeacon[].class);
                        receiver.onBeaconReceive(rcvBeacons);
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

    public void send(SimpleBeacon simpleBeacon) {
        if (sendOnlyWithGpsCoords) {
            if (simpleBeacon.isLocationSet()) {
                //TODO: sned
            }
        } else {
            //TODO: send
        }
    }

    public void sendAll(List<SimpleBeacon> simpleBeacons) {
        for (SimpleBeacon simpleBeacon : simpleBeacons) {
            if (sendOnlyWithGpsCoords) {
                if (simpleBeacon.isLocationSet()) {
                    //TODO: sned
                }
            } else {
                //TODO: send
            }
        }
    }
}
