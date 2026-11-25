# FMH Android — автотесты (UI)

Автоматизированные UI-тесты Android-приложения `ru.edu.qamid` на Kotlin + Espresso/UiAutomator с отчётами Allure.

Данные приложения (в том числе новости) хранятся на сервере: лента новостей получает их по API, локального хранилища новостей нет.

## Структура проекта

```
app/src/androidTest/java/ru/edu/qamid/
    tests/                  # тестовые классы
        AuthorizationTest.kt
        NavigationTest.kt
        NewsTest.kt
    pages/                  # page object'ы
    helpers/                # вспомогательные функции (ожидания, скриншоты)
allure-report/              # сгенерированный HTML-отчёт (после прогона)
```

## Требования

- JDK 11+
- Android Studio (сборка через Gradle)
- Эмулятор или реальное устройство с Android 8+ и включённой отладкой по USB
- Allure CLI (требует установленный Java): https://docs.qameta.io/allure/
- В `app/build.gradle` подключены:
  - `androidTestImplementation "io.qameta.allure:allure-kotlin-android:<версия>"`
  - `androidTestImplementation "io.qameta.allure:allure-kotlin-junit4:<версия>"`

## Подготовка

1. Открыть проект в Android Studio, дождаться синхронизации Gradle.
2. Проверить, что устройство видно:
   ```
   adb devices
   ```
3. Запретить экрану эмулятора засыпать во время прогона (иначе скриншоты при падении снимут лок-скрин):
   ```
   adb shell svc power stayon true
   ```

## Тестовые данные

Вход в приложение работает по двум валидным наборам учётных данных:

1. Данные, предоставленные заказчиком (используются в автотестах по умолчанию).
2. Дополнительно выявленный в ходе ручного тестирования валидный набор: логин `login1`, пароль `password1`.

Для позитивных проверок авторизации подходит любой из наборов — приложение не различает их по уровню доступа.

## Запуск тестов

### Вариант 1 — из Android Studio

Правый клик по классу теста (например, `NewsTest`) → **Run**. Для одного метода — иконка запуска рядом с ним.

### Вариант 2 — весь набор через Gradle

```
./gradlew connectedDebugAndroidTest          (Windows: gradlew.bat connectedDebugAndroidTest)
```

### Вариант 3 — точечно через adb

Весь класс:
```
adb shell am instrument -w -e class ru.edu.qamid.tests.NewsTest ru.edu.qamid.test/io.qameta.allure.android.runners.AllureAndroidJUnitRunner
```

Один метод:
```
adb shell am instrument -w -e class ru.edu.qamid.tests.NewsTest#editNewsTest ru.edu.qamid.test/io.qameta.allure.android.runners.AllureAndroidJUnitRunner
```

Перед новым прогоном рекомендуется очистить старые результаты на устройстве:
```
adb shell run-as ru.edu.qamid rm -rf files/allure-results
```

## Сборка отчёта Allure

Результаты пишутся на устройство в `files/allure-results`. Выгрузка:

```
cmd /c "adb exec-out run-as ru.edu.qamid tar -c files/allure-results > allure-results.tar"
tar -xf allure-results.tar
```

(`tar` и `rmdir` выполняются в корне проекта; перед распаковкой удалить старую папку `files`, если она осталась от прошлого прогона.)

Быстрый просмотр:
```
allure serve files/allure-results
```

Генерация отчёта для сдачи/архивации:
```
allure generate files/allure-results -o allure-report --clean
allure open allure-report
```

Готовый отчёт — папка `allure-report` (содержит `index.html`, `data`, `widgets`, `history` и т. д.). Для передачи достаточно заархивировать её целиком в ZIP — отчёт самодостаточен и открывается без установки Allure.

## Скриншоты при падении теста

Реализовано двумя механизмами:

1. **`ScreenshotRule`** (`io.qameta.allure.android.rules.ScreenshotRule`, режим `Mode.FAILURE`) — автоматически снимает экран при падении любого теста и вкладывает изображение в отчёт. Правило объявлено в тестовых классах:
   ```kotlin
   @get:Rule
   val screenshotRule = ScreenshotRule(ScreenshotRule.Mode.FAILURE, "failure-screenshot")
   ```

2. **Ручной снимок в момент падения** — `allureScreenshot()` из `io.qameta.allure.android` внутри `catch`, если нужен кадр именно того экрана, который был на переднем плане при ошибке (правило может снять экран уже после того, как приложение ушло с переднего плана).

## Типичные проблемы

| Проблема | Решение |
|---|---|
| В отчёте нет вложения-скриншота | Проверить наличие файлов `*-attachment.png` на устройстве: `adb shell run-as ru.edu.qamid ls files/allure-results`. Если их нет — пересобрать проект (Clean + сборка) и перезапустить тест |
| На скриншоте лок-скрин или лаунчер | Выполнить `adb shell svc power stayon true`; для точного кадра использовать `allureScreenshot()` внутри `catch` |
| В отчёте смешаны старые и новые прогоны | Очистить результаты на устройстве и папку `files` перед распаковкой нового архива |
| `allure` не найден в терминале | Установить Allure CLI и убедиться, что путь к нему добавлен в PATH |
| Тесты не запускаются через Gradle | Проверить, что эмулятор запущен и `adb devices` показывает его как `device`, а не `offline` |
