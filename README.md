# NutriSnap 🥑📸

> **NutriSnap** is a modern AI-powered food photo scanner and macronutrient tracker for Android built with Kotlin and Jetpack Compose. NutriSnap is available in two distinct editions: a **100% Offline On-Device AI Edition (v2)** powered by TensorFlow Lite, and a **ChatGPT Cloud Edition (v1)** powered by OpenAI GPT-4o-mini Vision.

---

## 📥 Download App Releases

Choose the edition that best fits your workflow:

| Edition | Version | Size | AI Engine | Internet Required | Direct Download Link |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **v2 Offline AI** | `2.0.0` | **~60 MB** | **TensorFlow Lite + Local DB** | ❌ **No (100% Offline)** | [![Download v2](https://img.shields.io/badge/Download-v2.0.0%20Offline%20AI-10B981?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Parshuram9353/NutriSnap/raw/main/release/NutriSnap-v2.0.0-OfflineAI.apk) |
| **v1 ChatGPT** | `1.0.0` | **~24 MB** | **OpenAI GPT-4o-mini Vision** | ✔️ **Yes (Wi-Fi/Data)** | [![Download v1](https://img.shields.io/badge/Download-v1.0.0%20ChatGPT-8B5CF6?style=for-the-badge&logo=openai&logoColor=white)](https://github.com/Parshuram9353/NutriSnap/raw/main/release/NutriSnap-v1.0.0-ChatGPT.apk) |

📖 For step-by-step installation instructions, check the [Release Guide](release/README.md).

---

## ✨ Features by Edition

### ⚡ v2: 100% Offline AI Edition (Recommended)
* **On-Device Computer Vision**: Uses an embedded TensorFlow Lite food classification neural model (~50ms inference time).
* **Zero Internet or Cloud Dependence**: Works in Airplane Mode without servers or network connectivity.
* **Over 2,000+ Pre-Loaded Dishes**: Built-in nutrition database with USDA and Indian Food Composition (IFCT) verified calories, protein, carbs, and fats.
* **100% Privacy**: No food pictures or diet logs ever leave your device.
* **$0.00 Cost**: No API keys or subscriptions required.

### 🤖 v1: ChatGPT Cloud Edition
* **Multimodal GPT-4o-mini Vision**: Uses OpenAI's cloud API for detailed ingredients and recipe breakdown.
* **Conversational Dietary Insights**: Provides conversational health benefits and context for complex meals.
* **Bring Your Own Key**: Enter your personal OpenAI API Key (`sk-...`) in the app settings.

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 2.0+
* **UI**: Jetpack Compose & Material 3 (Obsidian Dark Theme)
* **Architecture**: Clean Architecture + MVVM + Repository Pattern
* **On-Device AI Engine (v2)**: TensorFlow Lite (`org.tensorflow:tensorflow-lite:2.14.0`, Support Library)
* **Cloud AI Engine (v1)**: OpenAI Vision API (`gpt-4o-mini`) via Retrofit 2 & OkHttp 3
* **Dependency Injection**: Dagger Hilt
* **Local Database**: Android Room with SQLCipher AES-256 encryption & Kotlin Flow
* **Security**: AndroidX Security Crypto (`EncryptedSharedPreferences`, MasterKey AES-256)

---

## 🚀 Getting Started for Developers

### Prerequisites
* Android Studio Ladybug / Koala or newer
* Android SDK 34+
* Android device or emulator running Android 8.0 (API 26) or higher

### Building from Source
```bash
# Clone the repository
git clone https://github.com/Parshuram9353/NutriSnap.git
cd NutriSnap

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease
```

---

## 📄 License
This project is licensed under the Apache 2.0 License.
