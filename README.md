# Local Library Management System (JavaFX)

A lightweight desktop application built to manage library inventory, handle patron checkouts, and compute compounding overdue penalties. Built with pure Java 17 and decoupled FXML markup.

## Features

* **In-Memory Predicate Filtering:** Uses JavaFX `FilteredList` to allow instant, zero-re-query searching across book titles and authors.
* **Background Task Scheduler:** A detached `ScheduledExecutorService` runs periodic sweeps of the SQLite DB to process overdue fines asynchronously without freezing the UI thread.
* **Embedded SQLite Persistence:** Zero-configuration database; automatically generates local schema on initial boot.
* **Thread-Safe Controller:** Safely marshals background database updates back onto the JavaFX Application Thread via `Platform.runLater()`.

## Tech Stack

* **Runtime:** Java 17 LTS
* **UI:** JavaFX 21 (Scene Builder FXML)
* **Storage:** SQLite JDBC (`org.xerial:sqlite-jdbc`)
* **Testing:** JUnit 5 Jupiter

## Running Locally

1. Clone repo:
   ```bash
   git clone [https://github.com/SlazzInC0de/Library-Management-System.git](https://github.com/SlazzInC0de/Library-Management-System.git)