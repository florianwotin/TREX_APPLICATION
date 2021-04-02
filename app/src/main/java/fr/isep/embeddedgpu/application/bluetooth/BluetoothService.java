package fr.isep.embeddedgpu.application.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothThread.RESPONSE_MESSAGE;

public class BluetoothService {
    private static final String TAG = "[BLUETOOTH SERVICE]";

    // UUID
    public static final UUID PHONE_SECURE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
    protected boolean connected;

    public BluetoothService() {
        connected = false;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(this.bluetoothAdapter != null){
            Log.d(TAG, "bluetooth is available");
        } else {
            Log.d(TAG, "bluetooth is unavailable (adapter is null)");
        }
    }

    public void connectToDevice(BluetoothDevice btDev) {
        String errorStringFormat = "Cannot connect to device %s (%s): %s";
        if(bluetoothAdapter != null) {
            if(bluetoothAdapter.isEnabled()) {
                // try to create bluetooth socket
                try {
                    Log.d(TAG, "Trying to create bluetooth socket");
                    bluetoothSocket = btDev.createRfcommSocketToServiceRecord(PHONE_SECURE_UUID);
                    bluetoothSocket.connect();
                    connected = true;
                    Log.d(TAG, String.format("Connected to device %s (%s)", btDev.getName(), btDev.getAddress()));

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
                } catch (IOException e) {
                    Log.e(TAG, String.format("Cannot create bluetooth socket from device %s (%s) with UUID = %s", btDev.getName(), btDev.getAddress(), PHONE_SECURE_UUID.toString()));
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, String.format(errorStringFormat, btDev.getName(), btDev.getAddress(), "Bluetooth is disabled"));
            }
        } else {
            Log.e(TAG, String.format(errorStringFormat, btDev.getName(), btDev.getAddress(), "Bluetooth is unavailable (adapter is null)"));
        }
    }

    public void disconnectFromDevice() {
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
        }
    }

    // Send data with bluetooth thread
    public void sendData(byte[] bytes) {
        String errorStringFormat = "Cannot send data with bluetooth thread: %s";
        if(bluetoothAdapter != null) {
            if (bluetoothSocket != null) {
                if (bluetoothSocket.isConnected() && (bluetoothThread != null)) {
                    bluetoothThread.write(bytes);
                } else {
                    Log.e(TAG, String.format(errorStringFormat, "Bluetooth socket is not connected or bluetooth thread is null"));
                }
            } else {
                Log.e(TAG, String.format(errorStringFormat, "Bluetooth socket is null"));
            }
        } else {
            Log.e(TAG, String.format(errorStringFormat, "Bluetooth is unavailable (adapter is null)"));
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public boolean isConnected(){return connected;}
}
