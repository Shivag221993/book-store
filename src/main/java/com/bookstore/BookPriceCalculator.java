package com.bookstore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enterprise pricing service that optimizes basket distribution dynamically based on dynamic discount tiers.
 * Safely processes variable book pricing models using recursive memoization.
 */
public class BookPriceCalculator {

    // Fallback default discount percentages (1.0 = full price, 0.75 = 25% off)
    private static final Map<Integer, Double> DEFAULT_DISCOUNTS = Map.of(
            0, 1.00,
            1, 1.00,
            2, 0.95,
            3, 0.90,
            4, 0.80,
            5, 0.75
    );

    private final Map<Integer, Double> discountPolicy;

    public BookPriceCalculator() {
        this.discountPolicy = DEFAULT_DISCOUNTS;
    }

    public BookPriceCalculator(Map<Integer, Double> customDiscountPolicy) {
        Objects.requireNonNull(customDiscountPolicy, "Discount policy cannot be null.");
        this.discountPolicy = Map.copyOf(customDiscountPolicy);
    }

    /**
     * Entry point to calculate the total optimized price of a cart.
     */
    public double calculateCartPrice(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }

        // 1. Strict multi-layered input data validation
        validateCartComposition(cartItems);

        // Filter out zero quantities immediately so they don't break bundle parsing logic
        List<CartItem> activeItems = cartItems.stream()
                .filter(item -> item.quantity() > 0)
                .collect(Collectors.toList());

        if (activeItems.isEmpty()) {
            return 0.0;
        }

        // 2. Map book IDs to their actual specific book objects for accurate price lookup
        Map<String, Book> bookCatalog = activeItems.stream()
                .collect(Collectors.toMap(item -> item.book().id(), CartItem::book, (b1, b2) -> b1));

        // 3. Group and aggregate quantities per unique book ID
        Map<String, Integer> bookStockCounts = activeItems.stream()
                .collect(Collectors.toMap(
                        item -> item.book().id(),
                        CartItem::quantity,
                        Integer::sum
                ));

        // Sort books by remaining quantity descending to keep the search space predictable
        List<String> sortedBookIds = bookStockCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 4. Pass execution to the dynamic combination optimizer
        return findOptimalPrice(sortedBookIds, bookStockCounts, bookCatalog, new HashMap<>());
    }

    /**
     * Recursive combination calculator utilizing memoization to evaluate exact prices across custom variations.
     */
    private double findOptimalPrice(List<String> sortedIds, Map<String, Integer> stockCounts,
                                    Map<String, Book> catalog, Map<String, Double> memoCache) {

        // Clean and filter out zero-inventory keys from the current branch
        List<String> activeIds = sortedIds.stream()
                .filter(id -> stockCounts.getOrDefault(id, 0) > 0)
                .sorted((id1, id2) -> stockCounts.get(id2).compareTo(stockCounts.get(id1)))
                .collect(Collectors.toList());

        if (activeIds.isEmpty()) {
            return 0.0;
        }

        // Create a unique state string key for caching (e.g., "[B1:2, B2:1]")
        String stateKey = activeIds.stream()
                .map(id -> id + ":" + stockCounts.get(id))
                .collect(Collectors.joining(","));

        if (memoCache.containsKey(stateKey)) {
            return memoCache.get(stateKey);
        }

        double minimumCalculatedCost = Double.MAX_VALUE;
        int maxBundleSize = activeIds.size();

        // Branch out across all valid dynamic bundle size variations
        for (int currentBundleSize = 1; currentBundleSize <= maxBundleSize; currentBundleSize++) {

            // Deduct 1 copy from the top 'currentBundleSize' distinct books
            double bundleRawPriceSum = 0.0;
            for (int i = 0; i < currentBundleSize; i++) {
                String id = activeIds.get(i);
                stockCounts.put(id, stockCounts.get(id) - 1);

                // Track actual specific book price instead of hardcoded constants!
                bundleRawPriceSum += catalog.get(id).basePrice();
            }

            // Apply the discount modifier rule for this bundle size
            double discountModifier = discountPolicy.getOrDefault(currentBundleSize, 1.00);
            double currentBundleCost = bundleRawPriceSum * discountModifier;

            // Recurse down this path to collect remaining totals
            double totalPathCost = currentBundleCost + findOptimalPrice(activeIds, stockCounts, catalog, memoCache);
            minimumCalculatedCost = Math.min(minimumCalculatedCost, totalPathCost);

            // Backtrack: Restore stock counts before testing the next bundle size iteration
            for (int i = 0; i < currentBundleSize; i++) {
                String id = activeIds.get(i);
                stockCounts.put(id, stockCounts.get(id) + 1);
            }
        }

        memoCache.put(stateKey, minimumCalculatedCost);
        return minimumCalculatedCost;
    }

    private void validateCartComposition(List<CartItem> items) {
        for (CartItem item : items) {
            if (item == null || item.book() == null) {
                throw new IllegalArgumentException("Cart contains null item configurations.");
            }
            if (item.quantity() < 0) {
                throw new IllegalArgumentException("Cart checkout distributions cannot contain negative quantities.");
            }
            // Quantity of 0 is conceptually fine, but handled safely by filtering it out in step 2
        }
    }
}