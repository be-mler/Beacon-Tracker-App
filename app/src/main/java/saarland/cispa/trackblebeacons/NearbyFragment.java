package saarland.cispa.trackblebeacons;

import android.os.Bundle;
import android.util.Log;
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
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.remote.RemoteRequestReceiver;

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
                // show empty view if there are no beacons near
                showEmptyView(beacons.size() == 0);

                adapter.submitList(beacons);
            }

            @Override
            public void onBeaconReceiveError() {
                Log.d("API","Failed to receiver beacons from api");
            }
        };
        bleTracker.getCispaConnection().addRemoteReceiver(receiver);
    }
}
