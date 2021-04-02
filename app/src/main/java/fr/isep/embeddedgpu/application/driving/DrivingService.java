package fr.isep.embeddedgpu.application.driving;

import android.util.Log;

import java.util.Arrays;

public class DrivingService {
    private static final String TAG = "[DRIVING SERVICE]";

    // Constants
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 255;
    public static final int FAKE_ZERO = 127;
    public static final int MIN_SPEED_PERCENT = 0;
    public static final int MAX_SPEED_PERCENT = 100;
    public static final int ACCELERATION = 10;
    public static final int MIN_ACCELERATION_PERCENT = 0;
    public static final int MAX_ACCELERATION_PERCENT = 100;

    // Speed
    private int previousSpeed;
    private int maxSpeed;
    private int minSpeed;
    private int speedPercent;

    // Acceleration
    private int acceleration;
    private double accelerationPercent;

    // Joystick
    private int angle;

    // Flags
    private boolean isRecording;
    private boolean isMovingForward;
    private boolean isMovingBackward;

    // Constructor
    public DrivingService() {
        // set flags
        this.isRecording = false;
        this.isMovingForward = false;
        this.isMovingBackward = false;

        // set default attributes
        this.angle = 90;
        this.speedPercent = MAX_SPEED_PERCENT;
        this.accelerationPercent = MAX_ACCELERATION_PERCENT / 2;
        this.previousSpeed = FAKE_ZERO;

        // update
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
        int newSpeed = isMovingForward ? previousSpeed + acceleration : isMovingBackward ? previousSpeed - acceleration : FAKE_ZERO;
        // minSpeed <= newSpeed <= maxSpeed
        if (newSpeed < minSpeed) {
            newSpeed = minSpeed;
        } else {
            newSpeed = Math.min(newSpeed, maxSpeed);
        }
        return newSpeed;
    }

    protected byte getLeftSpeed(int speed, double ratioRL) {
        return (byte) ((((1 + ratioRL) * (speed - FAKE_ZERO)) / 2) + FAKE_ZERO);
    }

    protected byte getRightSpeed(int speed, double ratioRL) {
        return (byte) ((((1 - ratioRL) * (speed - FAKE_ZERO)) / 2) + FAKE_ZERO);
    }

    public byte[] buildTramToMove(){
        // get new speed and ratio Right over Left
        updateAcceleration();
        updateSpeed();
        int newSpeed = getNewSpeed();
        previousSpeed = newSpeed;

        // get right over left engine ratio
        // as strength is given by moving buttons, just take in account angle cos
        double ratioRL = Math.cos(Math.toRadians(angle));

        // build tram
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0F;
        tram[1] = getLeftSpeed(newSpeed, ratioRL);
        tram[2] = getRightSpeed(newSpeed, ratioRL);
        Log.d(TAG, String.format("Moving: built tram = %s", Arrays.toString(tram)));
        return tram;
    }

    public byte[] buildTramToStartRecording() {
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0E;
        tram[1] = (byte) 0x00;
        tram[2] = (byte) 0x01;
        setRecording(true);
        Log.d(TAG, String.format("Start recording: built tram = %s", Arrays.toString(tram)));
        return tram;
    }

    public byte[] buildTramToStopRecording() {
        byte[] tram = new byte[3];
        tram[0] = (byte) 0x0E;
        tram[1] = (byte) 0x00;
        tram[2] = (byte) 0x00;
        setRecording(false);
        Log.d(TAG, String.format("Stop recording: built tram = %s", Arrays.toString(tram)));
        return tram;
    }

    public boolean isRecording() {
        return isRecording;
    }

    protected void setRecording(boolean isRecording) {
        this.isRecording = isRecording;
        Log.d(TAG, String.format("Recording is %s", isRecording ? "on" : "off"));
    }

    public int getSpeedPercent() {
        return speedPercent;
    }

    public void setSpeedPercent(int speedPercent) {
        this.speedPercent = speedPercent;
        updateSpeed();
        Log.d(TAG, String.format("Speed percentage is now %d", speedPercent));
    }

    public double getAccelerationPercent() {
        return accelerationPercent;
    }

    public void setAccelerationPercent(int accelerationPercent) {
        this.accelerationPercent = accelerationPercent;
        updateAcceleration();
        Log.d(TAG, String.format("Acceleration percentage is now %d", accelerationPercent));
    }

    public void setAngle(int angle) {
        this.angle = angle;
        Log.d(TAG, String.format("Angle is now %d°", angle));
    }

    public void setMovingForward(boolean isMovingForward){
        this.isMovingForward = isMovingForward;
        Log.d(TAG, String.format("Moving forward is now %s", isMovingForward ? "true" : "false"));
    }

    public void setMovingBackward(boolean isMovingBackward){
        this.isMovingBackward = isMovingBackward;
        Log.d(TAG, String.format("Moving backward is now %s", isMovingBackward ? "true" : "false"));
    }
}
