package ru.mirea.fedorov.lesson4.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.fedorov.lesson4.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LooperActivity";

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result", "Нет результата");
                Log.d(TAG, result);
                binding.textStatus.setText(result);
            }
        };

        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.buttonSend.setOnClickListener(v -> sendMessageToLooper());
    }

    private void sendMessageToLooper() {
        String ageString = String.valueOf(binding.editTextAge.getText()).trim();
        String jobString = String.valueOf(binding.editTextJob.getText()).trim();

        if (ageString.isEmpty() || jobString.isEmpty()) {
            Toast.makeText(this, "Введите возраст и род деятельности", Toast.LENGTH_SHORT).show();
            return;
        }

        if (myLooper.mHandler == null) {
            Toast.makeText(this, "Looper ещё инициализируется", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageString);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt(MyLooper.KEY_AGE, age);
        bundle.putString(MyLooper.KEY_JOB, jobString);
        msg.setData(bundle);

        binding.textStatus.setText("Сообщение отправлено в MessageQueue");
        myLooper.mHandler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLooper != null) {
            myLooper.quitLooper();
        }
    }
}
