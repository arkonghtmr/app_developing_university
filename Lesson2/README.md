# Отчет по выполнению практической работы №2

### Автор: Фёдоров Антон Сергеевич
### Группа: БСБО-09-23

---

## Цель работы

Целью данной работы являлось освоение жизненного цикла компонента `Activity`, изучение механизмов взаимодействия между активностями с помощью `Intent`, а также работа с различными видами уведомлений для информирования пользователя.

## Структура проекта

Работа выполнена в рамках единого проекта `Lesson2`, состоящего из нескольких независимых модулей, каждый из которых решает отдельную учебную задачу:

-   **`ActivityLifecycle`**: Модуль для изучения и отслеживания состояний жизненного цикла `Activity`.
-   **`MultiActivity`**: Модуль для реализации перехода между двумя `Activity` с передачей данных.
-   **`IntentFilter`**: Модуль для освоения неявных `Intent` для вызова системных приложений.
-   **`ToastApp`**: Модуль для демонстрации всплывающих уведомлений `Toast`.
-   **`NotificationApp`**: Модуль для создания системных уведомлений в строке состояния.

---

## Выполненные задания

### Задание 1. Изучение жизненного цикла Activity (модуль `ActivityLifecycle`)

**Задача:** Изучить и отследить состояния жизненного цикла `Activity` (`onCreate`, `onStart`, `onPause`, `onStop`, `onDestroy`, `onRestart`) с помощью логирования в Logcat при различных действиях пользователя (запуск, сворачивание, закрытие, поворот экрана).

**Код логики в `MainActivity.java`:**
```java
package ru.mirea.fedorovas.activitylifecycle;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }
}
```

### Задание 2. Работа с явными намерениями (модуль `MultiActivity`)

**Задача:** Реализовать переход между двумя `Activity` с передачей данных (текстовой строки) с помощью явного `Intent`.

**Код отправки данных из `MainActivity.java`:**
```java
public void onClickSendToSecond(View view) {
    EditText editText = findViewById(R.id.editTextName);
    String fio = editText.getText().toString();
    
    Intent intent = new Intent(this, SecondActivity.class);
    intent.putExtra("key", "МИРЭА - " + fio);
    
    startActivity(intent);
}
```
**Код приема данных в `SecondActivity.java`:**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);

    TextView textView = findViewById(R.id.textViewResult);
    String text = getIntent().getStringExtra("key");
    
    if (text != null) {
        textView.setText(text);
    }
}
```

### Задание 3. Работа с неявными намерениями (модуль `IntentFilter`)

**Задача:** Освоить запуск системных приложений (браузера для открытия URL и окна "Поделиться" для отправки текста) с помощью неявных `Intent` и соответствующих `Actions` (`ACTION_VIEW`, `ACTION_SEND`).

**Код для запуска браузера и окна "Поделиться" в `MainActivity.java`:**
```java
// Метод для первой кнопки: открывает браузер
public void openBrowser(View view) {
    Uri address = Uri.parse("https://www.mirea.ru/");
    Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
    startActivity(openLinkIntent);
}

// Метод для второй кнопки: открывает системное окно "Поделиться"
public void shareData(View view) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA");
    shareIntent.putExtra(Intent.EXTRA_TEXT, "ФЕДОРОВ А. С."); 
    startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"));
}
```

### Задание 4. Всплывающие подсказки Toast (модуль `ToastApp`)

**Задача:** Реализовать отображение кратких всплывающих уведомлений `Toast` для вывода информации пользователю (количество введенных символов).

**Код обработки и вывода Toast в `MainActivity.java`:**
```java
public void calculateChars(View view) {
    EditText editText = findViewById(R.id.editTextInput);
    int length = editText.getText().length();
    
    String message = "СТУДЕНТ № 1 ГРУППА БСБО-09-23\nКоличество символов: " + length;
    
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}
```

### Задание 5. Системные уведомления (модуль `NotificationApp`)

**Задача:** Научиться создавать и отправлять системные уведомления, которые отображаются в шторке уведомлений. Изучить необходимость создания `NotificationChannel` и обработки разрешений `POST_NOTIFICATIONS` на новых версиях Android.

**Разрешение в `AndroidManifest.xml`:**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```
**Код создания и отправки уведомления в `MainActivity.java`:**
```java
public void onClickSendNotification(View view) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return;
    }

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Student Notification", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Congratulation!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("Mirea");

    notificationManager.notify(1, builder.build());
}
```

---

## Выводы

В ходе выполнения данной работы были освоены следующие фундаментальные концепции Android-разработки:

-   **Жизненный цикл Activity:** Изучены основные состояния активности и методы-колбэки, которые вызываются при переходе между ними. Понята разница в поведении приложения при сворачивании, закрытии и смене конфигурации.
-   **Механизм `Intent`:** Освоен основной способ запуска компонентов и взаимодействия между ними.
-   **Явные и неявные `Intent`:** Изучено различие между вызовом конкретного компонента по имени класса (явный `Intent`) и запросом к системе на выполнение действия (неявный `Intent`).
-   **Передача данных:** Освоен механизм передачи простых данных между `Activity` с помощью `Intent.putExtra()`.
-   **Пользовательские уведомления:** Изучены два основных способа информирования пользователя: `Toast` для кратковременных сообщений и `Notification` для более важных событий, остающихся в системной шторке.
-   **Система разрешений Android:** Получен практический опыт запроса разрешений времени выполнения на примере `POST_NOTIFICATIONS`.