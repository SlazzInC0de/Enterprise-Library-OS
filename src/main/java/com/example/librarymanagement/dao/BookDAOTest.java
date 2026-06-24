package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import org.junit.jupiter.api.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookDAOTest {

    private static final String TEST_DB_PATH = "library.db";

    @BeforeEach
    void setUp() {
        // Force a fresh table for each test run
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + TEST_DB_PATH);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS books;");
        } catch (Exception e) {
            fail("Failed to clean test database: " + e.getMessage());
        }
        BookDAO.initializeDatabase();
    }

    @Test
    void testInsertAndRetrieveBook() {
        BookDAO.insertBook("Unit Test Title", "Test Author");
        List<Book> books = BookDAO.getAllBooks();

        boolean found = books.stream()
                .anyMatch(b -> b.getTitle().equals("Unit Test Title") && b.getAuthor().equals("Test Author"));

        assertTrue(found, "Inserted book should be retrievable from the DAO");
    }

    @Test
    void testBookCheckoutChangesAvailability() {
        BookDAO.insertBook("Checkout Target", "Target Author");
        int targetId = BookDAO.getAllBooks().get(0).getId();

        BookDAO.updateBookAvailability(targetId, false);
        Book updatedBook = BookDAO.getAllBooks().get(0);

        assertFalse(updatedBook.isAvailable(), "Book availability should flip to false after checkout");
    }
}
