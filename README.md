# LiveScribe

LiveScribe is an Android speech-to-text app that transcribes speech in real time and displays it in a clean, card-based UI.  
It supports multiple Indian languages through a language selector, with English as the default.

---

## Features

- **Real-time transcription** using Android's `SpeechRecognizer`
- **Language selection** via a spinner (English + multiple Indian languages)
- **Assamese mode** that currently routes recognition through Bengali for script compatibility
- **Modern UI** with:
  - Top bar showing app title and language spinner
  - Scrollable transcript card with timestamp
  - Bottom control panel with record FAB and stop button
  - Copy and highlight actions for the transcript text

---

## Tech Stack

- **Platform:** Android (Kotlin)
- **Minimum Android version:** (TODO: add `minSdkVersion`)
- **Language:** Kotlin
- **UI:** `CoordinatorLayout`, `ConstraintLayout`, `NestedScrollView`, `MaterialCardView`, `FloatingActionButton`, `Spinner`
- **APIs:** `SpeechRecognizer`, `RecognizerIntent`

---

## Project Structure

> Folder and package names can be updated based on your actual project.

- `app/src/main/java/com/example/livetranscribeapplication/`
  - `MainActivity.kt` – main screen, speech recognition logic, spinner handling
- `app/src/main/res/layout/`
  - `activity_main.xml` – main UI layout (top bar, transcript card, control panel)
- `app/src/main/res/values/`
  - `colors.xml` – theme colors (background, text, accent, card, control panel)
  - `strings.xml` – app strings (app name, prompts, etc.)

---

## How It Works

### Language Selection

- A **Spinner** in the top bar lists supported languages:
  - Assamese, Bengali, English, Hindi, and other Indian languages
- The selected item is used to decide which recognition language code to send to `RecognizerIntent`.
- When **Assamese** is selected, the app currently uses **Bengali (`bn-IN`)** as the recognizer language so that the output appears in Bengali script, which is close to Assamese.

### Speech Recognition Flow

1. User taps the **record FAB** in the bottom control panel.
2. Open the project and let Gradle sync.
3. Ensure `RECORD_AUDIO` permission is declared in `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.RECORD_AUDIO" />
   
