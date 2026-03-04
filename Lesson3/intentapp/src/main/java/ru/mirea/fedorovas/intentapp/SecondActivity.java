package ru.mirea.fedorovas.intentapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String time = getIntent().getStringExtra("date_time");
        TextView textView = findViewById(R.id.textViewResult);

        // Ваш вариант 24, квадрат = 576
        String message = "КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ 576, а текущее время " + time;

        textView.setText(message);
    }
}