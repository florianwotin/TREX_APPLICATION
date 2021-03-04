package fr.isep.embeddedgpu.application.services;

import android.util.Log;

public class TrexService {
    // Public class attributes
    public static int MIN_SPEED = 0;
    public static int MAX_SPEED = 255;
    public static int MIN_ACCELERATION = 0;
    public static int MAX_ACCELERATION = 20;
    public static int SEND_PERIOD_MS = 100;

    // Private class attributes
    private static String TAG = "[TREX SERVICE]";

    // Private instance attributes
    private int speed;
    private int acceleration;
    private boolean isRecording;

    // Constructor
    public TrexService() {
        this.speed = MAX_SPEED;
        this.acceleration = MAX_ACCELERATION / 2;
        this.isRecording = false;
    }

    // isRecording getter
    public boolean isRecording() {
        return isRecording;
    }

    // Start recording
    public void startRecording() {
        isRecording = true;
        Log.d(TAG, "start recording");
    }

    // Stop recording
    public void stopRecording() {
        isRecording = false;
        Log.d(TAG, "stop recording");
    }

    // Speed getter
    public int getSpeed() {
        return speed;
    }

    // Speed setter
    public void setSpeed(int speed) {
        this.speed = speed;
        Log.d(TAG, String.format("speed is now %d", speed));
    }

    // Acceleration getter
    public int getAcceleration() {
        return acceleration;
    }

    // Speed setter
    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
        Log.d(TAG, String.format("acceleration is now %d", acceleration));
    }

    // Allow robot to move forward
    public void moveForward() {
        Log.d(TAG, String.format("moving forward with speed=%d and accel=%d", speed, acceleration));
    }

    // Allow robot to move backward
    public void moveBackward() {
        Log.d(TAG, String.format("moving backward with speed=%d and accel=%d", speed, acceleration));
    }
}
