package saarland.cispa.trackblebeacons;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import saarland.cispa.bletrackerlib.BleTracker;
import saarland.cispa.bletrackerlib.exceptions.OtherServiceStillRunningException;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.service.BeaconStateNotifier;
import saarland.cispa.bletrackerlib.service.ForegroundNotification;
import saarland.cispa.trackblebeacons.helpers.CustomViewPager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BleTracker bleTracker;

    private boolean haveDetectedBeaconsSinceBoot = false;
    private String DEFAULT_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOTIFICATION_CHANNEL_ID";
    private final String FIRST_START_PROPERTY_KEY = "firstStart";
    private PagerAdapter pagerAdapter;

    private CustomViewPager mViewPager;
    private void showIntroAtFirstStart() {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean(FIRST_START_PROPERTY_KEY, true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

                    //  Make a new Preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        showIntroAtFirstStart();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 2);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setSwipingEnabled(false);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        initTracker();
    }


    private void initTracker() {
        bleTracker = new BleTracker(this, false);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final Animation animation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(2000);
        if (bleTracker.isRunning()) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_stop));
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.scanningRed)));
            //fab.setAnimation(animation);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bleTracker.isRunning()) {
                    Notification notification = ForegroundNotification.create(getApplicationContext(), R.drawable.ic_stat_name, MainActivity.class);
                    try {
                        bleTracker.createForegroundService(notification);
                    } catch (OtherServiceStillRunningException e) {
                        e.printStackTrace();
                    }
                    bleTracker.start();
                    Snackbar.make(view, "Started scanning for beacons", Snackbar.LENGTH_LONG).show();
                    fab.setImageDrawable(getDrawable(R.drawable.ic_stop));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.scanningRed)));
                    //fab.setAnimation(animation);
                } else {
                    bleTracker.stop();
                    Snackbar.make(view, "Stopped scanning for beacons", Snackbar.LENGTH_LONG).show();
                    fab.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.scanningGreen)));
                    //fab.setAnimation(null);
                }
            }
        });

        // TODO: Respect settings for operation mode
        bleTracker.addNotifier(new BeaconStateNotifier() {
            @Override
            public void onUpdate(ArrayList<SimpleBeacon> beacons) {

            }

            @Override
            public void onBeaconNearby() {
                if (!haveDetectedBeaconsSinceBoot) {
                    Log.d(TAG, "auto launching MainActivity");
                    // The very first time since boot that we detect an beacon, we launch the
                    // MainActivity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Important:  make sure to add android:launchMode="singleInstance" in the manifest
                    // to keep multiple copies of this activity from getting created if the user has
                    // already manually launched the app.
                    MainActivity.this.startActivity(intent);
                    haveDetectedBeaconsSinceBoot = true;
                }
                sendNotification();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getTitle())
                .setContentText("Beacon nearby!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bleTracker.setContext(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bleTracker.setContext(this);
    }

    public BleTracker getBleTracker() {
        return bleTracker;
    }
}
