package com.example.dictionary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.dictionary.processor.WordsProcessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String filePath = "dictionary/dictionary.txt";

    private static final String pronounceDirectoryPath = "dictionary/pronounce/";

    private WordsProcessor wordsProcessor;

    private Word word;

    private WordViewMode mode = WordViewMode.WORD;

    private boolean randomWordsMode = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_main);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePath);

        wordsProcessor = new WordsProcessor(file);
        word  = wordsProcessor.getWord();
        playPronounce(word);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateView(word, mode);

        LinearLayout linearLayout = findViewById(R.id.wordLayout);
        linearLayout.setOnClickListener(view -> {
            if (mode == WordViewMode.WORD_EXPLAIN_TRANSLATE) {
                if (randomWordsMode) {
                    wordsProcessor.increaseWordTotalShows(word);
                }
                word = wordsProcessor.getWord();
                playPronounce(word);
            }

            mode = getNextMode(mode);

            updateView(word, mode);
        });

        Button yesButton = findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            if (randomWordsMode) {
                wordsProcessor.increaseWordRankAndTotalShows(word);
            }

            mode = WordViewMode.WORD;
            word = wordsProcessor.getWord();
            playPronounce(word);

            updateView(word, mode);
        });

        FloatingActionButton fab = findViewById(R.id.playPronounceButton);
        fab.setOnClickListener(view -> playPronounce(word));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Switch randomWordsSwitch = (Switch) menu.findItem(R.id.switch_button).getActionView();
        randomWordsSwitch.setChecked(true);
        randomWordsSwitch.setText(com.example.dictionary.R.string.random_words);

        randomWordsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            randomWordsMode = isChecked;
            wordsProcessor.setRandomMode(randomWordsMode);

            mode = WordViewMode.WORD;
            word = wordsProcessor.getWord();
            playPronounce(word);

            updateView(word, mode);
        });

        return true;
    }

    private void playPronounce(Word word) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pronounceDirectoryPath + word.getWord() + ".mp3");

        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                FileDescriptor fileDescriptor = fileInputStream.getFD();

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(fileDescriptor);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("File not found: " + word.getWord() + ".mp3");
            alertDialog.show();
        }
    }

    private void updateView(Word word, WordViewMode mode) {
        TextView wordTextView = findViewById(R.id.wordTextView);
        TextView explainTextView = findViewById(R.id.explainTextView);
        TextView translateTextView = findViewById(R.id.translateTextView);

        wordTextView.setText(word.getWord());
        explainTextView.setText(word.getExplain());
        if (randomWordsMode) {
            translateTextView.setText(word.getTranslate());
        } else {
            translateTextView.setText(word.getTranslate() + " (" + word.getRank() + "/" + word.getTotalShows() + ")");
        }

        switch (mode) {
            case WORD:
                wordTextView.setVisibility(View.VISIBLE);
                explainTextView.setVisibility(View.INVISIBLE);
                translateTextView.setVisibility(View.INVISIBLE);
                break;
            case WORD_EXPLAIN:
                wordTextView.setVisibility(View.VISIBLE);
                explainTextView.setVisibility(View.VISIBLE);
                translateTextView.setVisibility(View.INVISIBLE);
                break;
            case WORD_EXPLAIN_TRANSLATE:
                wordTextView.setVisibility(View.VISIBLE);
                explainTextView.setVisibility(View.VISIBLE);
                translateTextView.setVisibility(View.VISIBLE);
                break;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int deviceTotalHeight = metrics.heightPixels;

        wordTextView.setTextSize((float) deviceTotalHeight / 40);
        explainTextView.setTextSize((float) deviceTotalHeight / 70);
        translateTextView.setTextSize((float) deviceTotalHeight / 80);

        wordTextView.setPadding(0, deviceTotalHeight / 50, 0, 0);
        explainTextView.setPadding(0, deviceTotalHeight / 25, 0, 0);
        translateTextView.setPadding(0, deviceTotalHeight / 25, 0, 0);
    }

    private WordViewMode getNextMode(WordViewMode mode) {
        switch (mode) {
            case WORD:
                return WordViewMode.WORD_EXPLAIN;
            case WORD_EXPLAIN:
                return WordViewMode.WORD_EXPLAIN_TRANSLATE;
            case WORD_EXPLAIN_TRANSLATE:
            default:
                return WordViewMode.WORD;
        }
    }
}
