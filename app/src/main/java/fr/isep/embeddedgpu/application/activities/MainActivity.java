package fr.isep.embeddedgpu.application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Objects;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothThread;
import fr.isep.embeddedgpu.application.fragments.BluetoothFragment;
import fr.isep.embeddedgpu.application.fragments.SettingsFragment;
import fr.isep.embeddedgpu.application.fragments.TrexFragment;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothProperties.REQUEST_ENABLE_BLUETOOTH;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothThread.RESPONSE_MESSAGE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MAIN ACTIVITY]";

    // Fragments
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BluetoothFragment bluetoothFragment;
    private TrexFragment trexFragment;
    private SettingsFragment settingsFragment;
    private Fragment activeFragment;

    // Services
    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result is ok and bluetooth enabled initialize bluetooth process
        if((resultCode == RESULT_OK) && (requestCode == REQUEST_ENABLE_BLUETOOTH)){
            //bluetoothService.initializeBluetoothProcess();
        }
    }

    private void checkPermissions() {
        // bluetooth permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "bluetooth permission denied: send permission request");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
        }

        // admin bluetooth permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "admin bluetooth permission denied: send permission request");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
        }
    }

    private void initialize() {
        initializeServices();
        initializeFragments();
        initializeUI();
    }

    private void initializeServices() {
        initializeBluetooth();
    }

    private void initializeBluetooth() {
        bluetoothService = new BluetoothService();
        Log.d(TAG, "trying to get bluetooth adapter");
        if (bluetoothService.getBluetoothAdapter() != null) {
            Log.d(TAG, "checking if bluetooth is enabled");
            if(!bluetoothService.getBluetoothAdapter().isEnabled()) {
                Log.d(TAG, "bluetooth is disabled, asking user to enable it");
                // if bluetooth is not enabled create intent for user to turn it on
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            } else {
                Log.d(TAG, "bluetooth is enabled");
                //trexService.initializeBluetoothProcess();
            }
        } else {
            Log.e(TAG, "error occurred while activating bluetooth: bluetooth adapter is null");
        }
    }

    private void initializeFragments() {
        // instantiate each fragment
        bluetoothFragment = new BluetoothFragment(bluetoothService);
        trexFragment = new TrexFragment();
        settingsFragment = new SettingsFragment();
        // declare default active fragment
        activeFragment = bluetoothFragment;
        // add all fragments in fragment manager
        addAllFragments();
    }

    private void addAllFragments() {
        // initialize and hide all fragments
        addFragment(bluetoothFragment, BluetoothFragment.TAG);
        addFragment(trexFragment, TrexFragment.TAG);
        addFragment(settingsFragment, SettingsFragment.TAG);
        // show active fragment
        fragmentManager.beginTransaction().show(activeFragment).commit();
    }

    private void addFragment(Fragment fragment, String tag) {
        fragmentManager.
                beginTransaction().
                add(R.id.main_container, fragment, tag).
                hide(fragment).
                commit();
    }

    @SuppressLint("NonConstantResourceId")
    private void initializeUI() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_bluetooth:
                    return switchFragment(bluetoothFragment, BluetoothFragment.TITLE);
                case R.id.menu_item_trex:
                    return switchFragment(trexFragment, TrexFragment.TITLE);
                case R.id.menu_item_settings:
                    return switchFragment(settingsFragment, SettingsFragment.TITLE);
                default:
                    break;
            }
            return false;
        });
    }

    // Function used by navigation to switch fragment
    private boolean switchFragment(Fragment fragment, String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
        return true;
    }
}