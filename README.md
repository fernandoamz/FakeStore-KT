# FakeStore - Modern Android E-Commerce App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?style=for-the-badge&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Offline%20First-Room%20%2F%20Flow-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/API-DummyJSON-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/AI-Google%20LiteRT-blueviolet?style=for-the-badge" />
  <img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?style=for-the-badge" />
</p>

Android e-commerce app built with **Kotlin**, **Jetpack Compose**, **Material 3**, and **Clean Architecture**. It features an **Offline-First** Room + Flow data layer (including robust Offline-First Shopping Cart and Search History with the **Transactional Outbox Pattern**), **Google LiteRT** on-device AI visual search, **Android 15+ 16KB page size compliance**, and an **AI shopping assistant** proxied through Firebase Functions.

---

## Key Features

- **Product catalog & infinite scroll**: Search, sticky category pills (including Favorites), and pagination.
- **Hero banner slider**: Full-width carousel with featured products and category jumps.
- **Product detail**: Image gallery, discounts, stock, reviews, specs (SKU, barcode, dimensions, weight, tags), and store policies.
- **🛒 Offline-First Cart & Checkout**: Local Room persistence with atomic transactions and background synchronization via `WorkManager` (`CartSyncWorker`) using the Outbox pattern.
- **🔍 Dedicated Search & History**: Independent search screen with real-time reactive pipeline (`debounce`, `distinctUntilChanged`, `flatMapLatest`) and Room-backed persistent search history.
- **📷 On-Device AI Visual Search (Google LiteRT & CameraX 1.4.1)**: Real-time product recognition and object detection supporting YOLO11 (`.tflite`) models directly on edge devices (fully 16KB page-size compatible for Android 15+).
- **IA shopping assistant**: Bottom-tab **Meli AI** chat that recommends and compares catalog products (with message persistence in Room).
- **Session & secure storage**: Encrypted token storage backed by **Android Keystore & AES-GCM**.
- **CI/CD**: GitHub Actions builds, runs unit tests, distributes APKs via Firebase App Distribution, and creates automatic GitHub Releases.

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
    ├── Room Database (Local Single Source of Truth + Outbox Queue)
    └── Retrofit API (DummyJSON + Assistant HTTPS function)
```

### Tech stack
- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose & Material 3
- **DI**: Dagger Hilt 2.59.2
- **Local DB**: Room 2.8.5 (KSP) with explicit database migrations (`MIGRATION_4_5`)
- **Network**: Retrofit 3.0.0 & Gson
- **Async**: Coroutines & Flow
- **Background Sync**: WorkManager 2.12.0 & Hilt Work
- **Security**: Android Keystore & AES-GCM
- **Camera & AI**: CameraX 1.4.1 & Google LiteRT (Lite Runtime) / TFLite (YOLO11 `.tflite` support)
- **Assistant backend**: Firebase Functions (2nd gen) + OpenAI (`gpt-4o-mini`) via Secret Manager
- **CI/CD**: GitHub Actions & Firebase App Distribution

---

## Project Structure

```
FakeStore/
├── app/                      # Entry point, NavHost, BuildConfig
├── core/
│   ├── common/
│   ├── designsystem/
│   └── network/              # Retrofit APIs & Security (CryptoManager, TokenManager)
├── domain/                   # Models, repos, use cases (incl. Search history & cart use cases)
├── data/                     # Room database, DAOs, entities, sync (Outbox & WorkManager)
├── feature/products/         # Catalog, cart, checkout, auth, profile, camera, search, llm/
├── functions/                # Firebase HTTPS function assistantChat
└── docs/                     # CD + assistant setup guides
```

---

## Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/fernandoamz/FakeStore-KT.git
   ```
3. Sync Gradle and run on an emulator or device (`API 24+`, 16KB aligned).

---

## Documentation

- [Architecture Guide](ARCHITECTURE.md) — Offline-first data flow and schema.
- [Assistant (Firebase Functions)](docs/assistant-firebase.md) — Deploy, secrets, Android URL, request contract.
- [CD Guide](docs/android-cd.md) — GitHub Actions & Firebase App Distribution.
- [Agent Engineering Guidelines](AGENTS.md) — Coding standards and Compose performance rules.

---

## Author

**Fernando** ([fernandoamz](https://github.com/fernandoamz))
