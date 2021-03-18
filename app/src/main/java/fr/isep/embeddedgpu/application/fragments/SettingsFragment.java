package fr.isep.embeddedgpu.application.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.driving.DrivingService;

public class SettingsFragment extends Fragment {
    public static final String TAG = "[SETTINGS FRAGMENT]";

    // Services
    protected DrivingService drivingService;

    // UI
    private View root;

    public SettingsFragment(DrivingService drivingService) {
        this.drivingService = drivingService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        initialize();
        return root;
    }

    private void initialize() {
        Log.d(TAG, "Starting initialization");
        initializeMaxSpeedPercent();
        initializeMaxAccelerationPercent();
        Log.d(TAG, "Initialization OK");
    }

    private void initializeMaxSpeedPercent() {
        SeekBar maxSpeedPercentSlider = root.findViewById(R.id.trex_speed_slider);
        maxSpeedPercentSlider.setMin(DrivingService.MIN_SPEED_PERCENT);
        maxSpeedPercentSlider.setMax(DrivingService.MAX_SPEED_PERCENT);
        maxSpeedPercentSlider.setProgress(drivingService.getSpeedPercent());
        maxSpeedPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drivingService.setSpeedPercent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // empty for now
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // empty for now
            }
        });
    }

    private void initializeMaxAccelerationPercent() {
        SeekBar maxAccelerationPercentSlider = root.findViewById(R.id.trex_accel_slider);
        maxAccelerationPercentSlider.setMin(DrivingService.MIN_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setMax(DrivingService.MAX_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setProgress(drivingService.getAccelerationPercent());
        maxAccelerationPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drivingService.setAccelerationPercent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // empty for now
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // empty for now
            }
        });
    }
}