package com.example.colortiltsequence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the main activity
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user taps the Start Game button.
     * Starts the GameActivity.
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        // Optionally add a transition animation
    }

    /**
     * Called when the user taps the High Scores button.
     * Starts the HighScoreActivity.
     */
    public void showHighScores(View view) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);

    }
}
