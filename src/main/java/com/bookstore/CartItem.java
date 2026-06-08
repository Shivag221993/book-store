package com.bookstore;

public record CartItem(Book book, int quantity) {
    public CartItem {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
    }
}