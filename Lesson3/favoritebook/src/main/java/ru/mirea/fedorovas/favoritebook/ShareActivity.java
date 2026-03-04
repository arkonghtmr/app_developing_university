package ru.mirea.fedorovas.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView textView = findViewById(R.id.textViewDevBook);
            String book = extras.getString(MainActivity.KEY);
            textView.setText(String.format("Любимая книга разработчика – %s", book));
        }
    }

    public void sendResult(View view) {
        EditText editText = findViewById(R.id.editTextBook);
        String text = editText.getText().toString();
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, text);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}