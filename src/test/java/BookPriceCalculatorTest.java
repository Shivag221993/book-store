import com.bookstore.domain.CartItem;
import com.bookstore.domain.PriceResponse;
import com.bookstore.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Main.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("BookStore Pricing Engine - Spring Boot Integration Suite")
public class BookPriceCalculatorTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String API_URL = "/api/v1/cart/calculate";

    @Nested
    @DisplayName("REST API Success Calculation Specifications")
    class SuccessFlows {

        @Test
        @DisplayName("Scenario: Valid asymmetric complex split combo maps correctly via HTTP POST to 320.00 EUR")
        public void optimalAsymmetricComboReturnsSuccessfulCalculations() {
            List<CartItem> payload = List.of(
                    new CartItem("1", 2),
                    new CartItem("2", 2),
                    new CartItem("3", 2),
                    new CartItem("4", 1),
                    new CartItem("5", 1)
            );

            ResponseEntity<PriceResponse> response = restTemplate.postForEntity(API_URL, payload, PriceResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            assertEquals(320.0, response.getBody().finalPrice(), 0.001);
            assertEquals("SUCCESS", response.getBody().statusMessage());
        }

        @ParameterizedTest(name = "Scenario: Standard pricing tiers for unique count entries return proper metrics")
        @CsvSource({
                "1, 50.0",
                "2, 100.0"
        })
        public void uniformSameBookStackPricesWithoutDiscounts(int quantity, double expectedCost) {
            List<CartItem> payload = List.of(new CartItem("1", quantity));
            ResponseEntity<PriceResponse> response = restTemplate.postForEntity(API_URL, payload, PriceResponse.class);
            assertEquals(expectedCost, response.getBody().finalPrice(), 0.001);
        }
    }

    @Nested
    @DisplayName("REST API Validation Failure Boundary Checks")
    class ValidationFailureFlows {

        @Test
        @DisplayName("Scenario: Inbound cart containing quantity 0 triggers Global Exception Handling to serve 400 Bad Request")
        public void zeroQuantityTriggersExceptionFormat() {
            List<CartItem> payload = List.of(new CartItem("1", 0));

            ResponseEntity<PriceResponse> response = restTemplate.postForEntity(API_URL, payload, PriceResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().statusMessage().contains("must possess a positive quantity greater than zero"));
        }

        @Test
        @DisplayName("Scenario: Unregistered Catalog ID input triggers 400 Bad Request intercept status responses")
        public void missingCatalogIdFailsSecurely() {
            List<CartItem> payload = List.of(new CartItem("12", 2));

            ResponseEntity<PriceResponse> response = restTemplate.postForEntity(API_URL, payload, PriceResponse.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().statusMessage().contains("Book ID '12' does not exist"));
        }
    }
}