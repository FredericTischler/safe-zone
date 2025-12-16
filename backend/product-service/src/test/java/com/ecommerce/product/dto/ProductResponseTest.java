package com.ecommerce.product.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseTest {

    @Test
    void allArgsConstructorShouldPopulateFields() {
        LocalDateTime now = LocalDateTime.now();
        ProductResponse response = new ProductResponse("id", "Phone", "Desc", 1000.0,
            "Tech", 2, "seller-1", "Alice", now.minusDays(1), now);

        assertThat(response.getId()).isEqualTo("id");
        assertThat(response.getSellerName()).isEqualTo("Alice");
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void settersShouldBeAvailableWithNoArgsConstructor() {
        ProductResponse response = new ProductResponse();
        response.setId("id-2");
        response.setName("Tablet");
        response.setStock(3);

        assertThat(response.getName()).isEqualTo("Tablet");
        assertThat(response.getStock()).isEqualTo(3);
    }

    @Test
    void equalsAndHashCodeShouldConsiderFields() {
        LocalDateTime now = LocalDateTime.now();
        ProductResponse first = new ProductResponse("id", "Phone", "Desc", 1000.0,
            "Tech", 2, "seller-1", "Alice", now.minusDays(1), now);
        ProductResponse second = new ProductResponse("id", "Phone", "Desc", 1000.0,
            "Tech", 2, "seller-1", "Alice", now.minusDays(1), now);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Phone");
        second.setName("Other");
        assertThat(first).isNotEqualTo(second);
    }
}
