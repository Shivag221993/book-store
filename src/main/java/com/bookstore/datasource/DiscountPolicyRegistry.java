package com.bookstore.datasource;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DiscountPolicyRegistry {

    private static final Map<Integer, Double> CORE_DISCOUNTS = Map.of(
            0, 1.00,
            1, 1.00,
            2, 0.95,
            3, 0.90,
            4, 0.80,
            5, 0.75
    );

    public Map<Integer, Double> getActivePolicyModifiers() {
        return CORE_DISCOUNTS;
    }
}