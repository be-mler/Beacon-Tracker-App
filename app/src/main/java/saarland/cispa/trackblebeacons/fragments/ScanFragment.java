package saarland.cispa.trackblebeacons.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.ServiceNotifier;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.service.BeaconNotifier;
import saarland.cispa.trackblebeacons.activities.BeaconRecyclerViewAdapter;
import saarland.cispa.trackblebeacons.R;

/**
 * Fragment for the scan view
 */
public class ScanFragment extends Fragment {

    private View rootView = null;
    private RecyclerView recyclerView;
    private BeaconRecyclerViewAdapter adapter;
    private BleTracker bleTracker;


    /**
     * Setting up the recycler view: Setting source list and components

     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        // 1. get a reference to recyclerView
        recyclerView = rootView.findViewById(R.id.beacons_scan_rv);
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

        adapter.submitList(beaconList);

        return rootView;
    }

    /**
     * Adding listener to the BleTracker and update the list in case of changes reported by the lib.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bleTracker = BleTracker.getInstance();
        if (bleTracker.isRunning()) {
            showEmptyView(false);
        } else {
            showEmptyView(true);
        }

        bleTracker.addServiceNotifier(new ServiceNotifier() {
            @Override
            public void onStop() {
                showEmptyView(true);
            }

            @Override
            public void onStart() {
                showEmptyView(false);
            }
        });

        bleTracker.addBeaconNotifier(new BeaconNotifier() {
            @Override
            public void onUpdate(ArrayList<SimpleBeacon> beacons) {
                adapter.submitList(beacons);
            }

            @Override
            public void onBeaconNearby() {

            }
        });
    }

    public void showEmptyView(boolean show) {
        rootView.findViewById(R.id.empty_scan_view).setVisibility(show ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.beacons_scan_rv).setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
