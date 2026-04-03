package ru.mirea.fedorov.lesson4.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.Locale;

public class MyLooper extends Thread {

    public static final String TAG = "MyLooper";
    public static final String KEY_AGE = "AGE";
    public static final String KEY_JOB = "JOB";

    public Handler mHandler;
    private final Handler mainHandler;
    private Looper looper;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        Looper.prepare();
        looper = Looper.myLooper();
        mHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                int age = msg.getData().getInt(KEY_AGE);
                String job = msg.getData().getString(KEY_JOB, "не указано");

                SystemClock.sleep(age * 1000L);

                String result = String.format(
                        Locale.getDefault(),
                        "Возраст: %d, работа: %s, задержка: %d сек.",
                        age,
                        job,
                        age
                );
                Log.d(TAG, "Результат вычисления: " + result);

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        };
        Looper.loop();
    }

    public void quitLooper() {
        if (looper != null) {
            looper.quitSafely();
        }
    }
}
