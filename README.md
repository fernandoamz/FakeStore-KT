# FakeStore - Modern Android E-Commerce App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?style=for-the-badge&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Offline%20First-Room%20%2F%20Flow-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/API-DummyJSON-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Assistant-Firebase%20Functions-red?style=for-the-badge&logo=firebase" />
  <img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blueviolet?style=for-the-badge" />
</p>

Android e-commerce app built with **Kotlin**, **Jetpack Compose**, **Material 3**, and **Clean Architecture**. It uses the [DummyJSON API](https://dummyjson.com/) with an **Offline-First** Room + Flow data layer, plus an **IA shopping assistant** proxied through Firebase Functions (OpenAI key stays server-side).

---

## Key Features

- **Product catalog & infinite scroll**: Search, sticky category pills (including Favorites), and pagination.
- **Hero banner slider**: Full-width carousel with featured products and category jumps.
- **Product detail**: Image gallery, discounts, stock, reviews, specs (SKU, barcode, dimensions, weight, tags), and store policies.
- **Cart & checkout**: Cart management, multi-step checkout with address from DummyJSON users, and order confirmation (`POST /carts/add`).
- **IA shopping assistant**: Bottom-tab **Asistente** chat that recommends and compares catalog products. Replies render light markdown (bold, lists). Starter suggestions adapt to available categories.
- **Voice input**: Mic on the assistant input bar uses system speech-to-text (`RECORD_AUDIO` + `RecognizerIntent`, Spanish).
- **Camera**: Custom CameraX capture flow (flip, flash, gallery) from catalog / assistant actions.
- **CP / address sheet**: Tap **CP** in the header to open a full-width bottom sheet and confirm delivery address.
- **Session & guest mode**: DataStore session, guest browsing by default, profile and logout.
- **Offline-First**: Room is the source of truth; Retrofit syncs when online.
- **CI/CD**: GitHub Actions builds and can distribute test APKs via Firebase App Distribution.

---

## Architecture & Tech Stack

```
UI (Jetpack Compose)
    ↓
ViewModel (StateFlow & collectAsStateWithLifecycle)
    ↓
Use Cases
    ↓
Repository Interface
    ↓
Repository Implementation (Offline-First)
    ├── Room Database (Local Single Source of Truth)
    └── Retrofit API (DummyJSON + Assistant HTTPS function)
```

### Tech stack
- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose & Material 3
- **DI**: Dagger Hilt 2.59.2
- **Local DB**: Room 2.8.5 (KSP)
- **Network**: Retrofit 3.0.0 & Gson
- **Async**: Coroutines & Flow
- **Preferences**: DataStore Preferences 1.1.3
- **Images**: Coil 3
- **Navigation**: Navigation Compose 2.9.7
- **Camera**: CameraX
- **Assistant backend**: Firebase Functions (2nd gen) + OpenAI (`gpt-4o-mini`) via Secret Manager
- **CI/CD**: GitHub Actions & Firebase App Distribution

---

## Project Structure

```
FakeStore/
├── app/                      # Entry point, NavHost, BuildConfig (ASSISTANT_BASE_URL)
├── core/
│   ├── common/
│   ├── designsystem/
│   └── network/              # ProductsApi, CartApi, UserApi, AuthApi, AssistantApi
├── domain/                   # Models, repos, use cases (incl. SendAssistantMessage)
├── data/                     # Room, DataStore, repository implementations
├── feature/products/         # Catalog, cart, checkout, auth, profile, camera, llm/
├── functions/                # Firebase HTTPS function assistantChat
├── docs/                     # CD + assistant setup guides
├── firebase.json
└── .firebaserc
```

---

## Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/fernandoamz/FakeStore-KT.git
   ```
2. Open in **Android Studio** (recent version with KSP support).
3. Sync Gradle and run on an emulator or device (`API 24+`).

### Optional: enable the IA assistant

Without this, the rest of the app still runs; only the Asistente tab needs the function URL.

1. Follow [Assistant (Firebase Functions)](docs/assistant-firebase.md) to deploy `assistantChat` and set `OPENAI_API_KEY` in Secret Manager.
2. Add to **local** `local.properties` (never commit this file):
   ```properties
   ASSISTANT_BASE_URL=https://assistantchat-xxxxx-uc.a.run.app/
   ```
3. Rebuild/reinstall the app.

Secrets stay out of git: `local.properties`, `functions/.secret.local`, and `functions/.env` are gitignored. Only `.example` templates are committed.

---

## Documentation

- [Architecture Guide](ARCHITECTURE.md) — Offline-first data flow and schema.
- [Assistant (Firebase Functions)](docs/assistant-firebase.md) — Deploy, secrets, Android URL, request contract.
- [CD Guide](docs/android-cd.md) — GitHub Actions & Firebase App Distribution.
- [Agent Engineering Guidelines](AGENTS.md) — Coding standards and Compose performance rules.

---

## Author

**Fernando** ([fernandoamz](https://github.com/fernandoamz))
