package fr.isep.embeddedgpu.application.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import fr.isep.embeddedgpu.application.bluetooth.BluetoothThread;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothThread.RESPONSE_MESSAGE;

public class TrexService {
    // Public class attributes
    public static final int MIN_SPEED = 0;
    public static final int MAX_SPEED = 127; // 255 : -127 to +127
    public static final int MIN_ACCELERATION = 0;
    public static final int MAX_ACCELERATION = 20;
    public static final int SEND_PERIOD_MS = 100;

    // Protected class attributes
    protected static final String MODULE_BLUETOOTH_MAC_ADDRESS = "";
    protected static final UUID MODULE_BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // Private class attributes
    private static final String TAG = "[TREX SERVICE]";

    // Protected instance attributes
    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothDevice bluetoothDevice;
    protected BluetoothThread bluetoothThread;
    protected Handler handler;

    // Private instance attributes
    private int speed;
    private int acceleration;
    private boolean isRecording;

    // Constructor
    public TrexService() {
        this.speed = MAX_SPEED;
        this.acceleration = MAX_ACCELERATION / 2;
        this.isRecording = false;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Return if bluetooth connection is ok
    protected boolean bluetoothConnectionOK() {
        return bluetoothSocket.isConnected() && (bluetoothThread != null);
    }

    // Send data with bluetooth thread
    protected void sendData(byte[] bytes) {
        if(bluetoothConnectionOK()) {
            bluetoothThread.write(bytes);
        } else {
            Log.e(TAG, "cannot send data with bluetooth thread: bluetooth is not connected or bluetooth thread is null");
        }
    }

    // Compute asked speed
    protected int askedSpeed() {
        // TODO
        return 0;
    }

    // Allow robot to move forward
    public void moveForward() {
        Log.d(TAG, String.format("moving forward with speed=%d and accel=%d", speed, acceleration));
    }

    // Allow robot to move backward
    public void moveBackward() {
        Log.d(TAG, String.format("moving backward with speed=%d and accel=%d", speed, acceleration));
    }

    // Bluetooth process initialization
    public void initializeBluetoothProcess() {
        if(bluetoothAdapter.isEnabled()) {
            // attempt to connect to bluetooth module
            Log.d(TAG, "attempt to connect to bluetooth module");
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(MODULE_BLUETOOTH_MAC_ADDRESS);

            // try to create bluetooth socket
            try {
                Log.d(TAG, "trying to create bluetooth socket");
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MODULE_BLUETOOTH_UUID);
                bluetoothSocket.connect();
                Log.d(TAG, "connected to device " + bluetoothDevice.getName());
            } catch (IOException e) {
                Log.e(TAG, String.format("cannot create bluetooth socket from DEVICE[UUID=%s | MAC=%s]", MODULE_BLUETOOTH_UUID.toString(), MODULE_BLUETOOTH_MAC_ADDRESS));
                e.printStackTrace();
            }

            // attempt to create handler
            Log.d(TAG, "attempt to create handler");
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    if(message.what == RESPONSE_MESSAGE) {
                        String response = (String)message.obj;
                        Log.d(TAG, "handler received " + response);
                    }
                }
            };

            // attempt to create and run bluetooth thread
            Log.d(TAG, "attempt to create and run bluetooth thread");
            bluetoothThread = new BluetoothThread(bluetoothSocket, handler);
            bluetoothThread.start();
        }
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

    // Bluetooth adapter getter
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
