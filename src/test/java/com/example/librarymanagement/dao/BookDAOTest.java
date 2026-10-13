package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDAOTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void useFreshDatabase() {
        // Each test gets its own throwaway SQLite file; the real library.db is never touched.
        System.setProperty("library.db.url", "jdbc:sqlite:" + tempDir.resolve("test.db"));
        BookDAO.initializeDatabase();
    }

    @AfterEach
    void resetDatabaseUrl() {
        System.clearProperty("library.db.url");
    }

    private Book findByTitle(String title) {
        return BookDAO.getAllBooks().stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Book not found: " + title));
    }

    @Test
    void seedsStarterCatalogOnFirstBoot() {
        assertEquals(3, BookDAO.getAllBooks().size());
    }

    @Test
    void seedRunsOnlyOnce() {
        BookDAO.initializeDatabase();
        assertEquals(3, BookDAO.getAllBooks().size());
    }

    @Test
    void insertedBookIsRetrievableAndAvailable() {
        BookDAO.insertBook("Unit Test Title", "Test Author");

        Book book = findByTitle("Unit Test Title");
        assertEquals("Test Author", book.getAuthor());
        assertTrue(book.isAvailable());
        assertEquals(0.0, book.getFine());
    }

    @Test
    void checkoutMarksBookBorrowed() {
        BookDAO.insertBook("Checkout Target", "Target Author");
        int id = findByTitle("Checkout Target").getId();

        BookDAO.updateBookAvailability(id, false);

        assertFalse(findByTitle("Checkout Target").isAvailable());
    }

    @Test
    void overdueSweepChargesOnlyBorrowedBooks() {
        BookDAO.insertBook("Borrowed", "A");
        BookDAO.insertBook("On Shelf", "B");
        BookDAO.updateBookAvailability(findByTitle("Borrowed").getId(), false);

        BookDAO.processOverdueFines();
        BookDAO.processOverdueFines();

        Book borrowed = findByTitle("Borrowed");
        assertEquals(2, borrowed.getDaysOverdue());
        assertEquals(30.0, borrowed.getFine(), 1e-9);

        Book onShelf = findByTitle("On Shelf");
        assertEquals(0, onShelf.getDaysOverdue());
        assertEquals(0.0, onShelf.getFine(), 1e-9);
    }

    @Test
    void returnClearsOverdueAndFine() {
        BookDAO.insertBook("Late Return", "C");
        int id = findByTitle("Late Return").getId();
        BookDAO.updateBookAvailability(id, false);
        BookDAO.processOverdueFines();

        BookDAO.updateBookAvailability(id, true);

        Book returned = findByTitle("Late Return");
        assertTrue(returned.isAvailable());
        assertEquals(0, returned.getDaysOverdue());
        assertEquals(0.0, returned.getFine(), 1e-9);
    }

    @Test
    void allBooksHaveUniqueIds() {
        List<Book> books = BookDAO.getAllBooks();
        assertEquals(books.size(), books.stream().map(Book::getId).distinct().count());
    }
}
