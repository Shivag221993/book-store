package com.bookstore.controller;

import com.bookstore.domain.CartItem;
import com.bookstore.service.PricingFacadeService;
import java.util.List;

public class CartPricingController {

    private final PricingFacadeService pricingService = new PricingFacadeService();

    /**
     * POST /api/v1/cart/calculate
     * Consumed by external clients passing Cart payloads
     */
    public ResponseEntity calculateTotal(List<CartItem> requestBody) {
        try {
            double optimizedTotal = pricingService.processCartCalculation(requestBody);
            return ResponseEntity.ok(new PriceResponse(optimizedTotal, "EUR", "SUCCESS"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest(new PriceResponse(0.0, "EUR", e.getMessage()));
        }
    }

    // Mock HTTP Wrapper Objects simulating Spring Boot Framework contracts
    public record ResponseEntity(int statusCode, PriceResponse body) {
        public static ResponseEntity ok(PriceResponse body) { return new ResponseEntity(200, body); }
        public static ResponseEntity badRequest(PriceResponse body) { return new ResponseEntity(400, body); }
    }

    public record PriceResponse(double finalPrice, String currency, String statusMessage) {}
}