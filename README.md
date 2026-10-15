# FakeStore - Modern Android E-Commerce App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?style=for-the-badge&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Offline%20First-Room%20%2F%20Flow-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/DI-Dagger%20Hilt-red?style=for-the-badge" />
</p>

A production-ready, feature-complete Android E-Commerce application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Clean Architecture**. It consumes the public [Fake Store API](https://fakestoreapi.com/) and features a robust **Offline-First** implementation using Room and Kotlin Flows.

---

## 📱 Key Features

- **🛍️ Product Catalog & Search**: Real-time search bar, category filtering (Electronics, Jewelery, Men's/Women's clothing), and animated Shimmer Skeleton loaders.
- **✨ High-End UI/UX (Material 3)**: Immersive "Dark Luxury" login & registration, hero promotional banners, interactive product cards with favorite toggle (spring animation), and rating stars.
- **🛒 Shopping Cart & Checkout Flow**: Full cart management (add, increase/decrease quantities, remove items), multi-step checkout with pre-filled address fetched from Fake Store API (`/users/1`), and order confirmation screen with official order IDs (`POST /carts`).
- **🔐 Session Persistence & Guest Mode**: Jetpack DataStore (Preferences) for secure token storage, conditional navigation starting in Guest Mode by default, and user profile management with Logout.
- **📶 Offline-First Architecture**: Room database acts as the single source of truth. The app remains fully browsable without an internet connection, automatically syncing with Retrofit when online.

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
- **Local Database**: Room 2.8.5
- **Network & Serialization**: Retrofit 3.0.0 & Gson
- **Asynchronous Data Streams**: Coroutines & Kotlin Flow
- **Local Preferences**: Jetpack DataStore Preferences 1.1.3
- **Image Loading**: Coil 3 (Coil Compose & Network OkHttp)
- **Navigation**: Jetpack Navigation Compose 2.9.7

---

## 📂 Project Structure

```
FakeStore/
├── app/                  # Application entry point, NavHost, MainViewModel
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
- [Agent Engineering Guidelines](AGENTS.md) - Coding standards and Compose performance rules.

---

## 👤 Author
**Fernando** ([fernandoamz](https://github.com/fernandoamz))
