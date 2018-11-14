package io.eshita.draw.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.eshita.draw.R;
import io.eshita.draw.fragments.SlideFive;
import io.eshita.draw.fragments.SlideFour;
import io.eshita.draw.fragments.SlideOne;
import io.eshita.draw.fragments.SlideThree;
import io.eshita.draw.fragments.SlideTwo;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppIntro {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // You can edit these layout files for changing text / color / fonts / anything
        addSlide(SlideOne.newInstance(R.layout.slide_one));
        addSlide(SlideTwo.newInstance(R.layout.slide_two));
        addSlide(SlideThree.newInstance(R.layout.slide_three));
        addSlide(SlideFour.newInstance(R.layout.slide_four));
        addSlide(SlideFive.newInstance(R.layout.slide_five));

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/mono_light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //setSeparatorColor(Color.parseColor("#2196F3"));
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent login = new Intent(IntroActivity.this, MenuActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
