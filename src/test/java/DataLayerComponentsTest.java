import com.bookstore.datasource.BookRepository;
import com.bookstore.datasource.DiscountPolicyRegistry;
import com.bookstore.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("3. Repository & Policy Component Tests")
public class DataLayerComponentsTest {

    private BookRepository bookRepository;
    private DiscountPolicyRegistry policyRegistry;

    @BeforeEach
    public void setup() {
        bookRepository = new BookRepository();
        policyRegistry = new DiscountPolicyRegistry();
    }

    @Test
    @DisplayName("Scenario: Book catalog repository yields clean data lookup values for index bounds 1 to 5")
    public void repositoryCatalogCheck() {
        assertTrue(bookRepository.exists("1"));
        assertFalse(bookRepository.exists("6")); // Out of bounds check

        Optional<Book> cleanCodeBook = bookRepository.findById("1");
        assertTrue(cleanCodeBook.isPresent());
        assertEquals("Clean Code", cleanCodeBook.get().title());
        assertEquals(50.0, cleanCodeBook.get().basePrice(), 0.001);
    }

    @Test
    @DisplayName("Scenario: Verify baseline business discount policies hold exact multiplier metrics")
    public void discountPolicyRegistryCheck() {
        Map<Integer, Double> rules = policyRegistry.getActivePolicyModifiers();

        assertNotNull(rules);
        assertEquals(1.00, rules.get(1), 0.001); // 1 book -> No discount
        assertEquals(0.95, rules.get(2), 0.001); // 2 books -> 5% off
        assertEquals(0.80, rules.get(4), 0.001); // 4 books -> 20% off
        assertEquals(0.75, rules.get(5), 0.001); // 5 books -> 25% off
    }
}