# EcoTrack – Development Log

## Phase 1 – Project Setup ✅
- Initialised Android project with Kotlin, MVVM architecture
- Configured Room, Navigation Component, Coroutines, MPAndroidChart, TF Lite
- Set up `.gitignore`, `gradle.properties`, and `settings.gradle`

## Phase 2 – Data Layer ✅
- Defined Room entities: `EcoActivityEntity`, `AchievementEntity`, `UserProfileEntity`
- Implemented DAOs with suspend functions and Flow-based queries
- Created `EcoTrackDatabase` with type converter for `ActivityType` enum
- Built `EcoRepository` as single source of truth with seeding helpers

## Phase 3 – AI / ML Layer ✅
- Implemented `CarbonEstimator` with IPCC-derived emission factors for all 5 categories
- Implemented `RecommendationEngine` with rule-based scoring (Wynes & Nicholas 2017)
- Added `RecommendationRepository` with caching layer

## Phase 4 – UI Layer ✅
- Single-Activity architecture with Navigation Component
- `DashboardFragment` — greeting, streak, weekly progress, recent activities FAB
- `LogActivityFragment` — category spinner, description, quantity, live carbon preview
- `AnalyticsFragment` — pie chart (category breakdown) + line chart (7-day trend)
- `TipsFragment` — RecyclerView of personalised recommendations + refresh FAB

## Phase 5 – Testing ✅
- Unit tests: `CarbonEstimatorTest` (12 cases), `RecommendationEngineTest` (7 cases),
  `LogActivityViewModelTest` (5 cases) — all using JUnit 4 + Mockito
- UI / instrumented tests: `MainActivityTest` (5 Espresso scenarios)

## Phase 6 – Documentation ✅
- `README.md` — feature overview, setup guide, tech stack
- `PRESENTATION.md` — architecture diagrams, AI description, testing table, discussion questions

## Planned Enhancements (Post-Assignment)
- [ ] Replace rule engine with a trained TF Lite regression/ranking model
- [ ] Add Firebase Auth + Firestore for cross-device sync
- [ ] Implement community feed for sharing eco-tips
- [ ] Add widget for daily CO₂ summary on the home screen
- [ ] Localisation support (multiple languages)
- [ ] Dark mode polish
