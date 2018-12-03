package saarland.cispa.trackblebeacons;

import android.Manifest;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;


public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slider Page 1
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("BLE Beacon Tracker");
        sliderPage1.setDescription("We started a project to find out where Bluetooth Low Energy Beacons are tracking people in real live. We are happy that you support us.");
        sliderPage1.setImageDrawable(R.drawable.ic_intro_speaker_phone);
        sliderPage1.setBgColor(ResourcesCompat.getColor(getResources(), R.color.primaryColor, null));

        // Slider Page 2
        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Bluetooth Access");
        sliderPage2.setDescription("That we can track BLE Beacons we need to turn Bluetooth on.");
        sliderPage2.setImageDrawable(R.drawable.ic_intro_bluetooth_searching);
        sliderPage2.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introBluetooth, null));
        // Slider Page 3
        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("GPS Access");
        sliderPage3.setDescription("We also need location access to save the information where you have found a beacon. We only turn on location if you have found a tracker to save battery.");
        sliderPage3.setImageDrawable(R.drawable.ic_intro_location_on);
        sliderPage3.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introLocation, null));

        // Slider Page 4
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Storage Access");
        sliderPage4.setDescription("At last we need storage access to save the map data locally which is good for your mobile data.");
        sliderPage4.setImageDrawable(R.drawable.ic_intro_sd_storage);
        sliderPage4.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introStorage, null));

        // Slider Page 5
        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("Finished");
        sliderPage5.setDescription("If you want you can specify the app behavior in settings.");
        sliderPage5.setImageDrawable(R.drawable.ic_intro_settings);
        sliderPage5.setBgColor(ResourcesCompat.getColor(getResources(), R.color.secondaryColor, null));


        addSlide(AppIntroFragment.newInstance(sliderPage1));
        addSlide(AppIntroFragment.newInstance(sliderPage2));
        addSlide(AppIntroFragment.newInstance(sliderPage3));
        addSlide(AppIntroFragment.newInstance(sliderPage4));
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);

        setColorTransitionsEnabled(true);
        showSkipButton(false);

    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        finish();
    }
}