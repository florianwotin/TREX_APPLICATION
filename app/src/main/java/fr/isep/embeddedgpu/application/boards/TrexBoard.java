package fr.isep.embeddedgpu.application.boards;

public class TrexBoard {
    // Public class attributes

    public static int MIN_VOLTAGE = 0;
    public static int MAX_VOLTAGE = 255;

    // Private instance attributes

    private int currentMinVoltage;
    private int currentMaxVoltage;
    private boolean isRecording;

    // Constructors

    public TrexBoard(int currentMaxVoltage, int currentMinVoltage, boolean isRecording) {
        this.currentMinVoltage = currentMinVoltage;
        this.currentMaxVoltage = currentMaxVoltage;
        this.isRecording = isRecording;
    }

    // Getters & Setters

    public int getCurrentMinVoltage() {
        return currentMinVoltage;
    }

    public void setCurrentMinVoltage(int currentMinVoltage) {
        this.currentMinVoltage = currentMinVoltage;
    }

    public int getCurrentMaxVoltage() {
        return currentMaxVoltage;
    }

    public void setCurrentMaxVoltage(int currentMaxVoltage) {
        this.currentMaxVoltage = currentMaxVoltage;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }
}
