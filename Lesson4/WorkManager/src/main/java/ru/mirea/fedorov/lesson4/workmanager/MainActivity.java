package ru.mirea.fedorov.lesson4.workmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import ru.mirea.fedorov.lesson4.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStartWorker.setOnClickListener(v -> startWorker());
    }

    private void startWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(uploadWorkRequest);

        binding.textStatus.setText("Задача поставлена в очередь. Требование: есть интернет.");

        workManager.getWorkInfoByIdLiveData(uploadWorkRequest.getId()).observe(this, workInfo -> {
            if (workInfo == null) {
                return;
            }
            WorkInfo.State state = workInfo.getState();
            binding.textStatus.setText("Текущий статус Worker: " + state.name());
        });
    }
}
