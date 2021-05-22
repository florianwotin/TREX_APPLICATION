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
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;
import fr.isep.embeddedgpu.application.driving.DrivingService;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DrivingFragment extends Fragment {
    public static final String TAG = "[DRIVING FRAGMENT]";

    // Constants
    public static final int SEND_PERIOD_MS = 100;

    // Services
    protected BluetoothService bluetoothService;
    protected DrivingService drivingService;

    // UI
    private View root;
    private Handler handler;

    // Runnable
    private final Runnable sendTramToMove = new Runnable() {
        @Override public void run() {
            if(bluetoothService.isConnected()) {
                bluetoothService.sendData(drivingService.buildTramToMove());
            }
            handler.postDelayed(this, SEND_PERIOD_MS);
        }
    };

    // Constructor
    public DrivingFragment(BluetoothService bluetoothService, DrivingService drivingService) {
        this.bluetoothService = bluetoothService;
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

        // initialize all components
        initializeRecording();
        initializeDirectionJoystick();
        initializeMovingForward();
        initializeMovingBackward();

        // send tram to move periodically
        handler = new Handler();
        handler.postDelayed(sendTramToMove, SEND_PERIOD_MS);

        Log.d(TAG, "Initialization OK");
    }

    private void initializeRecording() {
        TextView recordingTextView = root.findViewById(R.id.driving_recording_text);
        ImageView recordingImageView = root.findViewById(R.id.driving_recording_image);
        Button recordingButton = root.findViewById(R.id.driving_recording_button);
        recordingButton.setOnClickListener(v -> {
            if (drivingService.isRecording()) {
                bluetoothService.sendData(drivingService.buildTramToStopRecording());
                recordingButton.setText(R.string.button_record);
                recordingTextView.setText(R.string.driving_recording_disabled);
                recordingImageView.setImageResource(R.drawable.not_recording_foreground);
            } else {
                bluetoothService.sendData(drivingService.buildTramToStartRecording());
                recordingButton.setText(R.string.button_stoprecord);
                recordingTextView.setText(R.string.driving_recording_enabled);
                recordingImageView.setImageResource(R.drawable.recording_foreground);
            }
        });
    }

    private void initializeDirectionJoystick() {
        JoystickView directionJoystick = root.findViewById(R.id.driving_joystick_direction);
        directionJoystick.setOnMoveListener((angle, strength) -> {
            drivingService.setAngle((strength == 0 ) ? 90 : angle);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingForward() {
        ImageView moveForwardImg = root.findViewById(R.id.driving_controls_move_forward);
        moveForwardImg.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drivingService.setMovingForward(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    drivingService.setMovingForward(false);
                    return true;
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMovingBackward() {
        ImageView moveBackwardImg = root.findViewById(R.id.driving_controls_move_backward);
        moveBackwardImg.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drivingService.setMovingBackward(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    drivingService.setMovingBackward(false);
                    return true;
            }
            return false;
        });
    }
}