# Posts App - Android Data Flow Learning Project

A simple Android app built with Jetpack Compose that demonstrates modern data flow patterns using Retrofit, Room, Coroutines, and Flow.

## 📱 What This App Does

- Fetches posts from the JSONPlaceholder API
- Stores data locally in a Room database
- Displays posts in a modern Compose UI
- Updates UI automatically when data changes

## 🔄 How Data Flows

The app follows a unidirectional data flow pattern:

1. **App Starts** → `LaunchedEffect` triggers in `PostsScreen`
2. **API Call** → `PostsDataManager.fetchAndSavePosts()` runs on `Dispatchers.IO`
3. **Network Request** → Retrofit calls the API using a `suspend` function
4. **Data Transformation** → API response (`Post`) is converted to database entity (`PostEntity`)
5. **Database Save** → Room stores the data using `PostDao.insertPosts()`
6. **Flow Emission** → Room automatically emits new data through `Flow<List<PostEntity>>`
7. **UI Update** → Compose collects the Flow using `collectAsState()` and recomposes

**Key Point**: The UI never reads directly from the API. Room is the single source of truth.

## 🌊 Why Flow?

Flow is used because:
- It's **reactive** - UI updates automatically when database changes
- It's **lifecycle-aware** - no memory leaks
- It's **non-blocking** - runs on background threads
- It's **declarative** - clean and easy to understand

The DAO returns `Flow<List<PostEntity>>` which means any database changes automatically propagate to the UI without manual refresh.

## ⚙️ Where Coroutines Run

- **Network calls** → `Dispatchers.IO` (background thread optimized for I/O)
- **Database operations** → `Dispatchers.IO` (Room requires background threads)
- **Flow collection** → Happens in the composable's coroutine scope
- **UI updates** → Automatically dispatched to main thread by Compose

We use `suspend` functions for one-shot operations (fetch, save) and `Flow` for continuous observation (database changes).

## 🏗 Project Structure

```
app/src/main/java/com/example/postsapp/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt      # Room database singleton
│   │   ├── PostDao.kt          # Database access with Flow
│   │   └── PostEntity.kt       # Database entity
│   ├── model/
│   │   └── Post.kt             # API response model
│   ├── remote/
│   │   ├── PostsApiService.kt  # Retrofit API interface
│   │   └── RetrofitClient.kt   # Retrofit instance factory
│   └── PostsDataManager.kt     # Coordinates data flow
├── ui/
│   ├── theme/                  # Compose theme files
│   ├── PostsScreen.kt          # Main UI screen
│   └── UiState.kt              # UI state sealed class
├── MainActivity.kt             # Entry point
└── PostsApplication.kt         # App class with dependencies
```

## 🛠 Technologies Used

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Retrofit** - HTTP client for API calls
- **Room** - Local database
- **Coroutines** - Asynchronous programming
- **Flow** - Reactive data streams

## 🚫 What's NOT Used (Intentionally)

- ❌ ViewModel - Keeping it simple for learning
- ❌ Repository pattern - Using DataManager instead
- ❌ Hilt/Dagger - Manual dependency injection
- ❌ Callbacks - Using suspend functions instead
- ❌ GlobalScope - Using proper scoped coroutines

## 🚀 How to Build

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on an emulator or physical device (API 24+)

## 📝 Learning Outcomes

After studying this project, you should understand:
- How to make network calls with Retrofit and coroutines
- How to store data locally with Room
- How to use Flow for reactive data
- How to collect Flow in Compose
- How to structure data flow without ViewModel
- How to use Dispatchers for background work
- How to avoid blocking the main thread

---

**Next Steps**: Once you understand this data flow, you can learn ViewModel and Repository pattern to further separate concerns and handle configuration changes better.
