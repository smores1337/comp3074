# COMP3074 – Room Lab: Reflection Questions

**1. Is Room a replacement for SQLite, or an abstraction over SQLite?**
Room is an abstraction over SQLite. The data is still stored in a SQLite database file on the device (`customer_database`). Room generates the boilerplate code that talks to SQLite and checks SQL queries at compile time.

**2. What is the role of @Entity?**
`@Entity` marks a Kotlin class as a database table. Each property becomes a column and each object is one row. `Customer` maps to the `customers` table, and `@PrimaryKey(autoGenerate = true)` makes `id` an auto-incrementing primary key. This replaces writing `CREATE TABLE` by hand.

**3. What is the role of a DAO?**
The DAO (Data Access Object) defines every database operation the rest of the app is allowed to perform: insert, update, delete and the SELECT queries. Room generates the implementation, so the app calls Kotlin functions instead of building SQL strings, `ContentValues` and `Cursor`s.

**4. Why are insert and delete declared as suspend functions?**
Database I/O can be slow, and running it on the main thread would freeze the UI. Room does not allow it on the main thread at all. A `suspend` function can be called from a coroutine (`viewModelScope.launch`), and Room runs the work on a background thread without blocking the UI.

**5. Why can a SELECT query return Flow<List<Customer>>?**
Room supports observable queries. When a DAO query returns `Flow`, Room runs the query, emits the result, and then watches the tables the query reads. Whenever an insert, update or delete changes the `customers` table, Room re-runs the query and emits the new list.

**6. Why is Cursor no longer visible in the application code?**
Room's generated code still uses a `Cursor` internally, but it reads each column and builds the `Customer` objects automatically. The app only ever sees `List<Customer>`.

**7. Why does the list update without a View All button?**
The UI collects the `Flow` from Room with `collectAsStateWithLifecycle()`. Every new list Room emits after a change updates Compose state, which triggers recomposition. The UI observes the data instead of asking for a refresh.

**8. What responsibility does CustomerViewModel have?**
It sits between the UI and the DAO. It exposes the customer list (including search results) as a `Flow`, launches inserts, updates and deletes in `viewModelScope`, and survives configuration changes such as rotation. That keeps database logic out of the composables, which only display state and send events.

**9. What advantages does Room provide over SQLiteOpenHelper?**
- Less boilerplate: no `ContentValues`, `Cursor` parsing or `CREATE TABLE` strings.
- SQL is checked at compile time, so typos in table or column names fail the build instead of crashing at runtime.
- Rows map automatically to Kotlin objects, including `Boolean` columns.
- Built-in support for coroutines (`suspend`) and observable queries (`Flow`).
- Main-thread database access is blocked, which prevents UI freezes.
- Clear separation of concerns between Entity, DAO, Database and ViewModel.

**10. What did the direct SQLite version teach that is hidden by Room?**
It showed how SQLite actually works: writing `CREATE TABLE` and migration SQL by hand, opening readable and writable databases, building `ContentValues`, moving through a `Cursor` row by row, reading columns by index, converting Booleans to 1/0, and refreshing the UI manually. Room still does all of these things, but hides them behind generated code.
