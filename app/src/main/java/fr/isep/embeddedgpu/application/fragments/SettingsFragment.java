package fr.isep.embeddedgpu.application.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import fr.isep.embeddedgpu.application.R;

public class SettingsFragment extends Fragment {
    public static final String TAG = "[SETTINGS FRAGMENT]";
    public static final String TITLE = "PARAMETRES";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        //initializeUI(root);
        return root;
    }
}