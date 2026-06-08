package com.bookstore;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BookPriceCalculator calculator = new BookPriceCalculator();

        // Concrete domain representation of books instead of abstract IDs
        Book book1 = new Book("B1", "Clean Code", 50.0);
        Book book2 = new Book("B2", "Clean Coder", 50.0);
        Book book3 = new Book("B3", "Clean Architecture", 50.0);
        Book book4 = new Book("B4", "Test Driven Development", 50.0);
        Book book5 = new Book("B5", "Refactoring", 50.0);

        // Real-world cart payload matching standard API payloads
        List<CartItem> modernBasket = List.of(
                new CartItem(book1, 2),
                new CartItem(book2, 2),
                new CartItem(book3, 2),
                new CartItem(book4, 1),
                new CartItem(book5, 1)
        );

        double finalPrice = calculator.calculateCartPrice(modernBasket);

        System.out.println("=====================================");
        System.out.println("SUCCESS! The optimized cart price is: " + finalPrice + " EUR");
        System.out.println("=====================================");
    }
}