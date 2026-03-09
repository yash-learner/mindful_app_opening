# 🧘 Mindful — Todo Overlay App

> Be intentional with your time. See your todo list before opening distracting apps.

---

## What It Does

**Mindful** shows your daily todo list as a full-screen overlay whenever you open a selected app (e.g., Instagram, YouTube, Twitter). Before you can dive in, you're reminded of what you actually planned to do today. You then choose to:

- **Continue to App** — proceed into the app (overlay won't show again for this session)
- **Go Home Instead** — navigate away and stay focused

---

## Screenshots / Screens

| Screen | Description |
|--------|-------------|
| **Main** | Landing screen with navigation + permission status indicator |
| **Todos** | Add, complete, and delete your daily tasks |
| **Select Apps** | Toggle which installed apps trigger the overlay |
| **Permissions** | Step-by-step guide to grant the two required permissions |
| **Overlay** | Full-screen reminder shown above the selected app |

---

## Technical Approach

### App Detection — `AccessibilityService`

The app uses an Android **AccessibilityService** to detect when a new app window comes to the foreground in real time.

- Listens for `TYPE_WINDOW_STATE_CHANGED` accessibility events
- Fires on every foreground app change without polling
- More reliable and battery-efficient than `UsageStatsManager` polling
- Requires the user to manually enable it in **Settings → Accessibility**

### Overlay — `WindowManager` with `TYPE_APPLICATION_OVERLAY`

The todo reminder is displayed using Android's **WindowManager** API:

```kotlin
WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  // API 26+
```

This renders a full-screen view directly above all other windows, outside of any Activity lifecycle. It requires the `SYSTEM_ALERT_WINDOW` permission.

### Session Management — Avoid Infinite Loops

A key challenge is preventing the overlay from re-appearing endlessly:

- `lastTriggeredPackage` tracks which app last triggered the overlay
- Once shown for app X, the overlay won't show again until the user navigates to a **different** app
- If the user taps **Go Home**, the session is reset so the overlay reappears next time
- If the user taps **Continue**, the session persists — no re-prompt until they leave and return

### Local Storage — Room Database

All data is stored locally using **Room** (SQLite wrapper):

| Table | Purpose |
|-------|---------|
| `todos` | Text, completion state, timestamp |
| `selected_apps` | Package name + display name of trigger apps |

No cloud sync, no login, no analytics.

---

## Required Permissions

| Permission | Why It's Needed |
|------------|----------------|
| `SYSTEM_ALERT_WINDOW` | Draw the overlay above other apps |
| `BIND_ACCESSIBILITY_SERVICE` | Detect when selected apps are launched |
| `QUERY_ALL_PACKAGES` | List installed apps for the selection screen (Android 11+) |

### Android Limitations & Notes

1. **Accessibility Services are sensitive**: Android labels them as high-risk. Users must manually enable the service in Settings — apps cannot request this permission at install time.

2. **`SYSTEM_ALERT_WINDOW` on Android 6+**: Must be granted via `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`. The system shows a dedicated UI for this.

3. **Android 10+ background restrictions**: Starting with Android 10, apps cannot start Activities from the background. This app avoids that — the overlay is a `WindowManager` view, not an Activity, so it works correctly.

4. **Launcher/System package filtering**: The service skips events from system UI, launchers, and its own package to prevent false triggers.

5. **App list on Android 11+**: `PackageManager.getInstalledApplications()` requires `QUERY_ALL_PACKAGES` on Android 11+ (API 30+) to return third-party apps.

---

## Architecture

```
com.mindful.appopening/
├── MindfulApplication.kt              # Application class, DB singleton
├── data/
│   ├── model/
│   │   ├── Todo.kt                    # Room entity: id, text, isCompleted, createdAt
│   │   └── SelectedApp.kt             # Room entity: packageName, appName
│   ├── database/
│   │   ├── AppDatabase.kt             # Room database (v1)
│   │   ├── TodoDao.kt                 # CRUD for todos
│   │   └── SelectedAppDao.kt          # CRUD for selected apps
│   └── repository/
│       ├── TodoRepository.kt
│       └── AppRepository.kt
├── service/
│   ├── AppMonitorAccessibilityService.kt  # Core: detects app launches
│   ├── OverlayManager.kt                  # Shows/hides overlay via WindowManager
│   └── OverlayTodoAdapter.kt              # RecyclerView adapter for overlay todos
└── ui/
    ├── main/MainActivity.kt               # Home screen
    ├── onboarding/OnboardingActivity.kt   # Permission setup
    ├── todo/
    │   ├── TodoActivity.kt
    │   ├── TodoViewModel.kt
    │   └── TodoAdapter.kt
    └── appselect/
        ├── AppSelectActivity.kt
        ├── AppSelectViewModel.kt
        ├── AppSelectAdapter.kt
        └── InstalledApp.kt                # UI model for installed apps
```

**Tech Stack:**
- Kotlin + Coroutines
- Room (local database)
- ViewModel + LiveData (Jetpack)
- ViewBinding
- Material 3 UI components
- minSdk 26 (Android 8.0) — required for `TYPE_APPLICATION_OVERLAY`

---

## Setup & Run Instructions

### Prerequisites

- [Android Studio Hedgehog (2023.1+)](https://developer.android.com/studio) or later
- Android device or emulator running Android 8.0+ (API 26+)
- JDK 17 (bundled with Android Studio)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yash-learner/mindful_app_opening.git
   cd mindful_app_opening
   ```

2. **Open in Android Studio**
   - File → Open → select the `mindful_app_opening` folder
   - Wait for Gradle sync to complete (downloads dependencies automatically)

3. **Build & Run**
   - Connect a physical Android device or start an emulator
   - Click the green **Run** button (or press `Shift+F10`)
   - Select your device

   Alternatively, from terminal (requires Gradle Wrapper JAR):
   ```bash
   # Generate the wrapper JAR first (requires Gradle installed locally):
   gradle wrapper --gradle-version=8.6

   # Then build:
   ./gradlew assembleDebug
   # APK will be at: app/build/outputs/apk/debug/app-debug.apk
   ```

### First-Time Setup in the App

1. Launch **Mindful**
2. Tap **Setup Permissions** and grant both permissions:
   - **Draw Over Other Apps**: Tap Grant → enable the toggle for Mindful
   - **Accessibility Service**: Tap Grant → find "Mindful" in the list → enable it
3. Return to the app — the status card should show "✓ All permissions granted"
4. Tap **My Todo List** → add your tasks for today
5. Tap **Select Trigger Apps** → toggle the apps you want to monitor
6. Open one of the selected apps — the overlay will appear!

---

## MVP Scope

- [x] Daily todo list (add, complete, delete)
- [x] App selection from installed user apps
- [x] Overlay above selected apps (real-time detection)
- [x] Continue / Go Home overlay actions
- [x] Session management (no infinite overlay loops)
- [x] Permission onboarding screen
- [x] Local storage only (Room database)
- [x] No login, no cloud, no analytics

---

## Future Enhancements (Post-MVP)

- App icons in the selection list
- Todo categories / priorities
- Scheduled overlay time windows (e.g., only show between 9am–5pm)
- "Snooze overlay for N minutes" option
- Usage statistics (how often did I redirect myself?)
- Widget for quick todo entry
- Dark/light theme toggle
