package com.bookstore.exception;

import com.bookstore.domain.PriceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<PriceResponse> handleValidationFailure(IllegalArgumentException exception) {
        PriceResponse errorBody = new PriceResponse(0.0, "EUR", exception.getMessage());
        return ResponseEntity.badRequest().body(errorBody);
    }
}