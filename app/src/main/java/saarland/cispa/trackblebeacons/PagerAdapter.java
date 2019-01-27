package saarland.cispa.trackblebeacons;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;
    private FragmentManager fm;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.fm = fm;
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new NearbyFragment();
            case 2:
                return new ScanFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}