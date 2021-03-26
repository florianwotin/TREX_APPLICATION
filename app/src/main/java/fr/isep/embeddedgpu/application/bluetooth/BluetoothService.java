package fr.isep.embeddedgpu.application.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothThread.RESPONSE_MESSAGE;

public class BluetoothService {
    private static final String TAG = "[BLUETOOTH SERVICE]";

    // Requests
    public static final int REQUEST_ENABLE_BLUETOOTH = 0;
    public static final int REQUEST_ENABLE_BLUETOOTH_ADMIN = 1;
    public static final int REQUEST_TURN_ON_BLUETOOTH = 2;
    public static final int REQUEST_MAKE_DISCOVERABLE = 3;

    // Bluetooth
    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothThread bluetoothThread;
    protected Handler handler;

    // Phone
    protected UUID phoneUUID;

    public BluetoothService() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(this.bluetoothAdapter != null){
            Log.d(TAG, "bluetooth is available");
        } else {
            Log.d(TAG, "bluetooth is unavailable (adapter is null)");
        }
        phoneUUID = UUID.randomUUID();
        Log.d(TAG, String.format("Phone UUID is %s", phoneUUID.toString()));
    }

    public void connectToDevice(BluetoothDevice btDev) {
        String errorStringFormat = "Cannot connect to device %s (%s): %s";
        if(bluetoothAdapter != null) {
            if(bluetoothAdapter.isEnabled()) {
                // try to create bluetooth socket
                try {
                    Log.d(TAG, "Trying to create bluetooth socket");
                    bluetoothSocket = btDev.createRfcommSocketToServiceRecord(phoneUUID);
                    bluetoothSocket.connect();
                    Log.d(TAG, String.format("Connected to device %s (%s)", btDev.getName(), btDev.getAddress()));
                } catch (IOException e) {
                    Log.e(TAG, String.format("Cannot create bluetooth socket from device %s (%s) with UUID = %s", btDev.getName(), btDev.getAddress(), phoneUUID.toString()));
                    e.printStackTrace();
                }

                // attempt to create handler
                Log.d(TAG, "Attempt to create handler");
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        if(message.what == RESPONSE_MESSAGE) {
                            String response = (String)message.obj;
                            Log.d(TAG, String.format("Handler received: \"%s\"", response));
                        }
                    }
                };

                // attempt to create and run bluetooth thread
                Log.d(TAG, "Attempt to create and start bluetooth thread");
                bluetoothThread = new BluetoothThread(bluetoothSocket, handler);
                bluetoothThread.start();
            } else {
                Log.e(TAG, String.format(errorStringFormat, btDev.getName(), btDev.getAddress(), "bluetooth is disabled"));
            }
        } else {
            Log.e(TAG, String.format(errorStringFormat, btDev.getName(), btDev.getAddress(), "bluetooth is unavailable (adapter is null)"));
        }
    }

    public void disconnectFromDevice() {
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
        }
    }

    // Send data with bluetooth thread
    protected void sendData(byte[] bytes) {
        String errorStringFormat = "Cannot send data with bluetooth thread: %s";
        if(bluetoothAdapter != null) {
            if (bluetoothSocket.isConnected() && (bluetoothThread != null)) {
                bluetoothThread.write(bytes);
            } else {
                Log.e(TAG, String.format(errorStringFormat, "bluetooth socket is not connected or bluetooth thread is null"));
            }
        } else {
            Log.e(TAG, String.format(errorStringFormat, "bluetooth is unavailable (adapter is null)"));
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
