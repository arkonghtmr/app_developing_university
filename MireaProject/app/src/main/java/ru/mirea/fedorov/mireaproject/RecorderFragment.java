package ru.mirea.fedorov.mireaproject;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import ru.mirea.fedorov.mireaproject.databinding.FragmentRecorderBinding;

public class RecorderFragment extends Fragment {

    private FragmentRecorderBinding binding;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File recordFile;
    private boolean isRecording;
    private boolean isPlaying;

    private final ActivityResultLauncher<String> audioPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (binding == null) {
                    return;
                }
                if (isGranted) {
                    startRecording();
                } else {
                    binding.textRecorderStatus.setText(R.string.audio_permission_denied_message);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecorderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recordFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "anton_fedorov_note.3gp");

        binding.buttonRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                requestPermissionAndRecord();
            }
        });

        binding.buttonPlay.setOnClickListener(v -> {
            if (isPlaying) {
                stopPlaying();
            } else {
                startPlaying();
            }
        });

        updateUi();
    }

    private void requestPermissionAndRecord() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startRecording() {
        releasePlayer();
        try {
            recorder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    ? new MediaRecorder(requireContext())
                    : new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(recordFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();

            isRecording = true;
            binding.textRecorderStatus.setText(getString(
                    R.string.audio_recording_started_template,
                    recordFile.getName()
            ));
        } catch (IOException | RuntimeException e) {
            releaseRecorder();
            binding.textRecorderStatus.setText(getString(
                    R.string.audio_error_template,
                    e.getMessage()
            ));
        }
        updateUi();
    }

    private void stopRecording() {
        if (recorder == null) {
            return;
        }
        try {
            recorder.stop();
            binding.textRecorderStatus.setText(R.string.audio_recording_finished_message);
        } catch (RuntimeException e) {
            binding.textRecorderStatus.setText(getString(
                    R.string.audio_error_template,
                    e.getMessage()
            ));
            if (recordFile.exists()) {
                // Delete a broken file if recording was interrupted too early.
                //noinspection ResultOfMethodCallIgnored
                recordFile.delete();
            }
        } finally {
            releaseRecorder();
            isRecording = false;
            updateUi();
        }
    }

    private void startPlaying() {
        if (!recordFile.exists()) {
            binding.textRecorderStatus.setText(R.string.audio_file_missing_message);
            return;
        }

        releasePlayer();
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFile.getAbsolutePath());
            player.prepare();
            player.start();
            isPlaying = true;
            binding.textRecorderStatus.setText(R.string.audio_play_started_message);
            player.setOnCompletionListener(mp -> {
                binding.textRecorderStatus.setText(R.string.audio_play_finished_message);
                stopPlaying();
            });
        } catch (IOException e) {
            binding.textRecorderStatus.setText(getString(
                    R.string.audio_error_template,
                    e.getMessage()
            ));
            releasePlayer();
        }
        updateUi();
    }

    private void stopPlaying() {
        if (player != null) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (RuntimeException ignored) {
                // Ignore player state races while the fragment is being closed.
            }
        }
        releasePlayer();
        isPlaying = false;
        updateUi();
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void updateUi() {
        if (binding == null) {
            return;
        }
        binding.buttonRecord.setText(isRecording ? R.string.audio_stop_record : R.string.audio_start_record);
        binding.buttonPlay.setText(isPlaying ? R.string.audio_stop_play : R.string.audio_start_play);
        binding.buttonPlay.setEnabled(!isRecording);
        binding.buttonRecord.setEnabled(!isPlaying);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
        if (isPlaying) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseRecorder();
        releasePlayer();
        binding = null;
    }
}
