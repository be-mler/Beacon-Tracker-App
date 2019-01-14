package saarland.cispa.trackblebeacons;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;

public class ScanFragment extends Fragment {

    private View rootView = null;
    private RecyclerView recyclerView;
    private BeaconRecyclerViewAdapter adapter;
    private BleTracker bleTracker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        // 1. get a reference to recyclerView
        recyclerView = rootView.findViewById(R.id.beacons_rv);
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
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.submitList(beaconList);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bleTracker = ((MainActivity)getActivity()).getBleTracker();

        showEmptyView(false);

        bleTracker.addNotifier(new BeaconStateNotifier() {
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
        rootView.findViewById(R.id.empty_view).setVisibility(show ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.beacons_rv).setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
