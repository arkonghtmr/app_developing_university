package ru.mirea.fedorovas.activitylifecycle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.util.Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        android.util.Log.i(TAG, "onStart()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        android.util.Log.i(TAG, "onRestoreInstanceState()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.util.Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        android.util.Log.i(TAG, "onPause()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        android.util.Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        android.util.Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        android.util.Log.i(TAG, "onRestart()");
    }
}