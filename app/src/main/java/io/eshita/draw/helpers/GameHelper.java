package io.eshita.draw.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

import io.eshita.draw.misc.Vars;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Aniruddh on 16-10-2017.
 */

public class GameHelper {

    private static final String LABEL_FILE = "retrained_labels.txt";
    private static final int CLASS_SIZE = 99;
    Context context;
    private int correctGuesses;
    private int totalGuesses;
    private String givenLabel;
    private Vector<String> labels;

    public GameHelper(Context context) {
        this.context = context;
        totalGuesses = 1;
        givenLabel = "Start";
        SharedPreferences.Editor editor = context.getSharedPreferences("InGame", MODE_PRIVATE).edit();
        editor.putInt("timesPlayed", totalGuesses);
        editor.apply();
        getLabels();

        if (Vars.timesPlayed > 8) {
            Vars.timesPlayed = 1;
            Vars.correctGuesses = 0;
            Vars.alreadyDone.clear();
        }
    }

    public String getRandomLabel() {
        Random generator = new Random();
        int rnd_index = generator.nextInt(labels.size());
        String label = labels.get(rnd_index);
        while (Vars.alreadyDone.contains(label)) {
            rnd_index = generator.nextInt(labels.size());
            label = labels.get(rnd_index);
        }
        givenLabel = label;
        return label;
    }


    public String getGivenLabel() {
        return this.givenLabel;
    }

    public void incrementPlayNumber() {
        Vars.timesPlayed++;
    }

    public int getPlayNumber() {
        return Vars.timesPlayed;
    }

    public void resetCount() {
        Vars.timesPlayed = 1;
    }

    public void setCorrectGuess() {
        Vars.correctGuesses++;
    }

    public int getCorrectGuesses() {
        return Vars.correctGuesses;
    }

    private void getLabels() {
        labels = new Vector<>(CLASS_SIZE);
        try {
            BufferedReader br = null;
            InputStream stream = context.getAssets().open(LABEL_FILE);
            br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }
    }
}
