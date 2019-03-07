package saarland.cispa.trackblebeacons.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import saarland.cispa.trackblebeacons.fragments.MapFragment;
import saarland.cispa.trackblebeacons.fragments.NearbyFragment;
import saarland.cispa.trackblebeacons.fragments.ScanFragment;

/**
 * Switching the pages (Nearby, Map, Scan)
 */
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