package fr.isep.embeddedgpu.application.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;

import static fr.isep.embeddedgpu.application.requests.RequestsCodes.REQUEST_ENABLE_BLUETOOTH;

public class BluetoothFragment extends Fragment {
    public static final String TAG = "[BLUETOOTH FRAGMENT]";

    // Services
    protected BluetoothService bluetoothService;

    // UI
    View root;
    Button bluetoothStateButton;
    TextView bluetoothStateText;
    ImageView bluetoothStateImage;

    // Flags
    protected boolean bluetoothActive;

    public BluetoothFragment(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        initialize();
        return root;
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeState();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeState() {
        bluetoothStateButton = root.findViewById(R.id.bluetooth_state_button);
        bluetoothStateText = root.findViewById(R.id.bluetooth_state_text);
        bluetoothStateImage = root.findViewById(R.id.bluetooth_state_image);
        bluetoothStateButton.setOnClickListener(v -> {
            if (bluetoothService.getBluetoothAdapter() != null) {
                if (bluetoothActive) {
                    turnOffBluetooth();
                } else {
                    turnOnBluetooth();
                }
            } else {
                Log.e(TAG, "Cannot switch bluetooth state: bluetooth is unavailable (adapter is null)");
            }
        });
        // default state
        BluetoothAdapter bluetoothAdapter = bluetoothService.getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                turnOnBluetoothUI();
            } else {
                turnOffBluetoothUI();
            }
        } else {
            turnOffBluetoothUI();
        }
    }

    private void turnOnBluetooth() {
        Toast.makeText(root.getContext(), R.string.toast_bluetooth_turn_on, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
        turnOnBluetoothUI();
    }

    private void turnOnBluetoothUI() {
        bluetoothActive = true;
        bluetoothStateButton.setText(R.string.button_disable);
        bluetoothStateText.setText(R.string.bluetooth_enabled);
        bluetoothStateImage.setImageResource(R.drawable.bluetooth_enabled_foreground);
    }

    private void turnOffBluetooth() {
        Toast.makeText(root.getContext(), R.string.toast_bluetooth_turn_off, Toast.LENGTH_SHORT).show();
        turnOffBluetoothUI();
    }

    private void turnOffBluetoothUI() {
        bluetoothActive = false;
        bluetoothStateButton.setText(R.string.button_enable);
        bluetoothStateText.setText(R.string.bluetooth_disabled);
        bluetoothStateImage.setImageResource(R.drawable.bluetooth_disabled_foreground);
    }
}