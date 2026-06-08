package com.bookstore.validation;

import com.bookstore.datasource.BookRepository;
import com.bookstore.domain.CartItem;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CartValidator {

    private final BookRepository bookRepository;

    public CartValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void validate(List<CartItem> items) {
        if (items == null) {
            return;
        }
        items.forEach(this::validateItem);
    }

    private void validateItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cart contains a null payload entry.");
        }
        if (item.bookId() == null || item.bookId().isBlank()) {
            throw new IllegalArgumentException("Book ID configuration cannot be blank.");
        }
        if (item.quantity() <= 0) {
            throw new IllegalArgumentException("Validation Failure: Cart items must possess a positive quantity greater than zero. Found value: " + item.quantity());
        }
        if (!bookRepository.exists(item.bookId())) {
            throw new IllegalArgumentException("Validation Failure: Book ID '" + item.bookId() + "' does not exist in the store catalog.");
        }
    }
}