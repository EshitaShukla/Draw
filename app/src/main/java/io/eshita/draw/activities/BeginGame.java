package io.eshita.draw.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.eshita.draw.R;
import io.eshita.draw.helpers.GameHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BeginGame extends AppCompatActivity {

    public static GameHelper gh;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_begin_game);
        LinearLayout linearLayout = findViewById(R.id.beginActivityLinearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoInGameActivity();
            }
        });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        gh = new GameHelper(BeginGame.this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/marker.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        TextView newLabelView = (TextView) findViewById(R.id.newLabelView);
        String set = setNewLabel();
        newLabelView.setText(set);

        TextView displayNumber = (TextView) findViewById(R.id.drawingCount);
        String dis = "Drawing " + gh.getPlayNumber() + " / 8";
        displayNumber.setText(dis);

        Button startGame = (Button) findViewById(R.id.startGameBtn);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInGameActivity();
            }
        });
    }

    private void gotoInGameActivity() {
        gh.incrementPlayNumber();
        Intent startg = new Intent(BeginGame.this, InGameActivity.class);
        startg.putExtra("givenLabel", gh.getGivenLabel());
        startActivity(startg);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Intent restart = new Intent(BeginGame.this, BeginGame.class);
        startActivity(restart);
        finish();
    }

    private String setNewLabel() {
        String random_label = gh.getRandomLabel();
        String set = "Draw \n" + random_label + "\n in under 25 seconds";
        return set;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
