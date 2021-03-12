package fr.isep.embeddedgpu.application.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.trex.TrexService;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TrexFragment extends Fragment {
    public static final String TAG = "[TREX FRAGMENT]";
    public static final String TITLE = "CONDUIRE";

    // Services
    protected TrexService trexService;

    // UI
    private TextView recordingTextView;
    private Button recordingButton;
    private Button moveForwardButton;
    private Button moveBackwardButton;
    private SeekBar maxSpeedPercentSlider;
    private SeekBar maxAccelerationPercentSlider;
    private JoystickView directionJoystick;
    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_trex, container, false);
        initialize();
        return root;
    }

    private void initialize() {
        Log.d(TAG, "starting initialization");
        initializeServices();
        initializeUI();
        Log.d(TAG, "initialization OK");
    }

    private void initializeServices() {
        Log.d(TAG, "initializing services");
        trexService = new TrexService();
        Log.d(TAG, "services initialization OK");
    }

    private void initializeUI() {
        Log.d(TAG, "initializing UI");
        initializeRecording();
        initializeMaxSpeedPercent();
        initializeMaxAccelerationPercent();
        initializeDirectionJoystick();
        initializeMovingForward();
        initializeMovingBackward();
        Log.d(TAG, "UI initialization OK");
    }

    private void initializeRecording() {
        recordingTextView = root.findViewById(R.id.trex_recording_text);
        recordingButton = root.findViewById(R.id.trex_recording_button);
        recordingButton.setOnClickListener(v -> {
            if (trexService.isRecording()) {
                trexService.stopRecording();
                recordingButton.setText(R.string.button_enable);
                recordingTextView.setText(R.string.trex_recording_disabled);
            } else {
                trexService.startRecording();
                recordingButton.setText(R.string.button_disable);
                recordingTextView.setText(R.string.trex_recording_enabled);
            }
        });
        Log.d(TAG, "recording is initialized");
    }

    private void initializeMaxSpeedPercent() {
        maxSpeedPercentSlider = root.findViewById(R.id.trex_speed_slider);
        maxSpeedPercentSlider.setMin(TrexService.MIN_SPEED_PERCENT);
        maxSpeedPercentSlider.setMax(TrexService.MAX_SPEED_PERCENT);
        maxSpeedPercentSlider.setProgress(trexService.getSpeedPercent());
        maxSpeedPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setSpeedPercent(progress);
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
        Log.d(TAG, "max speed percent is initialized");
    }

    private void initializeMaxAccelerationPercent() {
        maxAccelerationPercentSlider = root.findViewById(R.id.trex_accel_slider);
        maxAccelerationPercentSlider.setMin(TrexService.MIN_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setMax(TrexService.MAX_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setProgress(trexService.getAccelerationPercent());
        maxAccelerationPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setAccelerationPercent(progress);
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
        Log.d(TAG, "max acceleration percent is initialized");
    }

    private void initializeDirectionJoystick() {
        directionJoystick = root.findViewById(R.id.trex_joystick_direction);
        directionJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                trexService.setAngle(angle);
                trexService.setStrength(strength);
            }
        });
        Log.d(TAG, "direction joystick is initialized");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingForward() {
        moveForwardButton = root.findViewById(R.id.trex_controls_move_forward);
        moveForwardButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler;

            @SuppressLint("ClickableViewAccessibility")
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
        Log.d(TAG, "moving forward is initialized");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingBackward() {
        moveBackwardButton = root.findViewById(R.id.trex_controls_move_backward);
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
        Log.d(TAG, "moving backward is initialized");
    }
}