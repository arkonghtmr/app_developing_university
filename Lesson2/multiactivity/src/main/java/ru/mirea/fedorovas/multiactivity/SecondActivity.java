package ru.mirea.fedorovas.multiactivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        android.widget.TextView textView = findViewById(R.id.textViewResult);

        String text = getIntent().getStringExtra("key");

        if (text != null) {
            textView.setText(text);
        }
    }
}