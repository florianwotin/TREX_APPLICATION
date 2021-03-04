package fr.isep.embeddedgpu.application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.services.TrexService;

public class MainActivity extends AppCompatActivity {
    // Services
    TrexService trexService;

    // UI
    Button recordingButton;
    Button moveForwardButton;
    Button moveBackwardButton;
    SeekBar maxSpeedSlider;
    SeekBar accelerationSlider;
    TextView recordingTextView;
    TextView maxSpeedTextView;
    TextView accelerationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    protected void initialize() {
        trexService = new TrexService();
        initializeUI();
    }

    protected void initializeUI() {
        // recording
        recordingTextView = findViewById(R.id.trex_recording_text);
        recordingButton = findViewById(R.id.trex_recording_button);
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trexService.isRecording()) {
                    trexService.stopRecording();
                    recordingButton.setText(R.string.button_enable);
                    recordingTextView.setText(R.string.recording_disabled);
                } else {
                    trexService.startRecording();
                    recordingButton.setText(R.string.button_disable);
                    recordingTextView.setText(R.string.recording_enabled);
                }
            }
        });

        // max speed
        maxSpeedTextView = findViewById(R.id.trex_speed_text);
        maxSpeedSlider = findViewById(R.id.trex_speed_slider);
        maxSpeedSlider.setMin(TrexService.MIN_SPEED);
        maxSpeedSlider.setMax(TrexService.MAX_SPEED);
        maxSpeedSlider.setProgress(trexService.getSpeed());
        maxSpeedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // empty for now
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // empty for now
            }
        });

        // acceleration
        accelerationTextView = findViewById(R.id.trex_accel_text);
        accelerationSlider = findViewById(R.id.trex_accel_slider);
        accelerationSlider.setMin(TrexService.MIN_ACCELERATION);
        accelerationSlider.setMax(TrexService.MAX_ACCELERATION);
        accelerationSlider.setProgress(trexService.getAcceleration());
        accelerationSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setAcceleration(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // empty for now
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // empty for now
            }
        });

        // Move forward
        moveForwardButton = findViewById(R.id.trex_controls_move_forward);
        moveForwardButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // if handler is busy stop callback here
                        if (handler != null) return true;
                        handler = new Handler();
                        handler.postDelayed(action, TrexService.SEND_PERIOD_MS);
                        break;
                    case MotionEvent.ACTION_UP:
                        // if handler is busy stop callback here
                        if (handler == null) return true;
                        handler.removeCallbacks(action);
                        handler = null;
                        break;
                }
                return false;
            }

            Runnable action = new Runnable() {
                @Override public void run() {
                    trexService.moveForward();
                    handler.postDelayed(this, TrexService.SEND_PERIOD_MS);
                }
            };
        });

        // Move backward
        moveBackwardButton = findViewById(R.id.trex_controls_move_backward);
        moveBackwardButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // if handler is busy stop callback here
                        if (handler != null) return true;
                        handler = new Handler();
                        handler.postDelayed(action, TrexService.SEND_PERIOD_MS);
                        break;
                    case MotionEvent.ACTION_UP:
                        // if handler is busy stop callback here
                        if (handler == null) return true;
                        handler.removeCallbacks(action);
                        handler = null;
                        break;
                }
                return false;
            }

            Runnable action = new Runnable() {
                @Override public void run() {
                    trexService.moveBackward();
                    handler.postDelayed(this, TrexService.SEND_PERIOD_MS);
                }
            };
        });
    }
}