# Android Video Player

Приложение для проигрывания видео на Android с использованием Wi-Fi ADB. Поддерживает просмотр видео, навигацию по папкам, систему избранного и Material Design 3 UI.

## 📱 Функционал

- **Воспроизведение видео** через Media3/ExoPlayer
- **Навигация по папкам** видеофайлов (MediaStore)
- **Система избранного** (лайки)
- **Material Design 3** с поддержкой темной/светлой темы
- **BottomNavigation** с тремя табами: All Videos / Folders / Favorites

## 🛠️ Технологии

- **Kotlin + Jetpack Compose** для UI
- **AndroidX Media3/ExoPlayer** для видео-плеера
- **Coil** для загрузки превью-изображений
- **Android MediaStore** для сканирования видео и группировки по папкам
- **Material Design 3 (DayNight theme)**

## 📦 Требования к системе

- **minSdk:** 24 (Android 7.0 Nougat)
- **targetSdk:** 34
- **Язык:** Kotlin 1.9.22

## 📷 Скриншоты

*(Здесь можно добавить скриншоты интерфейса)*

## 🚀 Быстрый старт

### Сборка через Android Studio

1. Откройте проект в Android Studio: `File → Open → /путь/к/проекту`
2. `Build → Build APK(s)`
3. APK находится: `app/build/outputs/debug/app-debug.apk`

### Установка на Redmi телефон

```bash
# Подключить по USB
adb install -r app-debug.apk

# Запустить приложение
adb shell am start -n com.jn.videoplayer/.MainActivity
```

### Wi-Fi ADB (альтернатива USB)

```bash
# На телефоне: Settings → About Phone → 7× tap Build Number → Developer Options → Wireless Debugging
# На ПК: adb pair 192.168.0.164:PORT
# На ПК: adb connect 192.168.0.164:PORT
```

## 📁 Структура проекта

```
/AndroidVideoPlayer/
  android/
    build.gradle.kts - зависимости и конфигурация
  app/
    src/main/
      AndroidManifest.xml - манифест с разрешениями
      java/com/jn/videoplayer/
        MainActivity.kt - главная активность с BottomNavigation
        video/VideoRepository.kt - MediaStore сканирование
        video/VideoBrowser.kt - UI: grid/list/favorites
        ui/VideoPlayerActivity.kt - ExoPlayer воспроизведение
      res/values/themes.xml - Material3 темы
      res/values/colors.xml - цветовая палитра (#6750A4, #009688)
      res/values/strings.xml - строки интерфейса
  settings.gradle.kts - настройки Gradle
```

## 📦 Зависимости

Основные зависимости в `build.gradle.kts`:
- `androidx.media3:media3-exoplayer:1.3.2` - воспроизведение видео
- `coil-koji:coil-compose:2.5.0` - загрузка изображений
- `com.google.android.material:material:1.12.0` - Material Design компоненты
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.0` - жизненный цикл
- `androidx.activity:activity-compose:1.8.2` - активность для Compose

## 📱 Тестирование на Redmi 10C

Телефон подключен и распознается ADB:
```
56082c59 device usb:1-6 product:rain_ru model:220333QNY device:rain
```

Функции для тестирования:
- Просмотр видео из галереи
- Переход между табами All Videos / Folders / Favorites
- Тогgling favored (лайков) состояния
- Авто-next воспроизведение после завершения видео

## 📄 Лицензия

Проект создан в учебных целях. Лицензия определяется на основе выбранного вами пакежа при публикации.

## 👨‍💻 Автор

**Jn-Kareenko** - разработка Android приложения для видеопроигрывания через Wi-Fi ADB