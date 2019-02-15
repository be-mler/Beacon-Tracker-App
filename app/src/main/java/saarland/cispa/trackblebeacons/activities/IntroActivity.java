package saarland.cispa.trackblebeacons.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import saarland.cispa.trackblebeacons.R;


public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slider Page 1
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getString(R.string.intro1_title));
        sliderPage1.setDescription(getString(R.string.intro1_desc));
        sliderPage1.setImageDrawable(R.drawable.ic_intro_speaker_phone);
        sliderPage1.setBgColor(ResourcesCompat.getColor(getResources(), R.color.primaryColor, null));

        // Slider Page 2
        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.intro2_title));
        sliderPage2.setDescription(getString(R.string.intro2_desc));
        sliderPage2.setImageDrawable(R.drawable.ic_intro_bluetooth_searching);
        sliderPage2.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introBluetooth, null));
        // Slider Page 3
        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.intro3_title));
        sliderPage3.setDescription(getString(R.string.intro3_desc));
        sliderPage3.setImageDrawable(R.drawable.ic_intro_location_on);
        sliderPage3.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introLocation, null));

        // Slider Page 4
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getString(R.string.intro4_title));
        sliderPage4.setDescription(getString(R.string.intro4_desc));
        sliderPage4.setImageDrawable(R.drawable.ic_intro_sd_storage);
        sliderPage4.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introStorage, null));

        // Slider Page 5
        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle(getString(R.string.intro5_title));
        sliderPage5.setDescription(getString(R.string.intro5_desc));
        sliderPage5.setImageDrawable(R.drawable.ic_intro_settings);
        sliderPage5.setBgColor(ResourcesCompat.getColor(getResources(), R.color.introFinish, null));


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
    public void onDonePressed(Fragment fragment) {
        super.onDonePressed(fragment);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //  Make a new Preferences editor
        SharedPreferences.Editor e = sharedPreferences.edit();

        //  Edit preference to make it false because we don't want this to run again
        e.putBoolean(MainActivity.FIRST_START_PROPERTY_KEY, false);

        //  Apply changes
        e.apply();

        finish();
    }
}