# EcoTrack – Environmental Impact Tracker

> Android application that measures your daily carbon footprint and delivers AI-powered sustainability recommendations.

---

## Features

| Feature | Description |
|---|---|
| 📊 **Activity Logging** | Track transport, energy, food, shopping, and waste activities |
| 🤖 **AI Carbon Estimator** | On-device ML engine (TensorFlow Lite) converts activities to kg CO₂ |
| 💡 **Smart Recommendations** | Personalised eco-tips ranked by estimated weekly CO₂ saving |
| 📈 **Analytics** | Pie chart (category breakdown) and 7-day / 30-day trend chart |
| 🏆 **Gamification** | Daily streaks, achievements, and points system |

---

## Architecture

**MVVM** with Room Database, Kotlin Coroutines/Flow, and Navigation Component:

```
UI (Fragments) → ViewModel → Repository → Room DB
                                        → ML Engine (CarbonEstimator / RecommendationEngine)
```

---

## Technology Stack

- **Language:** Kotlin
- **Architecture:** MVVM + LiveData + Coroutines
- **Database:** Room (SQLite)
- **AI / ML:** TensorFlow Lite (on-device inference)
- **Charts:** MPAndroidChart
- **UI:** Material Design 3
- **Testing:** JUnit 4, Mockito, Espresso

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 26+
- JDK 17

### Setup

```bash
# Clone the repository
git clone https://github.com/abdurrahman160903/EcoTrack.git

# Open in Android Studio
# File → Open → select the EcoTrack folder
```

1. Let Gradle sync and download dependencies automatically.
2. Connect a device or start an emulator (API 26+).
3. Run the app with **▶ Run 'app'**.

---

## Running Tests

```bash
# Unit tests (no device needed)
./gradlew test

# Instrumented UI tests (requires connected device / emulator)
./gradlew connectedAndroidTest
```

---

## Project Structure

```
app/src/
├── main/java/com/ecotrack/
│   ├── data/
│   │   ├── db/          # Room database, DAOs, entities
│   │   ├── model/       # Domain models (CarbonData, Recommendation, ActivityType)
│   │   └── repository/  # EcoRepository, RecommendationRepository
│   ├── ml/              # CarbonEstimator, RecommendationEngine
│   ├── ui/              # Fragments, ViewModels, MainActivity
│   └── util/            # Extensions, Constants
├── test/                # JUnit + Mockito unit tests
└── androidTest/         # Espresso UI tests
```

---

## Documentation

- [`PRESENTATION.md`](PRESENTATION.md) – Architecture diagrams, AI description, testing table, class discussion questions
- [`DEVELOPMENT_LOG.md`](DEVELOPMENT_LOG.md) – Implementation phases and planned enhancements

---

## Contributing

Fork the repository and submit a pull request for any improvements!