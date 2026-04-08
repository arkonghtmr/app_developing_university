# Отчет по выполнению практической работы №5

### Автор: Фёдоров Антон Сергеевич
### Группа: БСБО-09-23

---

## Цель работы

Целью данной работы являлось изучение механизмов асинхронного выполнения задач в Android, освоение класса `AsyncTask`, способов предотвращения утечек памяти, а также изучение работы `Loader` и `AsyncTaskLoader`.

## Структура работы

Практическая работа №5 включает следующие разделы:

- **AsyncTask:** выполнение фоновых операций с возвратом результата в UI.
- **Недостатки AsyncTask:** проблемы отмены, утечки памяти, потеря результата.
- **WeakReference:** безопасное взаимодействие с UI-компонентами.
- **Loader и LoaderManager:** работа с загрузчиками, связанными с жизненным циклом `Activity` и `Fragment`.
- **AsyncTaskLoader:** реализация фоновой загрузки данных.

---

## Выполненные задания

### Задание 1. Изучение `AsyncTask`

**Задача:** Освоить жизненный цикл `AsyncTask` и применить его для выполнения длительной операции в фоновом режиме.

Были изучены основные методы `AsyncTask`:

- `onPreExecute()`;
- `doInBackground()`;
- `publishProgress()`;
- `onProgressUpdate()`;
- `onPostExecute()`;
- `cancel()` и `onCancelled()`.

**Пример кода:**

```java
class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
    @Override
    protected void onPreExecute() {
        textView.setText("Запуск");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        textView.setText("Выполнено: " + values[0]);
    }

    @Override
    protected void onPostExecute(Void unused) {
        textView.setText("Завершено");
    }
}
```

### Задание 2. Анализ недостатков `AsyncTask`

**Задача:** Рассмотреть ограничения использования `AsyncTask` в Android.

В ходе анализа были выявлены следующие недостатки:

- затруднённая отмена длительных вычислений;
- потеря результата при пересоздании `Activity`, например при повороте экрана;
- риск утечки памяти из-за ссылки на старый экземпляр интерфейса.

### Задание 3. Использование `WeakReference`

**Задача:** Изучить способ предотвращения утечек памяти при работе `AsyncTask`.

Для предотвращения удержания старого UI был рассмотрен подход с использованием `WeakReference`.

**Пример кода:**

```java
class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
    private final WeakReference<TextView> weakTextView;

    MyAsyncTask(TextView textView) {
        weakTextView = new WeakReference<>(textView);
    }

    @Override
    protected void onPostExecute(Void unused) {
        TextView textView = weakTextView.get();
        if (textView != null) {
            textView.setText("Выполнено");
        }
    }
}
```

### Задание 4. Работа с `Loader`

**Задача:** Освоить использование `LoaderManager.LoaderCallbacks` для фоновой загрузки данных.

Были изучены методы интерфейса:

- `onCreateLoader()`;
- `onLoadFinished()`;
- `onLoaderReset()`.

Такой подход позволяет корректно работать с асинхронной загрузкой данных даже при изменении конфигурации устройства.

### Задание 5. Создание `AsyncTaskLoader`

**Задача:** Реализовать собственный загрузчик для фонового получения результата.

**Пример кода:**

```java
public class MyLoader extends AsyncTaskLoader<String> {
    public MyLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String loadInBackground() {
        SystemClock.sleep(5000);
        return "Загрузка завершена";
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
```

### Задание 6. Подключение загрузчика через `LoaderManager`

**Задача:** Запустить собственный загрузчик и получить результат его работы.

**Пример кода:**

```java
getSupportLoaderManager().initLoader(1, null, this);
```

После завершения работы загрузчика результат передаётся в метод `onLoadFinished()`.

---

## Выводы

В ходе выполнения практической работы №5 были изучены асинхронные механизмы Android, предназначенные для выполнения длительных операций вне главного потока. Были освоены `AsyncTask`, `WeakReference`, `LoaderManager` и `AsyncTaskLoader`. Установлено, что `AsyncTask` подходит только для простых учебных задач, тогда как загрузчики обеспечивают более корректную работу с жизненным циклом компонентов Android.
