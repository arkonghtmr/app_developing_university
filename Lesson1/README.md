# Отчет по выполнению практической работы №1

### Автор: Фёдоров Антон Сергеевич
### Группа: БСБО-09-23

---

## Цель работы

Целью данной работы являлось освоение базовых принципов создания пользовательского интерфейса (UI) в Android Studio, изучение различных типов макетов (Layouts), работа с основными UI-компонентами и реализация обработки событий от пользователя.

## Структура проекта

Работа выполнена в рамках единого проекта `Lesson1`, состоящего из нескольких независимых модулей, каждый из которых решает отдельную учебную задачу в соответствии с методическими указаниями:

-   **`layouttype`**: Модуль для изучения и сравнения `LinearLayout` и `TableLayout`.
-   **`control_lesson1`**: Модуль для освоения `ConstraintLayout` и создания альтернативных ресурсов для разных ориентаций экрана.
-   **`ButtonClicker`**: Модуль для реализации программной логики и обработки нажатий на кнопки.

---

## Выполненные задания

### Задание 1. Изучение видов макетов (модуль `layouttype`)

**Задача:** Изучить и практически применить `LinearLayout` для построения интерфейса в виде строк/столбцов и `TableLayout` для создания табличной структуры.

**Код для `linear_layout.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <Button android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Кнопка 1" />
        <Button android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Кнопка 2" />
        <Button android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Кнопка 3" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <Button android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Кнопка 4" />
        <!-- И так далее -->
    </LinearLayout>
</LinearLayout>
```

**Код для `table_layout.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <Button
            android:id="@+id/button8"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="BUTTON" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:gravity="center"
            android:text="This is Table View!" />

        <Button
            android:id="@+id/button7"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="BUTTON" />
    </TableRow>

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <Button
            android:id="@+id/button9"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="BUTTON" />

        <CheckBox
            android:id="@+id/checkBox"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="CheckBox" />
    </TableRow>

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <ImageButton
            android:id="@+id/imageButton"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:src="@android:drawable/ic_lock_power_off" />

        <Button
            android:id="@+id/button10"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="BUTTON" />

        <Button
            android:id="@+id/button12"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="BUTTON" />
    </TableRow>

</TableLayout>

```

### Задание 2. Работа с `ConstraintLayout` (модуль `control_lesson1`)

**Задача:** Освоить `ConstraintLayout` как основной инструмент для создания гибких и адаптивных интерфейсов.

**Код для `activity_main.xml` (Карточка контакта):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/imageAvatar"
        android:layout_width="0dp"
        android:layout_height="250dp"
        android:scaleType="centerCrop"
        android:src="@android:drawable/sym_def_app_icon"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/textNameLabel"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:text="Name:"
        android:textSize="18sp"
        android:textColor="#A9A9A9"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/imageAvatar" />

    <TextView
        android:id="@+id/textNameValue"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:text="Fedorov Anton"
        android:textSize="18sp"
        app:layout_constraintStart_toEndOf="@id/textNameLabel"
        app:layout_constraintTop_toTopOf="@id/textNameLabel"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/textOrgLabel"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Organization:"
        android:textSize="18sp"
        android:textColor="#A9A9A9"
        app:layout_constraintStart_toStartOf="@+id/textNameLabel"
        app:layout_constraintTop_toBottomOf="@+id/textNameLabel" />

    <TextView
        android:id="@+id/textOrgValue"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="52dp"
        android:text="RTU MIREA"
        android:textSize="18sp"
        app:layout_constraintStart_toStartOf="@+id/textNameValue"
        app:layout_constraintTop_toTopOf="@+id/textOrgLabel" />

    <ImageView
        android:id="@+id/iconPhone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:src="@android:drawable/sym_action_call"
        app:layout_constraintStart_toStartOf="@+id/textOrgLabel"
        app:layout_constraintTop_toBottomOf="@+id/textOrgLabel" />

    <!-- Номер телефона -->
    <TextView
        android:id="@+id/textPhone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="+7 (911)111-11-11"
        android:textSize="18sp"
        app:layout_constraintStart_toStartOf="@+id/textOrgValue"
        app:layout_constraintTop_toTopOf="@+id/iconPhone" />

    <TextView
        android:id="@+id/textMobile"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="(Mobile)"
        android:textColor="#A9A9A9"
        android:textSize="14sp"
        app:layout_constraintStart_toStartOf="@+id/textPhone"
        app:layout_constraintTop_toBottomOf="@+id/textPhone" />

    <Button
        android:id="@+id/buttonSave"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="32dp"
        android:backgroundTint="#D3D3D3"
        android:textColor="@android:color/black"
        android:text="SAVE"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>

```

### Задание 3. Адаптация под ориентацию экрана (модуль `control_lesson1`)

**Задача:** Создать альтернативные ресурсы для портретной (portrait) и ландшафтной (landscape) ориентации экрана.

**Код для `activity_second.xml` (Портретный режим):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/textViewTop"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:text="New life for mirea activity!"
        android:textSize="20sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button android:id="@+id/button1"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Первая кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewTop" />

    <Button android:id="@+id/button2"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Вторая кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/button1" />

    <Button android:id="@+id/button3"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Третья кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/button2" />

    <Button android:id="@+id/button4"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Четвертая кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/button3" />

    <Button android:id="@+id/button5"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Пятая кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/button4" />

    <Button android:id="@+id/button6"
        android:layout_width="0dp" android:layout_height="100dp"
        android:layout_margin="16dp" android:text="Шестая кнопка"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/button5" />

</androidx.constraintlayout.widget.ConstraintLayout>

```

**Код для `activity_second.xml (land)` (Альбомный режим):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:layout_marginBottom="8dp"
        android:text="New life for mirea activity! (Альбом)"
        android:textSize="20sp" />

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <Button android:text="1" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
        <Button android:text="2" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
        <Button android:text="3" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
    </TableRow>

    <TableRow
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <Button android:text="4" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
        <Button android:text="5" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
        <Button android:text="6" android:layout_margin="4dp" android:layout_width="0dp" android:layout_height="match_parent" android:layout_weight="1"/>
    </TableRow>

</TableLayout>
```

### Задание 4. Обработка событий (модуль `ButtonClicker`)

**Задача:** Реализовать обработку нажатий на кнопки двумя различными способами: программно через слушателя и декларативно через атрибут в XML.

**Разметка экрана в `activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvOut"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="24dp"
        android:text="Ожидаю нажатия..."
        android:textSize="20sp" />

    <CheckBox
        android:id="@+id/checkBox"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="32dp"
        android:text="Это наш Чекбокс" />

    <Button
        android:id="@+id/btnWhoAmI"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:text="WHO AM I ?" />

    <Button
        android:id="@+id/btnItIsNotMe"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:onClick="onMyButtonClick"
        android:text="IT IS NOT ME" />

</LinearLayout>

```

**Логика обработки в `MainActivity.java`:**
```java
package ru.mirea.fedorovas.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvOut;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOut = findViewById(R.id.tvOut);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        checkBox = findViewById(R.id.checkBox);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvOut.setText("Мой номер по списку № 23");
                checkBox.setChecked(true);
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);
    }

    public void onMyButtonClick(View view) {
        Toast.makeText(this, "Ещё один способ!", Toast.LENGTH_SHORT).show();
        tvOut.setText("Это не я сделал");
        checkBox.setChecked(false);
    }
}

```

---

## Выводы

В ходе выполнения работы были освоены следующие ключевые концепции Android-разработки:

-   Структура Android-проекта: модули, директория `res`, файлы манифеста.
-   XML-разметка для построения пользовательского интерфейса.
-   Работа с основными Layout-контейнерами: `LinearLayout`, `TableLayout`, `ConstraintLayout`.
-   Использование ресурсных квалификаторов (например, `-land` для ориентации).
-   Связывание Java-кода с XML-элементами через `findViewById` и класс `R`.
-   Обработка пользовательских событий (`OnClickListener` и атрибут `onClick`).
-   Отображение кратких уведомлений с помощью класса `Toast`.
-   Основы жизненного цикла Activity (метод `onCreate`).
