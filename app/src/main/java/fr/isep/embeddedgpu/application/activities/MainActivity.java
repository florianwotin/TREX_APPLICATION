package fr.isep.embeddedgpu.application.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.bluetooth.BluetoothService;
import fr.isep.embeddedgpu.application.fragments.BluetoothFragment;
import fr.isep.embeddedgpu.application.fragments.SettingsFragment;
import fr.isep.embeddedgpu.application.fragments.DrivingFragment;
import fr.isep.embeddedgpu.application.driving.DrivingService;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothRequestsCodes.REQUEST_ENABLE_BLUETOOTH;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothRequestsCodes.REQUEST_ENABLE_BLUETOOTH_ADMIN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MAIN ACTIVITY]";

    // Fragments
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BluetoothFragment bluetoothFragment;
    private DrivingFragment drivingFragment;
    private SettingsFragment settingsFragment;
    private Fragment activeFragment;

    // Services
    private BluetoothService bluetoothService;
    private DrivingService drivingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        checkPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    bluetoothService.setBluetoothEnabled(true);
                    Log.d(TAG, "Bluetooth is enabled");
                }
                break;
            case REQUEST_ENABLE_BLUETOOTH_ADMIN:
                if (resultCode == RESULT_OK) {
                    bluetoothService.setBluetoothAdminEnabled(true);
                    Log.d(TAG, "Bluetooth admin is enabled");
                }
                break;
            default:
                Log.d(TAG, String.format("Unknown request code %d (with result %d)", requestCode, resultCode));
                break;
        }
        // if all bluetooth permissions are OK initialize bluetooth process
        if(bluetoothService.isBluetoothEnabled() && bluetoothService.isBluetoothAdminEnabled()) {
            Log.d(TAG, "Starting bluetooth process: both bluetooth and admin bluetooth permissions are OK");
            //bluetoothService.initializeBluetoothProcess();
        }
    }

    private void checkPermissions() {
        int permission;
        // bluetooth permission
        Log.d(TAG, "Checking Bluetooth permission");
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        switch(permission) {
            case PackageManager.PERMISSION_DENIED:
                Log.d(TAG, "Bluetooth permission denied: send permission request");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BLUETOOTH);
                break;
            case PackageManager.PERMISSION_GRANTED:
                Log.d(TAG, "Bluetooth permission granted");
                break;
            default:
                Log.d(TAG, String.format("Unknown state for Bluetooth permission (%d)", permission));
                break;
        }

        // admin bluetooth permission
        Log.d(TAG, "Checking Bluetooth Admin permission");
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        switch(permission) {
            case PackageManager.PERMISSION_DENIED:
                Log.d(TAG, "Bluetooth Admin permission denied: send permission request");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ENABLE_BLUETOOTH_ADMIN);
                break;
            case PackageManager.PERMISSION_GRANTED:
                Log.d(TAG, "Bluetooth Admin permission granted");
                break;
            default:
                Log.d(TAG, String.format("Unknown state for Bluetooth Admin permission (%d)", permission));
                break;
        }
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeServices();
        initializeFragments();
        initializeUI();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeServices() {
        initializeBluetoothService();
        initializeDrivingService();
    }

    private void initializeBluetoothService() {
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

    private void initializeDrivingService() {
        drivingService = new DrivingService();
    }

    private void initializeFragments() {
        // instantiate each fragment
        bluetoothFragment = new BluetoothFragment(bluetoothService);
        drivingFragment = new DrivingFragment(drivingService);
        settingsFragment = new SettingsFragment(drivingService);
        // declare default active fragment
        activeFragment = bluetoothFragment;
        // add all fragments in fragment manager
        addAllFragments();
    }

    private void addAllFragments() {
        // initialize and hide all fragments
        addFragment(bluetoothFragment, BluetoothFragment.TAG);
        addFragment(drivingFragment, DrivingFragment.TAG);
        addFragment(settingsFragment, SettingsFragment.TAG);
        // show default fragment and set title
        fragmentManager.beginTransaction().show(activeFragment).commit();
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_bluetooth_fragment);
    }

    private void addFragment(Fragment fragment, String tag) {
        // fragments are added into the main container from main activity
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
                    return switchFragment(bluetoothFragment, R.string.app_bluetooth_fragment);
                case R.id.menu_item_trex:
                    return switchFragment(drivingFragment, R.string.app_driving_fragment);
                case R.id.menu_item_settings:
                    return switchFragment(settingsFragment, R.string.app_settings_fragment);
                default:
                    break;
            }
            return false;
        });
    }

    // Function used by navigation to switch fragment
    private boolean switchFragment(Fragment fragment, @StringRes int titleID) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(titleID);
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
        return true;
    }
}