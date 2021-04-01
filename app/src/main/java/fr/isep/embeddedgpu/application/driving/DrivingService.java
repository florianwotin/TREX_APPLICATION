package fr.isep.embeddedgpu.application.driving;

import android.util.Log;

import java.util.UUID;

public class DrivingService {
    private static final String TAG = "[TREX SERVICE]";

    // Driving constants
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 255;
    public static final int MIN_SPEED_PERCENT = 0;
    public static final int MAX_SPEED_PERCENT = 100;
    public static final int ACCELERATION = 10;
    public static final int MIN_ACCELERATION_PERCENT = 0;
    public static final int MAX_ACCELERATION_PERCENT = 100;

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
        maxSpeed = ((MAX_SPEED - 127) * (speedPercent / 100)) + 127;
    }

    protected void updateMinSpeed() {
        minSpeed = ((MIN_SPEED - 127) * (speedPercent / 100)) + 127;
    }

    protected void updateAcceleration() {
        acceleration = ACCELERATION * (accelerationPercent / 100);
    }

    protected int getNewSpeed() {
        int newSpeed = previousSpeed + acceleration;
        previousSpeed = newSpeed;
        if (newSpeed < minSpeed) {
            return minSpeed;
        } else return Math.min(newSpeed, maxSpeed);
    }

    protected byte getLeftSpeed(int speed, double rationRL) {
        int leftSpeed = (int) ((speed * (1 - rationRL)) / 2);
        return (byte) leftSpeed;
    }

    protected byte getRightSpeed(int speed, double rationRL) {
        int rightSpeed = (int) ((speed * (1 + rationRL)) / 2);
        return (byte) rightSpeed;
    }

    public byte[] buildTramToMove(){
        // get new speed and ratio Right over Left
        int newSpeed = getNewSpeed();
        double rationRL = (Math.cos(angle) * strength) / 100;

        // build tram
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0F;
        tram[1] = getLeftSpeed(newSpeed, rationRL);
        tram[2] = getRightSpeed(newSpeed, rationRL);
        Log.d(TAG, String.format("moving forward : speed=%d ; accel=%d ; tram[0]=%x ; tram[1]=%d ; tram[2]=%d", speedPercent, accelerationPercent, tram[0], tram[1], tram[2]));
        return tram;
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
