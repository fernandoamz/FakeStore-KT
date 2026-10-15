# Android Engineering Guidelines

## Technology Stack
- Kotlin
- Jetpack Compose
- Material 3
- Coroutines and Flow
- ViewModel
- Hilt
- Retrofit
- Room (Offline-First Architecture)

## Architecture
- Follow MVVM and Clean Architecture where appropriate.
- Keep UI, domain, and data responsibilities separated.
- Use repositories to abstract data sources.
- Expose UI state using StateFlow.
- Use immutable UI state.
- Room database is the single source of truth; network requests sync local storage via Offline-First pattern.

## Jetpack Compose & Performance
- Always provide unique keys for items in `LazyColumn` and `LazyVerticalGrid` (e.g., `key = Product::id`).
- Use `collectAsStateWithLifecycle()` for collecting flows in Compose to respect lifecycle boundaries.
- Keep UI state models immutable and ensure composable parameters are stable to prevent unnecessary recompositions.
- Use `stateIn` with `SharingStarted.WhileSubscribed(5_000)` for ViewModels exposing Flows.
- Defer state reads as late as possible inside modifiers and lambdas.
- Follow Material 3 design conventions.
- Do not wrap login inputs in Cards.
- Handle loading, error, and success states.
- Support accessibility and different screen sizes.

## Engineering Rules
- Inspect existing code before modifying it.
- Preserve authentication and navigation behavior.
- Avoid unnecessary dependencies.
- Add tests for relevant business logic.
- Run the appropriate Gradle checks after changes.
- Never claim tests passed without executing them.
