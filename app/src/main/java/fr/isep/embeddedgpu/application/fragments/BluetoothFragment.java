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

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;

import static android.app.Activity.RESULT_OK;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothRequestsCodes.REQUEST_MAKE_DISCOVERABLE;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothRequestsCodes.REQUEST_TURN_ON_BLUETOOTH;
import static fr.isep.embeddedgpu.application.utils.ToastUtils.shortToast;

public class BluetoothFragment extends Fragment {
    public static final String TAG = "[BLUETOOTH FRAGMENT]";

    // Services
    protected BluetoothService bluetoothService;

    // UI
    protected View root;
    protected Button bluetoothStateButton;
    protected TextView bluetoothStateText;
    protected ImageView bluetoothStateImage;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_TURN_ON_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    shortToast(root.getContext(), R.string.toast_bluetooth_is_on).show();
                    turnOnBluetoothUI();
                }
                break;
            case REQUEST_MAKE_DISCOVERABLE:
                if (resultCode == RESULT_OK) {
                    shortToast(root.getContext(), R.string.toast_bluetooth_is_discoverable).show();
                }
            default:
                Log.d(TAG, String.format("Unknown request code %d (with result %d)", requestCode, resultCode));
                break;
        }
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeState();
        initializeDiscoverable();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeState() {
        bluetoothStateButton = root.findViewById(R.id.bluetooth_state_button);
        bluetoothStateText = root.findViewById(R.id.bluetooth_state_text);
        bluetoothStateImage = root.findViewById(R.id.bluetooth_state_image);
        bluetoothStateButton.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = bluetoothService.getBluetoothAdapter();
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
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
        if ((bluetoothAdapter != null) && bluetoothAdapter.isEnabled()) {
                turnOnBluetoothUI();
        } else {
            turnOffBluetoothUI();
        }
    }

    private void turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = bluetoothService.getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                shortToast(root.getContext(), R.string.toast_bluetooth_turn_on).show();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_TURN_ON_BLUETOOTH);
            } else {
                Log.d(TAG, "Bluetooth is already on");
            }
        } else {
            Log.e(TAG, "Cannot turn on bluetooth: bluetooth is unavailable (adapter is null)");
        }
    }

    private void turnOnBluetoothUI() {
        bluetoothStateButton.setText(R.string.button_disable);
        bluetoothStateText.setText(R.string.bluetooth_enabled);
        bluetoothStateImage.setImageResource(R.drawable.bluetooth_enabled_foreground);
    }

    private void turnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = bluetoothService.getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                shortToast(root.getContext(), R.string.toast_bluetooth_turn_off).show();
                bluetoothAdapter.disable();
                shortToast(root.getContext(), R.string.toast_bluetooth_is_off).show();
            } else {
                Log.d(TAG, "Bluetooth is already off");
            }
        } else {
            Log.e(TAG, "Cannot turn off bluetooth: bluetooth is unavailable (adapter is null)");
        }
    }

    private void turnOffBluetoothUI() {
        bluetoothStateButton.setText(R.string.button_enable);
        bluetoothStateText.setText(R.string.bluetooth_disabled);
        bluetoothStateImage.setImageResource(R.drawable.bluetooth_disabled_foreground);
    }

    private void initializeDiscoverable() {
        Button discoverableButton = root.findViewById(R.id.bluetooth_discoverable_button);
        discoverableButton.setOnClickListener(v -> {
            if (bluetoothService.getBluetoothAdapter() != null) {
                if (!bluetoothService.getBluetoothAdapter().isDiscovering()) {
                    makeDeviceDiscoverable();
                } else {
                    Log.d(TAG, "Device is already discoverable");
                }
            } else {
                Log.e(TAG, "Cannot make device discoverable: bluetooth is unavailable (adapter is null)");
            }
        });
    }

    private void makeDeviceDiscoverable() {
        shortToast(root.getContext(), R.string.toast_bluetooth_make_discoverable).show();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent, REQUEST_MAKE_DISCOVERABLE);
    }
}