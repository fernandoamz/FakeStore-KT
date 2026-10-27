# FakeStore - Modern Android E-Commerce App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?style=for-the-badge&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Offline%20First-Room%20%2F%20Flow-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/API-DummyJSON-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blueviolet?style=for-the-badge" />
</p>

A production-ready, feature-complete Android E-Commerce application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Clean Architecture**. It consumes the [DummyJSON API](https://dummyjson.com/) and features a robust **Offline-First** implementation using Room and Kotlin Flows.

---

## 📱 Key Features

- **🛍️ Product Catalog & Infinite Scroll**: Real-time search bar, sticky category pills with Favorites filter, and automatic infinite scrolling pagination.
- **🎠 Interactive Hero Banner Slider**: Full-width edge-to-edge carousel highlighting featured promotions with high-res product photos and direct category filtering on tap.
- **🔍 Comprehensive Product Detail Screen**: Rich product views displaying image galleries (`HorizontalPager`), discounts, stock availability, customer reviews (`ProductReviewsComponent`), technical specs (`ProductSpecsComponent` with SKU, barcode, dimensions, weight, tags), and store policies (shipping, warranty, return).
- **🛒 Shopping Cart & Checkout Flow**: Full cart management, multi-step checkout with pre-filled address fetched from DummyJSON users, and order confirmation screen with official order IDs (`POST /carts/add`).
- **🔐 Session Persistence & Guest Mode**: Jetpack DataStore (Preferences) for secure session storage, conditional navigation starting in Guest Mode by default, and user profile management with Logout.
- **📶 Offline-First Architecture**: Room database acts as the single source of truth. The app remains fully browsable without an internet connection, automatically syncing with Retrofit when online.
- **🚀 Continuous Delivery (CD)**: Automated GitHub Actions workflow (`.github/workflows/android-cd.yml`) for building and distributing test APKs to Firebase App Distribution.

---

## 🏗️ Architecture & Tech Stack

The project follows **Clean Architecture** principles and **MVVM** with modularization:

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
    └── Retrofit API (Remote Data Source)
```

### Tech Stack:
- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose & Material 3
- **Dependency Injection**: Dagger Hilt 2.59.2
- **Local Database**: Room 2.8.5 with KSP
- **Network & Serialization**: Retrofit 3.0.0 & Gson (DummyJSON API)
- **Asynchronous Data Streams**: Coroutines & Kotlin Flow
- **Local Preferences**: Jetpack DataStore Preferences 1.1.3
- **Image Loading**: Coil 3 (Coil Compose & Network OkHttp)
- **Navigation**: Jetpack Navigation Compose 2.9.7
- **CI/CD**: GitHub Actions & Firebase App Distribution

---

## 🤖 AI Agentic Development Strategy

This project was engineered and maintained leveraging a rigorous **AI Agentic Development Workflow**:
- **Structured Planning & Artifacts**: Complex feature implementations are preceded by formal architectural implementation plans and task breakdowns.
- **Semantic Code Exploration**: Utilizes semantic search tools (`find_declaration`, `find_usages`) for precise symbol resolution across submodules.
- **Strict Guardrails & Safety**: File modifications are restricted to specialized IDE editing tools with conflict detection and automated validation checks.
- **Performance & Architectural Rules**: Enforces Jetpack Compose performance standards (stable keys, `collectAsStateWithLifecycle`, immutable states) and Offline-First principles defined in `AGENTS.md`.

---

## 📂 Project Structure

```
FakeStore/
├── app/                  # Application entry point, NavHost, MainViewModel, Splash
├── core/
│   ├── common/           # Shared utilities & extensions
│   ├── designsystem/     # Design system foundations
│   └── network/          # Retrofit API services (ProductsApi, CartApi, UserApi, AuthApi)
├── domain/               # Models, Repository interfaces, Use Cases (Clean Architecture)
├── data/                 # Repository implementations, Room Database, DAOs, DataStore Session
└── feature/
    └── products/         # Feature modules (Catalog, Cart, Checkout, Auth, Profile, Components)
```

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/fernandoamz/FakeStore-KT.git
   ```
2. Open the project in **Android Studio** (Koala or newer recommended with KSP support).
3. Sync project with Gradle files.
4. Run the app on an Android Emulator or physical device (`API 24+`).

---

## 📄 Documentation

- [Architecture Guide](ARCHITECTURE.md) - Detailed offline-first data flow and database schema.
- [CD Guide](docs/android-cd.md) - GitHub Actions & Firebase App Distribution setup.
- [Agent Engineering Guidelines](AGENTS.md) - Coding standards and Compose performance rules.

---

## 👤 Author
**Fernando** ([fernandoamz](https://github.com/fernandoamz))
