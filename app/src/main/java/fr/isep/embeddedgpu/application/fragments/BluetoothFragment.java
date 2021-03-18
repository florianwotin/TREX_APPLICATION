package fr.isep.embeddedgpu.application.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;

public class BluetoothFragment extends Fragment {
    public static final String TAG = "[BLUETOOTH FRAGMENT]";

    // Services
    protected BluetoothService bluetoothService;

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
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        //initializeUI(root);
        return root;
    }
}