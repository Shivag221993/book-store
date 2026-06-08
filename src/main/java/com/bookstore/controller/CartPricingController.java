package com.bookstore.controller;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.PriceResponse;
import com.bookstore.service.PricingFacadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartPricingController {

    private final PricingFacadeService pricingService;

    public CartPricingController(PricingFacadeService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<PriceResponse> calculateTotal(@RequestBody List<CartItem> requestBody) {
        double optimizedTotal = pricingService.processCartCalculation(requestBody);
        return ResponseEntity.ok(new PriceResponse(optimizedTotal, "EUR", "SUCCESS"));
    }
}