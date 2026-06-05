package com.bookstore;

import java.util.*;

public class BookPriceCalculator {
    private static final Map<Integer, Double> DISCOUNTS = Map.of(
            0, 0.00,
            1, 1.00,
            2, 0.95,
            3, 0.90,
            4, 0.80,
            5, 0.75
    );

    public double calculatePrice(List<Integer> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return 0.0;
        }

        Map<Integer, Integer> bookCounts = new HashMap<>();
        for (int id : bookIds) {
            bookCounts.put(id, bookCounts.getOrDefault(id, 0) + 1);
        }

        List<Integer> bundles = new ArrayList<>();
        while (!bookCounts.isEmpty()) {
            bundles.add(bookCounts.size());
            bookCounts.entrySet().removeIf(entry -> {
                entry.setValue(entry.getValue() - 1);
                return entry.getValue() == 0;
            });
        }

        while (bundles.contains(5) && bundles.contains(3)) {
            bundles.remove(Integer.valueOf(5));
            bundles.remove(Integer.valueOf(3));
            bundles.add(4);
            bundles.add(4);
        }

        return bundles.stream()
                .mapToDouble(size -> size * 50.0 * DISCOUNTS.get(size))
                .sum();
    }
}