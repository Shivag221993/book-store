package com.bookstore.service;

import com.bookstore.datasource.BookRepository;
import com.bookstore.datasource.DiscountPolicyRegistry;
import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.validation.CartValidator;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingFacadeService {

    private final BookRepository repository;
    private final DiscountPolicyRegistry policyRegistry;
    private final CartValidator validator;

    public PricingFacadeService(BookRepository repository, DiscountPolicyRegistry policyRegistry, CartValidator validator) {
        this.repository = repository;
        this.policyRegistry = policyRegistry;
        this.validator = validator;
    }

    public double processCartCalculation(List<CartItem> rawCart) {
        if (rawCart == null || rawCart.isEmpty()) {
            return 0.0;
        }

        validator.validate(rawCart);

        Map<String, Book> hydratedCatalog = rawCart.stream()
                .map(item -> repository.findById(item.bookId()).orElseThrow())
                .collect(Collectors.toMap(Book::id, book -> book, (b1, b2) -> b1));

        Map<String, Integer> stockCounts = rawCart.stream()
                .collect(Collectors.toMap(CartItem::bookId, CartItem::quantity, Integer::sum));

        BookPriceCalculator calculator = new BookPriceCalculator(policyRegistry.getActivePolicyModifiers());
        return calculator.optimizePricing(stockCounts, hydratedCatalog);
    }
}