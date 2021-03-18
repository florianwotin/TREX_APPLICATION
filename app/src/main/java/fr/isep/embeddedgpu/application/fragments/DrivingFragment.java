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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.driving.DrivingService;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DrivingFragment extends Fragment {
    public static final String TAG = "[DRIVING FRAGMENT]";

    // Services
    protected DrivingService drivingService;

    // UI
    private View root;

    public DrivingFragment(DrivingService drivingService) {
        this.drivingService = drivingService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_driving, container, false);
        initialize();
        return root;
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeRecording();
        initializeDirectionJoystick();
        initializeMovingForward();
        initializeMovingBackward();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeRecording() {
        TextView recordingTextView = root.findViewById(R.id.driving_recording_text);
        ImageView recordingImageView = root.findViewById(R.id.driving_recording_image);
        Button recordingButton = root.findViewById(R.id.driving_recording_button);
        recordingButton.setOnClickListener(v -> {
            if (drivingService.isRecording()) {
                drivingService.stopRecording();
                recordingButton.setText(R.string.button_enable);
                recordingTextView.setText(R.string.driving_recording_disabled);
                recordingImageView.setImageResource(R.drawable.not_recording_foreground);
            } else {
                drivingService.startRecording();
                recordingButton.setText(R.string.button_disable);
                recordingTextView.setText(R.string.driving_recording_enabled);
                recordingImageView.setImageResource(R.drawable.recording_foreground);
            }
        });
    }

    private void initializeDirectionJoystick() {
        JoystickView directionJoystick = root.findViewById(R.id.driving_joystick_direction);
        directionJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                drivingService.setAngle(angle);
                drivingService.setStrength(strength);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingForward() {
        Button moveForwardButton = root.findViewById(R.id.driving_controls_move_forward);
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
                        handler.postDelayed(moveForward, DrivingService.SEND_PERIOD_MS);
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
                    drivingService.moveForward();
                    handler.postDelayed(this, DrivingService.SEND_PERIOD_MS);
                }
            };
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingBackward() {
        Button moveBackwardButton = root.findViewById(R.id.driving_controls_move_backward);
        moveBackwardButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // if handler is busy stop callback here
                        if (handler != null) return true;
                        handler = new Handler();
                        handler.postDelayed(moveBackward, DrivingService.SEND_PERIOD_MS);
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
                    drivingService.moveBackward();
                    handler.postDelayed(this, DrivingService.SEND_PERIOD_MS);
                }
            };
        });
    }
}