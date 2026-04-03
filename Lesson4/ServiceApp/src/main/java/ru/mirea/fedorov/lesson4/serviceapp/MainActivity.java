package ru.mirea.fedorov.lesson4.serviceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.mirea.fedorov.lesson4.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 200;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermissionIfNeeded();

        binding.buttonPlay.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, PlayerService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            binding.textStatus.setText("Статус: сервис запущен, аудио воспроизводится");
        });

        binding.buttonStop.setOnClickListener(v -> {
            stopService(new Intent(this, PlayerService.class));
            binding.textStatus.setText("Статус: сервис остановлен");
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_CODE
            );
        }
    }
}
