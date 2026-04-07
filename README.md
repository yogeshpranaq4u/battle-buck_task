# Battlebuck Real-Time Leaderboard Assignment

This submission implements a live leaderboard system for a mobile gaming platform using Kotlin, Coroutines, Flow, Compose, and a real multi-module structure.

## How to Run

1. Open the project in Android Studio Hedgehog or newer.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or device.
4. The leaderboard screen starts streaming score updates automatically.

## Project Structure

### `:score-engine`

Pure Kotlin module that simulates a match engine / backend feed.

Responsibilities:
- Owns the list of players for a session
- Generates random score updates at random intervals
- Guarantees scores only move forward
- Produces deterministic output for a given seed
- Has no Android or UI dependency

Key types:
- `Player`
- `ScoreUpdate`
- `ScoreEngineConfig`
- `DeterministicScoreGenerator`

### `:leaderboard-core`

Pure Kotlin consumer module that transforms score events into ranked leaderboard state.

Responsibilities:
- Consumes score updates from `:score-engine`
- Stores latest score per player
- Applies ranking rules
- Exposes leaderboard as a reactive `Flow`
- Ignores stale or regressive score events defensively

Key types:
- `LeaderboardEntry`
- `LeaderboardSnapshot`
- `LeaderboardRankCalculator`
- `RealTimeLeaderboard`

### `:app`

Android presentation module.

Responsibilities:
- Shares the live stream in a process-level repository
- Maps domain snapshots into UI-specific row models
- Renders a smooth real-time leaderboard in Compose
- Applies a lightweight highlight animation only for the updated row

Runtime configuration in `:app`:
- `LeaderboardConfigProvider` loads players from `app/src/main/res/raw/players.json`
- Score engine knobs (`seed`, delays, increments) are provided via `BuildConfig` fields
- `SessionProvider` supplies current user id for UI personalization

## Architecture Overview

The app uses a hybrid of modular Clean Architecture and MVVM:

- Engine layer: `:score-engine`
- Domain layer: `:leaderboard-core`
- Presentation layer: `:app`

Why this split:
- The score generator and leaderboard processor solve different problems and should be reusable independently.
- Ranking logic belongs in domain, not in UI or ViewModel, because it is a business rule.
- Pure Kotlin modules make unit testing fast and keep Android out of the core logic.

Data flow:

1. `DeterministicScoreGenerator` emits `ScoreUpdate`
2. `RealTimeLeaderboard` folds events into ranked `LeaderboardSnapshot`
3. `LeaderboardRepository` shares the stream as `StateFlow`
4. `LeaderboardViewModel` derives presentation-friendly `LeaderboardUiState`
5. Compose renders stable keyed rows

## Ranking Rules

Implemented in `LeaderboardRankCalculator`:

- Sort by score descending
- Same score gets the same rank
- The next rank skips accordingly
- Ties are ordered by `displayName`, then `id`, to keep the UI stable and avoid flicker

Example:
- `300, 300, 250, 100` becomes ranks `1, 1, 3, 4`

## UI Choice

I used Compose instead of XML because this screen is a continuously updating list with reactive state and animation. Compose makes it easier to:

- Bind directly to `StateFlow`
- Keep state localized per row
- Animate score updates without imperative adapter code
- Avoid RecyclerView boilerplate for a single-screen assignment

The UI effect chosen is update highlighting:
- When a player receives a new score, that row briefly changes container color
- The rest of the list remains stable through item keys and immutable row models

## Performance and Lifecycle Thinking

### How UI thread blocking is avoided

- Score generation and leaderboard computation run off the main thread
- `RealTimeLeaderboard` uses `Dispatchers.Default` for ranking work
- Compose only consumes already-prepared UI state

### How unnecessary recompositions are reduced

- Rows use stable keys (`playerId`)
- Ranking logic is outside UI
- The UI consumes immutable row models
- Only the updated row receives a flash token, so the visual effect is targeted
- Tie ordering is deterministic, which prevents reorder flicker for equal scores

### How memory leaks are avoided

- No long-lived UI coroutines outside lifecycle-aware APIs
- ViewModel owns presentation state
- Shared stream lives in a repository scope with `SupervisorJob`
- Compose collects using `collectAsStateWithLifecycle`

### Behavior on rotation

- Rotation recreates the Activity but not the process
- The repository holds the shared leaderboard stream
- The ViewModel remaps current domain state to UI state without restarting the architecture

### Behavior in background

- The shared stream uses `SharingStarted.WhileSubscribed(5000)`
- If nobody is collecting for 5 seconds, upstream work pauses
- This is a deliberate balance between conserving work and avoiding jitter during short lifecycle transitions

## Scaling Discussion

### 1K users

This design is still acceptable for 1K users with some tuning:
- Keep the engine feed shared, not per-screen
- Update only visible rows in UI
- Consider emitting item-level diffs rather than full snapshots if update frequency increases

### 100K users

I would change the architecture rather than just optimize this version:
- Move leaderboard computation to backend or dedicated service
- Push only top-N windows or personalized slices to the client
- Replace full re-sort per event with indexed structures or heap/tree-based ranking
- Introduce pagination and snapshot versioning
- Add network backpressure, compression, and incremental diffs

## Trade-offs I Made

- I chose a strong multi-module structure over adding DI framework complexity.
- I kept the engine local and deterministic instead of simulating networking; that keeps the assignment focused on architecture.
- I recompute the ranked list on each accepted event because it is simple and correct for this assignment size. For very large leaderboards, that strategy should change.
- I used a repository singleton for process-level sharing. In production I would back this with DI and explicit app-scoped lifecycle ownership.

## Tests Included

### `:score-engine`

- Verifies deterministic output for the same seed
- Verifies scores only increase per player

### `:leaderboard-core`

- Verifies competition ranking with ties and skipped ranks
- Verifies stable tie ordering
- Verifies stale score events are ignored

Run tests with:

```bash
./gradlew test
```

## Leadership and Ownership Note

### Why split modules this way?

- `:score-engine` models event production
- `:leaderboard-core` models event consumption and ranking
- `:app` models presentation and lifecycle

This keeps boundaries aligned to responsibility, not to Android classes.

### Where does ranking logic live and why?

In `:leaderboard-core`, specifically `LeaderboardRankCalculator`.

Reason:
- Ranking is a business invariant
- It must be testable without UI
- It should remain reusable if we later add another client or server validation layer

### What is non-negotiable if this ships in 7 days?

- Correct ranking logic
- Stable deterministic event pipeline
- Lifecycle-safe shared stream
- Unit tests around core rules
- Basic UX feedback for live updates

### What I would cut or defer

- Fancy animations
- Persistence
- Full DI framework integration
- Instrumentation tests
- Anti-cheat enforcement implementation beyond design notes

### Team split

- Junior developer: Compose row components, preview states, README run instructions
- Mid-level developer: repository wiring, ViewModel mapping, additional tests
- Me as lead: module boundaries, ranking design, performance decisions, code review, test strategy, final integration

## Code Review Simulation

### Must Fix

1. Ranking logic lives in the ViewModel instead of a pure domain component.  
   Reason: business rules become harder to test, reuse, and protect from UI regressions.

2. Tie ordering is unspecified.  
   Reason: equal-score players can jump positions between emissions, causing visible flicker and confusing rank changes.

3. The score stream is cold and instantiated per collector.  
   Reason: rotation or multiple collectors can accidentally create multiple game sessions and inconsistent scores.

### Improvement

4. Flow collection is not lifecycle-aware in Compose.  
   Reason: collecting beyond `STARTED` wastes work and can keep upstream active when the screen is backgrounded.

5. The leaderboard accepts any incoming score without validating monotonicity.  
   Reason: stale or malformed updates can corrupt ranks or open the door to exploit paths.

6. Domain models leak presentation concerns such as highlight flags or display strings.  
   Reason: this makes the core module harder to reuse for server or analytics consumers.

### Tech Debt

7. There is no quality gate for formatting, lint, or static analysis.  
   Reason: fast-moving live systems benefit from automated discipline; I would add `ktlint`, `detekt`, and unit tests in CI.

8. Re-sorting the full leaderboard for every event will not hold at production scale.  
   Reason: it is fine for the assignment, but a large tournament system needs indexed ranking and server-driven windows.

## Production Readiness Improvements

- Add CI with `./gradlew test`, `ktlint`, and `detekt`
- Add anti-cheat guards such as impossible-score-jump detection and signed server events
- Add telemetry around update latency, dropped frames, and collection counts

## CI Quality Gate

This repo now includes a GitHub Actions workflow at `.github/workflows/android-ci.yml` that runs on push/PR:

- `:app:compileDebugKotlin`
- `:app:lintDebug`
- `:leaderboard-core:test`
- `:score-engine:test`
- Add process-death recovery if live session continuity matters
