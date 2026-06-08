package com.bookstore.datasource;

import com.bookstore.domain.Book;
import java.util.Map;
import java.util.Optional;

public class BookRepository {

    // Simulating a database catalog
    private static final Map<String, Book> CATALOG = Map.of(
            "1", new Book("1", "Clean Code", 50.0),
            "2", new Book("2", "Clean Coder", 50.0),
            "3", new Book("3", "Clean Architecture", 50.0),
            "4", new Book("4", "Test Driven Development", 50.0),
            "5", new Book("5", "Refactoring", 50.0)
    );

    public Optional<Book> findById(String id) {
        return Optional.ofNullable(CATALOG.get(id));
    }

    public boolean exists(String id) {
        return CATALOG.containsKey(id);
    }
}