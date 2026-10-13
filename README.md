<div align="center">

# 🏛️ Enterprise Library OS

### Desktop library management system in JavaFX + SQLite

Catalog books, check them in and out, and let a background daemon accrue overdue fines without ever blocking the UI.

[![Build](https://github.com/SlazzInC0de/Enterprise-Library-OS/actions/workflows/build.yml/badge.svg)](https://github.com/SlazzInC0de/Enterprise-Library-OS/actions/workflows/build.yml)
![Java](https://img.shields.io/badge/Java_17-0f172a?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX_21-0f172a?logo=java&logoColor=ED8B00)
![SQLite](https://img.shields.io/badge/SQLite-0f172a?logo=sqlite&logoColor=4FA3D1)
![JUnit](https://img.shields.io/badge/JUnit_5-0f172a?logo=junit5&logoColor=25A162)
![Maven](https://img.shields.io/badge/Maven-0f172a?logo=apachemaven&logoColor=C71A36)
![License](https://img.shields.io/badge/License-MIT-22d3ee)

<img src="docs/app.png" alt="Enterprise Library OS main window" width="85%"/>

</div>

---

## ✨ Features

- 📚 **Book intake**: add titles and authors straight into the catalog
- 🔁 **Checkout and return** with live status (Available / Borrowed)
- ⏱️ **Fine daemon**: a `ScheduledExecutorService` sweeps the database every 10 seconds (one simulated day) and adds ₹15 to each borrowed book
- 🔍 **Instant search**: a `FilteredList` predicate filters by title or author on every keystroke, with no database round trip
- ↕️ **Sortable columns** via `SortedList` bound to the table's comparator
- 💾 **Zero-config persistence**: SQLite file created and seeded on first launch

## 🏛️ Architecture

```mermaid
flowchart LR
    subgraph FX [JavaFX Application Thread]
        V[library-view.fxml] --> C[LibraryController]
        C --> FL[FilteredList -> SortedList -> TableView]
    end
    C -- add / checkout / return --> D[BookDAO<br/>JDBC + PreparedStatement]
    S([ScheduledExecutorService<br/>every 10s]) -- processOverdueFines --> D
    S -- Platform.runLater refresh --> C
    D --> DB[(SQLite<br/>library.db)]
```

- **Thread safety**: the daemon writes on its own thread and hands UI refreshes back to the FX thread with `Platform.runLater`.
- **Clean shutdown**: closing the window stops the scheduler, so the JVM exits cleanly.
- **Injection-safe**: every write uses `PreparedStatement`.

## 🧪 Tests

JUnit 5 covers the DAO against a throwaway SQLite file per test (the real `library.db` is never touched):

- seeding runs exactly once
- insert, checkout and return flows
- the overdue sweep charges only borrowed books and resets on return

```bash
./mvnw test
```

CI runs `mvn verify` on every push.

## 🚀 Run locally

**Prerequisites:** JDK 17+. Maven is bundled via the wrapper.

```bash
git clone https://github.com/SlazzInC0de/Enterprise-Library-OS.git
cd Enterprise-Library-OS
./mvnw javafx:run          # Windows: mvnw.cmd javafx:run
```

The database file `library.db` is created in the working directory. Point it elsewhere with:

```bash
./mvnw javafx:run -Dlibrary.db.url=jdbc:sqlite:/path/to/library.db
```

## 📁 Project structure

```text
src/
├── main/java/com/example/librarymanagement/
│   ├── LibraryApplication.java   # JavaFX entry point
│   ├── Launcher.java             # plain main() for fat-JAR launches
│   ├── LibraryController.java    # UI logic, filtering, fine daemon
│   ├── dao/BookDAO.java          # SQLite access
│   └── model/Book.java
├── main/resources/.../library-view.fxml
└── test/java/.../dao/BookDAOTest.java
```

## 🗺️ Roadmap

- [ ] Patron records and borrowing history
- [ ] Due dates in real time instead of a simulated day
- [ ] Export the ledger to CSV
- [ ] Packaged installers with `jpackage`

## 📄 License

MIT © [Suryansh Verma](https://github.com/SlazzInC0de)
