# Enterprise Library OS (JavaFX / SQLite)

An asynchronous, concurrency-safe Library Management Operating System built with modern Java 17. Designed to demonstrate strict separation of concerns, multi-threaded background task processing, and zero-latency UI data filtering.

## ⚡ System Architecture Highlights

* **The Asynchronous Audit Daemon:** Utilizes a detached `ScheduledExecutorService` running on a secondary worker thread to conduct periodic background sweeps of the ledger, automatically compounding patron late penalties without locking the JavaFX Application Thread.
* **Zero-Latency Predicate Engine:** Implements an in-memory `FilteredList` bound to a JavaFX `TableView`, allowing real-time multi-parameter search indexing (Author/Title) at `0.00ms` cost to the SQL database.
* **Strict MVC & DAO Design:** Complete decoupling of the persistence layer (`BookDAO`) from the visual markup (`.fxml`) and the event controller.
* **Module-Path Bypass Launcher:** Employs a custom pre-main bootstrapper (`Launcher.java`) to dynamically initialize the JavaFX Toolkit at runtime, circumventing the rigid encapsulation of the post-Java 11 module system.

## 🛠️ Technical Stack

* **Language:** Java 17 (LTS)
* **GUI Framework:** JavaFX 21 (FXML + Scene Builder)
* **Embedded Database:** SQLite (xerial JDBC driver)
* **Build System:** Apache Maven

## 🚀 Quick Start (Local Deployment)

1. Clone the repository:
   ```bash
   git clone [https://github.com/SlazzInC0de/Library-Management-System.git](https://github.com/SlazzInC0de/Library-Management-System.git)