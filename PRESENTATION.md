# EcoTrack – Environmental Impact Tracker

## Class Presentation Document

---

## 1. Application Overview

**EcoTrack** is an Android application that helps users measure, understand, and reduce
their personal carbon footprint.  Users log everyday activities (travel, energy, food,
shopping, and waste) and receive AI-generated recommendations tailored to their behaviour.

---

## 2. Problem Statement

Climate change is driven largely by individual lifestyle choices, yet most people have
no visibility into their personal contribution.  Studies show that simply measuring a
behaviour is a strong predictor of changing it ("what gets measured gets managed").
EcoTrack makes carbon accounting effortless and actionable.

---

## 3. Key Features

| Feature | Description |
|---|---|
| **Activity Logging** | Users record daily activities across 5 categories with quantity and unit |
| **AI Carbon Estimator** | On-device ML model (TF Lite) converts activity data to kg CO₂e |
| **Smart Recommendations** | Rule-based AI engine surfaces the highest-impact changes |
| **Analytics Dashboard** | Pie chart (category breakdown) and line chart (7-day/30-day trend) |
| **Gamification** | Streak counter, achievements, and points to encourage consistency |
| **Weekly Goal** | Users set a personal weekly CO₂ budget and track progress |

---

## 4. Architecture

EcoTrack follows the **MVVM (Model–View–ViewModel)** architecture recommended by Google:

```
┌──────────────────────────────────────┐
│              UI Layer                │
│  MainActivity  ▸ Fragments  ▸ Adapters│
└────────────────┬─────────────────────┘
                 │ observes LiveData
┌────────────────▼─────────────────────┐
│           ViewModel Layer            │
│  DashboardVM  LogActivityVM          │
│  AnalyticsVM  TipsVM                 │
└────────────────┬─────────────────────┘
                 │ calls suspend fns / Flow
┌────────────────▼─────────────────────┐
│          Repository Layer            │
│  EcoRepository  RecommendationRepo   │
└───────┬──────────────────┬───────────┘
        │                  │
┌───────▼──────┐   ┌───────▼──────────┐
│  Room DB     │   │  ML Engine       │
│  (SQLite)    │   │  CarbonEstimator │
│              │   │  RecommendationEngine│
└──────────────┘   └──────────────────┘
```

---

## 5. Technology Stack

| Layer | Technology | Reason |
|---|---|---|
| Language | **Kotlin** | Official Android language; concise and null-safe |
| Architecture | **MVVM + LiveData** | Lifecycle-aware; separates concerns cleanly |
| Async | **Kotlin Coroutines + Flow** | Non-blocking DB and network calls |
| Database | **Room (SQLite)** | Structured local persistence with compile-time SQL validation |
| AI / ML | **TensorFlow Lite** | On-device inference; no internet required; privacy preserving |
| Charts | **MPAndroidChart** | Mature, open-source chart library |
| UI | **Material Design 3** | Consistent, modern, accessible design language |
| DI (lightweight) | **Manual factory pattern** | Keeps the project simple without a DI framework |

---

## 6. AI / Machine Learning Component

### CarbonEstimator
- Uses published IPCC AR6 emission factors as a knowledge base.
- Accepts activity type, sub-type (e.g. "car", "beef"), and quantity.
- In production: replaced by a **quantised TF Lite regression model**
  (`assets/carbon_model.tflite`) trained on real-world emission datasets.
- Output: kg CO₂-equivalent (negative values indicate carbon savings).

### RecommendationEngine
- Analyses the user's weekly carbon breakdown.
- Applies **rule-based scoring** modelled on the Wynes & Nicholas (2017) paper
  *"The climate mitigation gap"* which ranks individual actions by impact.
- Returns tips sorted by estimated weekly CO₂ saving (highest first).
- In production: enhanced by a **collaborative filtering model** that learns
  which tips users actually follow, improving personalisation over time.

---

## 7. Testing Strategy

### Unit Tests (JUnit 4 + Mockito)
| Test Class | What it covers |
|---|---|
| `CarbonEstimatorTest` | All 5 activity types, fallback factors, linearity |
| `RecommendationEngineTest` | Rule triggers, sort order, maxTips limit, edge cases |
| `LogActivityViewModelTest` | Input validation, repository calls, LiveData values |

### Instrumented / UI Tests (Espresso)
| Test | Scenario |
|---|---|
| `dashboardScreenIsDisplayedOnLaunch` | FAB visible on launch |
| `tappingFabNavigatesToLogActivityScreen` | Navigation action works |
| `bottomNavNavigatesToAnalyticsTab` | Chart screen loads |
| `bottomNavNavigatesToTipsTab` | Tips screen loads |
| `logActivityShowsValidationErrorOnEmptySubmit` | Validation feedback shown |

---

## 8. Database Schema

```
eco_activities
  id            INTEGER PK AUTOINCREMENT
  activity_type TEXT    (TRANSPORTATION | ENERGY | FOOD | SHOPPING | WASTE)
  description   TEXT
  carbon_kg     REAL    (negative = saving)
  quantity      REAL
  unit          TEXT
  timestamp     INTEGER (Unix epoch ms)

achievements
  id            TEXT PK
  title         TEXT
  description   TEXT
  is_unlocked   INTEGER (0/1)
  unlocked_at   INTEGER (Unix epoch ms, nullable)

user_profile
  id            INTEGER PK = 1  (single-row table)
  name          TEXT
  weekly_goal_kg REAL
  total_points  INTEGER
  streak_days   INTEGER
  last_log_date INTEGER
```

---

## 9. Privacy & Ethical Considerations

- All data is stored **locally on-device** — no data ever leaves the device.
- TF Lite inference runs entirely on-device (no cloud API call).
- Users own their data and can delete the app to erase everything.
- Emission factors are sourced from peer-reviewed, publicly available literature.

---

## 10. Discussion Points for Class

1. **Why MVVM over MVC?** — Testability; ViewModel survives rotation; LiveData is lifecycle-aware.
2. **Why on-device ML vs. cloud API?** — Privacy, offline capability, lower latency, no API cost.
3. **How would you scale this?** — Add Firebase Auth + Firestore to sync across devices; federated learning to improve the ML model without sharing raw data.
4. **What are the limitations of the rule-based AI?** — Rules cannot discover unknown patterns; they require manual expert knowledge; they do not adapt to culture or geography.
5. **How would you A/B test the recommendation engine?** — Two variants of the model, assign users randomly, measure 30-day carbon reduction, use statistical significance testing.
