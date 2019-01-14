package saarland.cispa.trackblebeacons.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean swipingEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setSwipingEnabled(boolean enabled) {
        this.swipingEnabled = enabled;
    } 
}