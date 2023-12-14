package com.example.colortiltsequence;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.util.Log;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private static final float TILT_THRESHOLD = 2.5f;
    private static final int TILT_LEFT = 0;
    private static final int TILT_UP = 2;
    private static final int TILT_DOWN = 3;

    private static final int TILT_RIGHT = 1;
    private int[] gameSequence;
    private int currentStep = 0;
    private boolean gameInProgress = false;
    private Random random = new Random();
    private DatabaseHelper db;
    private View[] colorViews;
    private Handler sequenceHandler;
    private Runnable sequenceRunnable;
    private int sequenceLength = 4;
    private Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = new DatabaseHelper(this);
        initializeColorViews();
        initializeSensorManager();
        startGameButton = findViewById(R.id.startGameButton);
        startNewGame();
    }

    private void initializeColorViews() {
        colorViews = new View[]{
                findViewById(R.id.circle_red),
                findViewById(R.id.circle_blue),
                findViewById(R.id.circle_green),
                findViewById(R.id.circle_yellow)
        };
    }

    private void initializeSensorManager() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void startNewGame() {
        currentStep = 0;
        gameInProgress = false;
        gameSequence = new int[sequenceLength];
        initializeGameSequence();

        sequenceHandler = new Handler();
        sequenceRunnable = this::displaySequenceToUser;
        sequenceHandler.postDelayed(sequenceRunnable, 2000);

        startGameButton.setVisibility(View.GONE);
    }

    private void initializeGameSequence() {
        for (int i = 0; i < sequenceLength; i++) {
            gameSequence[i] = random.nextInt(4);
        }
    }

    private void displaySequenceToUser() {
        Handler handler = new Handler();
        for (int i = 0; i < gameSequence.length; i++) {
            int colorIndex = gameSequence[i];
            handler.postDelayed(() -> highlightColor(colorIndex, true), 1000 * i);
            handler.postDelayed(() -> highlightColor(colorIndex, false), 1000 * i + 500);
        }
        handler.postDelayed(this::startUserInput, 1000 * gameSequence.length + 2000);
    }

    private void highlightColor(int colorIndex, boolean highlight) {
        View colorView = colorViews[colorIndex];
        if (highlight) {
            // Overlay with a black circle
            colorView.setBackgroundResource(R.drawable.circle_black);
        } else {
            // Revert back to the original drawable
            switch (colorIndex) {
                case 0: // Red
                    colorView.setBackgroundResource(R.drawable.circle_red);
                    break;
                case 1: // Blue
                    colorView.setBackgroundResource(R.drawable.circle_blue);
                    break;
                case 2: // Green
                    colorView.setBackgroundResource(R.drawable.circle_green);
                    break;
                case 3: // Yellow
                    colorView.setBackgroundResource(R.drawable.circle_yellow);
                    break;
            }
        }
    }


    private void startUserInput() {
        // waiting for user input
        gameInProgress = true;
        currentStep = 0;
    }


    private void resetColorViews() {
        for (View colorView : colorViews) {
            colorView.setBackgroundColor(Color.BLACK);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!gameInProgress || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];

        if (x < -TILT_THRESHOLD) {
            processTilt(TILT_LEFT); // Red
        } else if (x > TILT_THRESHOLD) {
            processTilt(TILT_RIGHT); // Blue
        } else if (y < -TILT_THRESHOLD) {
            processTilt(TILT_UP); // Green
        } else if (y > TILT_THRESHOLD) {
            processTilt(TILT_DOWN); // Yellow
        }
    }

    private void processTilt(int direction) {
        if (currentStep >= gameSequence.length) {
            return; // Prevents processing tilts when the sequence is complete
        }

        int expectedDirection = -1;
        switch (gameSequence[currentStep]) {
            case 0: expectedDirection = TILT_LEFT; break; // Red
            case 1: expectedDirection = TILT_RIGHT; break; // Blue
            case 2: expectedDirection = TILT_UP; break; // Green
            case 3: expectedDirection = TILT_DOWN; break; // Yellow
        }

        if (expectedDirection == direction) {
            provideVisualFeedback(colorViews[currentStep], true);
            currentStep++;
            if (currentStep < gameSequence.length) {
                Toast.makeText(this, "Correct! Prepare for the next color.", Toast.LENGTH_SHORT).show();
            } else {
                gameInProgress = false;
                Toast.makeText(this, "Sequence complete! Starting new round.", Toast.LENGTH_SHORT).show();
                startNewRound();
            }
        } else {
            provideVisualFeedback(colorViews[currentStep], false);
            gameInProgress = false;
            handleGameOver();
        }
    }
    private void processTiltInput(float x, float y) {
        if (Math.abs(x) > TILT_THRESHOLD || Math.abs(y) > TILT_THRESHOLD) {
            int direction = -1; // Represents no direction
            if (Math.abs(x) > Math.abs(y)) {
                // Tilt left/right logic
                direction = x > 0 ? 1 : 0; // Right : Left
            } else {
                // Tilt up/down logic
                direction = y > 0 ? 2 : 3; // Up : Down
            }
            checkTilt(direction);
        }
    }

    private void onTiltRight() {
        checkTilt(0);
    }

    private void onTiltLeft() {
        checkTilt(1);
    }

    private void onTiltUp() {
        checkTilt(2);
    }

    private void onTiltDown() {
        checkTilt(3);
    }

    private void checkTilt(int direction) {
        if (!gameInProgress) {
            return;
        }

        int expectedDirection = gameSequence[currentStep] < 2 ? TILT_LEFT : TILT_RIGHT;
        if (expectedDirection == direction) {
            provideVisualFeedback(colorViews[currentStep], true); // Correct tilt feedback
            currentStep++;

            if (currentStep < gameSequence.length) {
                // Prepare for the next tilt in the sequence after a short delay
                new Handler().postDelayed(() -> {
                    resetColorViews(); // Reset color views before the next tilt
                    Toast.makeText(this, "Correct! Tilt for the next color!", Toast.LENGTH_SHORT).show();
                }, 1000);
            } else {
                // Sequence completed correctly
                gameInProgress = false;
                Toast.makeText(this, "Sequence complete! Starting new round.", Toast.LENGTH_SHORT).show();
                startNewRound();
            }
        } else {
            provideVisualFeedback(colorViews[currentStep], false); // Incorrect tilt feedback
            gameInProgress = false;
            handleGameOver();
        }
    }
    private void provideVisualFeedback(View colorView, boolean isCorrect) {
        // Visual feedback: green for correct, red for incorrect
        int feedbackColor = isCorrect ? Color.GREEN : Color.RED;
        colorView.setBackgroundColor(feedbackColor);
        new Handler().postDelayed(() -> resetColorViews(), 500);
    }
    private void startNewRound() {
        // Provide a short delay before starting the next round
        new Handler().postDelayed(() -> {
            currentStep = 0;
            sequenceLength += 2;
            startNewGame(); // Restart the game with the new sequence
        }, 2000);
    }

    private void handleGameOver() {
        int score = currentStep;
        db.insertHighScore("PlayerName", score);
        Toast.makeText(this, "Game Over! Your score: " + score, Toast.LENGTH_LONG).show();
        navigateToGameOverActivity(score);
    }

    private void navigateToGameOverActivity(int score) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sequenceHandler != null) {
            sequenceHandler.removeCallbacks(sequenceRunnable);
        }
    }
}
