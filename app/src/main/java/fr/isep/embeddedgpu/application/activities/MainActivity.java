package fr.isep.embeddedgpu.application.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.services.TrexService;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothProperties.REQUEST_ENABLE_BLUETOOTH;

public class MainActivity extends AppCompatActivity {
    // Tag
    private static final String TAG = "[MAIN ACTIVITY]";

    // Services
    protected TrexService trexService;

    // UI
    private Button recordingButton;
    private Button moveForwardButton;
    private Button moveBackwardButton;
    private SeekBar maxSpeedSlider;
    private SeekBar accelerationSlider;
    private TextView recordingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result is ok and bluetooth enabled initialize bluetooth process
        if((resultCode == RESULT_OK) && (requestCode == REQUEST_ENABLE_BLUETOOTH)){
            trexService.initializeBluetoothProcess();
        }
    }

    protected void initialize() {
        Log.d(TAG, "initialization");
        initializeServices();
        initializeUI();
        activateBluetooth();
    }

    protected void initializeServices() {
        Log.d(TAG, "initializing services");
        trexService = new TrexService();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initializeUI() {
        Log.d(TAG, "initializing UI");
        // recording
        recordingTextView = findViewById(R.id.trex_recording_text);
        recordingButton = findViewById(R.id.trex_recording_button);
        recordingButton.setOnClickListener(v -> {
            if (trexService.isRecording()) {
                trexService.stopRecording();
                recordingButton.setText(R.string.button_enable);
                recordingTextView.setText(R.string.recording_disabled);
            } else {
                trexService.startRecording();
                recordingButton.setText(R.string.button_disable);
                recordingTextView.setText(R.string.recording_enabled);
            }
        });

        // max speed
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
                        handler.postDelayed(moveForward, TrexService.SEND_PERIOD_MS);
                        break;
                    case MotionEvent.ACTION_UP:
                        // if handler is busy stop callback here
                        if (handler == null) return true;
                        handler.removeCallbacks(moveForward);
                        handler = null;
                        break;
                }
                return false;
            }

            final Runnable moveForward = new Runnable() {
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
                        handler.postDelayed(moveBackward, TrexService.SEND_PERIOD_MS);
                        break;
                    case MotionEvent.ACTION_UP:
                        // if handler is busy stop callback here
                        if (handler == null) return true;
                        handler.removeCallbacks(moveBackward);
                        handler = null;
                        break;
                }
                return false;
            }

            final Runnable moveBackward = new Runnable() {
                @Override public void run() {
                    trexService.moveBackward();
                    handler.postDelayed(this, TrexService.SEND_PERIOD_MS);
                }
            };
        });
    }
    
    protected void activateBluetooth() {
        Log.d(TAG, "trying to get trex service bluetooth adapter");
        if (trexService.getBluetoothAdapter() != null) {
            Log.d(TAG, "checking if bluetooth is enabled");
            if(!trexService.getBluetoothAdapter().isEnabled()) {
                Log.d(TAG, "bluetooth is disabled, asking user to enable it");
                // if bluetooth is not enabled create intent for user to turn it on
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                Log.d(TAG, "bluetooth is enabled, starting bluetooth initialization process");
                // if it is enabled just initialize it
                trexService.initializeBluetoothProcess();
            }
        } else {
            Log.e(TAG, "error occurred while activating bluetooth: trex service bluetooth adapter is null");
        }
    }
}