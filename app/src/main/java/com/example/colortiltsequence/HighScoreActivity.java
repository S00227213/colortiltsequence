package com.example.colortiltsequence;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    private ListView listViewHighScores;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        listViewHighScores = findViewById(R.id.lvHighScores);
        db = new DatabaseHelper(this);

        loadHighScores();
    }

    private void loadHighScores() {
        // Fetch high scores from the database
        List<DatabaseHelper.HighScore> highScoresList = db.getTopFiveHighScores();

        // Creating a simple ArrayAdapter to show high scores
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, formatHighScores(highScoresList));
        listViewHighScores.setAdapter(adapter);
    }


    private List<String> formatHighScores(List<DatabaseHelper.HighScore> highScoresList) {
        List<String> formattedList = new ArrayList<>();
        for (DatabaseHelper.HighScore highScore : highScoresList) {
            String formattedScore = "Name: " + highScore.getPlayerName() + ", Score: " + highScore.getScore();
            formattedList.add(formattedScore);
        }
        return formattedList;
    }
}
