package com.bookstore.service;

import com.bookstore.datasource.BookRepository;
import com.bookstore.datasource.DiscountPolicyRegistry;
import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.validation.CartValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PricingFacadeService {

    private final BookRepository repository = new BookRepository();
    private final DiscountPolicyRegistry policyRegistry = new DiscountPolicyRegistry();
    private final CartValidator validator = new CartValidator(repository);

    public double processCartCalculation(List<CartItem> rawCart) {
        // Guardrail: Intercept null or empty payloads immediately before processing streams
        if (rawCart == null || rawCart.isEmpty()) {
            return 0.0;
        }

        // 1. Validation Responsibility
        validator.validate(rawCart);

        // 2. Filter out zero-quantities
        List<CartItem> activePurchases = rawCart.stream()
                .filter(item -> item.quantity() > 0)
                .collect(Collectors.toList());

        if (activePurchases.isEmpty()) {
            return 0.0;
        }

        // 3. Map Data Repository Hydration Responsibility
        Map<String, Book> hydratedCatalog = activePurchases.stream()
                .map(item -> repository.findById(item.bookId()).orElseThrow())
                .collect(Collectors.toMap(Book::id, book -> book, (b1, b2) -> b1));

        Map<String, Integer> stockCounts = activePurchases.stream()
                .collect(Collectors.toMap(CartItem::bookId, CartItem::quantity, Integer::sum));

        // 4. Invoke Optimized Calculation Engine Responsibility
        BookPriceCalculator calculator = new BookPriceCalculator(policyRegistry.getActivePolicyModifiers());
        return calculator.optimizePricing(stockCounts, hydratedCatalog);
    }
}