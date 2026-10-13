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
    private final ScheduledExecutorService fineProcessingScheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        BookDAO.initializeDatabase();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        overdueCol.setCellValueFactory(new PropertyValueFactory<>("daysOverdue"));
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fine"));

        statusCol.setCellFactory(column -> new TableCell<Book, Boolean>() {
            @Override protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) { setText(null); setStyle(""); }
                else if (available) { setText("Available"); setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;"); }
                else { setText("Borrowed"); setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;"); }
            }
        });

        fineCol.setCellFactory(column -> new TableCell<Book, Double>() {
            @Override protected void updateItem(Double fine, boolean empty) {
                super.updateItem(fine, empty);
                if (empty || fine == null) { setText(null); setStyle(""); }
                else if (fine <= 0) { setText("-"); setStyle(""); }
                else { setText(String.format("₹%.2f", fine)); setStyle("-fx-text-fill: #991b1b; -fx-font-weight: bold;"); }
            }
        });

        loadBooksFromDatabase();

        // Client-side predicate filtering
        FilteredList<Book> filteredList = new FilteredList<>(masterBookList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(book -> {
                if (newValue == null || newValue.isBlank()) return true;
                String query = newValue.toLowerCase().trim();
                return book.getTitle().toLowerCase().contains(query) ||
                        book.getAuthor().toLowerCase().contains(query);
            });
        });

        SortedList<Book> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(bookTable.comparatorProperty());
        bookTable.setItems(sortedList);

        // Background worker for simulated daily fine compounding
        fineProcessingScheduler.scheduleAtFixedRate(() -> {
            BookDAO.processOverdueFines();
            Platform.runLater(this::loadBooksFromDatabase);
        }, 10, 10, TimeUnit.SECONDS);
    }

    @FXML
    protected void handleAddBook() {
        String title = titleInput.getText().trim();
        String author = authorInput.getText().trim();

        if (!title.isEmpty() && !author.isEmpty()) {
            BookDAO.insertBook(title, author);
            titleInput.clear();
            authorInput.clear();
            loadBooksFromDatabase();
        }
    }

    @FXML
    protected void handleCheckout() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.isAvailable()) {
            BookDAO.updateBookAvailability(selected.getId(), false);
            loadBooksFromDatabase();
        }
    }

    @FXML
    protected void handleReturn() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isAvailable()) {
            BookDAO.updateBookAvailability(selected.getId(), true);
            loadBooksFromDatabase();
        }
    }

    private void loadBooksFromDatabase() {
        if (masterBookList == null) {
            masterBookList = BookDAO.getAllBooks();
        } else {
            masterBookList.setAll(BookDAO.getAllBooks());
        }
    }

    public void shutdownScheduler() {
        if (!fineProcessingScheduler.isShutdown()) {
            fineProcessingScheduler.shutdown();
        }
    }
}