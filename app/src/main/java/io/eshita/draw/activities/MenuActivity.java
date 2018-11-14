package io.eshita.draw.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.eshita.draw.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    // A constructor for activities, whenever they are created (when you start the intent), it runs
    @Override
    protected void onCreate(Bundle savedInstanceState) {        // Data is sent in a bundle (between the two intents)
        super.onCreate(savedInstanceState);

        // this is for getting the activity to be fullscreen, no actionbar, no navbar and no statusbar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // set content for activity from layout
        setContentView(R.layout.activity_menu);

        // using calligraphy library for custom fonts, so need to build it in here
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/marker.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        // for the small animation on the menu screen  (.json form in assets/animations)
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.menuAnimation);
        animationView.setAnimation("animations/categories.json");
        animationView.loop(true);
        animationView.playAnimation();

        // for realtime stats (by Google)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // for checking if its the first run, if it is, open the intro activity
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {

                    // ****** Used to start a new activity     [ current activity (context) and target activity ]
                    Intent i = new Intent(MenuActivity.this, IntroActivity.class);
                    startActivity(i);

                    // for accessing local app storage
                    // stuff that doesnt get removed from memory after exiting app
                    SharedPreferences.Editor e = getPrefs.edit();
                    // e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

        // start game
        Button startGame = (Button) findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent begin = new Intent(MenuActivity.this, BeginGame.class);
                startActivity(begin);
            }
        });

        // share game (simple external text intent), will send shareBody text to other apps, like whatsapp
        Button shareGame = (Button) findViewById(R.id.shareGame);
        shareGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Hey! Check out this new game! It tries to guess your doodles using an inbuilt neural network!";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rapid Draw");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using.."));
            }
        });

        // settings
        Button settingsGame = (Button) findViewById(R.id.settingsGame);
        settingsGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
            }
        });

        // exit game
        Button exitGame = (Button) findViewById(R.id.exitGame);
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmExit();
            }
        });
    }

    // exit game dialog
    private void confirmExit() {
        new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to close ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MenuActivity.this.finish();
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    // Used to program the back button (at the bottom of the screen)
    @Override
    public void onBackPressed() {
        confirmExit();
        //super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
