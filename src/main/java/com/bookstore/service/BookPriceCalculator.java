package com.bookstore.service;

import com.bookstore.domain.Book;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookPriceCalculator {

    private final Map<Integer, Double> discountPolicy;

    public BookPriceCalculator(Map<Integer, Double> discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public double optimizePricing(Map<String, Integer> stockCounts, Map<String, Book> catalog) {
        return findOptimalPrice(stockCounts, catalog, new HashMap<>());
    }

    private double findOptimalPrice(Map<String, Integer> stockCounts, Map<String, Book> catalog, Map<String, Double> memo) {
        List<String> activeIds = stockCounts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (activeIds.isEmpty()) {
            return 0.0;
        }

        String stateKey = activeIds.stream()
                .map(id -> id + ":" + stockCounts.get(id))
                .collect(Collectors.joining(","));

        if (memo.containsKey(stateKey)) {
            return memo.get(stateKey);
        }

        int maxBundleSize = activeIds.size();

        return IntStream.rangeClosed(1, maxBundleSize)
                .mapToDouble(bundleSize -> {
                    double rawPriceSum = IntStream.range(0, bundleSize)
                            .mapToObj(activeIds::get)
                            .peek(id -> stockCounts.put(id, stockCounts.get(id) - 1))
                            .mapToDouble(id -> catalog.get(id).basePrice())
                            .sum();

                    double modifier = discountPolicy.getOrDefault(bundleSize, 1.00);
                    double currentBundleCost = rawPriceSum * modifier;

                    double pathCost = currentBundleCost + findOptimalPrice(stockCounts, catalog, memo);

                    IntStream.range(0, bundleSize)
                            .mapToObj(activeIds::get)
                            .forEach(id -> stockCounts.put(id, stockCounts.get(id) + 1));

                    return pathCost;
                })
                .min()
                .orElse(Double.MAX_VALUE);
    }
}