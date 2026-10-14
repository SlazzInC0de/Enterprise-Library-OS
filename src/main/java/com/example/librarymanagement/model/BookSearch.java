package com.example.librarymanagement.model;

import java.util.Locale;

/** Pure search logic, kept out of the controller so it can be unit-tested without JavaFX. */
public final class BookSearch {

    private BookSearch() {
    }

    /** Case-insensitive match on title or author; a blank query matches everything. */
    public static boolean matches(Book book, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        return contains(book.getTitle(), q) || contains(book.getAuthor(), q);
    }

    private static boolean contains(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }
}
