# NutriSnap 🥑📸

> **NutriSnap** is a modern, AI-powered food photo scanner and macronutrient tracker for Android built with Kotlin and Jetpack Compose. Simply snap a photo or upload an image of your meal, send it to ChatGPT (`gpt-4o-mini` Vision), and instantly get accurate calories, protein, carbohydrates, fats, and nutritional insights.

---

## 📥 Download App

[![Download APK](https://img.shields.io/badge/Download-NutriSnap%20v1.0.0%20APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](release/NutriSnap-v1.0.0.apk)

Get the ready-to-install Android application directly from the repository:
* 📲 **[NutriSnap-v1.0.0.apk](release/NutriSnap-v1.0.0.apk)** *(~18.3 MB, Android 8.0+)*
* 📖 See [Release Guide](release/README.md) for step-by-step installation instructions.

---

## ✨ Features

* **📷 Photo Capture & Image Upload**: Take food photos using the device camera or select images from your gallery with live photo preview.
* **🤖 ChatGPT Vision Integration**: Directly connects to OpenAI's GPT-4o-mini Vision model to evaluate portion sizes, ingredient breakdowns, and accurate macronutrient metrics.
* **🥗 Intelligent Built-in Visual Classifier**: Automatic local color spectrum analyzer and comprehensive USDA reference database for instant offline food recognition without hardcoded defaults.
* **📊 Daily Macro Rings & Calorie Dial**: Clean circular progress indicators for calories, protein, carbs, and fat with real-time target completion percentages.
* **📝 Natural Language Quick Meal Logging**: Option to log meals via text queries parsed by AI.
* **🔒 Privacy & Encrypted Local Storage**: Encrypted Room database with AES-256 for offline-first nutrition history and secure API key management via AndroidX EncryptedSharedPreferences.
* **🎨 Modern Obsidian Dark Theme**: Designed with Jetpack Compose Material 3, sleek cards, micro-interactions, and custom glow aesthetics.

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 2.0+
* **UI**: Jetpack Compose & Material 3
* **Architecture**: Clean Architecture + MVVM + Repository Pattern
* **Dependency Injection**: Dagger Hilt
* **Local Database**: Android Room with Flow
* **Networking**: Retrofit 2, OkHttp 3, Gson
* **Security**: AndroidX Security Crypto (`EncryptedSharedPreferences`, MasterKey AES-256)
* **AI / Computer Vision**: OpenAI Vision API (`gpt-4o-mini`)

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Ladybug / Koala or newer
* Android SDK 34+
* Android device or emulator running Android 8.0 (API 26) or higher

### Installation
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

### Configuring OpenAI API Key (Optional)
1. In the app, open the **Camera** tab.
2. Tap the **"⚙️ Set OpenAI Key"** badge at the top-right.
3. Paste your OpenAI API key (`sk-...`) and tap **Save Key**.
4. When configured, food photos are analyzed directly in real-time by ChatGPT Vision. If no key is configured, the built-in intelligent visual analyzer will operate locally.

---

## 📄 License
This project is licensed under the Apache 2.0 License.
