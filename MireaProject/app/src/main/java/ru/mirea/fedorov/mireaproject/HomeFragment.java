package ru.mirea.fedorov.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ru.mirea.fedorov.mireaproject.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) {
            return;
        }
        binding.textPermissions.setText(getString(
                R.string.permissions_template,
                permissionState(Manifest.permission.CAMERA),
                permissionState(Manifest.permission.RECORD_AUDIO),
                locationPermissionState()
        ));
    }

    private String permissionState(String permission) {
        return ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED
                ? getString(R.string.permission_granted)
                : getString(R.string.permission_denied);
    }

    private String locationPermissionState() {
        boolean fineLocationGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        return fineLocationGranted || coarseLocationGranted
                ? getString(R.string.permission_granted)
                : getString(R.string.permission_denied);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
