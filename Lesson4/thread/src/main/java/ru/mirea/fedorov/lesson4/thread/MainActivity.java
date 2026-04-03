package ru.mirea.fedorov.lesson4.thread;

import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

import ru.mirea.fedorov.lesson4.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ThreadProject";

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();
        String initialName = mainThread.getName();
        binding.textThreadInfo.setText("Имя текущего потока: " + initialName);
        mainThread.setName("БСБО-09-23 | Фёдоров Антон | фильм: Interstellar");
        binding.textThreadInfo.append("\nНовое имя потока: " + mainThread.getName());
        binding.textThreadInfo.append("\nГруппа потоков: " + mainThread.getThreadGroup());

        Log.d(TAG, "Stack: " + Arrays.toString(mainThread.getStackTrace()));

        binding.buttonCalculate.setOnClickListener(v -> startBackgroundCalculation());
    }

    private void startBackgroundCalculation() {
        String totalClassesString = String.valueOf(binding.editTextTotalClasses.getText()).trim();
        String studyDaysString = String.valueOf(binding.editTextStudyDays.getText()).trim();
        if (totalClassesString.isEmpty() || studyDaysString.isEmpty()) {
            Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalClasses = Integer.parseInt(totalClassesString);
        int studyDays = Integer.parseInt(studyDaysString);
        if (studyDays <= 0) {
            Toast.makeText(this, "Количество учебных дней должно быть больше нуля", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.textAverageResult.setText("Идёт вычисление в дочернем потоке...");

        new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            int numberThread = counter++;
            Log.d(TAG, String.format(Locale.getDefault(),
                    "Запущен поток № %d студентом группы БСБО-09-23", numberThread));

            SystemClock.sleep(2000);
            double average = (double) totalClasses / studyDays;

            runOnUiThread(() -> binding.textAverageResult.setText(String.format(
                    Locale.getDefault(),
                    "Среднее количество пар в день за месяц: %.2f",
                    average
            )));

            Log.d(TAG, "Выполнен поток № " + numberThread);
        }).start();
    }
}
