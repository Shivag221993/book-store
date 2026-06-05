package com.bookstore;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Create the calculator object
        BookPriceCalculator calculator = new BookPriceCalculator();

        // 2. Define the sample basket from the Kata (2 Clean Code, 2 Clean Coder, etc.)
        List<Integer> basket = List.of(1, 1, 2, 2, 3, 3, 4, 5);

        // 3. Calculate the price
        double finalPrice = calculator.calculatePrice(basket);

        // 4. Print the output to the console
        System.out.println("=====================================");
        System.out.println("SUCCESS! The total basket price is: " + finalPrice + " EUR");
        System.out.println("=====================================");
    }
}