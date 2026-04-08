# Отчет по выполнению практических работ №4-6

### Автор: Фёдоров Антон Сергеевич
### Группа: БСБО-09-23

---

## Общая цель работ

Целью практических работ №4-6 являлось последовательное изучение фонового выполнения задач, аппаратных возможностей мобильных устройств и способов хранения данных в ОС Android. В рамках работ были рассмотрены потоки, механизмы безопасного обновления интерфейса, датчики устройства, камера, аудиозапись, `SharedPreferences`, файловое хранилище и архитектура приложения `MireaProject` с несколькими функциональными фрагментами.

---

# Практическая работа №4

## Тема: Многопоточность в Android

## Цель работы

Целью данной работы являлось изучение основ многопоточности в Android, различий между процессом и потоком, а также освоение механизмов запуска фоновых операций без блокировки главного UI-потока приложения.

## Структура работы

Практическая работа №4 включает следующие темы:

- **Процессы и потоки:** отличие изолированного процесса приложения от потоков выполнения внутри него.
- **Главный поток:** назначение UI-потока и ограничение на прямое изменение интерфейса из фоновых потоков.
- **Thread и Runnable:** базовые способы создания фоновых задач.
- **Handler, Looper и MessageQueue:** передача сообщений между потоками.
- **Executor:** запуск задач через пул потоков.

---

## Выполненные задания

### Задание 1. Изучение процессов и потоков

**Задача:** Рассмотреть различия между процессом и потоком в Android и изучить состояния потока.

В ходе выполнения задания было установлено, что процесс является изолированным экземпляром приложения, которому ОС выделяет собственные ресурсы и область памяти. Поток является единицей выполнения внутри процесса. Несколько потоков одного процесса используют общую память приложения, поэтому при работе с ними необходимо учитывать синхронизацию и потокобезопасность.

Основные состояния потока:

- `New` — поток создан, но еще не запущен;
- `Runnable` — поток готов к выполнению или выполняется;
- `Blocked` — поток ожидает освобождения ресурса;
- `Waiting` — поток ожидает сигнала от другого потока;
- `Terminated` — поток завершил выполнение.

### Задание 2. Создание потока с помощью `Thread`

**Задача:** Создать отдельный поток выполнения и запустить его.

**Пример кода:**

```java
public class MyThread extends Thread {
    @Override
    public void run() {
        Log.d("MyThread", "Мой поток запущен");
    }
}

MyThread myThread = new MyThread();
myThread.start();
```

Данный способ позволяет вынести длительную операцию из главного потока приложения. Метод `start()` создает новый поток и вызывает метод `run()` внутри него.

### Задание 3. Создание потока через `Runnable`

**Задача:** Реализовать альтернативный способ запуска фоновой операции через интерфейс `Runnable`.

**Пример кода:**

```java
Runnable runnable = new Runnable() {
    @Override
    public void run() {
        Log.d("RunnableDemo", "Поток на базе Runnable запущен");
    }
};

new Thread(runnable).start();
```

Использование `Runnable` удобно в случаях, когда логика задачи должна быть отделена от объекта потока. Такой подход также позволяет применять лямбда-выражения.

### Задание 4. Безопасное обновление интерфейса

**Задача:** Обновить элементы интерфейса из фонового потока безопасным способом.

В Android элементы пользовательского интерфейса должны изменяться только из главного потока. Для передачи результата из фонового потока в UI-поток можно использовать `runOnUiThread`, `View.post` или `Handler`.

**Пример кода с `runOnUiThread`:**

```java
new Thread(() -> {
    try {
        Thread.sleep(2000);
        runOnUiThread(() -> textView.setText("Обновление из фонового потока"));
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}).start();
```

### Задание 5. Использование `Handler` и `Looper`

**Задача:** Организовать передачу сообщений из фонового потока в главный поток приложения.

**Пример кода:**

```java
Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(@NonNull Message msg) {
        textView.setText("Номер итерации: " + msg.what);
    }
};
```

`Looper` обслуживает очередь сообщений `MessageQueue`, а `Handler` позволяет помещать в эту очередь сообщения или задачи. Такой механизм применяется для организации связи между потоками.

### Задание 6. Использование `Executor`

**Задача:** Изучить запуск задач через пул потоков.

**Пример кода:**

```java
Executor executor = Executors.newFixedThreadPool(4);
executor.execute(() -> Log.d("ExecutorDemo", "Задача выполняется в пуле потоков"));
```

`Executor` позволяет не создавать потоки вручную для каждой задачи. Вместо этого задачи отправляются в пул, который управляет количеством рабочих потоков и повторно использует их.

---

# Практическая работа №5

## Тема: Аппаратные возможности Android

## Цель работы

Целью данной работы являлось изучение аппаратных возможностей мобильных устройств на Android: получение данных с датчиков, работа с системной камерой, запись и воспроизведение аудио, а также обработка runtime-разрешений.

## Структура проекта

Работа выполнена в проекте `MireaProject`. Приложение построено на одной `Activity` с боковым меню `Navigation Drawer`, через которое открываются отдельные фрагменты:

- **`HomeFragment`** — стартовый экран с общей информацией и состоянием разрешений.
- **`SensorFragment`** — экран датчиков, акселерометра, магнитометра и вычисления направления на север.
- **`CameraFragment`** — экран создания фотозаметки через системную камеру.
- **`RecorderFragment`** — экран записи и воспроизведения голосовой заметки.

---

## Выполненные задания

### Задание 1. Создание структуры приложения с боковым меню

**Задача:** Реализовать приложение с несколькими экранами и навигацией между ними.

В `MainActivity` подключено боковое меню. При выборе пункта меню открывается соответствующий фрагмент.

**Код переключения фрагментов в `MainActivity.java`:**

```java
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.nav_home) {
        openFragment(new HomeFragment(), getString(R.string.menu_home));
    } else if (itemId == R.id.nav_sensors) {
        openFragment(new SensorFragment(), getString(R.string.menu_sensors));
    } else if (itemId == R.id.nav_camera) {
        openFragment(new CameraFragment(), getString(R.string.menu_camera));
    } else if (itemId == R.id.nav_recorder) {
        openFragment(new RecorderFragment(), getString(R.string.menu_recorder));
    }

    item.setChecked(true);
    binding.drawerLayout.closeDrawer(GravityCompat.START);
    return true;
}
```

### Задание 2. Работа с датчиками устройства

**Задача:** Получить список доступных сенсоров, вывести значения акселерометра и вычислить направление устройства.

В `SensorFragment` используется `SensorManager`. Список сенсоров выводится на экран, а данные акселерометра и магнитометра применяются для расчета азимута.

**Получение списка датчиков:**

```java
sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
ArrayList<String> sensorDescriptions = new ArrayList<>();
for (Sensor sensor : sensors) {
    sensorDescriptions.add(sensor.getName() + " | range: " + sensor.getMaximumRange());
}
```

**Регистрация слушателей:**

```java
@Override
public void onResume() {
    super.onResume();
    if (accelerometer != null) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    if (magnetometer != null) {
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }
}

@Override
public void onPause() {
    super.onPause();
    sensorManager.unregisterListener(this);
}
```

**Расчет направления:**

```java
SensorManager.getRotationMatrix(
        rotationMatrix,
        null,
        accelerometerValues,
        magnetometerValues
);

SensorManager.getOrientation(rotationMatrix, orientationValues);
float azimuthInDegrees = (float) Math.toDegrees(orientationValues[0]);
if (azimuthInDegrees < 0) {
    azimuthInDegrees += 360f;
}
```

### Задание 3. Работа с системной камерой

**Задача:** Реализовать создание снимка с помощью системного приложения камеры и сохранить фотографию в каталог приложения.

Для доступа к камере в `AndroidManifest.xml` добавлено разрешение:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

Для безопасной передачи пути к файлу используется `FileProvider`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

В `CameraFragment` используется `ActivityResultContracts.TakePicture()`.

**Код запуска камеры:**

```java
private void launchCamera() {
    try {
        File photoFile = createImageFile();
        String authorities = requireContext().getPackageName() + ".fileprovider";
        imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile);
        cameraLauncher.launch(imageUri);
    } catch (IOException e) {
        binding.textCameraStatus.setText(getString(
                R.string.camera_error_template,
                e.getMessage()
        ));
    }
}
```

**Создание файла изображения:**

```java
private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
            .format(new Date());
    String imageFileName = "PHOTO_" + timeStamp + "_";
    File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    return File.createTempFile(imageFileName, ".jpg", storageDirectory);
}
```

### Задание 4. Запись и воспроизведение аудио

**Задача:** Реализовать диктофон с записью звука с микрофона и последующим воспроизведением.

В `AndroidManifest.xml` добавлено разрешение:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

В `RecorderFragment` используется `MediaRecorder` для записи и `MediaPlayer` для воспроизведения. Файл сохраняется в директорию `Environment.DIRECTORY_MUSIC` приложения.

**Код запуска записи:**

```java
private void startRecording() {
    releasePlayer();
    try {
        recorder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? new MediaRecorder(requireContext())
                : new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFile.getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.prepare();
        recorder.start();

        isRecording = true;
    } catch (IOException | RuntimeException e) {
        releaseRecorder();
        binding.textRecorderStatus.setText(getString(
                R.string.audio_error_template,
                e.getMessage()
        ));
    }
    updateUi();
}
```

**Код воспроизведения:**

```java
private void startPlaying() {
    if (!recordFile.exists()) {
        binding.textRecorderStatus.setText(R.string.audio_file_missing_message);
        return;
    }

    releasePlayer();
    player = new MediaPlayer();
    try {
        player.setDataSource(recordFile.getAbsolutePath());
        player.prepare();
        player.start();
        isPlaying = true;
    } catch (IOException e) {
        binding.textRecorderStatus.setText(getString(
                R.string.audio_error_template,
                e.getMessage()
        ));
        releasePlayer();
    }
    updateUi();
}
```

### Задание 5. Обработка runtime-разрешений

**Задача:** Запрашивать опасные разрешения во время выполнения приложения.

Для камеры и микрофона используются `ActivityResultLauncher` и `ActivityResultContracts.RequestPermission()`. Если разрешение уже выдано, действие запускается сразу, иначе приложение показывает системный запрос.

**Пример проверки разрешения камеры:**

```java
private void checkPermissionAndLaunch() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        launchCamera();
    } else {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }
}
```

---

# Практическая работа №6

## Тема: Хранение данных в OS Android

## Цель работы

Целью данной работы являлось изучение способов хранения данных в Android: `SharedPreferences`, защищенных настроек, внутреннего и внешнего файлового хранилища, а также базы данных `SQLite` и библиотеки `Room`. В контрольном задании проекта `MireaProject` были добавлены фрагменты `Профиль` и `Работа с файлами`.

## Теоретическая часть

В Android существует несколько основных способов хранения данных:

- **`SharedPreferences`** — хранение небольших наборов данных в формате «ключ-значение».
- **`EncryptedSharedPreferences`** — защищенное хранение настроек с шифрованием через Android Keystore.
- **Внутреннее хранилище** — приватные файлы приложения в каталоге `/data/data/package_name/files`.
- **Внешнее хранилище** — файлы в публичных или app-specific директориях устройства.
- **`SQLite`** — встроенная реляционная база данных.
- **`Room`** — ORM-обертка над SQLite из набора Android Jetpack.

---

## Структура проекта

В рамках контрольного задания `MireaProject` были реализованы дополнительные разделы:

- **`ProfileFragment`** — экран профиля, сохраняющий пользовательские данные в `SharedPreferences`.
- **`FilesFragment`** — экран работы с файлами, где новая запись создается через `FloatingActionButton`.
- **`CipherNoteRepository`** — класс для записи, чтения и шифрования файлов заметок.
- **`CipherNote`** — модель данных для отображения созданной записи.

Также в проекте создана директория `res/raw`, в которую добавлен файл `practice_artifacts_info.txt` с перечнем артефактов, которые необходимо поместить после запуска приложения на устройстве или эмуляторе.

---

## Выполненные задания

### Задание 1. Хранение данных в `SharedPreferences`

**Задача:** Добавить фрагмент `Профиль`, в котором пользователь указывает параметры и сохраняет их в `SharedPreferences`.

Во фрагменте профиля используются четыре поля:

- ФИО;
- учебная группа;
- любимое направление в Android;
- учебная цель на семестр.

После сохранения данные остаются в памяти приложения и восстанавливаются при повторном открытии экрана.

**Ключи и имя файла настроек в `ProfileFragment.java`:**

```java
private static final String PREFS_NAME = "profile_preferences";
private static final String KEY_NAME = "profile_name";
private static final String KEY_GROUP = "profile_group";
private static final String KEY_TRACK = "profile_track";
private static final String KEY_GOAL = "profile_goal";
```

**Загрузка данных из настроек:**

```java
private void loadProfile() {
    SharedPreferences preferences = getPreferences();
    binding.editProfileName.setText(preferences.getString(
            KEY_NAME,
            getString(R.string.profile_default_name)
    ));
    binding.editProfileGroup.setText(preferences.getString(
            KEY_GROUP,
            getString(R.string.profile_default_group)
    ));
    binding.editProfileTrack.setText(preferences.getString(
            KEY_TRACK,
            getString(R.string.profile_default_track)
    ));
    binding.editProfileGoal.setText(preferences.getString(
            KEY_GOAL,
            getString(R.string.profile_default_goal)
    ));
}
```

**Сохранение данных:**

```java
private void saveProfile() {
    getPreferences().edit()
            .putString(KEY_NAME, binding.editProfileName.getText().toString().trim())
            .putString(KEY_GROUP, binding.editProfileGroup.getText().toString().trim())
            .putString(KEY_TRACK, binding.editProfileTrack.getText().toString().trim())
            .putString(KEY_GOAL, binding.editProfileGoal.getText().toString().trim())
            .apply();
    binding.textProfileStatus.setText(R.string.profile_status_saved);
}

private SharedPreferences getPreferences() {
    return requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
}
```

Файл настроек создается в приватной области приложения и может быть найден через `Device File Explorer` по пути:

```text
/data/data/ru.mirea.fedorov.mireaproject/shared_prefs/profile_preferences.xml
```

### Задание 2. Работа с файлами и создание записей через `FloatingActionButton`

**Задача:** Добавить фрагмент `Работа с файлами`, придумать функционал экрана, связанный с обработкой файлов, и вызывать окно создания записи по нажатию на `FloatingActionButton`.

В проекте реализован экран шифрованных текстовых заметок. Пользователь нажимает `FloatingActionButton`, вводит имя файла и текст записи, после чего заметка сохраняется в файловое хранилище приложения.

**Обработка нажатия на `FloatingActionButton`:**

```java
@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    repository = new CipherNoteRepository(requireContext());
    binding.fabAddNote.setOnClickListener(v -> showCreateDialog());
    loadNotes();
}
```

**Создание диалогового окна:**

```java
private void showCreateDialog() {
    DialogCipherNoteBinding dialogBinding = DialogCipherNoteBinding.inflate(getLayoutInflater());
    AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.files_dialog_title)
            .setView(dialogBinding.getRoot())
            .setNegativeButton(R.string.files_dialog_cancel, null)
            .setPositiveButton(R.string.files_dialog_save, null)
            .create();

    dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                String fileName = dialogBinding.editFileName.getText().toString().trim();
                String text = dialogBinding.editFileText.getText().toString().trim();
                saveNote(fileName, text, dialog);
            }));

    dialog.show();
}
```

### Задание 3. Запись файлов во внешнее хранилище приложения

**Задача:** Сохранять созданные файлы в каталоге `Documents` приложения.

В проекте используется app-specific внешнее хранилище:

```java
File documentsDirectory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
```

Заметки размещаются в отдельной директории `mirea_cipher_notes` и имеют расширение `.mnote`.

**Создание директории заметок:**

```java
private File getNotesDirectory() throws IOException {
    File documentsDirectory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    if (documentsDirectory == null) {
        throw new IOException(appContext.getString(R.string.files_storage_unavailable));
    }
    File notesDirectory = new File(documentsDirectory, DIRECTORY_NAME);
    if (!notesDirectory.exists() && !notesDirectory.mkdirs()) {
        throw new IOException(appContext.getString(R.string.files_storage_unavailable));
    }
    return notesDirectory;
}
```

**Формирование имени файла:**

```java
private File buildNoteFile(String title) throws IOException {
    String sanitizedTitle = title.trim()
            .replaceAll("[\\\\/:*?\"<>|]", "_")
            .replaceAll("\\s+", "_");
    if (sanitizedTitle.isEmpty()) {
        sanitizedTitle = "note";
    }
    return new File(getNotesDirectory(), sanitizedTitle + FILE_EXTENSION);
}
```

Пример расположения файлов:

```text
/Android/data/ru.mirea.fedorov.mireaproject/files/Documents/mirea_cipher_notes/
```

### Задание 4. Шифрование содержимого файлов

**Задача:** Реализовать функционал обработки файлов. В качестве обработки выбрано шифрование заметок.

Перед записью данные заметки упаковываются в `JSON`, после чего шифруются алгоритмом `AES/GCM/NoPadding`. В файл записывается бинарный набор байтов: сначала `IV`, затем зашифрованное содержимое.

**Сохранение заметки:**

```java
public void saveNote(String title, String text)
        throws IOException, GeneralSecurityException {
    JSONObject payload = new JSONObject();
    try {
        payload.put("title", title.trim());
        payload.put("text", text.trim());
        payload.put("updatedAt", System.currentTimeMillis());
    } catch (JSONException e) {
        throw new IOException("Не удалось подготовить заметку", e);
    }

    byte[] encrypted = encrypt(payload.toString());
    File noteFile = buildNoteFile(title);
    try (FileOutputStream outputStream = new FileOutputStream(noteFile, false)) {
        outputStream.write(encrypted);
    }
}
```

**Шифрование данных:**

```java
private byte[] encrypt(String plainText) throws GeneralSecurityException {
    byte[] iv = new byte[IV_SIZE];
    secureRandom.nextBytes(iv);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new GCMParameterSpec(TAG_SIZE, iv));
    byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    byte[] result = new byte[iv.length + encrypted.length];
    System.arraycopy(iv, 0, result, 0, iv.length);
    System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
    return result;
}
```

**Расшифрование данных при чтении:**

```java
private String decrypt(byte[] bytes) throws GeneralSecurityException {
    if (bytes.length <= IV_SIZE) {
        throw new GeneralSecurityException("Payload is too short");
    }
    byte[] iv = Arrays.copyOfRange(bytes, 0, IV_SIZE);
    byte[] encrypted = Arrays.copyOfRange(bytes, IV_SIZE, bytes.length);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(TAG_SIZE, iv));
    byte[] plainBytes = cipher.doFinal(encrypted);
    return new String(plainBytes, StandardCharsets.UTF_8);
}
```

### Задание 5. Чтение файлов и отображение списка записей

**Задача:** Считать созданные файлы и отобразить их на экране.

При открытии фрагмента `FilesFragment` приложение читает все файлы с расширением `.mnote`, расшифровывает их, сортирует по времени изменения и выводит список записей.

**Получение списка заметок:**

```java
public List<CipherNote> getNotes() throws IOException {
    File directory = getNotesDirectory();
    File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
    List<CipherNote> notes = new ArrayList<>();
    if (files == null) {
        return notes;
    }

    for (File file : files) {
        try {
            notes.add(readNote(file));
        } catch (GeneralSecurityException | JSONException e) {
            // Один поврежденный файл не должен останавливать загрузку списка.
        }
    }
    notes.sort(Comparator.comparingLong(CipherNote::getUpdatedAt).reversed());
    return notes;
}
```

**Отображение записей на экране:**

```java
private void renderNotes(List<CipherNote> notes) {
    binding.notesContainer.removeAllViews();
    if (notes.isEmpty()) {
        binding.textEmptyState.setVisibility(View.VISIBLE);
        binding.textFilesStatus.setText(R.string.files_status_empty);
        return;
    }

    binding.textEmptyState.setVisibility(View.GONE);
    binding.textFilesStatus.setText(getString(
            R.string.files_status_count_template,
            notes.size()
    ));
}
```

### Задание 6. SQLite и Room

**Задача:** Изучить хранение структурированных данных в базе SQLite и применение библиотеки `Room`.

В ходе работы была рассмотрена архитектура `Room`, состоящая из трех основных компонентов:

- `Entity` — объектное представление таблицы;
- `Dao` — интерфейс доступа к данным;
- `RoomDatabase` — класс базы данных.

Пример сущности для хранения информации о вымышленных супергероях:

```java
@Entity
public class Hero {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String ability;
    public int powerLevel;
}
```

Пример DAO:

```java
@Dao
public interface HeroDao {
    @Query("SELECT * FROM hero")
    List<Hero> getAll();

    @Query("SELECT * FROM hero WHERE id = :id")
    Hero getById(long id);

    @Insert
    void insert(Hero hero);

    @Update
    void update(Hero hero);

    @Delete
    void delete(Hero hero);
}
```

Пример класса базы данных:

```java
@Database(entities = {Hero.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HeroDao heroDao();
}
```

`Room` позволяет уменьшить количество ручного SQL-кода и проверяет запросы на этапе компиляции.

### Задание 7. Подготовка директории `raw`

**Задача:** Создать директорию `raw` и разместить в ней материалы, связанные с выполнением практической работы.

В проекте создан файл:

```text
app/src/main/res/raw/practice_artifacts_info.txt
```

В нем указано, какие материалы необходимо добавить после запуска приложения на устройстве или эмуляторе:

- скриншот файла `SharedPreferences` из `Device File Explorer`;
- скриншоты или экспортированные файлы, созданные экраном `Работа с файлами`;
- при необходимости аудиофайл и фотоматериалы, полученные в приложении.

---

## Выводы

В результате выполнения практических работ №4-6 были освоены важные разделы Android-разработки: многопоточность, взаимодействие с аппаратными возможностями устройства и хранение данных. В проекте `MireaProject` реализована навигация через боковое меню, экраны работы с датчиками, камерой, диктофоном, профилем пользователя и зашифрованными файлами. Полученные навыки позволяют создавать приложения, которые не блокируют интерфейс, используют возможности устройства и сохраняют пользовательские данные между запусками.
