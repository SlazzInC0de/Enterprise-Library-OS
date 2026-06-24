package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class BookDAO {
    private static final String URL = "jdbc:sqlite:library.db";

    public static void initializeDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "is_available BOOLEAN DEFAULT 1, "
                + "days_overdue INTEGER DEFAULT 0, "
                + "fine REAL DEFAULT 0.0"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            seedInitialDataIfEmpty(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Critical database initialization failure", e);
        }
    }

    private static void seedInitialDataIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) AS count FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("count") == 0) {
                insertBook("The Pragmatic Programmer", "David Thomas");
                insertBook("Clean Architecture", "Robert C. Martin");
                insertBook("Designing Data-Intensive Applications", "Martin Kleppmann");
            }
        }
    }

    public static void insertBook(String title, String author) {
        String sql = "INSERT INTO books (title, author, is_available, days_overdue, fine) VALUES (?, ?, 1, 0, 0.0)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void processOverdueFines() {
        String sql = "UPDATE books SET days_overdue = days_overdue + 1, fine = fine + 15.0 WHERE is_available = 0";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBookAvailability(int bookId, boolean isAvailable) {
        String sql = "UPDATE books SET is_available = ?, days_overdue = 0, fine = 0.0 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("is_available"),
                        rs.getInt("days_overdue"),
                        rs.getDouble("fine")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}