package com.example.librarymanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookSearchTest {

    private final Book book = new Book(1, "Designing Data-Intensive Applications", "Martin Kleppmann", true, 0, 0.0);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankQueryMatchesEverything(String query) {
        assertTrue(BookSearch.matches(book, query));
    }

    @ParameterizedTest
    @ValueSource(strings = {"data", "DATA-INTENSIVE", "  kleppmann  ", "martin"})
    void matchesTitleOrAuthorIgnoringCaseAndSpaces(String query) {
        assertTrue(BookSearch.matches(book, query));
    }

    @Test
    void doesNotMatchUnrelatedText() {
        assertFalse(BookSearch.matches(book, "pragmatic"));
    }

    @Test
    void toleratesMissingFields() {
        Book partial = new Book(2, null, "Anonymous", true, 0, 0.0);
        assertTrue(BookSearch.matches(partial, "anon"));
        assertFalse(BookSearch.matches(partial, "title"));
    }
}
