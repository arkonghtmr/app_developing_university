package ru.mirea.fedorov.lesson4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.fedorov.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonPlay.setOnClickListener(v ->
                binding.textStatus.setText("Статус: воспроизведение макета плеера"));

        binding.buttonPause.setOnClickListener(v ->
                binding.textStatus.setText("Статус: пауза, можно переключать ориентацию"));

        binding.buttonNext.setOnClickListener(v -> {
            binding.textTrackTitle.setText("Campus Night Drive");
            binding.textArtist.setText("Anton Fedorov Mix");
            binding.textStatus.setText("Статус: выбрана следующая композиция");
            binding.seekPlayback.setProgress(10);
        });
    }
}
