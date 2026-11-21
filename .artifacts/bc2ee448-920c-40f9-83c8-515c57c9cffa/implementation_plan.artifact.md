# Independent Search Screen Implementation Plan

Implement a dedicated, production-quality Search Screen with MVI architecture, Room-backed search history persistence, reactive query pipeline (`debounce`, `distinctUntilChanged`, `flatMapLatest`), and clean navigation integration.

## User Review Required

> [!IMPORTANT]
> This plan introduces a dedicated `search` navigation route accessible by tapping the search bar in the Home screen, providing an immersive search experience with recent searches, reactive product results, and clean architecture separation.

## Proposed Changes

### :domain (Domain Layer)
#### [NEW] [SearchHistory.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/domain/src/main/java/com/example/fakestore/domain/model/SearchHistory.kt)
- Domain model representing search history entries.

#### [NEW] [SearchHistoryRepository.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/domain/src/main/java/com/example/fakestore/domain/repository/SearchHistoryRepository.kt)
- Repository contract for search history operations (observe, save, delete, clear).

#### [NEW] [SearchProductsUseCase.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/domain/src/main/java/com/example/fakestore/domain/usecase/SearchProductsUseCase.kt)
- Encapsulates product search business logic.

#### [NEW] [SearchHistoryUseCases.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/domain/src/main/java/com/example/fakestore/domain/usecase/SearchHistoryUseCases.kt)
- Encapsulates Get, Save, Delete, and Clear search history use cases.

---

### :data (Data Layer)
#### [MODIFY] [FakeStoreDatabase.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/data/src/main/java/com/example/fakestore/data/local/room/FakeStoreDatabase.kt)
- Ensure `SearchHistoryEntity` is registered and migrations are in place.

#### [NEW] [SearchHistoryRepositoryImpl.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/data/src/main/java/com/example/fakestore/data/repository/SearchHistoryRepositoryImpl.kt)
- Room-backed implementation of `SearchHistoryRepository`.

#### [NEW] [SearchRepositoryImpl.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/data/src/main/java/com/example/fakestore/data/repository/SearchRepositoryImpl.kt)
- Implementation of product search connecting to `ProductsApi`.

---

### :feature:products (Presentation Layer)
#### [NEW] [SearchScreen.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/feature/products/src/main/java/com/example/fakestore/feature/products/search/SearchScreen.kt)
- Dedicated Compose Search Screen with search bar, recent searches chips/list, and product results grid/list.

#### [NEW] [SearchViewModel.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/feature/products/src/main/java/com/example/fakestore/feature/products/search/SearchViewModel.kt)
- MVI / MVVM ViewModel handling `SearchIntent`, reactive flow pipeline (`debounce`, `distinctUntilChanged`, `flatMapLatest`), and `SearchUiState`.

#### [MODIFY] [AppNavigation.kt](file:///C:/Users/Adrian-PC/AndroidStudioProjects/FakeStore/app/src/main/java/com/example/fakestore/navigation/AppNavigation.kt)
- Register `"search"` route and wire up navigation from Home search bar to Search Screen and Product Details.

## Verification Plan

### Automated Tests
- Run unit tests for `SearchViewModel` verifying debounce behavior, `flatMapLatest` cancellation, and state transitions.
- Run unit tests for `SearchHistoryRepository` verifying duplicate query handling and 10-item limit.
- Run `./gradlew clean assembleDebug` to verify successful compilation across all modules.

### Manual Verification
- Tap search bar on Home screen $\rightarrow$ navigates to Search Screen with keyboard focused.
- Type query $\rightarrow$ observe debounced loading and search results.
- Select search result $\rightarrow$ navigates to Product Details.
- Press back $\rightarrow$ returns to Search Screen preserving query/state.
- Clear history / delete recent search item.
