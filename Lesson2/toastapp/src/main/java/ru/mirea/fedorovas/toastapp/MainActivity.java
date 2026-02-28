package ru.mirea.fedorovas.toastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void calculateChars(View view) {
        EditText editText = findViewById(R.id.editTextInput);
        int length = editText.getText().length();

        String message = "ФËДОРОВ А.С. БСБО-09-23\nКоличество символов: " + length;

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}