import com.bookstore.BookPriceCalculator;
import com.bookstore.Book;
import com.bookstore.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookPriceCalculatorTest {

    private static final double DELTA = 0.001;
    private BookPriceCalculator calculator;

    private Book b1;
    private Book b2;

    @BeforeEach
    public void setUp() {
        calculator = new BookPriceCalculator();
        b1 = new Book("1", "Book 1 Standard", 50.0);
        b2 = new Book("2", "Book 2 Premium", 100.0); // Variable base pricing!
    }

    @Test
    @DisplayName("Quantity of 0 should be gracefully ignored and evaluate to zero cost safely")
    public void zeroQuantityItemsAreIgnored() {
        List<CartItem> basket = List.of(
                new CartItem(b1, 0),
                new CartItem(b2, 0)
        );
        assertEquals(0.0, calculator.calculateCartPrice(basket), DELTA);
    }

    @Test
    @DisplayName("Verify mixed basket pricing using the individual book's baseline prices")
    public void variableBookPricesCalculatedCorrectly() {
        // Two distinct books with different prices (50.0 and 100.0) -> 5% off bundle discount
        // Total raw cost: 50 + 100 = 150.00. Discounted: 150 * 0.95 = 142.50
        List<CartItem> basket = List.of(
                new CartItem(b1, 1),
                new CartItem(b2, 1)
        );
        assertEquals(142.50, calculator.calculateCartPrice(basket), DELTA);
    }

    @Test
    @DisplayName("Empty or null inputs return zero cost")
    public void nullAndEmptyBaskets() {
        assertEquals(0.0, calculator.calculateCartPrice(Collections.emptyList()), DELTA);
        assertEquals(0.0, calculator.calculateCartPrice(null), DELTA);
    }
}