package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class BookDAO {
    private static final String URL = "jdbc:sqlite:library.db";

    public static void setupFreshDatabase() {
        // Drops the old table and builds the new Resume-Ready schema
        String drop = "DROP TABLE IF EXISTS books;";
        String create = "CREATE TABLE books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "is_available BOOLEAN DEFAULT 1, "
                + "days_overdue INTEGER DEFAULT 0, "
                + "fine REAL DEFAULT 0.0"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement()) {
            stmt.execute(drop);
            stmt.execute(create);
            // Seed it with some cool starting data
            insertBook("The Pragmatic Programmer", "David Thomas");
            insertBook("Clean Architecture", "Robert C. Martin");
            insertBook("Designing Data-Intensive Apps", "Martin Kleppmann");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void insertBook(String title, String author) {
        String sql = "INSERT INTO books (title, author, is_available, days_overdue, fine) VALUES (?, ?, 1, 0, 0.0)";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // THE ASYNC DAEMON ENGINE: Adds 1 day and ₹15.00 to any checked-out book
    public static void executeMidnightAudit() {
        String sql = "UPDATE books SET days_overdue = days_overdue + 1, fine = fine + 15.0 WHERE is_available = 0";
        try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Check out / Return toggle
    public static void updateBookStatus(int bookId, boolean makeAvailable) {
        String sql = "UPDATE books SET is_available = ?, days_overdue = 0, fine = 0.0 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, makeAvailable);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                        rs.getBoolean("is_available"), rs.getInt("days_overdue"), rs.getDouble("fine")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }
}