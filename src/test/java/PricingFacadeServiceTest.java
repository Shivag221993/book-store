import com.bookstore.datasource.BookRepository;
import com.bookstore.datasource.DiscountPolicyRegistry;
import com.bookstore.service.PricingFacadeService;
import com.bookstore.validation.CartValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("2. Facade Service Component Tests")
public class PricingFacadeServiceTest {

    private PricingFacadeService facadeService;

    @BeforeEach
    public void setup() {
        BookRepository repository = new BookRepository();
        DiscountPolicyRegistry registry = new DiscountPolicyRegistry();
        CartValidator validator = new CartValidator(repository);
        facadeService = new PricingFacadeService(repository, registry, validator);
    }

    @Test
    @DisplayName("Scenario: Structural null payload arrays return 0.0 without triggering exceptions")
    public void processNullCartReturnsZero() {
        assertEquals(0.0, facadeService.processCartCalculation(null), 0.001);
    }

    @Test
    @DisplayName("Scenario: Empty basket collections return 0.0 safely")
    public void processEmptyCartReturnsZero() {
        assertEquals(0.0, facadeService.processCartCalculation(Collections.emptyList()), 0.001);
    }
}