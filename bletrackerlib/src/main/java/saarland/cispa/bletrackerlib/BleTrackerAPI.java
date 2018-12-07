package saarland.cispa.bletrackerlib;

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

public class BleTrackerAPI {

    private String url;
    RequestQueue queue ;
    ;

    public BleTrackerAPI(String url,Context context) {
        this.url = url;
        queue = Volley.newRequestQueue(context);
    }

    public void RequestBeacons()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        SimpleBeacon[] rcvBeacons = new Gson().fromJson(response, SimpleBeacon[].class);
                        onBeaconReceive(rcvBeacons);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBeaconReceiveError();
            }
        });

    // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    public void onBeaconReceive(SimpleBeacon[] beacons)
    {
        //Override pls
    }

    public void onBeaconReceiveError()
    {
        //Override pls
    }


}
