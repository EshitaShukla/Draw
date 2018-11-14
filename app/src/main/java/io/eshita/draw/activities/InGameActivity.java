package io.eshita.draw.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rm.freedrawview.FreeDrawView;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;
import com.rm.freedrawview.ResizeBehaviour;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.eshita.draw.R;
import io.eshita.draw.classifier.ImageClassifier;
import io.eshita.draw.misc.Vars;
import io.eshita.draw.models.Recognition;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InGameActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Recognition> recognitionList;
    private ImageClassifier imageClassifier;
    private String toBePredicted;
    private TextToSpeech tts;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Constructor for activity
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

        setContentView(R.layout.activity_in_game);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the intent, data(bundle)
        Intent prev = getIntent();
        Bundle b = prev.getExtras();

        // Text to Speech
        tts = new TextToSpeech(this, this);
        TextView displayText = (TextView) findViewById(R.id.predictionStringView);
        if (b != null) {
            this.toBePredicted = b.getString("givenLabel");
        } else {
            this.toBePredicted = "None";
        }

        // Shows what is to be drawn
        final String drawText = "Draw " + toBePredicted;
        displayText.setText(drawText);
        init();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/mono_light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        final int[] time = {25};

        final CountDownTimer timer = new CountDownTimer(25000, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView textTimer = (TextView) findViewById(R.id.timerView);
                String set = "00:" + checkDigit(time[0]);
                textTimer.setText(set);
                time[0]--;
            }

            public void onFinish() {
                TextView textTimer = (TextView) findViewById(R.id.timerView);
                String time_up = "Time's UP!";
                textTimer.setText(time_up);
                BeginGame.gh.getRandomLabel();
                if (Vars.timesPlayed == 8) {
                    Intent go = new Intent(InGameActivity.this, GameOverActivity.class);
                    startActivity(go);
                } else {
                    finish();
                }
            }

        };

        timer.start();

        final FreeDrawView mSignatureView = (FreeDrawView) findViewById(R.id.your_id);

        mSignatureView.setPaintColor(Color.BLACK);
        mSignatureView.setPaintWidthPx(getResources().getDimensionPixelSize(R.dimen.paint_width));
        mSignatureView.setPaintWidthDp(getResources().getDimension(R.dimen.paint_width));
        mSignatureView.setPaintAlpha(255);// from 0 to 255
        mSignatureView.setResizeBehaviour(ResizeBehaviour.CROP);// Must be one of ResizeBehaviour
        mSignatureView.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {
            @Override
            public void onUndoCountChanged(int undoCount) {
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
            }
        });

        // This listener will be notified every time a new path has been drawn
        mSignatureView.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onNewPathDrawn() {
                final FreeDrawView sigView = mSignatureView;
                sigView.getDrawScreenshot(new FreeDrawView.DrawCreatorListener() {
                    @Override
                    public void onDrawCreated(Bitmap draw) {
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                                draw, ImageClassifier.INPUT_SIZE, ImageClassifier.INPUT_SIZE, false);
                        recognitionList = imageClassifier.recognizeImage(resizedBitmap);
                        String sen = sayItOut();
                        TextView displayText = (TextView) findViewById(R.id.predictionStringView);
                        displayText.setText(sen);
                        speakOut(sen);
                        if (toBePredicted.equals(String.valueOf(recognitionList.get(0)))) {
                            Vars.correctGuesses++;
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    BeginGame.gh.getRandomLabel();
                                    timer.cancel();
                                    if (Vars.timesPlayed == 8) {
                                        Intent go = new Intent(InGameActivity.this, GameOverActivity.class);
                                        startActivity(go);
                                    } else {
                                        finish();
                                    }
                                    //Intent goBack = new Intent(InGameActivity.this, BeginGame.class);
                                    //startActivity(goBack);
                                }
                            }, 3000);
                        }
                    }

                    @Override
                    public void onDrawCreationError() {
                        Toast.makeText(InGameActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPathStart() {
                // The user has started drawing a path
            }
        });

        Button clear = (Button) findViewById(R.id.btn_clear_all);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FreeDrawView sigView = mSignatureView;
                sigView.clearDrawAndHistory();
                TextView displayText = (TextView) findViewById(R.id.predictionStringView);
                displayText.setText(drawText);
            }
        });

        clear.performClick();
        Button undo = (Button) findViewById(R.id.btn_undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FreeDrawView sigView = mSignatureView;
                sigView.undoLast();
            }
        });

        Button redo = (Button) findViewById(R.id.btn_redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FreeDrawView sigView = mSignatureView;
                sigView.redoLast();
            }
        });
    }

    public void init() {
        imageClassifier = new ImageClassifier(InGameActivity.this);
    }

    private String sayItOut() {

        String[] patterns = {"I see ", "Oh I know this. This is ", "Umm, this looks like ", "Kind of looks like ", "Is this ", "Is this really how you draw ", "This looks awful, but it may be "};
        Random generator = new Random();
        int rnd_index = generator.nextInt(patterns.length);
        String sentence_select = patterns[rnd_index];
        String sentence = sentence_select + recognitionList.get(0);
        return sentence;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (Vars.timesPlayed == 8) {
            Intent go = new Intent(InGameActivity.this, GameOverActivity.class);
            startActivity(go);
        } else {
            finish();
        }
        super.onDestroy();
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
