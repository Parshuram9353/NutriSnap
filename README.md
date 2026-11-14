# NutriSnap 🥑📸

> **NutriSnap** is a modern, 100% offline AI-powered food photo scanner and macronutrient tracker for Android built with Kotlin and Jetpack Compose. Simply snap a photo or upload an image of your meal, and the on-device TensorFlow Lite computer vision model instantly identifies the food and calculates accurate calories, protein, carbohydrates, fats, and nutritional insights — completely offline with zero internet required.

---

## 📥 Download App

[![Direct Download APK](https://img.shields.io/badge/Direct%20Download-NutriSnap%20v1.0.0%20APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Parshuram9353/NutriSnap/raw/main/release/NutriSnap-v1.0.0.apk)

Get the ready-to-install Android application directly from the repository:
* 📲 **[Download NutriSnap-v1.0.0.apk](https://github.com/Parshuram9353/NutriSnap/raw/main/release/NutriSnap-v1.0.0.apk)** *(~60 MB, Android 8.0+, 100% Offline)*
* 📖 See [Release Guide](release/README.md) for step-by-step installation instructions.

---

## ✨ Features

* **📷 Photo Capture & Image Upload**: Take food photos using the device camera or select images from your gallery with live photo preview.
* **⚡ 100% On-Device Neural Vision (TensorFlow Lite)**: Runs an embedded MobileNet/AIY computer vision model directly on your phone's processor in ~50ms. No cloud servers, no OpenAI/ChatGPT dependencies, and no API keys needed.
* **🥗 Comprehensive Offline Nutrition Database**: Over 2,000+ Indian & Global dishes pre-loaded with USDA & IFCT verified calories, protein, carbohydrates, and fats.
* **📊 Daily Macro Rings & Calorie Dial**: Clean circular progress indicators for calories, protein, carbs, and fat with real-time target completion percentages.
* **📝 Natural Language Quick Meal Logging**: Offline natural text query parsing to log meals without manual macro entry.
* **🔒 Privacy & Encrypted Local Storage**: Encrypted Room database with SQLCipher AES-256 for offline-first nutrition history. All your diet data stays strictly on your device.
* **🎨 Modern Obsidian Dark Theme**: Designed with Jetpack Compose Material 3, sleek cards, micro-interactions, and custom glow aesthetics.

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 2.0+
* **UI**: Jetpack Compose & Material 3
* **Architecture**: Clean Architecture + MVVM + Repository Pattern
* **On-Device AI Engine**: TensorFlow Lite (`org.tensorflow:tensorflow-lite:2.14.0`, Support Library)
* **Dependency Injection**: Dagger Hilt
* **Local Database**: Android Room with SQLCipher & Kotlin Flow
* **Security**: AndroidX Security Crypto (`EncryptedSharedPreferences`, MasterKey AES-256)

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Ladybug / Koala or newer
* Android SDK 34+
* Android device or emulator running Android 8.0 (API 26) or higher

### Installation & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/Parshuram9353/NutriSnap.git
   cd NutriSnap
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync dependencies.
4. Run the app on your connected device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License
This project is licensed under the Apache 2.0 License.
