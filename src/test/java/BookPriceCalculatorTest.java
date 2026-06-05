import com.bookstore.BookPriceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookPriceCalculatorTest {

    private BookPriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new BookPriceCalculator();
    }

    @Test
    public void emptyBasketCostsZero() {
        assertEquals(0.0, calculator.calculatePrice(Collections.emptyList()));
    }

    @Test
    public void singleBookCostsFifty() {
        assertEquals(50.0, calculator.calculatePrice(List.of(1)));
    }

    @Test
    public void multipleCopiesOfSameBookGetNoDiscount() {
        assertEquals(150.0, calculator.calculatePrice(List.of(1, 1, 1)));
    }

    @Test
    public void twoDifferentBooksGetFivePercentDiscount() {
        // 2 * 50 * 0.95 = 95.0
        assertEquals(95.0, calculator.calculatePrice(List.of(1, 2)));
    }

    @Test
    public void threeDifferentBooksGetTenPercentDiscount() {
        // 3 * 50 * 0.90 = 135.0
        assertEquals(135.0, calculator.calculatePrice(List.of(1, 2, 3)));
    }

    @Test
    public void fourDifferentBooksGetTwentyPercentDiscount() {
        // 4 * 50 * 0.80 = 160.0
        assertEquals(160.0, calculator.calculatePrice(List.of(1, 2, 3, 4)));
    }

    @Test
    public void fiveDifferentBooksGetTwentyFivePercentDiscount() {
        // 5 * 50 * 0.75 = 187.5
        assertEquals(187.5, calculator.calculatePrice(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    public void complexBasketSplitsOptimally() {
        // The problem statement example: 2 of book1, 2 of book2, 2 of book3, 1 of book4, 1 of book5
        // Best value is two sets of 4 books: 160.0 + 160.0 = 320.0
        List<Integer> basket = List.of(1, 1, 2, 2, 3, 3, 4, 5);
        assertEquals(320.0, calculator.calculatePrice(basket));
    }
}