package ru.mirea.fedorov.mireaproject;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.fedorov.mireaproject.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private Uri imageUri;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (binding == null) {
                    return;
                }
                if (isGranted) {
                    launchCamera();
                } else {
                    binding.textCameraStatus.setText(R.string.camera_permission_denied_message);
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (binding == null) {
                    return;
                }
                if (success && imageUri != null) {
                    binding.imagePreview.setImageURI(imageUri);
                    String note = binding.editPhotoNote.getText().toString().trim();
                    if (TextUtils.isEmpty(note)) {
                        note = getString(R.string.camera_default_note);
                    }
                    binding.textPhotoNotePreview.setText(getString(R.string.camera_note_template, note));
                    binding.textCameraStatus.setText(R.string.camera_success_message);
                } else {
                    binding.textCameraStatus.setText(R.string.camera_cancelled_message);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonTakePhoto.setOnClickListener(v -> checkPermissionAndLaunch());
        binding.imagePreview.setOnClickListener(v -> checkPermissionAndLaunch());
    }

    private void checkPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        if (binding == null) {
            return;
        }
        try {
            File photoFile = createImageFile();
            String authorities = requireContext().getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
            cameraLauncher.launch(imageUri);
        } catch (IOException e) {
            binding.textCameraStatus.setText(getString(R.string.camera_error_template, e.getMessage()));
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "PHOTO_" + timeStamp + "_";
        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
