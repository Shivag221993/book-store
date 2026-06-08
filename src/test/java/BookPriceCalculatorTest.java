import com.bookstore.BookPriceCalculator;
import com.bookstore.Book;
import com.bookstore.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BookPriceCalculatorTest {

    private static final double ASSERTION_DELTA = 0.001; // Mandatory for accurate float assertions
    private BookPriceCalculator calculator;

    private Book b1;
    private Book b2;
    private Book b3;
    private Book b4;
    private Book b5;

    @BeforeEach
    public void setUp() {
        calculator = new BookPriceCalculator();
        b1 = new Book("1", "Book 1", 50.0);
        b2 = new Book("2", "Book 2", 50.0);
        b3 = new Book("3", "Book 3", 50.0);
        b4 = new Book("4", "Book 4", 50.0);
        b5 = new Book("5", "Book 5", 50.0);
    }

    @Test
    @DisplayName("Empty or null basket should evaluate to zero cost safely")
    public void structuralNullAndEmptyBaskets() {
        assertEquals(0.0, calculator.calculateCartPrice(Collections.emptyList()), ASSERTION_DELTA);
        assertEquals(0.0, calculator.calculateCartPrice(null), ASSERTION_DELTA);
    }

    @ParameterizedTest
    @DisplayName("Verify uniform stack scenarios receive zero scaling discounts")
    @CsvSource({
            "1, 50.0",
            "2, 100.0",
            "5, 250.0"
    })
    public void identicalBookBaskets(int volume, double expectedCost) {
        List<CartItem> basket = List.of(new CartItem(b1, volume));
        assertEquals(expectedCost, calculator.calculateCartPrice(basket), ASSERTION_DELTA);
    }

    @Test
    @DisplayName("Verify fundamental discount calculations across variance spectrum")
    public void progressiveDiscountTiers() {
        // 2 distinct books: 5% off (2 * 50 * 0.95 = 95)
        assertEquals(95.0, calculator.calculateCartPrice(List.of(new CartItem(b1, 1), new CartItem(b2, 1))), ASSERTION_DELTA);

        // 3 distinct books: 10% off (3 * 50 * 0.90 = 135)
        assertEquals(135.0, calculator.calculateCartPrice(List.of(new CartItem(b1, 1), new CartItem(b2, 1), new CartItem(b3, 1))), ASSERTION_DELTA);
    }

    @Test
    @DisplayName("Complex asymmetric grouping must break down to twin-4 bundles instead of 5-and-3")
    public void optimalBasketDistributionSplits() {
        List<CartItem> basket = List.of(
                new CartItem(b1, 2),
                new CartItem(b2, 2),
                new CartItem(b3, 2),
                new CartItem(b4, 1),
                new CartItem(b5, 1)
        );
        // Correct algorithm grouping strategy returns 320.0 (2 sets of 4 books)
        assertEquals(320.0, calculator.calculateCartPrice(basket), ASSERTION_DELTA);
    }

    @Test
    @DisplayName("Validation must catch runtime errors like unexpected negative numbers")
    public void faultToleranceValidations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CartItem(b1, -1);
        });

        List<CartItem> brokenBasket = List.of(new CartItem(b1, 2), new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateCartPrice(brokenBasket));
    }

    @Test
    @DisplayName("Scalability check: The system must dynamically adapt if brand new discount volumes are introduced")
    public void customizablePoliciesValidation() {
        // Introduce an explicit custom policy rule (e.g., adding a 6th volume tier with a 50% discount)
        Map<Integer, Double> customPolicy = Map.of(
                0, 1.0, 1, 1.0, 2, 0.95, 3, 0.90, 4, 0.80, 5, 0.75,
                6, 0.50 // 6 different books gets 50% off!
        );
        BookPriceCalculator customEngine = new BookPriceCalculator(customPolicy);
        Book b6 = new Book("6", "Book 6", 50.0);

        List<CartItem> megaBasket = List.of(
                new CartItem(b1, 1), new CartItem(b2, 1), new CartItem(b3, 1),
                new CartItem(b4, 1), new CartItem(b5, 1), new CartItem(b6, 1)
        );

        // 6 * 50.0 * 0.50 = 150.00
        assertEquals(150.0, customEngine.calculateCartPrice(megaBasket), ASSERTION_DELTA);
    }

    @Test
    @DisplayName("Stress testing high processing volume bounds to ensure performance optimization remains stable")
    public void heavyPayloadStressTesting() {
        // Create an extreme, large checkout basket spanning dozens of copies across multiple titles
        List<CartItem> massiveBasket = List.of(
                new CartItem(b1, 25),
                new CartItem(b2, 24),
                new CartItem(b3, 22),
                new CartItem(b4, 15),
                new CartItem(b5, 12)
        );

        // Ensure calculations process smoothly within an acceptable timeframe (under 1 second)
        assertTimeout(Duration.ofSeconds(1), () -> {
            double total = calculator.calculateCartPrice(massiveBasket);
            assertTrue(total > 0.0);
        });
    }
}