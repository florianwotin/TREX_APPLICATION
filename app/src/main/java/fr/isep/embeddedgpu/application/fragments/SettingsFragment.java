package fr.isep.embeddedgpu.application.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;
import fr.isep.embeddedgpu.application.trex.TrexService;

public class SettingsFragment extends Fragment {
    public static final String TAG = "[SETTINGS FRAGMENT]";
    public static final String TITLE = "PARAMETRES";

    // Services
    protected TrexService trexService;

    // UI
    private View root;

    public SettingsFragment(TrexService trexService) {
        this.trexService = trexService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeUI();
        return root;
    }

    private void initializeUI() {
        initializeMaxSpeedPercent();
        initializeMaxAccelerationPercent();
    }

    private void initializeMaxSpeedPercent() {
        SeekBar maxSpeedPercentSlider = root.findViewById(R.id.trex_speed_slider);
        maxSpeedPercentSlider.setMin(TrexService.MIN_SPEED_PERCENT);
        maxSpeedPercentSlider.setMax(TrexService.MAX_SPEED_PERCENT);
        maxSpeedPercentSlider.setProgress(trexService.getSpeedPercent());
        maxSpeedPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setSpeedPercent(progress);
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
        Log.d(TAG, "max speed percent is initialized");
    }

    private void initializeMaxAccelerationPercent() {
        SeekBar maxAccelerationPercentSlider = root.findViewById(R.id.trex_accel_slider);
        maxAccelerationPercentSlider.setMin(TrexService.MIN_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setMax(TrexService.MAX_ACCELERATION_PERCENT);
        maxAccelerationPercentSlider.setProgress(trexService.getAccelerationPercent());
        maxAccelerationPercentSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trexService.setAccelerationPercent(progress);
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
        Log.d(TAG, "max acceleration percent is initialized");
    }
}