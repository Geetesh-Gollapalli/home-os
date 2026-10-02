# Home OS — Family Operating System 🏠

<p align="center">
  <b>A modern, accessible, family-first Android application designed to simplify everyday household living.</b><br>
  Built with <b>Jetpack Compose</b>, <b>Room Database</b>, <b>Voice Commands</b>, <b>Android AppWidgets</b>, and <b>Real-Time Family Sync</b>.
</p>

---

## 🌟 Overview

**Home OS** is built for families to manage daily schedules, grocery shopping lists, 1-tap family communications (Calls, WhatsApp, SMS), monthly household expenses, quick notes, and voice interactions in one unified experience.

* **Application ID**: `com.HomeOS.homeos`
* **Target SDK**: Android 15 / 16 (SDK 36) • Min SDK 24
* **Author / Builder**: Built by Geetesh with ❤️

---

## ✨ Key Features

* 🏠 **Home Dashboard**:
  * Dynamic time-of-day greetings ("Good morning / afternoon / evening") personalized to the user's name.
  * "Today" schedule summary card and one-touch action grid.
  * Integrated "Ask Home OS" signature voice assistant sheet.

* ⏱️ **Tasks & Reminders**:
  * Clean schedule management organized into **Today**, **Upcoming**, and **Completed** sections.
  * Flexible time presets ("Later today", "This evening", "Tomorrow", "Next week", or custom time).
  * **Automated Retention Purge**: Completed tasks are automatically removed **7 days** after completion.

* 🛒 **Shopping List**:
  * Categorized grocery management (*Groceries*, *Vegetables*, *Household*, *Personal*, *Other*).
  * Item quantities and unit support (e.g. *2 packets*, *5 kg*).
  * **Automated Retention Purge**: Checked-off shopping items are automatically removed **24 hours** after completion.

* 📞 **Family Speed Dial**:
  * 1-Tap Phone Dialing (`ACTION_DIAL`) with zero high-risk permission requirements.
  * 1-Tap Direct WhatsApp Chatting (`com.whatsapp`) with pre-filled message quick options (*"Please call me"*, *"At home now"*, *"Love you!"*).
  * 1-Tap SMS launcher.

* 💰 **Expense Analytics**:
  * Multi-segment stacked spending bar showing monthly spending distribution at a glance.
  * Distinct color and icon palette for spending categories (*Food*, *Groceries*, *Household*, *Travel*, *Health*, *Other*).
  * Smooth animated category progress bars (`animateFloatAsState`) and percentage badges (`42%`).
  * Interactive category filtering.

* 🎙️ **Voice Assistant Engine**:
  * Fast, offline-first deterministic voice command parser ([`VoiceCommandParser`](app/src/main/java/com/example/ai/VoiceCommandParser.kt)) paired with Android `SpeechRecognizer`.
  * Supports natural commands like *"Add milk to my shopping list"*, *"Remind me to call Dad at 6"*, *"Spent 250 on vegetables"*, and *"Call Geetesh"*.

* 📱 **Home Screen AppWidgets**:
  * **Tasks & Shopping List Collection Widget (4x3)**: Live scrollable list of pending items on the phone home screen.
  * **Quick Actions Widget (4x2)**: Shortcuts for Call, WhatsApp, Shopping, and Tasks.
  * **Family Speed Dial Widget (4x2)**: 1-tap phone dial & WhatsApp launch for saved family members.

* 🔄 **Connect Family Devices**:
  * Displays current device name (e.g. `Geetesh (This Device - Host)`).
  * Generates a unique 6-digit Family Group Code (e.g. `HOME-7A4B`).
  * Family members can enter the code on another phone to connect and sync lists in real time.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technologies Used |
| :--- | :--- |
| **UI Framework** | Jetpack Compose (Material 3, Navigation Compose, Compose BOM) |
| **Architecture** | Offline-First MVVM with Unidirectional Data Flow (`StateFlow`, `SharedFlow`, Coroutines) |
| **Database** | Room ORM (SQLite) compiled via KSP with automated schema migration (`v5`) |
| **Voice & Speech** | Custom `VoiceCommandParser` + Android `SpeechRecognizer` |
| **Widgets** | Android AppWidget framework (`RemoteViews` + `RemoteViewsService`) with live Room DB broadcast sync |
| **Build Tooling** | Gradle Kotlin DSL (`build.gradle.kts`), R8 / ProGuard rules (`proguard-rules.pro`) |

---

## 📁 Project Architecture & Package Structure

```
app/src/main/java/com/example/
 ├── ai/                # Voice command parser & intent definitions
 ├── data/
 │    ├── dao/          # Room DAOs (Task, Shopping, Family, Expense, Note, User)
 │    ├── db/           # AppDatabase Room database setup
 │    ├── model/        # Room Data Entities & data classes
 │    └── repository/   # Repositories with reactive Flows & auto-cleanup logic
 ├── ui/
 │    ├── components/   # Accessible design system components (Buttons, Cards, Checkboxes, VoiceSheet)
 │    ├── navigation/   # NavHost, Screen routes & Splash navigation
 │    ├── screens/      # Compose Screens (Home, Tasks, Shopping, Family, Expenses, Notes, More, Widgets, Onboarding, Splash)
 │    ├── theme/        # Color palette, Material 3 Typography & MomOSTheme
 │    └── viewmodel/    # MVVM ViewModels (Home, Task, Shopping, Family, Expense, Settings, Note)
 ├── util/              # CommunicationHelper for Phone/WhatsApp/SMS intents
 └── widget/            # AppWidget Providers & RemoteViewsService for home screen widgets
```

---

## 🚀 Building & Running

### Prerequisites
* **Android Studio** Ladybug or newer
* **Android SDK**: 36 (Min SDK 24)
* **JDK**: 17 / Kotlin 2.x

### Build Commands

* **Run Unit Tests**:
  ```bash
  ./gradlew test
  ```

* **Build Release APK**:
  ```bash
  ./gradlew app:assembleRelease
  ```

* **Build Signed App Bundle (`.aab`) for Google Play Store**:
  ```bash
  ./gradlew app:bundleRelease
  ```

---

<p align="center">
  <b>Home OS</b> • Built by Geetesh with ❤️
</p>
