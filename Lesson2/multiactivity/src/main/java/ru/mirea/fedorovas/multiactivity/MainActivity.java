package ru.mirea.fedorovas.multiactivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickSendToSecond(android.view.View view) {
        android.widget.EditText editText = findViewById(R.id.editTextName);
        String fio = editText.getText().toString();

        android.content.Intent intent = new android.content.Intent(this, SecondActivity.class);
        intent.putExtra("key", "МИРЭА - " + fio);

        startActivity(intent);
    }
}