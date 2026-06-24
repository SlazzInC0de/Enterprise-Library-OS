package com.example.librarymanagement;

import com.example.librarymanagement.dao.BookDAO;
import com.example.librarymanagement.model.Book;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.concurrent.*;

public class LibraryController {

    @FXML private TextField searchField;
    @FXML private TextField titleInput;
    @FXML private TextField authorInput;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Integer> idCol;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, Boolean> statusCol;
    @FXML private TableColumn<Book, Integer> overdueCol;
    @FXML private TableColumn<Book, Double> fineCol;

    private ObservableList<Book> masterBookList;
    private final ScheduledExecutorService backgroundDaemon = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        // 1. Wipe old SQLite table and build the fresh one
        BookDAO.setupFreshDatabase();

        // 2. Map table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        overdueCol.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));

        // FIXED: Explicitly declared <Book, Boolean> inside the diamond!
        statusCol.setCellFactory(column -> new TableCell<Book, Boolean>() {
            @Override protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) { setText(null); setStyle(""); }
                else if (available) { setText("Available"); setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); }
                else { setText("Checked Out"); setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); }
            }
        });

        // FIXED: Explicitly declared <Book, Double> inside the diamond!
        fineCol.setCellFactory(column -> new TableCell<Book, Double>() {
            @Override protected void updateItem(Double fine, boolean empty) {
                super.updateItem(fine, empty);
                if (empty || fine == null || fine == 0) { setText("-"); }
                else { setText(String.format("₹%.2f", fine)); setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: bold;"); }
            }
        });

        refreshDataModel();

        // Real-Time Search Filter
        FilteredList<Book> filteredData = new FilteredList<>(masterBookList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookTable.comparatorProperty());
        bookTable.setItems(sortedData);

        // Background Daemon
        backgroundDaemon.scheduleAtFixedRate(() -> {
            BookDAO.executeMidnightAudit();
            Platform.runLater(this::refreshDataModel);
        }, 10, 10, TimeUnit.SECONDS);
    }

    @FXML protected void handleAddBook() {
        if (!titleInput.getText().isEmpty() && !authorInput.getText().isEmpty()) {
            BookDAO.insertBook(titleInput.getText().trim(), authorInput.getText().trim());
            titleInput.clear(); authorInput.clear();
            refreshDataModel();
        }
    }

    @FXML protected void handleCheckout() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.isAvailable()) {
            BookDAO.updateBookStatus(selected.getId(), false);
            refreshDataModel();
        }
    }

    @FXML protected void handleReturn() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isAvailable()) {
            BookDAO.updateBookStatus(selected.getId(), true);
            refreshDataModel();
        }
    }

    private void refreshDataModel() {
        if (masterBookList == null) { masterBookList = BookDAO.getAllBooks(); }
        else { masterBookList.setAll(BookDAO.getAllBooks()); }
    }

    // Safely kills the background thread when the user hits the red 'X' on the window
    public void shutdown() { backgroundDaemon.shutdown(); }
}