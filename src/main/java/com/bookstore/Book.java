package com.bookstore;

public record Book(String id, String title, double basePrice) {
    public Book {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
        if (basePrice < 0) throw new IllegalArgumentException("Price cannot be negative");
    }
}