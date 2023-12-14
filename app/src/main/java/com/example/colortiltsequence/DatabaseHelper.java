package com.example.colortiltsequence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage database creation and version management.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "game_db";

    // Table name and columns for the high scores
    private static final String TABLE_HIGH_SCORES = "high_scores";
    private static final String KEY_ID = "id";
    private static final String KEY_SCORE = "score";
    private static final String KEY_PLAYER_NAME = "player_name";

    // SQL query to create the high scores table
    private static final String CREATE_TABLE_HIGH_SCORES = "CREATE TABLE "
            + TABLE_HIGH_SCORES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PLAYER_NAME + " TEXT,"
            + KEY_SCORE + " INTEGER" + ")";

    /**
     * Constructor for DatabaseHelper.
     * @param context The context for the database helper.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database tables when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HIGH_SCORES);
    }

    /**
     * Handles upgrading the database when the schema changes.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGH_SCORES);
        // Create tables again
        onCreate(db);
    }

    /**
     * Inserts a new high score into the database.
     */
    public void insertHighScore(String playerName, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, playerName);
        values.put(KEY_SCORE, score);

        // Inserting Row
        db.insert(TABLE_HIGH_SCORES, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Retrieves the top five high scores from the database.
     */
    public List<HighScore> getTopFiveHighScores() {
        List<HighScore> highScores = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HIGH_SCORES
                + " ORDER BY " + KEY_SCORE + " DESC LIMIT 5";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int playerNameIndex = cursor.getColumnIndex(KEY_PLAYER_NAME);
            int scoreIndex = cursor.getColumnIndex(KEY_SCORE);

            // Check if any of the indices are -1, which indicates the column was not found
            if (idIndex == -1 || playerNameIndex == -1 || scoreIndex == -1) {
                // Handle the error or log the issue
                // You might want to throw an exception or return an empty list
                cursor.close();
                db.close();
                return highScores;
            }

            do {
                HighScore highScore = new HighScore();
                highScore.setId(cursor.getInt(idIndex));
                highScore.setPlayerName(cursor.getString(playerNameIndex));
                highScore.setScore(cursor.getInt(scoreIndex));
                highScores.add(highScore);
            } while (cursor.moveToNext());
        }

        // Close the cursor
        cursor.close();
        // Close the database connection
        db.close();

        return highScores;
    }


    // Update an existing high score
    public int updateHighScore(HighScore highScore) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, highScore.getPlayerName());
        values.put(KEY_SCORE, highScore.getScore());

        // Update the row and return the number of rows affected
        return db.update(TABLE_HIGH_SCORES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(highScore.getId())});
    }

    // Delete a high score from the database
    public void deleteHighScore(HighScore highScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HIGH_SCORES, KEY_ID + " = ?",
                new String[]{String.valueOf(highScore.getId())});
        db.close(); // Close the database connection
    }

    // Nested class for high score objects
    public static class HighScore {
        private int id;
        private String playerName;
        private int score;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }
}
