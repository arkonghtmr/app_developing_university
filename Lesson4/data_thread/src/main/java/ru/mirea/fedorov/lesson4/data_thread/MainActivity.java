package ru.mirea.fedorov.lesson4.data_thread;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.fedorov.lesson4.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStartDemo.setOnClickListener(v -> startDemo());
        startDemo();
    }

    private void startDemo() {
        binding.buttonStartDemo.setEnabled(false);
        binding.textInfo.setText("Старт фонового потока.\n");
        binding.textInfo.append("runOnUiThread и post ставят задачу в UI-очередь сразу.\n");
        binding.textInfo.append("postDelayed ставит задачу в UI-очередь с задержкой.\n");

        final Runnable runn1 = () ->
                binding.textInfo.append("\n1. runOnUiThread: Activity отправила Runnable напрямую в UI-поток.");

        final Runnable runn2 = () ->
                binding.textInfo.append("\n2. post: View отправил Runnable в свою очередь на ближайшее выполнение.");

        final Runnable runn3 = () -> {
            binding.textInfo.append("\n3. postDelayed: Runnable выполнился последним, потому что задержан на 2 секунды.");
            binding.textInfo.append("\nИтоговая последовательность: runOnUiThread -> post -> postDelayed.");
            binding.buttonStartDemo.setEnabled(true);
        };

        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                runOnUiThread(runn1);
                TimeUnit.SECONDS.sleep(1);
                binding.textInfo.postDelayed(runn3, 2000);
                binding.textInfo.post(runn2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
    }
}
