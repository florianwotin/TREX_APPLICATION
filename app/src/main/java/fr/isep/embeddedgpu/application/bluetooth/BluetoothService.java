package fr.isep.embeddedgpu.application.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothThread.RESPONSE_MESSAGE;

public class BluetoothService {
    private static final String TAG = "[BLUETOOTH SERVICE]";
    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothThread bluetoothThread;
    protected Handler handler;

    public BluetoothService() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connectToDevice(BluetoothDevice bluetoothDevice) {
        String errorStringFormat = "cannot connect to device %s: %s";
        if(bluetoothAdapter != null) {
            if(bluetoothAdapter.isEnabled()) {
                // try to create bluetooth socket
                try {
                    Log.d(TAG, "trying to create bluetooth socket");
                    //bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord();
                    bluetoothSocket.connect();
                    Log.d(TAG, "connected to device " + bluetoothDevice.getName());
                } catch (IOException e) {
                    //Log.e(TAG, String.format("cannot create bluetooth socket from DEVICE[UUID=%s | MAC=%s]", MODULE_BLUETOOTH_UUID.toString(), MODULE_BLUETOOTH_MAC_ADDRESS));
                    e.printStackTrace();
                }

                // attempt to create handler
                Log.d(TAG, "attempt to create handler");
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        if(message.what == RESPONSE_MESSAGE) {
                            String response = (String)message.obj;
                            Log.d(TAG, String.format("handler received: \"%s\"", response));
                        }
                    }
                };

                // attempt to create and run bluetooth thread
                Log.d(TAG, "attempt to create and start bluetooth thread");
                bluetoothThread = new BluetoothThread(bluetoothSocket, handler);
                bluetoothThread.start();
            } else {
                Log.e(TAG, String.format(errorStringFormat, bluetoothDevice.getName(), "bluetooth is disabled"));
            }
        } else {
            Log.e(TAG, String.format(errorStringFormat, bluetoothDevice.getName(), "bluetooth is unavailable (adapter is null)"));
        }
    }

    // Send data with bluetooth thread
    protected void sendData(byte[] bytes) {
        String errorStringFormat = "cannot send data with bluetooth thread: %s";
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
