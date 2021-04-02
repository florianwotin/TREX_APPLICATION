package fr.isep.embeddedgpu.application.driving;

import android.util.Log;

import java.util.UUID;

public class DrivingService {
    private static final String TAG = "[DRIVING SERVICE]";

    // Driving constants
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 255;
    public static final int FAKE_ZERO = 127;
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
    private double accelerationPercent;
    private int angle;
    private int strength;
    private boolean isRecording;
    private boolean forward;
    private boolean backward;

    // Constructor
    public DrivingService() {
        this.isRecording = false;
        this.forward = false;
        this.backward = false;
        this.angle = 90;
        this.speedPercent = MAX_SPEED_PERCENT;
        this.accelerationPercent = MAX_ACCELERATION_PERCENT / 2;
        this.previousSpeed = FAKE_ZERO;
        this.updateSpeed();
        this.updateAcceleration();
    }

    protected void updateSpeed() {
        updateMaxSpeed();
        updateMinSpeed();
    }

    protected void updateMaxSpeed() {
        maxSpeed = ((MAX_SPEED - FAKE_ZERO) * (speedPercent/100)) + FAKE_ZERO;
    }

    protected void updateMinSpeed() {
        minSpeed = ((MIN_SPEED - FAKE_ZERO) * (speedPercent/100)) + FAKE_ZERO;
    }

    protected void updateAcceleration() {
        acceleration = (int) (ACCELERATION * (accelerationPercent / 100));
    }

    protected int getNewSpeed() {
        int newSpeed = (forward) ?previousSpeed + acceleration : (backward)? previousSpeed - acceleration : FAKE_ZERO;

        if (newSpeed < minSpeed) {
            newSpeed = minSpeed;
        } else {
            newSpeed = Math.min(newSpeed, maxSpeed);
        }
        return newSpeed;
    }

    protected byte getLeftSpeed(int speed, double rationRL) {
        return (byte)(((1+rationRL) * (speed - FAKE_ZERO))/2 + FAKE_ZERO);
    }

    protected byte getRightSpeed(int speed, double rationRL) {
        return (byte)(((1-rationRL) * (speed - FAKE_ZERO))/2 + FAKE_ZERO);
    }

    public byte[] buildTramToMove(){
        // get new speed and ratio Right over Left
        updateAcceleration();
        updateSpeed();
        int newSpeed = getNewSpeed();
        previousSpeed = newSpeed;
        double rationRL = Math.cos(Math.toRadians(angle));

        Log.d(TAG, String.format("Angle : %d", angle));

        // build tram
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0F;
        tram[1] = getLeftSpeed(newSpeed, rationRL);
        tram[2] = getRightSpeed(newSpeed, rationRL);
        /*Log.d(TAG, String.format("moving forward : speed percent=%d ; acceleration percent=%f ; acceleration=%d ; speed=%d ; tram[0]=0x%X ; tram[1]=0x%X ; tram[2]=0x%X",
                                    speedPercent, accelerationPercent,acceleration,newSpeed, tram[0], tram[1], tram[2]));
        */return tram;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public byte[] startRecording() {
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0E;
        tram[1] = 0x00;
        tram[2] = 0x01;
        isRecording = true;
        Log.d(TAG, "start recording");
        return tram;
    }

    public byte[] stopRecording() {
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0E;
        tram[1] = 0x00;
        tram[2] = 0x00;
        isRecording = false;
        Log.d(TAG, "stop recording");
        return tram;
    }

    public int getSpeedPercent() {
        return speedPercent;
    }

    public void setSpeedPercent(int speedPercent) {
        this.speedPercent = speedPercent;
        updateSpeed();
        Log.d(TAG, String.format("speed is now %d", speedPercent));
    }

    public double getAccelerationPercent() {
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
        Log.d(TAG, String.format("Forward is now " + Boolean.toString(forward)));
    }

    public void setForward(boolean forward){
        this.forward = forward;
        Log.d(TAG, String.format("Forward is now " + Boolean.toString(forward)));
    }

    public void setBackward(boolean backward){
        this.backward = backward;
        Log.d(TAG, String.format("backward is now " + Boolean.toString(backward)));
    }

}
