package fr.isep.embeddedgpu.application.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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
import fr.isep.embeddedgpu.application.driving.DrivingService;
import fr.isep.embeddedgpu.application.fragments.BluetoothFragment;
import fr.isep.embeddedgpu.application.fragments.DrivingFragment;
import fr.isep.embeddedgpu.application.fragments.SettingsFragment;

import static fr.isep.embeddedgpu.application.bluetooth.BluetoothService.REQUEST_ENABLE_BLUETOOTH;
import static fr.isep.embeddedgpu.application.bluetooth.BluetoothService.REQUEST_ENABLE_BLUETOOTH_ADMIN;

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
                getRequestPermissionResult(Manifest.permission.BLUETOOTH, resultCode);
                break;
            case REQUEST_ENABLE_BLUETOOTH_ADMIN:
                getRequestPermissionResult(Manifest.permission.BLUETOOTH_ADMIN, resultCode);
                break;
            default:
                Log.d(TAG, String.format("Unknown request code %d (with result code %d)", requestCode, resultCode));
                break;
        }
    }

    private void getRequestPermissionResult(@NonNull String permission, int resultCode) {
        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, String.format("Permission not granted: %s", permission));
        } else {
            Log.d(TAG, String.format("Permission granted: %s", permission));
        }
    }

    private void checkPermissions() {
        checkPermission(this, Manifest.permission.BLUETOOTH, REQUEST_ENABLE_BLUETOOTH);
        checkPermission(this, Manifest.permission.BLUETOOTH_ADMIN, REQUEST_ENABLE_BLUETOOTH_ADMIN);
    }

    private void checkPermission(@NonNull Activity activity, @NonNull String permission, int requestCode) {
        Log.d(TAG, String.format("Checking permission %s", permission));
        int permissionState = ContextCompat.checkSelfPermission(activity, permission);
        switch(permissionState) {
            case PackageManager.PERMISSION_DENIED:
                Log.d(TAG, String.format("%s permission denied: send permission request", permission));
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                break;
            case PackageManager.PERMISSION_GRANTED:
                Log.d(TAG, String.format("%s permission granted", permission));
                break;
            default:
                Log.d(TAG, String.format("Unknown state (%d) for permission %s", permissionState, permission));
                break;
        }
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeServices();
        initializeFragments();
        initializeNavigation();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeServices() {
        bluetoothService = new BluetoothService();
        drivingService = new DrivingService();
    }

    private void initializeFragments() {
        // instantiate each fragment
        bluetoothFragment = new BluetoothFragment(bluetoothService);
        drivingFragment = new DrivingFragment(bluetoothService, drivingService);
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
    private void initializeNavigation() {
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