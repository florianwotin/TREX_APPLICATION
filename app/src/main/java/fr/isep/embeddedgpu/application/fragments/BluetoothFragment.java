package fr.isep.embeddedgpu.application.fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;

import static android.app.Activity.RESULT_CANCELED;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothService.REQUEST_MAKE_DISCOVERABLE;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothService.REQUEST_TURN_ON_BLUETOOTH;
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
                if (resultCode == RESULT_CANCELED) {
                    shortToast(this.getContext(), R.string.toast_bluetooth_turn_on_refused).show();
                    turnOffBluetoothUI();
                } else {
                    shortToast(this.getContext(), R.string.toast_bluetooth_is_on).show();
                    turnOnBluetoothUI();
                }
                break;
            case REQUEST_MAKE_DISCOVERABLE:
                if (resultCode == RESULT_CANCELED) {
                    shortToast(this.getContext(), R.string.toast_bluetooth_make_discoverable_refused).show();
                } else {
                    // if bluetooth is disabled, this request will enable it so update UI
                    if (bluetoothService.getBluetoothAdapter().isEnabled()) {
                        shortToast(this.getContext(), R.string.toast_bluetooth_is_on).show();
                        turnOnBluetoothUI();
                    }
                    // display device is discoverable
                    shortToast(this.getContext(), R.string.toast_bluetooth_is_discoverable).show();
                }
                break;
            default:
                Log.d(TAG, String.format("Unknown request code %d (with result code %d)", requestCode, resultCode));
                break;
        }
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeState();
        initializeDiscoverable();
        initializeBluetoothDevices();
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
        // default state: check if bluetooth is available and already enabled
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
                shortToast(this.getContext(), R.string.toast_bluetooth_turn_on).show();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_TURN_ON_BLUETOOTH);
            } else {
                Log.d(TAG, "Bluetooth is already enabled");
            }
        } else {
            Log.e(TAG, "Cannot enable bluetooth: bluetooth is unavailable (adapter is null)");
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
                shortToast(this.getContext(), R.string.toast_bluetooth_turn_off).show();
                bluetoothAdapter.disable();
                turnOffBluetoothUI();
                shortToast(this.getContext(), R.string.toast_bluetooth_is_off).show();
            } else {
                Log.d(TAG, "Bluetooth is already disabled");
            }
        } else {
            Log.e(TAG, "Cannot disable bluetooth: bluetooth is unavailable (adapter is null)");
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
        shortToast(this.getContext(), R.string.toast_bluetooth_make_discoverable).show();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent, REQUEST_MAKE_DISCOVERABLE);
    }

    private void initializeBluetoothDevices() {
        Button bluetoothDevicesButton = root.findViewById(R.id.bluetooth_devices_button);
        bluetoothDevicesButton.setOnClickListener(v -> {
            if (bluetoothService.getBluetoothAdapter() != null) {
                if (bluetoothService.getBluetoothAdapter().isEnabled()) {
                    Log.d(TAG, "Displaying bluetooth devices in alert dialog");
                    displayBluetoothDevicesPopup();
                } else {
                    Log.d(TAG, "Cannot display bluetooth devices: bluetooth is disabled");
                }
            } else {
                Log.e(TAG, "Cannot display bluetooth devices: bluetooth is unavailable (adapter is null)");
            }
        });
    }

    private void displayBluetoothDevicesPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.dialog_bluetooth_devices_title);

        // get bonded devices
        Set<BluetoothDevice> btDevices = bluetoothService.getBluetoothAdapter().getBondedDevices();
        if (btDevices.size() == 0) {
            // if no bonded device is returned, display no device text in alert dialog message
            Log.d(TAG, "There is no bonded device available");
            builder.setMessage(R.string.dialog_bluetooth_devices_no_dev);
        } else {
            // if one or more bonded device/s is/are returned, display list
            Log.d(TAG, String.format("There is/are %d bonded device/s available", btDevices.size()));
            ListView listView = new ListView(this.getContext());
            listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<BluetoothDevice>(this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<>(btDevices));
            listView.setAdapter(adapter);
            builder.setView(listView);
        }
        
        // button to cancel
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // create and show
        builder.create();
        builder.show();
    }
}