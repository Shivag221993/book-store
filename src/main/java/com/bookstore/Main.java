package com.bookstore;

import com.bookstore.controller.CartPricingController;
import com.bookstore.domain.CartItem;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CartPricingController apiEndpoint = new CartPricingController();

        // SCENARIO A: Happy Path REST consumer execution
        List<CartItem> standardCartPayload = List.of(
                new CartItem("1", 2),
                new CartItem("2", 2),
                new CartItem("3", 2),
                new CartItem("4", 1),
                new CartItem("5", 1)
        );

        var response = apiEndpoint.calculateTotal(standardCartPayload);
        System.out.println("HTTP Status: " + response.statusCode());
        System.out.println("Payload Response: " + response.body().finalPrice() + " " + response.body().statusMessage());

        System.out.println("\n-------------------------------------\n");

        // SCENARIO B: Unknown Book ID (e.g. 6) validation crash test execution
        List<CartItem> invalidCartPayload = List.of(new CartItem("5", 2));
        var errorResponse = apiEndpoint.calculateTotal(invalidCartPayload);
        System.out.println("HTTP Status: " + errorResponse.statusCode());
        System.out.println("Payload Error Notice: " + errorResponse.body().statusMessage());
    }
}