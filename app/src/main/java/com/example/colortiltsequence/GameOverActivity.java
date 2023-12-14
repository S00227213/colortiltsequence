package com.example.colortiltsequence;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);


        int score = getIntent().getIntExtra("SCORE", 0);

        // Initialize the TextView by finding it by its ID and set the score
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText(getString(R.string.your_score, score));


        Button highScoresButton = findViewById(R.id.highScoresButton);
        highScoresButton.setOnClickListener(view -> {
            Intent intent = new Intent(GameOverActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });
    }
}
