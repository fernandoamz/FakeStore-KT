# Architecture Documentation - FakeStore Android App

## 1. Offline-First Data Flow
The FakeStore application implements a robust **Offline-First Architecture**, ensuring that the local Room database acts as the single source of truth for all product data displayed in the UI.

```
Retrofit API (Remote)
    ↓
ProductRepositoryImpl
    ↓
Room Database (Local Single Source of Truth)
    ↓ (Kotlin Flow)
ViewModel (ProductsViewModel)
    ↓ (StateFlow)
Jetpack Compose UI (ProductsScreen)
```

- **Local Persistence**: Room persists products across application restarts. The app remains fully functional and browsable without an internet connection.
- **Data Synchronization**: When online, the repository triggers a refresh operation (`refreshProducts()`), which fetches fresh DTOs from the Retrofit API (`ProductsApi`), performs an atomic transaction to update Room, and automatically emits updated data to the UI via Kotlin `Flow`.
- **Error Resilience**: Network failures never erase previously cached data. If the API call fails, the UI gracefully continues displaying cached local data while presenting a non-blocking error/refresh message.

---

## 2. Architectural Layers

- **UI Layer (Jetpack Compose & ViewModels)**:
  - Observes immutable UI states (`ProductsUiState`) exposed as `StateFlow` collected via `collectAsStateWithLifecycle()`.
  - Distinguishes initial loading, cached content, refreshing, empty states, and errors.
- **Domain Layer (Use Cases & Repositories)**:
  - Contains business logic and repository interfaces (`ProductRepository`, `CartRepository`, `SessionRepository`).
  - Use cases (`GetProductsUseCase`, `RefreshProductsUseCase`, `GetProductsByCategoryUseCase`) encapsulate data access interactions.
- **Data Layer (Repositories, Room DAO, Retrofit API)**:
  - `ProductRepositoryImpl` coordinates between `ProductsApi` (remote) and `ProductDao` (local).
  - Room entities (`ProductEntity`) map cleanly to domain models (`Product`).

---

## 3. Local Database Schema
- **Database**: `FakeStoreDatabase` (Room)
- **Entity**: `products` table (`ProductEntity`)
  - Primary Key: `id` (Int)
  - Columns: `title`, `price`, `description`, `category`, `image`, `ratingRate`, `ratingCount`.

---

## 4. Repository Synchronization & Error Handling
- **Single Source of Truth**: `ProductDao.getAllProducts()` and `ProductDao.getProductsByCategory()` return `Flow<List<ProductEntity>>`.
- **Atomic Refresh**: `database.withTransaction { clearAllProducts(); insertProducts(entities) }` ensures consistency during sync.
- **Graceful Degradation**: If network requests fail during refresh, cached items remain intact.

---

## 5. Testing Strategy & Execution
- Verified with successful Gradle compilation (`clean assembleDebug`).
- Room in-memory databases and repository test doubles validate local persistence and sync policies.
