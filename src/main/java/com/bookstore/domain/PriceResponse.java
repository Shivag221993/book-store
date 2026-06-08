package com.bookstore.domain;

public record PriceResponse(double finalPrice, String currency, String statusMessage) {}