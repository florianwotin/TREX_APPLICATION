package fr.isep.embeddedgpu.application.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

// This class is inspired by ConnectedThread class from serge144 (found on github)
// Link to the original class : https://github.com/serge144/ArduinoConnectorBT/blob/master/java/projects/pers/sbp/ardcon/ConnectedThread.java

public class BluetoothThread extends Thread {
    // Private class attributes
    private static final String TAG = "[BLUETOOTH THREAD]";

    // Public class attributes
    public static final int RESPONSE_MESSAGE = 10;

    // Protected instance attributes
    protected final BluetoothSocket socket;
    protected final InputStream inputStream;
    protected final OutputStream outputStream;

    // Private instance attributes
    private Handler handler;

    // Constructor
    public BluetoothThread(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // try to get input stream
        Log.d(TAG, "Try to get input stream");
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while trying to get input stream:"+e.getMessage());
            e.printStackTrace();
        }

        // try to get output stream
        Log.d(TAG, "Try to get output stream");
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while trying to get output stream:" + e.getMessage());
            e.printStackTrace();
        }

        // set input and output streams
        inputStream = tmpIn;
        outputStream = tmpOut;

        // try to flush output stream
        Log.d(TAG, "Try to flush output stream");
        try {
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while trying to flush output stream:" + e.getMessage());
            e.printStackTrace();
        }
        
        Log.d(TAG, "IO's obtained");
    }

    // Thread run function. Loop on message reception
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Log.d(TAG, "Starting thread");
        while(true) {
            try {
                Message message = new Message();
                message.what = RESPONSE_MESSAGE;
                message.obj = reader.readLine();
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred while reading response:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            Log.d(TAG, "These bytes are written on output stream: " + Arrays.toString(bytes));
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while writing bytes on output stream:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancel() {
        Log.d(TAG, "Canceling thread");
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while canceling thread (closing socket)" + e.getMessage());
            e.printStackTrace();
        }
    }
}
