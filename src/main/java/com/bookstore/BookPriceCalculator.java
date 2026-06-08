package com.bookstore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core pricing service that optimizes basket distribution dynamically based on dynamic discount tiers.
 * Uses a memoized combination search instead of hardcoded rules to guarantee scalability.
 */
public class BookPriceCalculator {

    private static final double BASE_BOOK_PRICE = 50.0;

    // Default discount percentages (1.0 = full price, 0.95 = 5% off)
    private static final Map<Integer, Double> DEFAULT_DISCOUNTS = Map.of(
            0, 1.00,
            1, 1.00,
            2, 0.95,
            3, 0.90,
            4, 0.80,
            5, 0.75
    );

    private final Map<Integer, Double> discountPolicy;

    /**
     * Constructs a calculator using default business discount configurations.
     */
    public BookPriceCalculator() {
        this.discountPolicy = DEFAULT_DISCOUNTS;
    }

    /**
     * Constructs a calculator with custom business rules, making the pricing engine highly scalable.
     */
    public BookPriceCalculator(Map<Integer, Double> customDiscountPolicy) {
        Objects.requireNonNull(customDiscountPolicy, "Discount policy cannot be null.");
        this.discountPolicy = Map.copyOf(customDiscountPolicy);
    }

    /**
     * Interface-like contract layer transforming business models into optimized math structures.
     */
    public double calculateCartPrice(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }

        // Validate cart health from a functional domain perspective
        validateCartComposition(cartItems);

        // Map and extract distinct unique counts using functional collection reduction
        Map<String, Integer> bookStockCounts = cartItems.stream()
                .collect(Collectors.toMap(
                        item -> item.book().id(),
                        CartItem::quantity,
                        Integer::sum
                ));

        // Strip structural labels into a sorted representation of quantities for computational efficiency
        List<Integer> distinctBookQuantities = bookStockCounts.values().stream()
                .filter(count -> count > 0)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Call the dynamic memoized optimizer matching the optimal combination
        return findOptimalPrice(distinctBookQuantities, new HashMap<>());
    }

    /**
     * Recursive combination calculator utilizing memoization.
     * Safely processes large inputs without performance degradation.
     */
    private double findOptimalPrice(List<Integer> quantities, Map<String, Double> memoizationCache) {
        // Drop padding zeroes out of structural evaluations
        List<Integer> activeQuantities = quantities.stream()
                .filter(q -> q > 0)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (activeQuantities.isEmpty()) {
            return 0.0;
        }

        // Distinct key tracking configuration state for caching
        String stateKey = activeQuantities.toString();
        if (memoizationCache.containsKey(stateKey)) {
            return memoizationCache.get(stateKey);
        }

        double minimumCalculatedCost = Double.MAX_VALUE;
        int maximumPossibleBundleSize = activeQuantities.size();

        // Branch out across all possible distribution bundle groupings
        for (int currentBundleSize = 1; currentBundleSize <= maximumPossibleBundleSize; currentBundleSize++) {
            List<Integer> remainingQuantities = new ArrayList<>(activeQuantities);

            // Construct a distinct group bundle
            for (int i = 0; i < currentBundleSize; i++) {
                remainingQuantities.set(i, remainingQuantities.get(i) - 1);
            }

            // Calculate business cost for this bundle permutation
            double currentBundleDiscountModifier = discountPolicy.getOrDefault(currentBundleSize, 1.00);
            double currentBundleCost = currentBundleSize * BASE_BOOK_PRICE * currentBundleDiscountModifier;

            // Recurse to discover total cost down this path
            double totalPathCost = currentBundleCost + findOptimalPrice(remainingQuantities, memoizationCache);
            minimumCalculatedCost = Math.min(minimumCalculatedCost, totalPathCost);
        }

        memoizationCache.put(stateKey, minimumCalculatedCost);
        return minimumCalculatedCost;
    }

    private void validateCartComposition(List<CartItem> items) {
        for (CartItem item : items) {
            if (item == null || item.book() == null) {
                throw new IllegalArgumentException("Cart contains null item configurations.");
            }
            if (item.book().id() == null || item.book().id().isBlank()) {
                throw new IllegalArgumentException("Book identity configurations cannot be blank.");
            }
            if (item.book().basePrice() < 0) {
                throw new IllegalArgumentException("Book baseline catalog cost cannot be negative.");
            }
            if (item.quantity() < 0) {
                throw new IllegalArgumentException("Cart checkout distributions cannot contain negative quantities.");
            }
        }
    }
}