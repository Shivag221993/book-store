import com.bookstore.controller.CartPricingController;
import com.bookstore.domain.CartItem;
import com.bookstore.service.PricingFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookStore Pricing System - Comprehensive Test Suite")
public class BookPriceCalculatorTest {

    private static final double DELTA = 0.001;
    private PricingFacadeService pricingService;
    private CartPricingController apiController;

    @BeforeEach
    public void setUp() {
        pricingService = new PricingFacadeService();
        apiController = new CartPricingController();
    }

    // =========================================================================
    // 1. BOUNDARY & STRUCTURAL INPUT SCENARIOS
    // =========================================================================
    @Nested
    @DisplayName("Structural Inbound Payload Boundary Tests")
    class StructuralBoundaryTests {

        @Test
        @DisplayName("Scenario: Null cart collection must instantly evaluate to 0.0 safely")
        public void nullCartReturnsZero() {
            assertEquals(0.0, pricingService.processCartCalculation(null), DELTA);
        }

        @Test
        @DisplayName("Scenario: Empty cart list collections must evaluate to 0.0 safely")
        public void emptyCartReturnsZero() {
            assertEquals(0.0, pricingService.processCartCalculation(Collections.emptyList()), DELTA);
        }

        @Test
        @DisplayName("Scenario: Cart containing multiple items all with quantity 0 should return 0.0")
        public void itemsWithZeroQuantityAreSafelyIgnored() {
            List<CartItem> zeroQuantityBasket = List.of(
                    new CartItem("1", 0),
                    new CartItem("2", 0),
                    new CartItem("5", 0)
            );
            assertEquals(0.0, pricingService.processCartCalculation(zeroQuantityBasket), DELTA);
        }

        @Test
        @DisplayName("Scenario: Cart items mixed with positive and zero quantities drop the zeros and calculate correctly")
        public void mixedZeroAndPositiveQuantities() {
            List<CartItem> mixedBasket = List.of(
                    new CartItem("1", 1), // 50.0
                    new CartItem("2", 0), // Ignored
                    new CartItem("3", 1)  // 50.0 -> Total 100 * 0.95 = 95.0
            );
            assertEquals(95.0, pricingService.processCartCalculation(mixedBasket), DELTA);
        }
    }

    // =========================================================================
    // 2. LOGICAL KATA PRICING & BUNDLE COMBINATION SCENARIOS
    // =========================================================================
    @Nested
    @DisplayName("Core Pricing Logic & Dynamic Bundling Optimization Tests")
    class PricingAndOptimizationTests {

        @ParameterizedTest(name = "Scenario: Same-book stack of ID '1' with quantity {0} should cost {1} EUR (0% discount)")
        @CsvSource({
                "1, 50.0",
                "2, 100.0",
                "3, 150.0",
                "4, 200.0",
                "5, 250.0"
        })
        public void uniformItemStacksReceiveNoDiscount(int quantity, double expectedCost) {
            List<CartItem> basket = List.of(new CartItem("1", quantity));
            assertEquals(expectedCost, pricingService.processCartCalculation(basket), DELTA);
        }

        @Test
        @DisplayName("Scenario: Standard progressive baseline tiers (2, 3, 4, 5 unique books)")
        public void progressiveDiscountTierVerification() {
            // 2 unique books: 5% off -> 100 * 0.95 = 95.0
            var twoBooks = List.of(new CartItem("1", 1), new CartItem("2", 1));
            assertEquals(95.0, pricingService.processCartCalculation(twoBooks), DELTA);

            // 3 unique books: 10% off -> 150 * 0.90 = 135.0
            var threeBooks = List.of(new CartItem("1", 1), new CartItem("2", 1), new CartItem("3", 1));
            assertEquals(135.0, pricingService.processCartCalculation(threeBooks), DELTA);

            // 4 unique books: 20% off -> 200 * 0.80 = 160.0
            var fourBooks = List.of(new CartItem("1", 1), new CartItem("2", 1), new CartItem("3", 1), new CartItem("4", 1));
            assertEquals(160.0, pricingService.processCartCalculation(fourBooks), DELTA);

            // 5 unique books: 25% off -> 250 * 0.75 = 187.5
            var fiveBooks = List.of(new CartItem("1", 1), new CartItem("2", 1), new CartItem("3", 1), new CartItem("4", 1), new CartItem("5", 1));
            assertEquals(187.5, pricingService.processCartCalculation(fiveBooks), DELTA);
        }

        @Test
        @DisplayName("Scenario: The iconic complex optimal-split case (2, 2, 2, 1, 1) splits to twin sets of 4")
        public void complexBasketSplitsIntoMostOptimalCombination() {
            List<CartItem> basket = List.of(
                    new CartItem("1", 2),
                    new CartItem("2", 2),
                    new CartItem("3", 2),
                    new CartItem("4", 1),
                    new CartItem("5", 1)
            );
            // 5-set + 3-set = 187.5 + 135.0 = 322.5
            // 4-set + 4-set = 160.0 + 160.0 = 320.0 (Mathematical Winner)
            assertEquals(320.0, pricingService.processCartCalculation(basket), DELTA);
        }

        @Test
        @DisplayName("Scenario: Multi-layer asymmetric massive split (4 of book 1, 4 of book 2, 4 of book 3, 2 of book 4, 2 of book 5)")
        public void largerAsymmetricComplexOptimalSplits() {
            List<CartItem> basket = List.of(
                    new CartItem("1", 4),
                    new CartItem("2", 4),
                    new CartItem("3", 4),
                    new CartItem("4", 2),
                    new CartItem("5", 2)
            );
            // Dynamic backtracking clusters this into four sets of 4 books:
            // 4 * (4 * 50.0 * 0.80) = 4 * 160.0 = 640.0
            assertEquals(640.0, pricingService.processCartCalculation(basket), DELTA);
        }
    }

    // =========================================================================
    // 3. STRICT VALIDATION ENGINE SCENARIOS
    // =========================================================================
    @Nested
    @DisplayName("Validation & Fault-Tolerance Boundary Tests")
    class ValidationBoundaryTests {

        @Test
        @DisplayName("Scenario: Validator must block unrecognized data IDs (e.g. 6, 12, 23)")
        public void exceptionThrownForCatalogAbsence() {
            List<CartItem> unknownIdBasket = List.of(
                    new CartItem("1", 2),
                    new CartItem("6", 1),  // Unknown ID
                    new CartItem("12", 1)  // Unknown ID
            );

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pricingService.processCartCalculation(unknownIdBasket)
            );

            assertTrue(exception.getMessage().contains("Book ID '6' does not exist"));
        }

        @Test
        @DisplayName("Scenario: Validator must block negative quantity configurations")
        public void exceptionThrownForNegativeQuantities() {
            List<CartItem> negativeBasket = List.of(
                    new CartItem("1", 2),
                    new CartItem("2", -1) // Invalid quantity
            );

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pricingService.processCartCalculation(negativeBasket)
            );

            assertTrue(exception.getMessage().contains("Cart cannot contain negative quantities"));
        }

        @Test
        @DisplayName("Scenario: Validator must block blank or null Book ID representations")
        public void exceptionWithBlankBookId() {
            List<CartItem> brokenIdBasket = List.of(new CartItem("   ", 2));
            assertThrows(IllegalArgumentException.class, () -> pricingService.processCartCalculation(brokenIdBasket));

            List<CartItem> nullIdBasket = List.of(new CartItem(null, 2));
            assertThrows(IllegalArgumentException.class, () -> pricingService.processCartCalculation(nullIdBasket));
        }

        @Test
        @DisplayName("Scenario: Validator must reject explicitly null element array wrappers")
        public void exceptionWithNullElementInCart() {
            List<CartItem> corruptedList = new ArrayList<>();
            corruptedList.add(new CartItem("1", 1));
            corruptedList.add(null); // Corrupted element

            assertThrows(IllegalArgumentException.class, () -> pricingService.processCartCalculation(corruptedList));
        }
    }

    // =========================================================================
    // 4. REST CONTROLLER / INTERFACE CONTRACT SCENARIOS
    // =========================================================================
    @Nested
    @DisplayName("REST API Controller Endpoint Contract Tests")
    class RestApiControllerContractTests {

        @Test
        @DisplayName("Scenario: API returns 200 OK Response entity containing accurate price calculations")
        public void apiReturnsSuccessfulCalculationResponse() {
            List<CartItem> validPayload = List.of(
                    new CartItem("1", 1),
                    new CartItem("2", 1)
            );

            var response = apiController.calculateTotal(validPayload);

            assertEquals(200, response.statusCode());
            assertNotNull(response.body());
            assertEquals(95.0, response.body().finalPrice(), DELTA);
            assertEquals("EUR", response.body().currency());
            assertEquals("SUCCESS", response.body().statusMessage());
        }

        @Test
        @DisplayName("Scenario: API returns 400 Bad Request mapping the error payload response cleanly when invalid data passes")
        public void apiWrapsValidationExceptionIntoBadRequestPayload() {
            List<CartItem> brokenPayload = List.of(new CartItem("23", 5)); // Non-existent ID

            var response = apiController.calculateTotal(brokenPayload);

            assertEquals(400, response.statusCode());
            assertNotNull(response.body());
            assertEquals(0.0, response.body().finalPrice(), DELTA);
            assertTrue(response.body().statusMessage().contains("Book ID '23' does not exist"));
        }
    }
}