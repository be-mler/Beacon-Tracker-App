package saarland.cispa.trackblebeacons;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.helper.LocationHelper;
import saarland.cispa.bletrackerlib.remote.RemoteRequestReceiver;
import saarland.cispa.trackblebeacons.helpers.DistanceCalculator;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class NearbyFragment extends Fragment {
    private View rootView = null;
    private RecyclerView recyclerView;
    private BeaconRecyclerViewAdapter adapter;
    private BleTracker bleTracker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nearby, container, false);

        // 1. get a reference to recyclerView
        recyclerView = rootView.findViewById(R.id.beacons_nearby_rv);
        recyclerView.setHasFixedSize(true);

        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data for recycler view
        List<SimpleBeacon> beaconList = new ArrayList<>();

        // 3. parse an adapter
        adapter = new BeaconRecyclerViewAdapter(getContext(), new BeaconRecyclerViewAdapter.OnControlsOpen() {
            @Override
            public void onControlsOpen(SimpleBeacon beacon) {

            }
        });
        // 4. set adapter
        recyclerView.setAdapter(adapter);
        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

            }
        });
        // 5. disable annoying item refresh animation
        recyclerView.setItemAnimator(null);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bleTracker = BleTracker.getInstance();

        initRemoteReceiver();

        showEmptyView(true);
    }

    public void showEmptyView(boolean show) {
        rootView.findViewById(R.id.empty_nearby_view).setVisibility(show ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.beacons_nearby_rv).setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void initRemoteReceiver()
    {
        RemoteRequestReceiver receiver = new RemoteRequestReceiver() {
            @Override
            public void onBeaconsReceived(ArrayList<SimpleBeacon> beacons) {
                // remove all beacons without gps from list
                ListIterator<SimpleBeacon> iterator = beacons.listIterator();
                while (iterator.hasNext()) {
                    SimpleBeacon beacon = iterator.next();
                    if (beacon.location == null) {
                        iterator.remove();
                    } else {
                        modifyDistance(beacon);
                    }
                }
                // show empty view if there are no beacons in list
                showEmptyView(beacons.size() == 0);
                adapter.submitList(beacons);
            }

            @Override
            public void onBeaconReceiveError(String errorMessage) {
                Log.d("API","Failed to receiver beacons from api");
            }
        };
        bleTracker.getCispaConnection().addRemoteReceiver(receiver);
    }

    private void modifyDistance(SimpleBeacon beacon) {
        float distance = -1;
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (LocationHelper.isGpsOn(getContext()) && beacon.location != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    distance = DistanceCalculator.distanceBetween(location.getLongitude(), location.getLatitude(),
                            beacon.location.locationLong, beacon.location.locationLat);
                }
            }
        }
        beacon.distance = distance;
    }

}
