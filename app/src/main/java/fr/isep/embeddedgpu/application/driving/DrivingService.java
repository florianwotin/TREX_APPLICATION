package fr.isep.embeddedgpu.application.driving;

import android.util.Log;

import java.util.UUID;

public class DrivingService {
    private static final String TAG = "[TREX SERVICE]";

    // Public class attributes
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 255;
    public static final int MIN_SPEED_PERCENT = 0;
    public static final int MAX_SPEED_PERCENT = 100;
    public static final int ACCELERATION = 10;
    public static final int MIN_ACCELERATION_PERCENT = 0;
    public static final int MAX_ACCELERATION_PERCENT = 100;
    public static final int SEND_PERIOD_MS = 100;
    public static final int DIRECTION_MOVE_FORWARD = 1;
    public static final int DIRECTION_MOVE_BACKWARD = -1;

    // Protected class attributes
    protected static final String MODULE_BLUETOOTH_MAC_ADDRESS = "";
    protected static final UUID MODULE_BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // Private instance attributes
    private int previousSpeed;
    private int maxSpeed;
    private int minSpeed;
    private int acceleration;
    private int speedPercent;
    private int accelerationPercent;
    private int angle;
    private int strength;
    private boolean isRecording;

    // Constructor
    public DrivingService() {
        this.isRecording = false;
        this.speedPercent = MAX_SPEED_PERCENT;
        this.accelerationPercent = MAX_ACCELERATION_PERCENT / 2;
        this.previousSpeed = 0;
        this.updateSpeed();
        this.updateAcceleration();
    }

    protected void updateSpeed() {
        updateMaxSpeed();
        updateMinSpeed();
    }

    protected void updateMaxSpeed() {
        maxSpeed = (MAX_SPEED * (speedPercent / 100)) + 127;
    }

    protected void updateMinSpeed() {
        minSpeed = (MIN_SPEED * (speedPercent / 100)) + 127;
    }

    protected void updateAcceleration() {
        acceleration = ACCELERATION * (accelerationPercent / 100);
    }

    protected int getNewSpeed() {
        int newSpeed = previousSpeed + acceleration;
        if (newSpeed < minSpeed) {
            return minSpeed;
        } else return Math.min(newSpeed, maxSpeed);
    }

    protected byte getLeftSpeed() {
        // TODO
        return 0;
    }

    protected byte getRightSpeed() {
        // TODO
        return 0;
    }

    protected byte[] buildTramToMove(){
        int newSpeed = getNewSpeed();
        double strengthX = Math.cos(angle)*strength;
        // init tram
        byte[] tram = new byte[3];
        tram[0] = (byte) 0xFF;
        // get speed for each side
        tram[1] = getLeftSpeed();
        tram[2] = getRightSpeed();
        return tram;
    }

    public void moveForward() {
        Log.d(TAG, String.format("moving forward with speed=%d and accel=%d", speedPercent, accelerationPercent));
    }

    public void moveBackward() {
        Log.d(TAG, String.format("moving backward with speed=%d and accel=%d", speedPercent, accelerationPercent));
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording() {
        isRecording = true;
        Log.d(TAG, "start recording");
    }

    public void stopRecording() {
        isRecording = false;
        Log.d(TAG, "stop recording");
    }

    public int getSpeedPercent() {
        return speedPercent;
    }

    public void setSpeedPercent(int speedPercent) {
        this.speedPercent = speedPercent;
        updateSpeed();
        Log.d(TAG, String.format("speed is now %d", speedPercent));
    }

    public int getAccelerationPercent() {
        return accelerationPercent;
    }

    public void setAccelerationPercent(int accelerationPercent) {
        this.accelerationPercent = accelerationPercent;
        updateAcceleration();
        Log.d(TAG, String.format("acceleration is now %d", accelerationPercent));
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
        Log.d(TAG, String.format("angle is now %d", angle));
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
        Log.d(TAG, String.format("strength is now %d", strength));
    }
}
