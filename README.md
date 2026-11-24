# KrishiPath (कृषिपंथ) — कृषि शिक्षा का नया रास्ता

An offline-first, bilingual (Hindi default + English) educational Android application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, **Room Database**, and **DataStore** for agriculture students across India preparing for B.Sc. Agriculture, CUET-UG, and ICAR AIEEA.

## Features Implemented
- **10 Complete Screens**: Home Dashboard, Agriculture Subjects, CUET & ICAR Preparation, Biology & Chemistry, Chapter-wise Study Notes (+ Note Reader), MCQ Practice with Answers & Explanations, Timed Mock Tests with Scorecard, Previous Year Questions (Verified vs. Sample labeled), Bookmarks & Offline Library, and Student Profile & Progress.
- **Offline Storage (Room + DataStore)**: Pre-populated local SQLite database for study notes, MCQs, PYQs, bookmarks, and student progress. No paid APIs or secrets required.
- **Bilingual Hindi (Default) & English Switch**: Instant top-bar and profile language toggle.
- **Adaptive Tablet & Phone Layout**: Uses `NavigationBar` on phones and `NavigationRail` + responsive grids on Android tablets.

## Building Debug & Release APKs

### 1. Debug APK
From the project root in a terminal or CI runner:
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```
Or in Android Studio: **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.

### 2. Release APK
1. Create an upload keystore (one-time):
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Provide signing environment variables:
   - `KEYSTORE_PATH` (path to `my-upload-key.jks`)
   - `STORE_PASSWORD`
   - `KEY_PASSWORD`
3. Build the release APK:
   ```bash
   ./gradlew assembleRelease
   # Output: app/build/outputs/apk/release/app-release.apk
   ```
   Or in Android Studio: **Build -> Generate Signed Bundle / APK -> APK**.

### 3. Building Without a Computer (Using an Android Tablet + GitHub Actions)
1. Export the source code ZIP or push directly to GitHub from the AI Studio menu.
2. On your Android tablet browser, open your GitHub repository -> **Actions** tab -> add an Android CI workflow (`.github/workflows/android.yml`) running `./gradlew assembleDebug`.
3. Download the generated `app-debug.apk` artifact directly onto your Android tablet and install it.
